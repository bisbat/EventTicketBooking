package com.bisbat.event_ticket_booking_system.models;

import com.bisbat.event_ticket_booking_system.dto.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 1 User สามารถมีได้หลาย Booking (จองได้หลายงาน)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 1 Event สามารถมีได้หลาย Booking (มีคนจองหลายคน)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private BookingStatus status;

    @CreationTimestamp
    @Column(name="booked_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String failReason;
}
