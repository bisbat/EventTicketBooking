package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.dto.event.EventRequest;
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
        System.out.println("hi");
        if(request.startDate().isBefore(OffsetDateTime.now())){
            throw new IllegalArgumentException("วันที่เริ่มอีเว้นต้องไม่เป็นอดีต");
        }

        if(request.startDate().isAfter(request.endDate())){
            throw new IllegalArgumentException("วันที่เริ่มอีเวนต์ ต้องเกิดก่อนวันที่สิ้นสุด");
        }

        if(request.totalSeats()<=0){
            throw new IllegalArgumentException("จำนวนที่นั่งทั้งหมดต้องมากกว่า 0");
        }

        Event event = new Event(request.name(),request.totalSeats(),request.startDate(),request.endDate());
        event.setStatus(EventStatus.OPEN);
        eventRepository.save(event);
        return event;
    }

    @Transactional
    public Event updateEvent(UUID id, EventRequest request) {

        // 1. ค้นหา Event จาก Database ด้วย ID
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูล Event ที่ต้องการแก้ไข"));

        // 2. ตรวจสอบเงื่อนไขวันที่
        if (request.startDate().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("วันที่เริ่มอีเวนต์ต้องไม่เป็นอดีต");
        }
        if (request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException("วันที่เริ่มเกิดก่อนวันที่สิ้นสุด");
        }

        // --- หัวใจสำคัญ: คำนวณที่นั่ง ---

        // 3. หาว่า "มีคนจองไปแล้วกี่ที่" = (ที่นั่งรวมทั้งหมดของเก่า - ที่นั่งว่างของเก่า)
        int bookedSeats = event.getTotalSeats() - event.getAvailableSeats();

        // 4. ถ้า Admin ดันใส่ค่า TotalSeats ใหม่ "น้อยกว่า" จำนวนคนที่จองไปแล้ว ระบบต้องด่ากลับครับ
        if (request.totalSeats() < bookedSeats) {
            throw new IllegalArgumentException(
                    "แก้ไขที่นั่งไม่สำเร็จ! ไม่สามารถกำหนดที่นั่งรวม (" + request.totalSeats() +
                            ") ให้น้อยกว่าจำนวนตั๋วที่ถูกจองไปแล้ว (" + bookedSeats + " ที่นั่ง) ได้"
            );
        }

        // 5. คำนวณที่นั่งว่างใหม่ = (ที่นั่งรวมทั้งหมดที่ Admin กรอกมาใหม่ - จำนวนคนที่จองไปแล้ว)
        int newAvailableSeats = request.totalSeats() - bookedSeats;

        // 6. อัปเดตข้อมูล (สถานะ Status เราจะไม่ให้แก้ตรงนี้)
        event.setName(request.name());
        event.setTotalSeats(request.totalSeats());
        event.setAvailableSeats(newAvailableSeats);
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());

        // 7. บันทึกทับลง Database
        return eventRepository.save(event);
    }

    // 1. ดึงข้อมูล Event ทั้งหมด
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // 2. ดึงข้อมูล Event ตาม ID
    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูล Event ที่ต้องการค้นหา"));
    }

    @Transactional
    public void deleteEvent(UUID id) {
        // หา Event ก่อนว่ามีอยู่จริงไหม (ใช้ Method getEventById ที่เพิ่งสร้าง)
        Event event = getEventById(id);

        // --- Business Logic สำคัญ ---
        // เช็คว่ามีคนจองหรือยัง? ถ้าที่นั่งว่าง ไม่เท่ากับ ที่นั่งรวม แปลว่ามีคนกดจองไปแล้ว!
        if (event.getTotalSeats() != event.getAvailableSeats()) {
            throw new IllegalArgumentException(
                    "ไม่อนุญาตให้ลบ Event นี้เนื่องจากมีผู้จองตั๋วไปแล้ว! (แนะนำให้ทำระบบยกเลิก หรือเปลี่ยนสถานะเป็น CANCELED แทน)"
            );
        }

        // ถ้ายังไม่มีใครจอง (ที่นั่งว่าง = ที่นั่งรวม) ก็สั่งลบทิ้งได้เลยอย่างปลอดภัย
        eventRepository.delete(event);
    }
}
