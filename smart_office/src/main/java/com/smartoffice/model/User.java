package com.smartoffice.model;

import com.smartoffice.exception.ValidationException;
import java.util.Objects;

/**
 * Simple domain User model (minimal).
 * In real systems, this would include authentication fields; for this exercise it's an identity holder.
 */
public final class User {
    private final String userId;     // unique user id (email or username)
    private final String displayName; // optional friendly name

    public User(String userId, String displayName) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("userId is required");
        }
        this.userId = userId.trim();
        this.displayName = (displayName == null) ? this.userId : displayName.trim();
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return STR."User{userId='\{userId}', displayName='\{displayName}'}";
    }
}
