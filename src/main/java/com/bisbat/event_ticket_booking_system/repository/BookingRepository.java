package com.bisbat.event_ticket_booking_system.repository;

import com.bisbat.event_ticket_booking_system.models.Booking;
import com.bisbat.event_ticket_booking_system.models.Event;
import com.bisbat.event_ticket_booking_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    boolean existsByUserAndEvent(User user, Event event);

    List<Booking> findByUser(User user);
    List<Booking> findByEventId(UUID eventId);
}
