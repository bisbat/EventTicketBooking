package com.bisbat.event_ticket_booking_system.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: "+ email);
    }
}
