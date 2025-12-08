package com.flightservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightservice.dto.SearchRequestDto;
import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.Flight;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.flightservice.repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchTripTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightRepository flightRepo;

    @MockBean
    private FlightInventoryRepository inventoryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightInventory onwardFlight;
    private FlightInventory returnFlight;

    @BeforeEach
    void setup() {

        Flight flight1 = Flight.builder().flightNumber("6E-111").airlineName("IndiGo").fromPlace(AIRPORT_NAME.MUMBAI).toPlace(AIRPORT_NAME.DELHI).build();

        Flight flight2 = Flight.builder().flightNumber("6E-222").airlineName("IndiGo").fromPlace(AIRPORT_NAME.DELHI).toPlace(AIRPORT_NAME.MUMBAI).build();

        onwardFlight = FlightInventory.builder().flightId("6E-111").price(5000)
                .totalSeats(180).availableSeats(180).source(AIRPORT_NAME.MUMBAI).destination(AIRPORT_NAME.DELHI)
                .departureTime(LocalDateTime.of(2026, 11, 26, 10, 0)).arrivalTime(LocalDateTime.of(2026, 11, 26, 12, 0)).build();

        returnFlight = FlightInventory.builder().flightId("6E-222").price(4800).totalSeats(180)
                .availableSeats(180).source(AIRPORT_NAME.DELHI).destination(AIRPORT_NAME.MUMBAI)
                .departureTime(LocalDateTime.of(2026, 11, 27, 15, 30)).arrivalTime(LocalDateTime.of(2026, 11, 27, 17, 30)).build();

        Mockito.when(flightRepo.findById("6E-111")).thenReturn(Optional.of(flight1));
        Mockito.when(flightRepo.findById("6E-222")).thenReturn(Optional.of(flight2));

        Mockito.when(inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(
                AIRPORT_NAME.MUMBAI,
                AIRPORT_NAME.DELHI,
                LocalDate.of(2026, 11, 26).atStartOfDay(),
                LocalDate.of(2026, 11, 26).atTime(23, 59, 59)
        )).thenReturn(List.of(onwardFlight));

        Mockito.when(inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(
                AIRPORT_NAME.DELHI,
                AIRPORT_NAME.MUMBAI,
                LocalDate.of(2026, 11, 27).atStartOfDay(),
                LocalDate.of(2026, 11, 27).atTime(23, 59, 59)
        )).thenReturn(List.of(returnFlight));

        Mockito.when(inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(
                AIRPORT_NAME.valueOf("BHUBANESWAR"),
                AIRPORT_NAME.valueOf("KOLKATA"),
                LocalDate.of(2026, 11, 26).atStartOfDay(),
                LocalDate.of(2026, 11, 26).atTime(23, 59, 59)
        )).thenReturn(List.of());
    }

    private SearchRequestDto buildSearchDto() {
        SearchRequestDto searchDto = new SearchRequestDto();
        searchDto.setFromPlace("MUMBAI");
        searchDto.setToPlace("DELHI");
        searchDto.setJourneyDate(LocalDate.of(2026, 11, 26));
        searchDto.setTripType("ONE_WAY");
        searchDto.setReturnDate(null);
        return searchDto;
    }

    @Test
    void testSearchFlights_OneWay_Success() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchFlights_OneWay_NoFlights() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setFromPlace("BHUBANESWAR");
        searchDto.setToPlace("KOLKATA");

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchFlights_RoundTrip_MissingReturnDate() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setTripType("ROUND_TRIP");

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchFlights_RoundTrip_NoReturnFlights() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setTripType("ROUND_TRIP");
        searchDto.setReturnDate(LocalDate.of(2026, 11, 28)); 

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchFlights_RoundTrip_Success() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setTripType("ROUND_TRIP");
        searchDto.setReturnDate(LocalDate.of(2026, 11, 26));

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk());
    }
}
