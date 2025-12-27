package com.bookingservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.service.implementation.BookingServiceImplementation;

import jakarta.validation.Valid;

@RestController
public class BookingController {
	@Autowired
	BookingServiceImplementation bookingService;

	@PostMapping("/api/flight/booking/{flightId}")
	public ResponseEntity<Map<String,String>> book(@PathVariable String flightId, @RequestBody @Valid BookingRequestDto bookingDto) {
		String pnr = bookingService.bookTicket(flightId, bookingDto);
		Map<String,String> responce=new HashMap<>();
		responce.put("pnr", pnr);
		return ResponseEntity.status(HttpStatus.CREATED).body(responce);
	}
	@GetMapping("/api/flight/seats/{flightId}")
	public ResponseEntity<Map<String,List<String>>> book(@PathVariable String flightId) {
		List<String> seats = bookingService.seats(flightId);
		Map<String,List<String>> responce=new HashMap<>();
		responce.put("Seats", seats);
		return ResponseEntity.status(HttpStatus.OK).body(responce);
	}
	@GetMapping("/api/flight/ticket/{pnr}")
	public Object history(@PathVariable String pnr){
		return bookingService.getHistory(pnr);
	}

	@GetMapping("api/flight/booking/history/{email}")
	public Object historyEmail(@PathVariable String email){
		return bookingService.getTicket(email);
	}
	
	@DeleteMapping("/api/flight/booking/cancel/{pnr}")
	public String deleteBooking(@PathVariable String pnr){
		bookingService.cancelTicket(pnr);
		return null;
	}
}
