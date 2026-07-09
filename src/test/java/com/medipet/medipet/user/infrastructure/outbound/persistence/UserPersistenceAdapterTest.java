package com.medipet.medipet.user.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class UserPersistenceAdapterTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.create("test@example.com", "hashed_password",
                "John", "Doe", Role.VETERINARIO);
    }

    @Test
    void shouldSaveAndFindById() {
        User saved = userRepository.save(testUser);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("John", found.get().getFirstName());
        assertEquals(Role.VETERINARIO, found.get().getRole());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldFindByEmail() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        userRepository.save(testUser);

        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void shouldFindAllPaginated() {
        for (int i = 0; i < 5; i++) {
            userRepository.save(User.create("user" + i + "@example.com", "pass",
                    "User", "Number" + i, Role.RECEPCIONISTA));
        }

        Page<User> page = userRepository.findAll(PageRequest.of(0, 3));

        assertEquals(3, page.getContent().size());
        assertEquals(6, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void shouldDeleteById() {
        User saved = userRepository.save(testUser);
        UserId id = saved.getId();

        userRepository.deleteById(id);

        assertTrue(userRepository.findById(id).isEmpty());
    }

    @Test
    void shouldUpdateExistingUser() {
        User saved = userRepository.save(testUser);

        saved.setFirstName("Jane");
        saved.setPhone("123456789");
        userRepository.save(saved);

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
        assertEquals("123456789", found.get().getPhone());
    }
}
