package com.bisbat.event_ticket_booking_system.dto.booking;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MyBookingResponse(
        UUID bookingId,
        String eventName,
        OffsetDateTime startDate,
        BookingStatus status
) {}