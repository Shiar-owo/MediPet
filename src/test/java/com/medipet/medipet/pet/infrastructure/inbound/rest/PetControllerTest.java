package com.medipet.medipet.pet.infrastructure.inbound.rest;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.inbound.CreatePetUseCase;
import com.medipet.medipet.pet.domain.port.inbound.PetQueryService;
import com.medipet.medipet.pet.domain.port.inbound.UpdatePetUseCase;
import com.medipet.medipet.pet.domain.valueobject.PetId;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@AutoConfigureMockMvc(addFilters = false)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePetUseCase createPetUseCase;

    @MockitoBean
    private UpdatePetUseCase updatePetUseCase;

    @MockitoBean
    private PetQueryService petQueryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Pet createTestPet(String id, String clientId) {
        return Pet.withId(PetId.from(UUID.fromString(id)),
                "Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, true, ClientId.from(UUID.fromString(clientId)));
    }

    @Test
    void shouldCreatePet() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);

        when(createPetUseCase.execute("Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, ClientId.from(clientId)))
                .thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Max",
                                    "clientId": "%s",
                                    "species": "Dog",
                                    "breed": "Golden Retriever",
                                    "dateOfBirth": "2020-05-15",
                                    "sex": "MALE",
                                    "color": "Golden",
                                    "weight": "30.5",
                                    "microchipId": "MC12345",
                                    "tattooNumber": "TAT001"
                                }
                                """.formatted(clientId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/pets/")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Max"))
                .andExpect(jsonPath("$.data.species").value("Dog"));
    }

    @Test
    void shouldReturn409WhenMicrochipExists() throws Exception {
        String clientId = UUID.randomUUID().toString();
        when(createPetUseCase.execute("Max", "Dog", null, null, null, null, null,
                "MC12345", null, null, ClientId.from(clientId)))
                .thenThrow(new ConflictException("Pet with microchip MC12345 already exists"));

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Max","clientId":"%s","species":"Dog","microchipId":"MC12345"}
                                """.formatted(clientId)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdatePet() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = Pet.withId(PetId.from(UUID.fromString(petId)),
                "Maxi", "Dog", "Labrador", LocalDate.of(2021, 1, 1),
                "MALE", "Black", BigDecimal.valueOf(25.0),
                "MC12345", "TAT001", null, true, ClientId.from(UUID.fromString(clientId)));

        when(updatePetUseCase.execute(PetId.from(petId),
                "Maxi", null, "Labrador", LocalDate.of(2021, 1, 1),
                null, "Black", new BigDecimal("25.0"),
                null, null, null))
                .thenReturn(pet);

        mockMvc.perform(put("/api/pets/{id}", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Maxi",
                                    "breed": "Labrador",
                                    "dateOfBirth": "2021-01-01",
                                    "color": "Black",
                                    "weight": "25.0"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Maxi"))
                .andExpect(jsonPath("$.data.breed").value("Labrador"));
    }

    @Test
    void shouldReturn404WhenPetNotFoundForUpdate() throws Exception {
        String petId = UUID.randomUUID().toString();
        when(updatePetUseCase.execute(PetId.from(petId),
                "Maxi", null, null, null, null, null, null, null, null, null))
                .thenThrow(new NotFoundException("Pet", petId));

        mockMvc.perform(put("/api/pets/{id}", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maxi\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPetById() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);

        when(petQueryService.findById(PetId.from(petId))).thenReturn(pet);

        mockMvc.perform(get("/api/pets/{id}", petId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Max"))
                .andExpect(jsonPath("$.data.species").value("Dog"));
    }

    @Test
    void shouldReturn404WhenPetNotFoundById() throws Exception {
        String petId = UUID.randomUUID().toString();
        when(petQueryService.findById(PetId.from(petId)))
                .thenThrow(new NotFoundException("Pet", petId));

        mockMvc.perform(get("/api/pets/{id}", petId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPetByMicrochip() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);

        when(petQueryService.findByMicrochipId("MC12345")).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/api/pets/microchip/{microchipId}", "MC12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.microchipId").value("MC12345"));
    }

    @Test
    void shouldReturn404WhenPetNotFoundByMicrochip() throws Exception {
        when(petQueryService.findByMicrochipId("NONEXISTENT"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pets/microchip/{microchipId}", "NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetPetsByClientId() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);
        PageResponse<Pet> page = new PageResponse<>(List.of(pet), 0, 20, 1, 1);

        when(petQueryService.findByClientId(ClientId.from(clientId), PageRequest.of(0, 20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/pets/client/{clientId}", clientId)
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Max"));
    }

    @Test
    void shouldListPets() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);
        PageResponse<Pet> page = new PageResponse<>(List.of(pet), 0, 20, 1, 1);

        when(petQueryService.findAll(eq(null), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Max"));
    }

    @Test
    void shouldSearchPetsByName() throws Exception {
        String petId = UUID.randomUUID().toString();
        String clientId = UUID.randomUUID().toString();
        Pet pet = createTestPet(petId, clientId);
        PageResponse<Pet> page = new PageResponse<>(List.of(pet), 0, 10, 1, 1);

        when(petQueryService.findAll(eq("Max"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets").param("q", "Max").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Max"));
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"clientId\":\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\",\"species\":\"Dog\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankSpecies() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Max\",\"clientId\":\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\",\"species\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithNullClientId() throws Exception {
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Max\",\"species\":\"Dog\"}"))
                .andExpect(status().isBadRequest());
    }
}
