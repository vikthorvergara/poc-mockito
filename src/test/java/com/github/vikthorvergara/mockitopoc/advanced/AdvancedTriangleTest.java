package com.github.vikthorvergara.mockitopoc.advanced;

import com.github.vikthorvergara.mockitopoc.model.User;
import com.github.vikthorvergara.mockitopoc.repository.UserRepository;
import com.github.vikthorvergara.mockitopoc.service.UserService;
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
 * LEVEL 3: ADVANCED TRIANGLE MASTERY
 *
 * This test class demonstrates advandced integration of the Mockito Triangle:
 *
 * PILLAR 1: MOCK CREATION - Spies, Static Mocking, Partial Mocks
 * PILLAR 2: STUBBING - Dynamic Responses, Custom Matchers, thenAnswer()
 * PILLAR 3: VERIFICATION - Complex Patterns, Timeout, Custom Matchers
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Level 3: Advanced Mockito Triangle Mastery")
class AdvancedTriangleTest {

    @Mock
    private UserRepository userRepository;

    // =====================================================
    // PILLAR 1: ADVANCED MOCK CREATION - SPIES
    // =====================================================

    @Test
    @DisplayName("Spy - Partial Mocking with Real Object Behavior")
    void demonstrateSpyPartialMocking() {
        // PILLAR 1: MOCK CREATION - Create a spy of the real UserService
        // Spies allow calling real methods while stubing specific behaviors
        UserService realService = new UserService(userRepository);
        UserService spyService = spy(realService);

        // Setup test data
        Long userId = 1L;
        User user = new User(userId, "Walter White", "heisenberg@graymatter.com", LocalDateTime.now());

        // PILLAR 2: STUBBING - Stub the repository dependency
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Execute - Call the REAL method on the spy
        Optional<User> result = spyService.findUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        // PILLAR 3: VERIFICATION - Verify the spy called the real method
        verify(spyService).findUserById(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Spy - Stubbing Specific Methods And Keeping Others Real")
    void demonstrateSpySelectiveStubbing() {
        // PILLAR 1: Create spy
        UserService realService = new UserService(userRepository);
        UserService spyService = spy(realService);

        // PILLAR 2: Stub ONLY the findAllUsers method, keep others real
        List<User> mockUsers = List.of(
            new User(1L, "Jesse Pinkman", "capncook@yahoo.com", LocalDateTime.now()),
            new User(2L, "Saul Goodman", "saul@bettercallsaul.com", LocalDateTime.now())
        );
        doReturn(mockUsers).when(spyService).findAllUsers();

        // Execute
        List<User> result = spyService.findAllUsers();

        // Assert - The stubbed method returns mock data
        assertEquals(2, result.size());
        assertEquals("Jesse Pinkman", result.get(0).getName());

        // PILLAR 3: Verify
        verify(spyService).findAllUsers();
    }

    // =====================================================
    // PILLAR 1: ADVANCED MOCK CREATION - STATIC MOCKING
    // =====================================================

    // WIP
}
