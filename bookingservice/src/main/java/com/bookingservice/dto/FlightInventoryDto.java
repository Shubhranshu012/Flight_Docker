package com.bookingservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInventoryDto {
	
    private String id;
    private String airline;
    private String flightId;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private float price;
    private Integer totalSeats;
    private Integer availableSeats;
}