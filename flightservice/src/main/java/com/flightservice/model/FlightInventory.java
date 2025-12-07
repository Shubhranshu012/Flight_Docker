package com.flightservice.model;


import lombok.*;


import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("flight_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInventory {
	
	@Id
	private String id;
	private String airline;
	private String flightId;
	private AIRPORT_NAME source;   
    private AIRPORT_NAME destination;
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;	
	private float price;		
	private Integer totalSeats;
	private Integer availableSeats;
}
