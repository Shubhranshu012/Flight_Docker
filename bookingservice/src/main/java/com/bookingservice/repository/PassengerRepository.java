package com.bookingservice.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bookingservice.model.Passenger;
import java.util.*
;@Repository
public interface PassengerRepository extends MongoRepository<Passenger, String> {

    List<Passenger> findByBookingId(String bookingId);
    
    List<Passenger> findByFlightInventoryId(String flightInventoryId);
}