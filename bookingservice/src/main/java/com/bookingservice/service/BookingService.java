package com.bookingservice.service;

import com.bookingservice.dto.BookingRequestDto;

public interface BookingService {
	public Object bookTicket(String flightId, BookingRequestDto bookingDto);
}
