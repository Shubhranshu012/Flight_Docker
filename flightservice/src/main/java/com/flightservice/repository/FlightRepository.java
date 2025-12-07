package com.flightservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.flightservice.model.Flight;

@Repository
public interface FlightRepository extends MongoRepository<Flight,String>{

}
