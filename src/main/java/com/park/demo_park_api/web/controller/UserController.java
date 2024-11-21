package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.User;
import com.park.demo_park_api.services.UserService;
import com.park.demo_park_api.web.dto.UserCreateDTO;
import com.park.demo_park_api.web.dto.UserPasswordDTO;
import com.park.demo_park_api.web.dto.UserResponseDTO;
import com.park.demo_park_api.web.dto.mapper.UserMapper;
import com.park.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.PostRemove;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Contains all operations related to resources for registering, editing and reading a user")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Resource to create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Email already registered in the system",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to invalid input data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> insert(@Valid @RequestBody UserCreateDTO userDTO) {
        User userTest = userService.insert(UserMapper.toUser(userDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDTO(userTest));
    }

    @Operation(summary = "Search user by id", description = "Resource to search user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource search completed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR ( hasRole('CLIENT') AND #id == authentication.principal.id)")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        User userTest = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDTO(userTest));
    }

    @Operation(summary = "Search all users", description = "Resource to search all registered users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource search completed successfully",
                            content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))))
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(UserMapper.toListDTO(users));
    }

    @Operation(summary = "Update password", description = "Resource to update password",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "400", description = "Password does not match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT') AND #id == authentication.principal.id")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO passwordDTO) {
        userService.updatePassword(id, passwordDTO.getCurrentPassword(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

}
