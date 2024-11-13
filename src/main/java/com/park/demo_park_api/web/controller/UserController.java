package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.User;
import com.park.demo_park_api.services.UserService;
import com.park.demo_park_api.web.dto.UserCreateDTO;
import com.park.demo_park_api.web.dto.UserResponseDTO;
import com.park.demo_park_api.web.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> insert(@RequestBody UserCreateDTO userDTO) {
        User userTest = userService.insert(UserMapper.toUser(userDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(userTest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User userTest = userService.findById(id);
        return ResponseEntity.ok(userTest);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody UserCreateDTO userDTO) {
        User userTest = userService.updatePassword(id, userDTO.getPassword());
        return ResponseEntity.ok(userTest);
    }

}
