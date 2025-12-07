package com.bookingservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class BookingProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @CircuitBreaker(name = "kafkaCB", fallbackMethod = "fallbackSendBookingEmail")
    public void sendBookingEmail(String email, String bookingId) {
        String message = email + "," + bookingId;
        kafkaTemplate.send("booking-email", message);
    }
    public void fallbackSendBookingEmail(String email, String pnr, Throwable ex) {
        System.err.println("Kafka is down");
    }
}