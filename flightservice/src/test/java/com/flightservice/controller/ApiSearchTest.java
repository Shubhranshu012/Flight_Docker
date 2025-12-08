package com.flightservice.controller;

import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightInventoryRepository inventoryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private String id;
    private FlightInventory inventory;

    @BeforeEach
    void setup() {

        id = "123";
        inventory = FlightInventory.builder().id(id)
                .flightId("6E-101").airline("IndiGo").source(AIRPORT_NAME.MUMBAI).destination(AIRPORT_NAME.DELHI)
                .totalSeats(180).availableSeats(180).price(5000).departureTime(LocalDateTime.of(2026, 1, 1, 10, 0))
                .arrivalTime(LocalDateTime.of(2026, 1, 1, 12, 0)).build();

        Mockito.when(inventoryRepo.findById(id)).thenReturn(Optional.of(inventory));

        Mockito.when(inventoryRepo.findById("INVALID")).thenReturn(Optional.empty());

        Mockito.when(inventoryRepo.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testSearchFlight_Success() throws Exception {
        mockMvc.perform(get("/api/flight/search/" + id))
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

        mockMvc.perform(put("/api/flight/update/seat/" + id)
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
