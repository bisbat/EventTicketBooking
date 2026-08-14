package com.bisbat.event_ticket_booking_system.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record EventRequest(
        @NotBlank(message = "กรุณาระบุชื่ออีเวนต์")
        String name,
        @NotNull(message = "กรุณาระบุจำนวนที่นั่ง")
        @Positive(message = "จำนวนที่นั่งต้องมากกว่า 0")
        int totalSeats,
        @NotNull(message = "กรุณาระบุวันที่เริ่มอีเวนต์")
        OffsetDateTime startDate,
        @NotNull(message = "กรุณาระบุวันที่สิ้นสุด")
        OffsetDateTime endDate

) {

}
