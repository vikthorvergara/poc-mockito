package com.github.vikthorvergara.mockitopoc.fundamentals;

import com.github.vikthorvergara.mockitopoc.model.User;
import com.github.vikthorvergara.mockitopoc.repository.UserRepository;
import com.github.vikthorvergara.mockitopoc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Demonstrates Mockito Triangle
 * 1. Mock Creation: how to create mock objects
 * 2. Stubbing: how to define mock behavior
 * 3. Verification: how to verify mock interactions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito Triangle Fundamentals")
class MockitoTriangleTest {

    // PILLAR 1: MOCK CREATION
    // Using @Mock annotation the most common approach
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialize the service with our mock
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Triangle Demo: Create → Stub → Verify - Find User Success")
    void demonstrateCompleteTriangle_FindUserSuccess() {
        // Given - Test data setup
        Long userId = 1L;
        String userName = "Vikthor Vergara";
        String userEmail = "vikthorvergara@gmail.com";
        User expectedUser = new User(userId, userName, userEmail, LocalDateTime.now());

        // PILLAR 2: STUBBING
        // Define what the mock should return when called
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // When - Execute the method under test
        Optional<User> result = userService.findUserById(userId);

        // Then - Assert the results
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        assertEquals(userName, result.get().getName());
        assertEquals(userEmail, result.get().getEmail());

        // PILLAR 3: VERIFICATION
        // Verify the mock was called with correct arguments
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Triangle Demo: Create → Stub → Verify - User Not Found")
    void demonstrateCompleteTriangle_UserNotFound() {
        // Given
        Long nonExistentUserId = 999L;

        // PILLAR 2: STUBBING
        // Stub the repository to return empty Optional
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findUserById(nonExistentUserId);

        // Then
        assertTrue(result.isEmpty());

        // PILLAR 3: VERIFICATION
        // Verify the repository was called
        verify(userRepository).findById(nonExistentUserId);
    }

    @Test
    @DisplayName("Mock Creation: Programmatic mock() creation")
    void demonstrateProgrammaticMockCreation() {
        // PILLAR 1: MOCK CREATION
        // Alternative way to create mocks - programmatically
        UserRepository programmaticMock = mock(UserRepository.class);
        UserService serviceWithProgrammaticMock = new UserService(programmaticMock);

        // Given
        Long userId = 42L;
        User user = new User(userId, "Jane Smith", "jane@example.com", LocalDateTime.now());

        // PILLAR 2: STUBBING
        when(programmaticMock.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = serviceWithProgrammaticMock.findUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Jane Smith", result.get().getName());

        // PILLAR 3: VERIFICATION
        verify(programmaticMock).findById(userId);
    }

    @Test
    @DisplayName("Stubbing: Multiple return values with thenReturn()")
    void demonstrateBasicStubbing_MultipleReturns() {
        // Given
        User user1 = new User(1L, "Alice", "alice@example.com", LocalDateTime.now());
        User user2 = new User(2L, "Bob", "bob@example.com", LocalDateTime.now());
        List<User> allUsers = List.of(user1, user2);

        // PILLAR 2: STUBBING
        // Stub different methods with different return types
        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // When & Then
        List<User> resultList = userService.findAllUsers();
        assertEquals(2, resultList.size());
        assertEquals("Alice", resultList.get(0).getName());

        Optional<User> resultAlice = userService.findUserById(1L);
        assertTrue(resultAlice.isPresent());
        assertEquals("Alice", resultAlice.get().getName());

        Optional<User> resultBob = userService.findUserById(2L);
        assertTrue(resultBob.isPresent());
        assertEquals("Bob", resultBob.get().getName());

        // PILLAR 3: VERIFICATION
        verify(userRepository).findAll();
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
    }

    @Test
    @DisplayName("Stubbing: Exception scenarios with thenThrow()")
    void demonstrateStubbing_Exceptions() {
        // Given
        Long validId = 999L;

        // PILLAR 2: STUBBING
        // Stub to throw an exception when repository is called
        when(userRepository.findById(validId))
            .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userService.findUserById(validId)
        );

        assertEquals("Database connection failed", exception.getMessage());

        // PILLAR 3: VERIFICATION
        verify(userRepository).findById(validId);
    }

    @Test
    @DisplayName("Verification: Basic method call verification")
    void demonstrateBasicVerification() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        boolean exists = userService.userExists(userId);

        // Then
        assertTrue(exists);

        // PILLAR 3: VERIFICATION
        // Verify the method was called exactly once
        verify(userRepository).existsById(userId);

        // Verify that certain methods were NOT called
        verify(userRepository, never()).findAll();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Verification: Argument-specific verification")
    void demonstrateArgumentSpecificVerification() {
        // Given
        User user = new User("Test User", "test@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail("test@example.com")).thenReturn(List.of());

        // When
        userService.createUser("Test User", "test@example.com");

        // Then - Verify exact arguments
        // PILLAR 3: VERIFICATION
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));

        // Could also verify with exact object (but any() is more flexible)
        verify(userRepository).save(argThat(u ->
            "Test User".equals(u.getName()) &&
            "test@example.com".equals(u.getEmail())
        ));
    }

    @Test
    @DisplayName("Complete Triangle: Create User Workflow")
    void demonstrateCompleteTriangle_CreateUserWorkflow() {
        // Given - Test setup
        String userName = "New User";
        String userEmail = "newuser@example.com";
        User savedUser = new User(1L, userName, userEmail, LocalDateTime.now());

        // PILLAR 2: STUBBING
        // Stub the dependencies - email check returns empty (no existing user)
        when(userRepository.findByEmail(userEmail)).thenReturn(List.of());
        // Stub the save operation
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When - Execute the business logic
        User result = userService.createUser(userName, userEmail);

        // Then - Assert the outcome
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(userName, result.getName());
        assertEquals(userEmail, result.getEmail());

        // PILLAR 3: VERIFICATION
        // Verify all the expected interactions occurred
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).save(any(User.class));

        // Verify specific argument details
        verify(userRepository).save(argThat(user ->
            userName.equals(user.getName()) &&
            userEmail.equals(user.getEmail()) &&
            user.getCreatedAt() != null
        ));
    }
}