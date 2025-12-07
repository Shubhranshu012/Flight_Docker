package com.bookingservice.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bookingservice.model.Passenger;
import java.util.*
;@Repository
public interface PassengerRepository extends MongoRepository<Passenger, String> {

    List<Passenger> findByBookingId(String bookingId);
    
    @Aggregation(pipeline = {
    	    "{ '$match': { 'flightInventoryId': ?0, 'status': 'BOOKED' }}",
    	    "{ '$project': { '_id': 0, 'seatNumber': 1 }}"
    })
    String findSeatNumbersByFlightInventoryId(String flightInventoryId);
}