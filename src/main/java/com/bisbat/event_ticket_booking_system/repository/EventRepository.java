package com.bisbat.event_ticket_booking_system.repository;

import com.bisbat.event_ticket_booking_system.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
