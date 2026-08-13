package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.dto.booking.BookingStatus;
import com.bisbat.event_ticket_booking_system.dto.booking.EventBookingResponse;
import com.bisbat.event_ticket_booking_system.dto.booking.MyBookingResponse;
import com.bisbat.event_ticket_booking_system.exception.BusinessRuleException;
import com.bisbat.event_ticket_booking_system.exception.ResourceNotFoundException;
import com.bisbat.event_ticket_booking_system.models.Booking;
import com.bisbat.event_ticket_booking_system.models.Event;
import com.bisbat.event_ticket_booking_system.models.EventStatus;
import com.bisbat.event_ticket_booking_system.models.User;
import com.bisbat.event_ticket_booking_system.repository.BookingRepository;
import com.bisbat.event_ticket_booking_system.repository.EventRepository;
import com.bisbat.event_ticket_booking_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createPendingBooking(UUID eventId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลผู้ใช้งาน"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูล Event"));

        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessRuleException("ขออภัย อีเวนต์นี้ปิดรับการจองแล้ว");
        }

        if (bookingRepository.existsByUserAndEvent(user, event)) {
            throw new BusinessRuleException("คุณได้ทำการจองตั๋วสำหรับอีเวนต์นี้ไปแล้ว (จำกัด 1 สิทธิ์ต่อ 1 บัญชี)");
        }

        if (event.getAvailableSeats() <= 0) {
            throw new BusinessRuleException("ขออภัย ที่นั่งเต็มแล้ว");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        return booking;
    }

    @Transactional
    public void reserveTicket(UUID bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลคำสั่งจองหมายเลข: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            return;
        }

        Event event = booking.getEvent();

        if (event.getAvailableSeats() <= 0) {
            throw new BusinessRuleException("ขออภัย ที่นั่งเต็มแล้ว");
        }

        event.setAvailableSeats(event.getAvailableSeats() - 1);
        eventRepository.save(event);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }


    public List<MyBookingResponse> getMyBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้ใช้งาน"));

        return bookingRepository.findByUser(user)
                .stream()
                .map(booking -> new MyBookingResponse(
                        booking.getId(),
                        booking.getEvent().getName(),
                        booking.getEvent().getStartDate(),
                        booking.getStatus()
                ))
                .toList();
    }

    public List<EventBookingResponse> getBookingsByEvent(UUID eventId) {

        return bookingRepository.findByEventId(eventId)
                .stream()
                .map(booking -> new EventBookingResponse(
                        booking.getId(),
                        booking.getUser().getEmail(),
                        booking.getUser().getFname() + " " + booking.getUser().getLname(),
                        booking.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsFailed(UUID bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูลคำสั่งจองหมายเลข: " + bookingId));

        booking.setStatus(BookingStatus.FAILED);
        booking.setFailReason(reason);

        bookingRepository.save(booking);
    }

}
