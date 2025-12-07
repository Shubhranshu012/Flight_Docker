package com.bookingservice.dto;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class BookingRequestDto {
	@NotBlank(message = "Email is required")
    private String email;
	
	@NotNull(message = "Passengers list cannot be null")
    @Size(min = 1, message = "At least 1 passenger is required")
    @Valid   
    private List<PassengerDto> passengers;
	
	
}
