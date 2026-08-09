package com.bisbat.event_ticket_booking_system.dto.auth;

public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String role
) {

}
