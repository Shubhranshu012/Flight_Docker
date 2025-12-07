package com.flightservice.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SearchTripTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private FlightInventoryRepository inventoryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        inventoryRepo.deleteAll();
        flightRepo.deleteAll();

        flightRepo.save(Flight.builder().flightNumber("6E-111").airlineName("IndiGo").fromPlace(AIRPORT_NAME.valueOf("MUMBAI")).toPlace(AIRPORT_NAME.valueOf("DELHI")).build());

        inventoryRepo.save(
                FlightInventory.builder().flightId("6E-111").price(5000).totalSeats(180).availableSeats(180).source(AIRPORT_NAME.MUMBAI).destination(AIRPORT_NAME.DELHI) 
                        .departureTime(LocalDateTime.of(2026, 11, 26, 10, 0)).arrivalTime(LocalDateTime.of(2026, 11, 26, 12, 0)).build()
        );
    }
    @AfterEach
    void cleanup() {
    	inventoryRepo.deleteAll();
        flightRepo.deleteAll();

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
        searchDto.setTripType("Round_Trip");
        
        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchFlights_RoundTrip_NoReturnFlights() throws Exception {
        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setTripType("Round_Trip");
        searchDto.setReturnDate(LocalDate.of(2026, 11, 27));

        mockMvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchFlights_RoundTrip_Success() throws Exception {

        flightRepo.save(Flight.builder().flightNumber("6E-222").airlineName("IndiGo").fromPlace(AIRPORT_NAME.valueOf("MUMBAI")).toPlace(AIRPORT_NAME.valueOf("DELHI")).build());

        inventoryRepo.save(FlightInventory.builder().flightId("6E-222").price(4800).totalSeats(180).availableSeats(180).source(AIRPORT_NAME.DELHI).destination(AIRPORT_NAME.MUMBAI) 
        		.departureTime(LocalDateTime.of(2026, 11, 27, 15, 30)).arrivalTime(LocalDateTime.of(2026, 11, 27, 17, 30)).build());

        SearchRequestDto searchDto = buildSearchDto();
        searchDto.setTripType("Round_Trip");
        searchDto.setReturnDate(LocalDate.of(2026, 11, 26));

        mockMvc.perform(post("/api/flight/search")
        		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk());
    }
}
