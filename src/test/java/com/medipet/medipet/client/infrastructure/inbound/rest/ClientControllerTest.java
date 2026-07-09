package com.medipet.medipet.client.infrastructure.inbound.rest;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.inbound.ClientQueryService;
import com.medipet.medipet.client.domain.port.inbound.CreateClientUseCase;
import com.medipet.medipet.client.domain.port.inbound.UpdateClientUseCase;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateClientUseCase createClientUseCase;

    @MockitoBean
    private UpdateClientUseCase updateClientUseCase;

    @MockitoBean
    private ClientQueryService clientQueryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Client createTestClient(String id) {
        return Client.withId(ClientId.from(UUID.fromString(id)),
                "Maria Garcia", "12345678", "+549115551234",
                "maria@example.com", "Av. Corrientes 1234", true);
    }

    @Test
    void shouldCreateClient() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = createTestClient(id);

        when(createClientUseCase.execute("Maria Garcia", "12345678",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234"))
                .thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Maria Garcia","dni":"12345678","phone":"+549115551234","email":"maria@example.com","address":"Av. Corrientes 1234"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/clients/")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Maria Garcia"))
                .andExpect(jsonPath("$.data.dni").value("12345678"));
    }

    @Test
    void shouldReturn409WhenDniAlreadyExists() throws Exception {
        when(createClientUseCase.execute("Maria Garcia", "12345678",
                null, null, null))
                .thenThrow(new ConflictException("Client with DNI 12345678 already exists"));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maria Garcia\",\"dni\":\"12345678\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateClient() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = Client.withId(ClientId.from(UUID.fromString(id)),
                "Maria Lopez", "12345678", "+549115559999",
                null, null, true);

        when(updateClientUseCase.execute(ClientId.from(id),
                "Maria Lopez", "+549115559999", null, null))
                .thenReturn(client);

        mockMvc.perform(put("/api/clients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maria Lopez\",\"phone\":\"+549115559999\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Maria Lopez"))
                .andExpect(jsonPath("$.data.phone").value("+549115559999"));
    }

    @Test
    void shouldReturn404WhenClientNotFoundForUpdate() throws Exception {
        String id = UUID.randomUUID().toString();
        when(updateClientUseCase.execute(ClientId.from(id),
                "Maria Lopez", null, null, null))
                .thenThrow(new NotFoundException("Client", id));

        mockMvc.perform(put("/api/clients/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maria Lopez\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetClientById() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = createTestClient(id);

        when(clientQueryService.findById(ClientId.from(id))).thenReturn(client);

        mockMvc.perform(get("/api/clients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("maria@example.com"));
    }

    @Test
    void shouldReturn404WhenClientNotFoundById() throws Exception {
        String id = UUID.randomUUID().toString();
        when(clientQueryService.findById(ClientId.from(id)))
                .thenThrow(new NotFoundException("Client", id));

        mockMvc.perform(get("/api/clients/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetClientByDni() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = createTestClient(id);

        when(clientQueryService.findByDni("12345678")).thenReturn(client);

        mockMvc.perform(get("/api/clients/dni/{dni}", "12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.dni").value("12345678"));
    }

    @Test
    void shouldReturn404WhenClientNotFoundByDni() throws Exception {
        when(clientQueryService.findByDni("00000000"))
                .thenThrow(new NotFoundException("Client not found with DNI: 00000000"));

        mockMvc.perform(get("/api/clients/dni/{dni}", "00000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListClients() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = createTestClient(id);
        PageResponse<Client> page = new PageResponse<>(List.of(client), 0, 20, 1, 1);

        when(clientQueryService.findAll(eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/clients").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Maria Garcia"));
    }

    @Test
    void shouldSearchClientsByName() throws Exception {
        String id = UUID.randomUUID().toString();
        Client client = createTestClient(id);
        PageResponse<Client> page = new PageResponse<>(List.of(client), 0, 10, 1, 1);

        when(clientQueryService.findAll(eq("Maria"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/clients").param("q", "Maria").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Maria Garcia"));
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"dni\":\"12345678\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankDni() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maria Garcia\",\"dni\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
