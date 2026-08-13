package com.bisbat.event_ticket_booking_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // ให้ Jackson รู้จักกับวันที่แบบมี Timezone (OffsetDateTime)
        mapper.registerModule(new JavaTimeModule());

        // ปิดการแปลงวันที่เป็น Timestamp เพื่อให้ได้เป็น String รูปแบบ ISO-8601
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
