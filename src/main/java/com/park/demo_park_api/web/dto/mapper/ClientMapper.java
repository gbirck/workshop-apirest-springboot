package com.park.demo_park_api.web.dto.mapper;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.web.dto.ClientCreateDTO;
import com.park.demo_park_api.web.dto.ClientResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class ClientMapper {

    public static Client toClient(ClientCreateDTO dto) {
        return new ModelMapper().map(dto, Client.class);
    }

    public static ClientResponseDTO toDTO(Client client) {
        return new ModelMapper().map(client, ClientResponseDTO.class);
    }
}
