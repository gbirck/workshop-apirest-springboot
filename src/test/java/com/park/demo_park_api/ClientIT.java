package com.park.demo_park_api;

import com.park.demo_park_api.web.dto.ClientCreateDTO;
import com.park.demo_park_api.web.dto.ClientResponseDTO;
import com.park.demo_park_api.web.dto.PageableDTO;
import com.park.demo_park_api.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clients/clients-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClientIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createClient_WithValidData_ReturnClientWithStatus201() {
        ClientResponseDTO responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDTO("Tobias Ferreira", "91191064085"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Tobias Ferreira");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("91191064085");
    }

    @Test
    public void createClient_WithCpfAlreadyRegistered_ReturnErrorMessageWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDTO("Tobias Ferreira", "55352517047"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClient_WithInvalidData_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDTO("Bobb", "00000000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClientCreateDTO("Bobb", "911.910.640-85"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void findClient_WithIdExistentByAdmin_ReturnClientWithStatus200() {
        ClientResponseDTO responseBody = testClient
                .get()
                .uri("/api/v1/clients/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findClient_WithIdNotExistentByAdmin_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findClient_WithIdExistentByClient_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClient_WithAdminPagination_ReturnClientsWithStatus200() {
        PageableDTO responseBody = testClient
                .get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = testClient
                .get()
                .uri("/api/v1/clients?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void findClient_WithClientPagination_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findClient_WithClientTokenData_ReturnClientWithStatus200() {
        ClientResponseDTO responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("79074426050");
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Bianca Silva");
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void findClient_WithAdminTokenData_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }
}
