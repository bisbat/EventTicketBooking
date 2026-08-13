package com.bisbat.event_ticket_booking_system.controller;

import com.bisbat.event_ticket_booking_system.dto.event.EventRequest;
import com.bisbat.event_ticket_booking_system.models.Event;
import com.bisbat.event_ticket_booking_system.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public Event createEvent(@RequestBody EventRequest request){
        return eventService.createEvent(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable UUID id,
            @RequestBody EventRequest request
    ) {
        return eventService.updateEvent(id, request);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return "ลบข้อมูล Event สำเร็จแล้ว!";
    }
}
