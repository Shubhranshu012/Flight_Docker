package com.flightservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.flightservice.dto.SearchRequestDto;
import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.FlightInventory;
import com.flightservice.service.implementation.FlightServiceImplment;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FlightSearchController {
	
	@Autowired
	FlightServiceImplment inventoryService;
	
    @PostMapping("/api/flight/search")
    public ResponseEntity<Map<String, List<FlightInventory>>> search(@RequestBody @Valid SearchRequestDto dto) {
        return ResponseEntity.ok(inventoryService.searchFlights(dto));
    } 
    @GetMapping("/api/airports")
    public AIRPORT_NAME[] allAirports() {
    		AIRPORT_NAME[] all = AIRPORT_NAME.values();
    		return all;
    }
    @GetMapping("/api/flight/search/{flightNumber}") 
    public ResponseEntity<Object> searchFlight(@PathVariable String flightNumber) {
    	return ResponseEntity.status(HttpStatus.OK).body(inventoryService.searchFlightBasedOnFlightNumber(flightNumber));
    }
    @PutMapping("/api/flight/update/seat/{flightNumber}")
    public ResponseEntity<Map<String, String>> updateAvailableSeat(@PathVariable String flightNumber,@RequestBody Integer seat) {
    	Map<String,String> responce=new HashMap<>();
    	responce.put("message",inventoryService.changeAvaliableSeat(flightNumber,seat));
    	return ResponseEntity.status(HttpStatus.OK).body(responce);
    }
}