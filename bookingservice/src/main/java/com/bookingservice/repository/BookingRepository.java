package com.bookingservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bookingservice.model.BOOKING_STATUS;
import com.bookingservice.model.Booking;
import java.util.*;

@Repository
public interface BookingRepository extends MongoRepository<Booking,String>{
	
	Booking findByPnrAndStatus(String pnr, BOOKING_STATUS status);
	
	List<Booking> findByEmail(String email);

}