package com.smartoffice.manager;

import com.smartoffice.config.OfficeConfiguration;
import com.smartoffice.exception.BookingConflictException;
import com.smartoffice.exception.InvalidRoomException;
import com.smartoffice.exception.ValidationException;
import com.smartoffice.model.Booking;
import com.smartoffice.util.LoggerUtil;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BookingManager: concurrency-aware in-memory booking manager.
 *
 * Responsibilities:
 * - Accept bookings and cancel bookings
 * - Prevent overlapping bookings for same room
 * - Schedule auto-release of bookings if room remains unoccupied for configured delay
 * - Maintain occupancy counts (updated by occupancy sensors/manager)
 *
 * Thread-safety strategy:
 * - Use a ConcurrentHashMap<Integer, List<Booking>> for per-room bookings
 * - Use a ReentrantLock per room to serialize modifications for that room only (fine-grained locking)
 * - Use ScheduledExecutorService to schedule auto-release tasks (no busy loops)
 */
public class BookingManager {

    private static final Logger log = LoggerUtil.getLogger(BookingManager.class);

    // maps roomId -> list of bookings (guarded by per-room lock when modified)
    private final ConcurrentHashMap<Integer, List<Booking>> bookingsByRoom = new ConcurrentHashMap<>();

    // map bookingId -> booking (for quick lookup and cancel)
    private final ConcurrentHashMap<String, Booking> bookingById = new ConcurrentHashMap<>();

    // occupancy tracking: roomId -> current occupant count (updated by occupancy sensors)
    private final ConcurrentHashMap<Integer, AtomicInteger> occupancy = new ConcurrentHashMap<>();

    // locks for per-room operations (fine-grained)
    private final ConcurrentHashMap<Integer, ReentrantLock> roomLockMap = new ConcurrentHashMap<>();

    // scheduler for auto-release tasks
    private final ScheduledExecutorService scheduler;

    // map bookingId -> ScheduledFuture for the auto-release, so we can cancel if occupied or booking cancelled
    private final ConcurrentHashMap<String, ScheduledFuture<?>> autoReleaseTasks = new ConcurrentHashMap<>();

    // how long to wait before auto-releasing an unoccupied booking (configurable)
    private final Duration autoReleaseDelay;

    // reference to global office config
    private final OfficeConfiguration config;

    /**
     * Create a BookingManager.
     *
     * @param autoReleaseDelay minutes (or Duration) - time after which an unoccupied booking will be auto released
     * @param threadPoolSize   number of threads used by scheduler/rescue tasks
     */
    public BookingManager(Duration autoReleaseDelay, int threadPoolSize) {
        Objects.requireNonNull(autoReleaseDelay, "autoReleaseDelay required");
        if (threadPoolSize <= 0) threadPoolSize = 1;
        this.autoReleaseDelay = autoReleaseDelay;
        this.scheduler = Executors.newScheduledThreadPool(threadPoolSize, runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setName(STR."booking-scheduler-\{t.getId()}");
            return t;
        });

