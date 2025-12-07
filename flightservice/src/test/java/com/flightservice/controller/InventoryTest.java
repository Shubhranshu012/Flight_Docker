package com.flightservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.flightservice.repository.FlightRepository;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightInventoryRepository inventoryRepo;

    @MockBean
    private FlightRepository flightRepo;   

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

    @BeforeEach
    void setup() {

        Mockito.when(inventoryRepo.findByAirlineAndFlightIdAndSourceAndDestinationAndDepartureTime(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())
        ).thenReturn(Optional.empty());

        Mockito.when(flightRepo.findById(Mockito.any())).thenReturn(Optional.empty());

        Mockito.when(flightRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        Mockito.when(inventoryRepo.save(Mockito.any()))
                .thenAnswer(i -> {
                    FlightInventory inv = i.getArgument(0);
                    inv.setId("MOCK_ID_123");
                    return inv;
                });
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
