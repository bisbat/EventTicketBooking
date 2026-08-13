package com.bisbat.event_ticket_booking_system.controller;

import com.bisbat.event_ticket_booking_system.dto.booking.BookingMessage;
import com.bisbat.event_ticket_booking_system.dto.booking.EventBookingResponse;
import com.bisbat.event_ticket_booking_system.dto.booking.MyBookingResponse;
import com.bisbat.event_ticket_booking_system.models.Booking;
import com.bisbat.event_ticket_booking_system.service.BookingProducer;
import com.bisbat.event_ticket_booking_system.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingProducer bookingProducer;

    @PostMapping("/event/{eventId}")
    public String reserveTicketAsync(
            @PathVariable UUID eventId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        Booking pendingBooking = bookingService.createPendingBooking(eventId, userEmail);

        BookingMessage message = new BookingMessage(pendingBooking.getId());

        bookingProducer.sendBookingRequest(message);

        return "ระบบได้รับคำสั่งจองของคุณแล้ว (เข้าคิวสำเร็จ) กรุณารอสักครู่เพื่อตรวจสอบสถานะตั๋วในหน้า My Bookings!";
    }

    @GetMapping("/my-bookings")
    public List<MyBookingResponse> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        return bookingService.getMyBookings(userEmail);
    }

    @GetMapping("/event/{eventId}/list")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EventBookingResponse> getEventBookings(@PathVariable UUID eventId) {
        return bookingService.getBookingsByEvent(eventId);
    }
}
