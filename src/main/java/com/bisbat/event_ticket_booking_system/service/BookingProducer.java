package com.bisbat.event_ticket_booking_system.service;

import com.bisbat.event_ticket_booking_system.config.RabbitMQConfig;
import com.bisbat.event_ticket_booking_system.dto.booking.BookingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendBookingRequest(BookingMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    jsonMessage
            );

            System.out.println("ส่งคำสั่งจองเข้า RabbitMQ สำเร็จ!: " + jsonMessage);

        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการส่งข้อความ: " + e.getMessage());
        }
    }
}
