package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.Spot;
import com.park.demo_park_api.services.SpotService;
import com.park.demo_park_api.web.dto.SpotCreateDTO;
import com.park.demo_park_api.web.dto.SpotResponseDTO;
import com.park.demo_park_api.web.dto.mapper.SpotMapper;
import com.park.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Spots", description = "Contains all operations related to a parking spot resource")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/spots")
public class SpotController {

    private final SpotService spotService;

    @Operation(summary = "Create a new spot", description = "Resource to create a new spot." +
            "Request require a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL of the created resource")),
                    @ApiResponse(responseCode = "409", description = "Spot already registered",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing or invalid data",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed in the CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpotResponseDTO> insert(@RequestBody @Valid SpotCreateDTO spotCreateDTO) {
        Spot spot = SpotMapper.toSpot(spotCreateDTO);
        spotService.insert(spot);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{code}")
                .buildAndExpand(spot.getCode()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Search a spot", description = "Resource to search a spot" +
            "Request require a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource created successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = SpotResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Spot not found",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed in the CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpotResponseDTO> findByCode(@PathVariable String code) {
        Spot spot = spotService.findByCode(code);
        return ResponseEntity.ok(SpotMapper.toDTO(spot));
    }
}
