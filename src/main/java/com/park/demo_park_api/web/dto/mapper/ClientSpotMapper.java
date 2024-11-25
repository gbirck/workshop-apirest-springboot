package com.park.demo_park_api.web.dto.mapper;

import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.web.dto.ParkingCreateDTO;
import com.park.demo_park_api.web.dto.ParkingResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientSpotMapper {

    public static ClientSpot toClientSpot(ParkingCreateDTO parkingCreateDTO) {
        return new ModelMapper().map(parkingCreateDTO, ClientSpot.class);
    }

    public static ParkingResponseDTO toParkingCreateDTO(ClientSpot clientSpot) {
        return new ModelMapper().map(clientSpot, ParkingResponseDTO.class);
    }
}
