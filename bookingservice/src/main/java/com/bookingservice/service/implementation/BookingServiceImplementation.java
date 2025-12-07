package com.bookingservice.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.dto.FlightInventoryDto;
import com.bookingservice.dto.PassengerDto;
import com.bookingservice.exception.BadRequestException;
import com.bookingservice.exception.NotFoundException;
import com.bookingservice.feign.FlightInterface;
import com.bookingservice.kafka.BookingProducer;
import com.bookingservice.model.BOOKING_STATUS;
import com.bookingservice.model.Booking;
import com.bookingservice.model.GENDER;
import com.bookingservice.model.Passenger;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.repository.PassengerRepository;
import com.bookingservice.service.BookingService;

@Service
public class BookingServiceImplementation implements BookingService {
	
	@Autowired
	FlightInterface flightClient;
	
	@Autowired
	BookingRepository bookingRepo;

	@Autowired
	PassengerRepository passengerRepo;
	
	@Autowired
	BookingProducer bookingProducer;

	public String bookTicket(String inventoryId, BookingRequestDto bookingDto) {

		String pnr = "PNR"+UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		
		String currentEmail = bookingDto.getEmail();
		if (!currentEmail.endsWith("@gmail.com")) {
		    throw new BadRequestException("Bad Email");
		}
		
		ResponseEntity<FlightInventoryDto> flightResponse;
		try {
			flightResponse = flightClient.searchFlight(inventoryId);
		}
		catch(Exception exception) {
			throw new NotFoundException("");
		}
		
		FlightInventoryDto flightInventory = flightResponse.getBody();
		if(bookingDto.getPassengers().size() > flightInventory.getAvailableSeats()) {
			throw new BadRequestException("No Seat Left");
		}

		Booking booking = Booking.builder().pnr(pnr)
				.email(bookingDto.getEmail()).bookingTime(LocalDateTime.now())
				.departureTime(flightInventory.getDepartureTime()).arrivalTime(flightInventory.getArrivalTime())
				.flightInventoryId(inventoryId).status(BOOKING_STATUS.BOOKED).build();

		Booking savedBooking = bookingRepo.save(booking);

		List<Passenger> passengers = bookingDto.getPassengers().stream()
				.map(passengerDto -> createPassenger(passengerDto, savedBooking.getId(), inventoryId))
				.collect(Collectors.toList());

		passengerRepo.saveAll(passengers);
		int seatsToBook = bookingDto.getPassengers().size();
		flightClient.updateAvailableSeat(inventoryId, seatsToBook);
		
		bookingProducer.sendBookingEmail(bookingDto.getEmail(),pnr);
		
		return pnr;
	}

	private Passenger createPassenger(PassengerDto dto, String bookingId, String flightId) {
	    return Passenger.builder().bookingId(bookingId).flightInventoryId(flightId)
	            .name(dto.getName()).gender(GENDER.valueOf(dto.getGender()
	            .toUpperCase())).age(dto.getAge()).seatNumber(dto.getSeatNumber())
	            .mealOption(dto.getMealOption()).build();
	}
	public Object getHistory(String pnr) {
		Booking currentBooking=bookingRepo.findByPnrAndStatus(pnr,BOOKING_STATUS.valueOf("BOOKED"));
		
		if (currentBooking == null) {
		    throw new NotFoundException("");
		}
		List<Passenger> passengers = passengerRepo.findByBookingId(currentBooking.getId());
		
		Map<String, Object> response = new HashMap<>();
		response.put("booking", currentBooking);
		response.put("passengers", passengers);
		return response;

	}
	
	public Object getTicket(String email) {
		List<Booking> bookings = bookingRepo.findByEmailAndStatus(email, BOOKING_STATUS.BOOKED);
		System.out.println(bookings);
		if (bookings == null || bookings.isEmpty()) {
		    throw new NotFoundException("");
		}
	    List<Map<String, Object>> ticketList = new ArrayList<>();

	    for (Booking booking : bookings) {
	        List<Passenger> passengers =passengerRepo.findByBookingId(booking.getId());

	        Map<String, Object> ticket = new HashMap<>();
	        ticket.put("booking", booking);
	        ticket.put("passengers", passengers);

	        ticketList.add(ticket);
	    }
	    return ticketList;
	}

	public Object cancelTicket(String pnr) {
		Booking currentBooking;
		try {
			currentBooking=bookingRepo.findByPnrAndStatus(pnr,BOOKING_STATUS.valueOf("BOOKED"));
		}
		catch(Exception exception) {
			throw new NotFoundException("");
		}
		if (currentBooking == null) {
		    throw new NotFoundException("");
		}
		if(!currentBooking.getDepartureTime().isAfter(LocalDateTime.now().plusHours(24))) {
			throw new BadRequestException("Depature is within 24 Hrs");
		}
		List<Passenger> passengers = passengerRepo.findByBookingId(currentBooking.getId());
		int numberOfPassenger=passengers.size();
		flightClient.updateAvailableSeat(currentBooking.getFlightInventoryId(),-numberOfPassenger);
		
		currentBooking.setStatus(BOOKING_STATUS.valueOf("CANCELLED"));
		bookingRepo.save(currentBooking);
		
		return "";
	}
}
