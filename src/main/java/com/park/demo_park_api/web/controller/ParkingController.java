package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.services.ParkingService;
import com.park.demo_park_api.web.dto.ParkingCreateDTO;
import com.park.demo_park_api.web.dto.ParkingResponseDTO;
import com.park.demo_park_api.web.dto.mapper.ClientSpotMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {
    private final ParkingService parkingService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkIn(@RequestBody @Valid ParkingCreateDTO parkingCreateDTO) {
        ClientSpot clientSpot = ClientSpotMapper.toClientSpot(parkingCreateDTO);
        parkingService.checkIn(clientSpot);
        ParkingResponseDTO parkingResponseDTO = ClientSpotMapper.toDTO(clientSpot);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(clientSpot.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(parkingResponseDTO);
    }
}
