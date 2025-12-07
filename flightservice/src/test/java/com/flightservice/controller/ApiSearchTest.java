package com.flightservice.controller;

import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightInventoryRepository inventoryRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    private String id;
    
    @BeforeEach
    void setup() {
        inventoryRepo.deleteAll();

        FlightInventory saved = inventoryRepo.save(
                FlightInventory.builder()
                        .flightId("6E-101")
                        .airline("IndiGo")
                        .source(AIRPORT_NAME.MUMBAI)
                        .destination(AIRPORT_NAME.DELHI)
                        .totalSeats(180)
                        .availableSeats(180)
                        .price(5000)
                        .departureTime(LocalDateTime.of(2026, 1, 1, 10, 0))
                        .arrivalTime(LocalDateTime.of(2026, 1, 1, 12, 0))
                        .build()
        );
        id = saved.getId();
    }

    @AfterEach
    void cleanup() {
        inventoryRepo.deleteAll();
    }

    @Test
    void testSearchFlight_Success() throws Exception {
        mockMvc.perform(get("/api/flight/search/"+id))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchFlight_NotFound() throws Exception {
        mockMvc.perform(get("/api/flight/search/INVALID"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateAvailableSeat_Success() throws Exception {
        
        Integer seatToReduce = 2;

        mockMvc.perform(put("/api/flight/update/seat/"+id)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(seatToReduce)))
        				.andExpect(status().isOk());
    }

    @Test
    void testUpdateAvailableSeat_NotFound() throws Exception {
        mockMvc.perform(put("/api/flight/update/seat/INVALID")
                        .contentType(MediaType.APPLICATION_JSON).content("2"))
        				.andExpect(status().isNotFound());
    }
}