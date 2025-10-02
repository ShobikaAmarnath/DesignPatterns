package com.smartoffice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Small helper to obtain loggers consistently across the app.
 * Central place to modify logging policy (e.g., add MDC, trace ids in future).
 */
public final class LoggerUtil {
    private LoggerUtil() {}

    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}
