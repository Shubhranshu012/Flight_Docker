package com.bookingservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    @KafkaListener(topics = "booking-email", groupId = "email-group")
    public void consume(String message) {
        String[] parts = message.split(",");
        String email = parts[0];
        String bookingId = parts[1];
        System.out.println("Sending email to " + email + " for booking " + bookingId);
    }
}
