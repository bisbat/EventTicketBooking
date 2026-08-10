package com.bisbat.event_ticket_booking_system.dto.event;

import java.time.OffsetDateTime;

public record EventRequest(
        String name,
        int totalSeats,
        OffsetDateTime startDate,
        OffsetDateTime endDate

) {

}
