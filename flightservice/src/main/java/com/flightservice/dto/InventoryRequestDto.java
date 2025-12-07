package com.flightservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Data
public class InventoryRequestDto {
	@NotBlank(message = "Airline name is required")
	private String airlineName;
	
	@NotBlank(message = "From place is required")
	private String fromPlace;
	
	@NotBlank(message = "To place is required")
	private String toPlace;
	
	@NotBlank(message = "Flight number is required")
	private String flightNumber;
	
	@NotNull(message = "Departure time is required")
	@Future(message = "Departure time Should Be in Future")
	private LocalDateTime departureTime;
	
	
	@NotNull(message = "Arrival time is required")
	@Future(message = "Arrival time Should Be in Future")
	private LocalDateTime arrivalTime;
	
	@NotNull 
	@Positive
	private float price;
	
	@NotNull 
	@Positive
	private Integer totalSeats;
	
	@NotNull 
	@Positive
	private Integer availableSeats;
}