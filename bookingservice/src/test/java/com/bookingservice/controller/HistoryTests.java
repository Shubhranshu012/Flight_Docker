package com.bookingservice.controller;

import com.bookingservice.feign.FlightInterface;
import com.bookingservice.model.BOOKING_STATUS;
import com.bookingservice.model.Booking;
import com.bookingservice.model.GENDER;
import com.bookingservice.model.Passenger;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.repository.PassengerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HistoryTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingRepository bookingRepo;

    @MockBean
    private PassengerRepository passengerRepo;

    @MockBean
    private FlightInterface flightClient;

    private String bookingId;
    private String pnr;
    private String inventoryId = "INV123";

    @BeforeEach
    void setup() {

        Booking booking = Booking.builder().id("BOOK1").pnr("PNR12345").email("test@gmail.com").flightInventoryId(inventoryId)
                .departureTime(LocalDateTime.now().plusDays(2))   .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2)).status(BOOKING_STATUS.BOOKED).build();

        bookingId = booking.getId();
        pnr = booking.getPnr();

        Passenger passenger = Passenger.builder().id("PAS1").bookingId(bookingId).flightInventoryId(inventoryId)
                .name("Rohit").gender(GENDER.MALE).age(28).seatNumber("12A").mealOption("VEG").build();

        when(bookingRepo.findByPnrAndStatus(pnr, BOOKING_STATUS.BOOKED)).thenReturn(booking);
        when(bookingRepo.findByPnrAndStatus("pnrInvalid", BOOKING_STATUS.BOOKED)).thenReturn(null);

        when(bookingRepo.findByPnrAndStatus("INVALID",BOOKING_STATUS.BOOKED)).thenReturn(null);

        when(bookingRepo.findByEmail("test@gmail.com")).thenReturn(List.of(booking));

        when(bookingRepo.findByEmail("INVALID")).thenReturn(List.of());

        when(passengerRepo.findByBookingId(bookingId)).thenReturn(List.of(passenger));

        when(bookingRepo.save(any())).thenAnswer(output -> output.getArgument(0));

        when(flightClient.updateAvailableSeat(anyString(), anyInt())).thenReturn(ResponseEntity.ok(Map.of("message", "OK")));
    }

    @Test
    void testGetHistory_Success() throws Exception {

        mockMvc.perform(get("/api/flight/ticket/" + pnr))
                .andExpect(status().isOk());
    }
    
    @Test
    void testGetHistory_BAD() throws Exception {

        mockMvc.perform(get("/api/flight/ticket/" + "pnrInvalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetHistory_NotFound() throws Exception {

        mockMvc.perform(get("/api/flight/ticket/INVALID"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetTicketByEmail_Success() throws Exception {

        mockMvc.perform(get("/api/flight/booking/history/test@gmail.com"))
                .andExpect(status().isOk());
    }
    @Test
    void testGetTicketByEmail_NotFound() throws Exception {

        mockMvc.perform(get("/api/flight/booking/history/INVALID"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testCancelTicket_Success() throws Exception {

        mockMvc.perform(delete("/api/flight/booking/cancel/" + pnr))
                .andExpect(status().isOk());
    }
    @Test
    void testCancelTicket_InvalidPNR() throws Exception {

        mockMvc.perform(delete("/api/flight/booking/cancel/INVALID"))
                .andExpect(status().isNotFound());
    }
}
