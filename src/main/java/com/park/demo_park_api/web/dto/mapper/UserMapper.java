package com.park.demo_park_api.web.dto.mapper;

import com.fasterxml.jackson.databind.PropertyName;
import com.park.demo_park_api.entities.User;
import com.park.demo_park_api.web.dto.UserCreateDTO;
import com.park.demo_park_api.web.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.swing.*;

public class UserMapper {

    public static User toUser(UserCreateDTO userdto) {
        return new ModelMapper().map(userdto, User.class);
    }

    public static UserResponseDTO toDTO(User user) {
        String role = user.getRole().name().substring("ROLE_".length());
        PropertyMap<User, UserResponseDTO> props = new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(user, UserResponseDTO.class);
    }

}
