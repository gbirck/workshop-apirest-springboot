package com.park.demo_park_api.web.controller;

import com.park.demo_park_api.entities.Client;
import com.park.demo_park_api.jwt.JwtUserDetails;
import com.park.demo_park_api.repositories.projection.ClientProjection;
import com.park.demo_park_api.services.ClientService;
import com.park.demo_park_api.services.UserService;
import com.park.demo_park_api.web.dto.*;
import com.park.demo_park_api.web.dto.mapper.ClientMapper;
import com.park.demo_park_api.web.dto.mapper.PageableMapper;
import com.park.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Clients", description = "Contains all operations related to resources for registering, editing and reading a user")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Create a new client", description = "Resource to create a new client linked to a registered user. " +
            "Request requires a bearer token. Access restricted to Role='CLIENT'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resource created successfully",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "CPF already registered in the system",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Resource not processed due to invalid input data",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Resource not permited to ADMIN profile",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> insert(@Valid @RequestBody ClientCreateDTO clientCreateDTO,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = ClientMapper.toClient(clientCreateDTO);
        client.setUser(userService.findById(userDetails.getId()));
        clientService.insert(client);
        return ResponseEntity.status(201).body(ClientMapper.toDTO(client));
    }

    @Operation(summary = "Search client by id", description = "Resource to find client by id. " +
            "Request require a bearer token. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource search completed successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Client without permission to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDTO> findById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(ClientMapper.toDTO(client));
    }

    @Operation(summary = "Search all clients",
            description = "Request require a bearer token. Access restricted to Role='ADMIN'",
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
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "name,asc")),
                            description = "Represents the ordering of results. Multiple sorting criteria are supported.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClientResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Resource not allowed for CLIENT profile ",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> findAll(@Parameter(hidden = true)
                                              @PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
        Page<ClientProjection> clients = clientService.findAll(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clients));
    }

    @Operation(summary = "Retrieve authenticated client data",
            description = "Request require a bearer token. Access restricted to Role='CLIENT'",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClientResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Feature not allowed for ADMIN profile",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> getDetails(@AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.findByUserId(userDetails.getId());
        return ResponseEntity.ok(ClientMapper.toDTO(client));
    }
}
