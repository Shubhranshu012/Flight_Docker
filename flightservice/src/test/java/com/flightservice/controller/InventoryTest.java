package com.flightservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightservice.dto.InventoryRequestDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryRequestDto buildValidDto() {
        InventoryRequestDto inventoryDto = new InventoryRequestDto();
        inventoryDto.setAirlineName("IndiGo");
        inventoryDto.setFromPlace("DELHI");
        inventoryDto.setToPlace("MUMBAI");
        inventoryDto.setFlightNumber("6E-517");
        inventoryDto.setDepartureTime(LocalDateTime.now().plusDays(1));
        inventoryDto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        inventoryDto.setPrice(4500);
        inventoryDto.setTotalSeats(180);
        inventoryDto.setAvailableSeats(180);
        return inventoryDto;
    }

    @Test
    void addInventory_success() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isCreated());
    }
    @Test
    void addInventory_validationError_timeDeparture() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setDepartureTime(null);

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_validationError_timeArival() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setArrivalTime(null);

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_validationError_timeMisMatch() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setDepartureTime(LocalDateTime.now().plusDays(2));
        inventoryDto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_samePlace() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setFromPlace("DELHI");
        inventoryDto.setToPlace("DELHI");

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_timeError() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setDepartureTime(LocalDateTime.now().plusDays(2));
        inventoryDto.setArrivalTime(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_validationError_availableSeatsGreaterThanTotal() throws Exception {
        InventoryRequestDto inventoryDto = buildValidDto();
        inventoryDto.setAvailableSeats(300);  

        mockMvc.perform(post("/api/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(inventoryDto)))
                .andExpect(status().isBadRequest());
    }
}

