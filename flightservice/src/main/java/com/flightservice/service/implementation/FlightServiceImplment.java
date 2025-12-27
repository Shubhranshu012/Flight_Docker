package com.flightservice.service.implementation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.dto.SearchRequestDto;
import com.flightservice.exception.BadRequestException;
import com.flightservice.exception.NotFoundException;
import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.Flight;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.flightservice.repository.FlightRepository;
import com.flightservice.service.FlightService;

@Service
public class FlightServiceImplment implements FlightService {
	
	
	@Autowired
	FlightInventoryRepository inventoryRepo;
	
	@Autowired 
	FlightRepository flightRepo;
	
	public FlightInventory addInventory(InventoryRequestDto inventoryDto) {
    	
    	if (inventoryDto.getAvailableSeats() > inventoryDto.getTotalSeats()) {
            throw new BadRequestException("Available seats cannot be greater than total seats");
        }
    	if (inventoryDto.getArrivalTime().isBefore(inventoryDto.getDepartureTime())) {
    	    throw new BadRequestException("Arrival time cannot be before departure time");
    	}
    	if(inventoryDto.getFromPlace().equals(inventoryDto.getToPlace())) {
    		throw new BadRequestException("From To Cant be Same");
    	}
    	AIRPORT_NAME source;
    	AIRPORT_NAME destination;
    	
    	try {
    		source = AIRPORT_NAME.valueOf(inventoryDto.getFromPlace().toUpperCase());
    		destination = AIRPORT_NAME.valueOf(inventoryDto.getToPlace().toUpperCase());
    	}catch (Exception exc) {
    		throw new BadRequestException("Invalid Source or Destination");
    	}
    	Optional<FlightInventory> duplicate = inventoryRepo.findByAirlineAndFlightIdAndSourceAndDestinationAndDepartureTime(inventoryDto.getAirlineName(),inventoryDto.getFlightNumber(),source,destination,inventoryDto.getDepartureTime());

    	if (duplicate.isPresent()) {
    	        throw new BadRequestException("Flight already exists with same details (airline, flightNumber, route, departureTime)");
    	}
    	
    	flightRepo.findById(inventoryDto.getFlightNumber()).orElseGet(() -> {
    			Flight newFlight = Flight.builder().flightNumber(inventoryDto.getFlightNumber()).airlineName(inventoryDto.getAirlineName()).fromPlace(source).toPlace(destination).build();
    			return flightRepo.save(newFlight);
    	});

    	FlightInventory fi = FlightInventory.builder().flightId(inventoryDto.getFlightNumber()).departureTime(inventoryDto.getDepartureTime()).arrivalTime(inventoryDto.getArrivalTime()).price(inventoryDto.getPrice())
    			.totalSeats(inventoryDto.getTotalSeats()).availableSeats(inventoryDto.getAvailableSeats()).source(source).destination(destination).airline(inventoryDto.getAirlineName()).build();

    	return inventoryRepo.save(fi);
    }
	
	public Map<String, List<FlightInventory>> searchFlights(SearchRequestDto searchDto) {

        Map<String, List<FlightInventory>> response = new HashMap<>();

        LocalDateTime onwardStart = searchDto.getJourneyDate().atStartOfDay();
        LocalDateTime onwardEnd = searchDto.getJourneyDate().atTime(23, 59, 59);
        AIRPORT_NAME source;
    	AIRPORT_NAME destination;
    	
    	try {
    		source = AIRPORT_NAME.valueOf(searchDto.getFromPlace().toUpperCase());
    		destination = AIRPORT_NAME.valueOf(searchDto.getToPlace().toUpperCase());
    	}catch (Exception exc) {
    		throw new BadRequestException("Invalid Source or Destination");
    	}
        List<FlightInventory> onwardFlights =inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(source,destination,onwardStart,onwardEnd );

        if (onwardFlights.isEmpty()) {
            throw new NotFoundException();
        }

        response.put("onwardFlights", onwardFlights);

        if (searchDto.getTripType().equalsIgnoreCase("ROUND_TRIP")) {

            if (searchDto.getReturnDate() == null) {
                throw new BadRequestException("Return date is required for ROUND_TRIP");
            }

            LocalDateTime returnStart = searchDto.getReturnDate().atStartOfDay();
            LocalDateTime returnEnd = searchDto.getReturnDate().atTime(23, 59, 59);

            List<FlightInventory> returnFlights =inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(source,destination,returnStart,returnEnd);

            if (returnFlights.isEmpty()) {
                throw new NotFoundException();
            }

            response.put("returnFlights", returnFlights);
        }

        return response;
    }
	
	public List<FlightInventory> getAll(){
		return inventoryRepo.findAll();
	}
	public FlightInventory searchFlightBasedOnFlightNumber(String flightNumber){
		Optional<FlightInventory> inventory=inventoryRepo.findById(flightNumber);
		if(inventory.isEmpty()) {
			throw new NotFoundException();
		}
		return inventory.get();
	}
	public String changeAvaliableSeat(String flightNumber, Integer seat) {

	    Optional<FlightInventory> currentInventory = inventoryRepo.findById(flightNumber);

	    if (currentInventory.isEmpty()) {
	        throw new NotFoundException();
	    }
	    currentInventory.get().setAvailableSeats(currentInventory.get().getAvailableSeats()-seat);
	    inventoryRepo.save(currentInventory.get());
	    return "Available seats updated successfully for flight " + flightNumber;
	}
}
