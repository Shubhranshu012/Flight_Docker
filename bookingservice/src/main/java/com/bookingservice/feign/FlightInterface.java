package com.bookingservice.feign;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bookingservice.dto.FlightInventoryDto;


@FeignClient("FLIGHTSERVICE") 
public interface FlightInterface {
	@GetMapping("/api/flight/search/{flightNumber}")
	   public ResponseEntity<FlightInventoryDto> searchFlight(@PathVariable String flightNumber);
	
    @PutMapping("/api/flight/update/seat/{flightNumber}")
    public ResponseEntity<Map<String, String>> updateAvailableSeat(@PathVariable String flightNumber,@RequestBody Integer seat);
}
