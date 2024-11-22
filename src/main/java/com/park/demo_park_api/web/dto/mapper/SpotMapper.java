package com.park.demo_park_api.web.dto.mapper;

import com.park.demo_park_api.entities.Spot;
import com.park.demo_park_api.web.dto.SpotCreateDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotMapper {

    public static Spot toSpot(SpotCreateDTO spotCreateDTO) {
        return new ModelMapper().map(spotCreateDTO, Spot.class);
    }

    public static SpotCreateDTO toDTO(Spot spot) {
        return new ModelMapper().map(spot, SpotCreateDTO.class);
    }
}