        // Obtain reference to OfficeConfiguration (must have been initialized before creating manager)
        this.config = OfficeConfiguration.getInstance();
        // initialize maps for rooms
        for (int i = 1; i <= config.getTotalRooms(); i++) {
            bookingsByRoom.put(i, new ArrayList<>());
            occupancy.put(i, new AtomicInteger(0));
            roomLockMap.put(i, new ReentrantLock());
        }
        log.info("BookingManager initialized with {} rooms, autoReleaseDelay={}", config.getTotalRooms(), autoReleaseDelay);
    }

    /**
     * Attempt to book a room. Validates room existence and conflicts.
     *
     * @param booking Booking object (immutable)
     * @throws InvalidRoomException       if room doesn't exist
     * @throws BookingConflictException   if time overlap detected
     * @throws ValidationException        if booking invalid
     */
    public Booking bookRoom(Booking booking) {
        validateBooking(booking);
        int roomId = booking.getRoomId();
        validateRoomExists(roomId);

        ReentrantLock lock = getLockForRoom(roomId);
        lock.lock();
        try {
            List<Booking> list = bookingsByRoom.get(roomId);
            // check for overlap
            for (Booking existing : list) {
                if (existing.overlapsWith(booking)) {
                    String msg = String.format("New booking [%s] conflicts with [%s]", booking.getBookingId(), existing.getBookingId());
                    log.warn(msg);
                    throw new BookingConflictException(msg);
                }
            }
            // add booking
            list.add(booking);
            bookingById.put(booking.getBookingId(), booking);
            log.info("Booking created: {}", booking);

            // schedule auto-release: if room remains unoccupied for autoReleaseDelay after creation, release
            ScheduledFuture<?> future = scheduler.schedule(
                    () -> autoReleaseIfUnoccupied(booking.getBookingId()),
                    autoReleaseDelay.toMillis(), TimeUnit.MILLISECONDS
            );
            autoReleaseTasks.put(booking.getBookingId(), future);

            return booking;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancel a booking by id.
     *
     * @param bookingId booking id
     * @return true if cancelled; false if not found
     */
    public boolean cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) return false;
        Booking existing = bookingById.remove(bookingId);
        if (existing == null) {
            log.info("Attempted to cancel non-existent booking {}", bookingId);
            return false;
        }
        int roomId = existing.getRoomId();
        ReentrantLock lock = getLockForRoom(roomId);
        lock.lock();
        try {
            List<Booking> list = bookingsByRoom.get(roomId);
            list.removeIf(b -> bookingId.equals(b.getBookingId()));
            // cancel scheduled auto-release if exists
            ScheduledFuture<?> f = autoReleaseTasks.remove(bookingId);
            if (f != null) f.cancel(false);
            log.info("Booking {} cancelled and removed", bookingId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Called by occupancy sensors/manager to update occupant count for a room.
     * If occupancy >= 2, we interpret as "occupied" and we cancel any pending auto-release for current bookings.
     *
     * @param roomId     room id
     * @param occupantCount current occupant count (>=0)
     */
    public void updateOccupancy(int roomId, int occupantCount) {
        validateRoomExists(roomId);
        if (occupantCount < 0) throw new ValidationException("occupantCount must be >=0");
        occupancy.get(roomId).set(occupantCount);
        log.debug("Room {} occupancy updated to {}", roomId, occupantCount);

        if (occupantCount >= 2) {
            // cancel auto-release for all future bookings for this room (they are now occupied)
            ReentrantLock lock = getLockForRoom(roomId);
            lock.lock();
            try {
                List<Booking> list = bookingsByRoom.get(roomId);
                for (Booking b : list) {
                    ScheduledFuture<?> f = autoReleaseTasks.remove(b.getBookingId());
                    if (f != null) {
                        f.cancel(false);
                        log.info("Cancelled auto-release for booking {} because room {} is occupied", b.getBookingId(), roomId);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Returns current occupant count for a room.
     */
    public int getOccupancy(int roomId) {
        validateRoomExists(roomId);
        return occupancy.get(roomId).get();
    }

    /**
     * Find bookings for a room (defensive copy).
     */
    public List<Booking> getBookingsForRoom(int roomId) {
        validateRoomExists(roomId);
        List<Booking> copy;
        ReentrantLock lock = getLockForRoom(roomId);
        lock.lock();
        try {
            copy = new ArrayList<>(bookingsByRoom.get(roomId));
        } finally {
            lock.unlock();
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Find booking by id.
     */
    public Optional<Booking> findBookingById(String bookingId) {
        if (bookingId == null) return Optional.empty();
        return Optional.ofNullable(bookingById.get(bookingId));
    }

    /**
     * Auto-release logic executed by scheduler when booking's grace period elapses.
     * If the room is still unoccupied (occupancy < 2) and booking still exists and hasn't started,
     * remove the booking and log.
     *
     * Note: we check the booking's start time — if booking already started or was cancelled, do nothing.
     */
    private void autoReleaseIfUnoccupied(String bookingId) {
        try {
            Booking b = bookingById.get(bookingId);
            if (b == null) {
                log.debug("Auto-release: booking {} already gone", bookingId);
                return;
            }
            int roomId = b.getRoomId();
            int occ = occupancy.get(roomId).get();
            // if occupied, do not release
            if (occ >= 2) {
                log.info("Auto-release skipped for {}: room {} currently occupied ({} occupants)", bookingId, roomId, occ);
                autoReleaseTasks.remove(bookingId);
                return;
            }
            // if booking has already started, don't auto-release (user might be late, but policy could differ)
            LocalDateTime now = LocalDateTime.now();
            if (!now.isBefore(b.getStart())) {
                log.info("Auto-release skipped for {}: booking already started or in progress", bookingId);
                autoReleaseTasks.remove(bookingId);
                return;
            }

            // proceed to remove booking
            ReentrantLock lock = getLockForRoom(roomId);
            lock.lock();
            try {
                List<Booking> list = bookingsByRoom.get(roomId);
                boolean removed = list.removeIf(x -> bookingId.equals(x.getBookingId()));
                if (removed) {
                    bookingById.remove(bookingId);
                    log.info("Booking {} auto-released due to no occupancy within {} (room {})", bookingId, autoReleaseDelay, roomId);
                    // TODO: notify listeners / send notifications if implemented
                } else {
                    log.debug("Auto-release: booking {} not found in room list (maybe already removed)", bookingId);
                }
            } finally {
                lock.unlock();
                autoReleaseTasks.remove(bookingId);
            }
        } catch (Exception ex) {
            log.error("Error during autoRelease task for booking " + bookingId, ex);
        }
    }

    /**
     * Validate booking object basic invariants.
     */
    private void validateBooking(Booking b) {
        if (b == null) throw new ValidationException("booking is null");
        if (b.getDurationMinutes() <= 0) throw new ValidationException("duration must be >0");
        if (b.getStart() == null) throw new ValidationException("start time required");
        // Additional validation: booking start should be in the future or within allowed window
        if (b.getStart().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ValidationException("booking start time must be now or future");
        }
    }

    /**
     * Validate room existence using OfficeConfiguration.
     */
    private void validateRoomExists(int roomId) {
        if (roomId <= 0 || roomId > config.getTotalRooms()) {
            throw new InvalidRoomException("Invalid room id: " + roomId);
        }
    }

    /**
     * Get or create a lock for a room. This uses computeIfAbsent to guarantee only one lock per room.
     */
    private ReentrantLock getLockForRoom(int roomId) {
        return roomLockMap.computeIfAbsent(roomId, id -> new ReentrantLock());
    }

    /**
     * Shut down scheduler gracefully — call during app exit or test teardown.
     */
    public void shutdownNow() {
        log.info("Shutting down BookingManager scheduler...");
        scheduler.shutdownNow();
    }

}
