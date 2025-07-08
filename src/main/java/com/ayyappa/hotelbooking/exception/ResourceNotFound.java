package com.ayyappa.hotelbooking.exception;

/**
 * Custom exception to indicate that a requested resource was not found.
 * This exception extends RuntimeException and can be used throughout
 * the application to signal missing entities such as bookings, users, etc.
 */
public class ResourceNotFound extends RuntimeException {

    /**
     * Constructs a new ResourceNotFound exception with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ResourceNotFound(String message) {
        super(message);
    }
}