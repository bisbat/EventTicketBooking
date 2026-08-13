package com.bisbat.event_ticket_booking_system.dto.booking;

import java.util.UUID;

public record EventBookingResponse(
        UUID bookingId,
        String userEmail,
        String userFullName,
        BookingStatus status
) {}