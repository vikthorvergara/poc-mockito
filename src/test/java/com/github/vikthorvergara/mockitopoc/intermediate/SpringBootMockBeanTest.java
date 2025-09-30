package com.github.vikthorvergara.mockitopoc.intermediate;

import com.github.vikthorvergara.mockitopoc.model.User;
import com.github.vikthorvergara.mockitopoc.repository.UserRepository;
import com.github.vikthorvergara.mockitopoc.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SPRING BOOT INTEGRATION WITH @MockBean
 *
 * This test class demonstrates Spring Boot integration testing patterns:
 *
 * PILLAR 1: SPRING BOOT MOCK CREATION
 * - @MockBean replaces Spring beans in application context
 * - @Autowired injects the real service with mocked dependencies
 * - Full Spring Boot context for integration testing
 *
 * Key Differences from @Mock:
 * - @MockBean: Creates mock and adds it to Spring application context
 * - @Mock: Creates mock without Spring context involvement
 * - @MockBean is ideal for integration tests with Spring Boot
 * - @Mock is ideal for pure unit tests without Spring overhead
 */
@SpringBootTest
@DisplayName("Spring Boot @MockBean Integration Patterns")
class SpringBootMockBeanTest {

    // PILLAR 1: SPRING BOOT MOCK CREATION
    // @MockBean creates a mock AND registers it in the Spring application context
    // This mock replaces any existing UserRepository bean
    @MockBean
    private UserRepository userRepository;

    // @Autowired injects the REAL UserService from Spring context
    // The UserService will have the @MockBean userRepository injected into it
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("@MockBean: Spring Context Integration with Mocked Repository")
    void testMockBeanIntegration() {
        // Given - Test user
        User testUser = new User(1L, "Toby Flenderson", "toby@dundermifflin.com", LocalDateTime.now());

        // PILLAR 2: STUBBING - Mock the repository bean
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When - Call the real service (which uses the mocked repository)
        Optional<User> result = userService.findUserById(1L);

        // Then - Verify the result
        assertTrue(result.isPresent());
        assertEquals("Toby Flenderson", result.get().getName());

        // PILLAR 3: VERIFICATION
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("@MockBean: Complete Spring Boot Triangle with Real Service Logic")
    void testCompleteSpringBootTriangle() {
        // Given - Test data
        User user1 = new User(1L, "Oscar Martinez", "oscar@dundermifflin.com", LocalDateTime.now());
        User user2 = new User(2L, "Phyllis Vance", "phyllis@dundermifflin.com", LocalDateTime.now());

        // PILLAR 2: STUBBING - Mock repository behavior
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // When - Execute through real Spring Boot service
        List<User> allUsers = userService.findAllUsers();
        Optional<User> specificUser = userService.findUserById(1L);

        // Then - Verify results from real service logic
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertTrue(specificUser.isPresent());
        assertEquals("Oscar Martinez", specificUser.get().getName());

        // PILLAR 3: VERIFICATION - Verify Spring bean interactions
        verify(userRepository).findAll();
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("@MockBean: Testing Real Service Validation Logic")
    void testRealServiceValidationWithMockBean() {
        // This test shows how @MockBean allows testing REAL service logic
        // while mocking only the dependencies

        // PILLAR 2: STUBBING - Mock only what's needed
        when(userRepository.findByEmail("creed@dundermifflin.com"))
                .thenReturn(Arrays.asList(new User(99L, "Creed Bratton", "creed@dundermifflin.com", LocalDateTime.now())));

        // When/Then - The REAL service validation logic runs
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Creed Bratton", "creed@dundermifflin.com"));

        assertEquals("User with email already exists: creed@dundermifflin.com", exception.getMessage());

        // PILLAR 3: VERIFICATION
        verify(userRepository).findByEmail("creed@dundermifflin.com");
        verify(userRepository, never()).save(any(User.class)); // Save was never called due to validation
    }

    @Test
    @DisplayName("@MockBean vs @Mock: When to Use Which")
    void demonstrateMockBeanUseCase() {
        // USE @MockBean when:
        // 1. You need Spring Boot application context
        // 2. Testing integration between Spring components
        // 3. You want real service logic with mocked dependencies
        // 4. Using Spring-specific features (transactions, security, etc.)

        // USE @Mock when:
        // 1. Pure unit testing without Spring overhead
        // 2. Faster test execution (no context loading)
        // 3. Testing simple logic without Spring features
        // 4. No need for dependency injection framework

        // Given - This test uses Spring Boot context
        when(userRepository.existsById(1L)).thenReturn(true);

        // When - Real Spring service is used
        boolean exists = userService.userExists(1L);

        // Then
        assertTrue(exists);
        verify(userRepository).existsById(1L);
    }
}