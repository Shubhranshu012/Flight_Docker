package com.bookingservice.service;

import com.bookingservice.dto.BookingRequestDto;

public interface BookingService {
	public Object bookTicket(String flightId, BookingRequestDto bookingDto);
	
	public Object getHistory(String pnr);
	
	public Object getTicket(String email);
	
	public Object cancelTicket(String pnr);
}
