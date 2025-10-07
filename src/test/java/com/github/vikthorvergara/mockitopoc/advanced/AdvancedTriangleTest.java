package com.github.vikthorvergara.mockitopoc.advanced;

import com.github.vikthorvergara.mockitopoc.model.User;
import com.github.vikthorvergara.mockitopoc.repository.UserRepository;
import com.github.vikthorvergara.mockitopoc.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

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

    @Test
    @DisplayName("Static Mocking - Mocking Static Methods With Try-With-Resources")
    void demonstrateStaticMocking() {
        // PILLAR 1: MOCK CREATION - Static Mocking of a utility class
        LocalDateTime backThen = LocalDateTime.of(2008, 1, 20, 9, 41);
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            // PILLAR 2: STUBBING - Stub the static method
            mockedStatic.when(LocalDateTime::now).thenReturn(backThen);

            // Execute - Create a user which uses LocalDateTime.now()
            User user = new User("Gustavo Fring", "gus@pollos.com");

            // Assert - The user's createdAt should use our fixed time
            assertEquals(backThen, user.getCreatedAt());

            // PILLAR 3: VERIFICATION - Verify static method was called
            mockedStatic.verify(LocalDateTime::now);
        }
        // Static mock should be closed and cleaned by itself at this point
    }

    // =====================================================
    // PILLAR 2: ADVANCED STUBBING - thenAnswer()
    // =====================================================

    @Test
    @DisplayName("thenAnswer() - Dynamic Response Based on Input Parameters")
    void demonstrateThenAnswerDynamicResponse() {
        // PILLAR 2: STUBBING - Use thenAnswer for dynamic behavior
        // The answer changes based on the input argument
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User inputUser = invocation.getArgument(0);
            Long generatedId = 21L;
            // Simulate ID auto-generation
            inputUser.setId(generatedId);
            inputUser.setCreatedAt(LocalDateTime.now());
            return inputUser;
        });

        String userName = "Mike Ehrmantraut";
        String userEmail = "mike@investigator.com";

        // Execute
        when(userRepository.findByEmail(userEmail)).thenReturn(List.of());

        UserService userService = new UserService(userRepository);
        User savedUser = userService.createUser(userName, userEmail);

        // Assert - The ID was dynamically set by thenAnswer
        assertNotNull(savedUser.getId());
        assertEquals(999L, savedUser.getId());
        assertEquals(userName, savedUser.getName());

        // PILLAR 3: VERIFICATION
        verify(userRepository).save(argThat(user ->
            userName.equals(user.getName()) &&
            userEmail.equals(user.getEmail())
        ));
    }

    @Test
    @DisplayName("thenAnswer() - Multiple Dynamic Behaviors in Same Test")
    void demonstrateComplexThenAnswer() {
        String userName = "Hank Schrader";
        String userEmailDomain = "@dea.gov";

        // PILLAR 2: Stubbing with conditional logic
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);

            // Different behavior based on ID value
            if (id < 100) {
                return Optional.of(new User(id, userName, "hank" + id + userEmailDomain, LocalDateTime.now()));
            } else if (id == 999) {
                return Optional.empty();
            } else {
                throw new IllegalArgumentException("Invalid ID range");
            }
        });

        UserService userService = new UserService(userRepository);

        // Execute multiple scenarios
        Optional<User> user1 = userService.findUserById(1L);
        Optional<User> user50 = userService.findUserById(50L);
        Optional<User> user999 = userService.findUserById(999L);

        // Assert different behaviors
        assertTrue(user1.isPresent());
        assertEquals(userName, user1.get().getName());

        assertTrue(user50.isPresent());
        assertEquals("hank50" + userEmailDomain, user50.get().getEmail());

        assertFalse(user999.isPresent());

        // PILLAR 3: Verification
        verify(userRepository, times(3)).findById(anyLong());
    }

    // =====================================================
    // PILLAR 2: ADVANCED STUBBING - CUSTOM ARGUMENT MATCHERS
    // =====================================================

    @Test
    @DisplayName("Custom Argument Matchers - Domain-Specific Matching")
    void demonstrateCustomArgumentMatchers() {
        // Define a custom matcher for email validation
        ArgumentMatcher<User> hasValidEmail = user ->
            user.getEmail() != null && user.getEmail().contains("@") && user.getEmail().contains(".");

        // PILLAR 2: Stub with custom matcher
        when(userRepository.save(argThat(hasValidEmail)))
            .thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(100L);
                return u;
            });

        // Execute with valid email
        User validUser = new User("Skyler White", "skyler@white.com");
        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(List.of());

        UserService userService = new UserService(userRepository);
        User saved = userService.createUser(validUser.getName(), validUser.getEmail());

        // Assert
        assertEquals(100L, saved.getId());

        // PILLAR 3: Verify with custom matcher
        verify(userRepository).save(argThat(hasValidEmail));
    }

    @Test
    @DisplayName("Argument Matchers - Combining Multiple Matchers")
    void demonstrateComplexArgumentMatching() {
        User user = new User("Hector Salamanca", "hector@salamanca.com");

        // PILLAR 2: Combine eq() and any() matchers
        when(userRepository.save(any(User.class)))
                .thenReturn(new User(1L, user.getName(), user.getEmail(), LocalDateTime.now()));

        userRepository.save(user);

        // PILLAR 3: Verify with complex matcher logic
        verify(userRepository).save(argThat(u ->
            u.getName().equals("Hector Salamanca") &&
            u.getEmail().startsWith("hector@") &&
            u.getEmail().endsWith(".com")
        ));
    }

    // =====================================================
    // PILLAR 3: ADVANCED VERIFICATION - COMPLEX PATTERNS
    // =====================================================

    @Test
    @DisplayName("Complex Verification - InOrder with Multiple Mocks")
    void demonstrateComplexInOrderVerification() {
        // PILLAR 1: Create additional mocks for complex scenario
        UserRepository repo1 = mock(UserRepository.class);
        UserRepository repo2 = mock(UserRepository.class);

        // PILLAR 2: Stub both repositories
        when(repo1.findById(1L)).thenReturn(Optional.of(new User(1L, "Tuco Salamanca", "tuco@cartel.mx", LocalDateTime.now())));
        when(repo2.findById(2L)).thenReturn(Optional.of(new User(2L, "Gale Boetticher", "gale@chemist.edu", LocalDateTime.now())));

        // Execute in specific order
        repo1.findById(1L);
        repo2.findById(2L);
        repo1.findById(1L); // Call repo1 again

        // PILLAR 3: Verify complex interaction order
        var inOrder = inOrder(repo1, repo2);
        inOrder.verify(repo1).findById(1L);
        inOrder.verify(repo2).findById(2L);
        inOrder.verify(repo1).findById(1L);
    }

    @Test
    @DisplayName("Verification Modes - Advanced Patterns")
    void demonstrateAdvancedVerificationModes() {
        // PILLAR 2: Stub repository
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserService userService = new UserService(userRepository);

        // Execute multiple calls
        userService.findUserById(1L);
        userService.findUserById(2L);
        userService.findUserById(3L);

        // PILLAR 3: Advanced verification modes
        verify(userRepository, times(3)).findById(anyLong());
        verify(userRepository, atLeast(2)).findById(anyLong());
        verify(userRepository, atMost(5)).findById(anyLong());
        verify(userRepository, never()).findAll();
    }
}
