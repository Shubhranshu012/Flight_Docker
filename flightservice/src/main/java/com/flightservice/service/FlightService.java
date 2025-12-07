package com.flightservice.service;

import java.util.List;
import java.util.Map;

import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.dto.SearchRequestDto;
import com.flightservice.model.FlightInventory;

public interface FlightService {
	public FlightInventory addInventory(InventoryRequestDto dto);
	public Map<String, List<FlightInventory>> searchFlights(SearchRequestDto searchDto);
}
