package com.bisbat.event_ticket_booking_system.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "booking_queue";
    public static final String EXCHANGE_NAME = "booking_exchange";
    public static final String ROUTING_KEY = "booking_routing_key";

    @Bean
    public Queue bookingQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue bookingQueue, DirectExchange bookingExchange) {
        return BindingBuilder.bind(bookingQueue).to(bookingExchange).with(ROUTING_KEY);
    }
}
