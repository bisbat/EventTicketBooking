package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.config.RabbitMQConfig;
import com.bisbat.event_ticket_booking_system.dto.booking.BookingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConsumer {
    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeBookingMessage(String jsonMessage) {
        BookingMessage message = null;
        try {
            System.out.println("RabbitMQ ส่งข้อความมาให้ประมวลผล: " + jsonMessage);

            message = objectMapper.readValue(jsonMessage, BookingMessage.class);

            bookingService.reserveTicket(message.bookingId());

            System.out.println("จองตั๋วสำเร็จ! บันทึกลง Database เรียบร้อยสำหรับ: " + message.bookingId());

        } catch (IllegalArgumentException e) {
            System.err.println("ตั๋วถูกปฏิเสธ: " + e.getMessage());

            if (message != null && message.bookingId() != null) {
                bookingService.markAsFailed(message.bookingId(), e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("ระบบประมวลผลขัดข้อง: " + e.getMessage());
            if (message != null && message.bookingId() != null) {
                bookingService.markAsFailed(message.bookingId(), "ระบบขัดข้องไม่สามารถประมวลผลได้: " + e.getMessage());
            }
        }
    }
}
