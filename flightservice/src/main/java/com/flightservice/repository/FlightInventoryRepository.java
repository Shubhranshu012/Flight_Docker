package com.flightservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.FlightInventory;

@Repository
public interface FlightInventoryRepository extends MongoRepository<FlightInventory, String> {
	
	Optional<FlightInventory> findByAirlineAndFlightIdAndSourceAndDestinationAndDepartureTime(String airline,String flightId,AIRPORT_NAME source,AIRPORT_NAME destination,LocalDateTime departureTime);
	
	List<FlightInventory> findBySourceAndDestinationAndDepartureTimeBetween(AIRPORT_NAME source, AIRPORT_NAME destination, LocalDateTime start, LocalDateTime end);

	Optional<FlightInventory> findById(String id);
}
