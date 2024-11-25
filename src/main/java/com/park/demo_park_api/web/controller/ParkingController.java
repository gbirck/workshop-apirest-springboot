package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.ClientSpot;
import com.park.demo_park_api.jwt.JwtUserDetails;
import com.park.demo_park_api.repositories.projection.ClientSpotProjection;
import com.park.demo_park_api.services.ClientSpotService;
import com.park.demo_park_api.services.ParkingService;
import com.park.demo_park_api.web.dto.PageableDTO;
import com.park.demo_park_api.web.dto.ParkingCreateDTO;
import com.park.demo_park_api.web.dto.ParkingResponseDTO;
import com.park.demo_park_api.web.dto.mapper.ClientSpotMapper;
import com.park.demo_park_api.web.dto.mapper.PageableMapper;
import com.park.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Parking", description = "Operations for registering a vehicle in and out of the parking lot.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {
    private final ParkingService parkingService;
    private final ClientSpotService clientSpotService;

    @Operation(summary = "Check-in operation", description = "Resource for entering a vehicle into the parking lot. " +
            "Request requires use of a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL to access the created resource"),
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Possible causes: <br/>" +
                            "- Client CPF not registered in the system; <br/>" +
                            "- No free spots were found;",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to missing or invalid data",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "I don't allow the feature to the CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkIn(@RequestBody @Valid ParkingCreateDTO parkingCreateDTO) {
        ClientSpot clientSpot = ClientSpotMapper.toClientSpot(parkingCreateDTO);
        parkingService.checkIn(clientSpot);
        ParkingResponseDTO ParkingResponseDTO = ClientSpotMapper.toDTO(clientSpot);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(clientSpot.getReceipt())
                .toUri();
        return ResponseEntity.created(location).body(ParkingResponseDTO);
    }

    @Operation(summary = "Locate a parked vehicle", description = "Feature for returning a parked vehicle " +
            "by receipt number. Request requires use of a bearer token.",
            security = @SecurityRequirement(name = "Security"),
            parameters = {
                    @Parameter(in = PATH, name = "receipt", description = "Receipt number generated by check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Receipt number not found.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/check-in/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingResponseDTO> findByReceipt(@PathVariable String receipt) {
        ClientSpot clientSpot = clientSpotService.findByReceipt(receipt);
        ParkingResponseDTO dto = ClientSpotMapper.toDTO(clientSpot);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Check-out operation", description = "Resource for leaving a vehicle from the parking lot. " +
            "Request requires use of a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "Security"),
            parameters = {@Parameter(in = PATH, name = "receipt", description = "Receipt number generated by check-in",
                    required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource updated successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Nonexistent receipt number or " +
                            "the vehicle has already been checked out.",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Feature not allowed on CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingResponseDTO> checkOut(@PathVariable String receipt) {
        ClientSpot clientSpot = parkingService.checkOut(receipt);
        ParkingResponseDTO dto = ClientSpotMapper.toDTO(clientSpot);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Find client parking records by CPF", description = "Find the " +
            "client parking records by CPF. Request requires use of a bearer token.",
            security = @SecurityRequirement(name = "Security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "CPF number referring to the client to be consulted",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page", description = "Represents the returned page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0"))
                    ),
                    @Parameter(in = QUERY, name = "size", description = "Represents the total number of elements per page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5"))
                    ),
                    @Parameter(in = QUERY, name = "sort", description = "Default ordering field 'entryDate,asc'. ",
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            hidden = true
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Feature not allowed on CLIENT profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> findAllByClientCpf(@PathVariable String cpf, @Parameter(hidden = true)
    @PageableDefault(size = 5, sort = "entryDate",
            direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ClientSpotProjection> projection = clientSpotService.findAllByClientCpf(cpf, pageable);
        PageableDTO dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Find logged in client parking records",
            description = "Find logged in client parking records. " +
                    "Request requires use of a bearer token.",
            security = @SecurityRequirement(name = "Security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Represents the returned page"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Represents the total number of elements per page"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "entryDate,asc")),
                            description = "Default ordering field 'entryDate,asc'.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource located successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Feature not allowed for ADMIN profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageableDTO> findAllClientParkingByUserId(@AuthenticationPrincipal JwtUserDetails user,
                                                                    @Parameter(hidden = true) @PageableDefault(
                                                                            size = 5, sort = "entryDate",
                                                                            direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClientSpotProjection> projection = clientSpotService.findAllClientParkingByUserId(user.getId(), pageable);
        PageableDTO dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }
}
