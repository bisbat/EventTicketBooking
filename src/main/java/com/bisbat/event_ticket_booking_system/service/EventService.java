package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.dto.event.EventRequest;
import com.bisbat.event_ticket_booking_system.exception.BusinessRuleException;
import com.bisbat.event_ticket_booking_system.exception.ResourceNotFoundException;
import com.bisbat.event_ticket_booking_system.models.Event;
import com.bisbat.event_ticket_booking_system.models.EventStatus;
import com.bisbat.event_ticket_booking_system.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(EventRequest request){
        if(request.startDate().isBefore(OffsetDateTime.now())){
            throw new BusinessRuleException("วันที่เริ่มอีเวนต์ต้องไม่เป็นอดีต");
        }

        if(request.startDate().isAfter(request.endDate())){
            throw new BusinessRuleException("วันที่เริ่มอีเวนต์ ต้องเกิดก่อนวันที่สิ้นสุด");
        }

        if(request.totalSeats()<=0){
            throw new BusinessRuleException("จำนวนที่นั่งทั้งหมดต้องมากกว่า 0");
        }

        Event event = new Event(request.name(),request.totalSeats(),request.startDate(),request.endDate());
        event.setStatus(EventStatus.OPEN);
        eventRepository.save(event);
        return event;
    }

    @Transactional
    public Event updateEvent(UUID id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูล Event ที่ต้องการแก้ไข"));

        if (request.startDate().isBefore(OffsetDateTime.now())) {
            throw new BusinessRuleException("วันที่เริ่มอีเวนต์ต้องไม่เป็นอดีต");
        }
        if (request.startDate().isAfter(request.endDate())) {
            throw new BusinessRuleException("วันที่เริ่มเกิดกหลังันที่สิ้นสุดไม่ได้");
        }

        int bookedSeats = event.getTotalSeats() - event.getAvailableSeats();

        if (request.totalSeats() < bookedSeats) {
            throw new BusinessRuleException(
                    "แก้ไขที่นั่งไม่สำเร็จ! ไม่สามารถกำหนดที่นั่งรวม (" + request.totalSeats() +
                            ") ให้น้อยกว่าจำนวนตั๋วที่ถูกจองไปแล้ว (" + bookedSeats + " ที่นั่ง) ได้"
            );
        }

        int newAvailableSeats = request.totalSeats() - bookedSeats;


        event.setName(request.name());
        event.setTotalSeats(request.totalSeats());
        event.setAvailableSeats(newAvailableSeats);
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบ event นี้ในระบบ"));
    }

    @Transactional
    public void deleteEvent(UUID id) {
        Event event = getEventById(id);

        if (event.getTotalSeats() != event.getAvailableSeats()) {
            throw new BusinessRuleException(
                    "ไม่อนุญาตให้ลบ Event นี้เนื่องจากมีผู้จองตั๋วไปแล้ว! (แนะนำให้ทำระบบยกเลิก หรือเปลี่ยนสถานะเป็น CANCELED แทน)"
            );
        }

        eventRepository.delete(event);
    }
}
