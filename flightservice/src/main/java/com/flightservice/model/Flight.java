package com.flightservice.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document("flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
	
	@Id
	private String flightNumber; 
	private String airlineName;
	private AIRPORT_NAME fromPlace;
	private AIRPORT_NAME toPlace;
}