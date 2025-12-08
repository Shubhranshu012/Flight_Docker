package com.bookingservice.controller;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.dto.FlightInventoryDto;
import com.bookingservice.dto.PassengerDto;
import com.bookingservice.feign.FlightInterface;
import com.bookingservice.kafka.BookingProducer;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.repository.PassengerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingRepository bookingRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightInterface flightClient;
    
    @MockBean
    private BookingProducer bookingProducer;
    
    @MockBean
    private PassengerRepository passengerRepo;

    private String inventoryId = "INV123";
    private String inventoryIdLowSeat = "INV999";

    @BeforeEach
    void setup() {

        FlightInventoryDto normalFlight = new FlightInventoryDto();
        normalFlight.setId(inventoryId);
        normalFlight.setAvailableSeats(180);
        normalFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        normalFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));

        FlightInventoryDto lowSeatFlight = new FlightInventoryDto();
        lowSeatFlight.setId(inventoryIdLowSeat);
        lowSeatFlight.setAvailableSeats(1);
        lowSeatFlight.setDepartureTime(LocalDateTime.now().plusDays(2));
        lowSeatFlight.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));

        when(flightClient.searchFlight(eq(inventoryId))).thenReturn(ResponseEntity.ok(normalFlight));

        when(flightClient.searchFlight(eq(inventoryIdLowSeat))).thenReturn(ResponseEntity.ok(lowSeatFlight));

        when(flightClient.updateAvailableSeat(anyString(), anyInt())).thenReturn(ResponseEntity.ok(Map.of("message", "OK")));

        when(bookingRepo.save(any())).thenAnswer(output ->output.getArgument(0));
    }

    private PassengerDto passenger(String name, String gender, int age, String seat, String meal) {
        PassengerDto p = new PassengerDto();
        p.setName(name);
        p.setGender(gender);
        p.setAge(age);
        p.setSeatNumber(seat);
        p.setMealOption(meal);
        return p;
    }

    private BookingRequestDto booking(PassengerDto p) {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setPassengers(List.of(p));
        return dto;
    }

    private BookingRequestDto validBooking() {
        return booking(passenger("Rohit", "MALE", 28, "12A", "VEG"));
    }

    @Test
    void testBookTicket_Success() throws Exception {

        mockMvc.perform(post("/api/flight/booking/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validBooking())))
                .andExpect(status().isCreated());
    }
    @Test
    void testBookTicket_InvalidGender() throws Exception {

        mockMvc.perform(post("/api/flight/booking/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(booking(passenger("Rohit", "", 28, "12A", "VEG")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBookTicket_InvalidAge() throws Exception {

        mockMvc.perform(post("/api/flight/booking/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(booking(passenger("Rohit", "MALE", -28, "12A", "VEG")))))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_InvalidMeal() throws Exception {

        mockMvc.perform(post("/api/flight/booking/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(booking(passenger("Rohit", "MALE", 28, "12A", "")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBookTicket_FlightServiceThrowsException() throws Exception {

        when(flightClient.searchFlight(anyString())).thenThrow(new RuntimeException("Flight service down"));
        BookingRequestDto dto = validBooking();

        mockMvc.perform(post("/api/flight/booking/" + inventoryId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
