package com.bisbat.event_ticket_booking_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(nullable = false, length = 250)
    String name;
    @Column(name="total_seats", nullable = false)
    int totalSeats;
    @Column(name="available_seats", nullable = false)
    int availableSeats;
    @Column(name="start_date", nullable = false)
    OffsetDateTime startDate;
    @Column(name="end_date", nullable = false)
    OffsetDateTime endDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    EventStatus status;
    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    Date createdAt;

    public Event(String name, int totalSeats, OffsetDateTime startDate, OffsetDateTime endDate){
        this.name = name;
        this.totalSeats = totalSeats;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event() {

    }
}
