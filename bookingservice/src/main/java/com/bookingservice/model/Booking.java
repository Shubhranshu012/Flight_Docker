package com.bookingservice.model;


import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document("bookings")
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class Booking {

    @Id
    private String id;
    private String pnr;
    private String email; 
    private LocalDateTime bookingTime;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String flightInventoryId;
    private BOOKING_STATUS status;
}