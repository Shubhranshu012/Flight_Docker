package com.flightservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightservice.dto.InventoryRequestDto;

@RestController
public class InventoryBatchController {

    @PostMapping("/api/flight/airline/inventory/batch")
    public ResponseEntity<?> addingInventory(@RequestParam("file") MultipartFile file) {
        try {
            String inputFile = new String(file.getBytes());

            ObjectMapper mapper = new ObjectMapper();

            List<InventoryRequestDto> flights = mapper.readValue(inputFile,new TypeReference<List<InventoryRequestDto>>() {});

            flights.forEach(f -> System.out.println(f));

            return ResponseEntity.ok("Uploaded " + flights.size() + " items.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading: " + e.getMessage());
        }
    }
}

