package com.github.vikthorvergara.mockitopoc.intermediate;

import com.github.vikthorvergara.mockitopoc.model.User;
import com.github.vikthorvergara.mockitopoc.repository.UserRepository;
import com.github.vikthorvergara.mockitopoc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * INTERMEDIATE MOCKITO TRIANGLE PATTERNS
 *
 * This test class demonstrates intermediate-level Mockito patterns expanding on fundamentals:
 *
 * PILLAR 1: ADVANCED MOCK CREATION
 * - @InjectMocks for automatic dependency injection
 * - Multiple mocks working together
 * - Constructor injection patterns
 *
 * PILLAR 2: ADVANCED STUBBING
 * - Consecutive calls with thenReturn chaining
 * - Exception throwing patterns
 * - Void method stubbing
 * - Conditional stubbing with different arguments
 *
 * PILLAR 3: ADVANCED VERIFICATION
 * - Verification modes: times(), never(), atLeast(), atMost()
 * - ArgumentCaptor for complex verification
 * - Method call ordering verification
 * - Verification with argument matchers
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Intermediate Mockito Triangle Patterns")
class IntermediateTriangleTest {

    // PILLAR 1: ADVANCED MOCK CREATION
    // @Mock creates the mock dependency
    @Mock
    private UserRepository userRepository;

    // @InjectMocks creates instance and injects mocks automatically
    // This simulates constructor injection used in production code
    @InjectMocks
    private UserService userService;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new User(1L, "Michael Scott", "michael@dundermifflin.com", LocalDateTime.now());
        testUser2 = new User(2L, "Jim Halpert", "jim@dundermifflin.com", LocalDateTime.now());
        testUser3 = new User(3L, "Pam Beesly", "pam@dundermifflin.com", LocalDateTime.now());
    }

    // =========================================================================
    // ADVANCED STUBBING PATTERNS
    // =========================================================================

    @Test
    @DisplayName("Advanced Stubbing: Consecutive Calls with Different Return Values")
    void testConsecutiveCalls_DifferentReturns() {
        // PILLAR 2: STUBBING - Consecutive calls return different values
        // First call returns testUser1, second call returns testUser2, third call returns testUser3
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser1))
                .thenReturn(Optional.of(testUser2))
                .thenReturn(Optional.of(testUser3));

        // When - Execute multiple calls
        Optional<User> firstCall = userService.findUserById(1L);
        Optional<User> secondCall = userService.findUserById(1L);
        Optional<User> thirdCall = userService.findUserById(1L);

        // Then - Verify each call returns different user
        assertTrue(firstCall.isPresent());
        assertEquals("Michael Scott", firstCall.get().getName());

        assertTrue(secondCall.isPresent());
        assertEquals("Jim Halpert", secondCall.get().getName());

        assertTrue(thirdCall.isPresent());
        assertEquals("Pam Beesly", thirdCall.get().getName());

        // PILLAR 3: VERIFICATION - Verify method was called exactly 3 times
        verify(userRepository, times(3)).findById(1L);
    }

    @Test
    @DisplayName("Advanced Stubbing: Exception Throwing Patterns")
    void testExceptionThrowingPatterns() {
        // PILLAR 2: STUBBING - Different IDs trigger different exceptions
        when(userRepository.findById(999L))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When/Then - Verify exceptions are thrown
        RuntimeException dbException = assertThrows(RuntimeException.class,
                () -> userRepository.findById(999L));
        assertEquals("Database connection failed", dbException.getMessage());

        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class,
                () -> userService.findUserById(null));
        assertEquals("ID cannot be null", nullException.getMessage());

        // PILLAR 3: VERIFICATION - Verify methods were called
        verify(userRepository).findById(999L);
        verify(userRepository, never()).findById(1L); // Verify this was never called
    }

    @Test
    @DisplayName("Advanced Stubbing: Void Method Stubbing with doThrow")
    void testVoidMethodStubbing_Exception() {
        // PILLAR 2: STUBBING - Void methods use doThrow().when() syntax
        doThrow(new RuntimeException("Delete operation failed"))
                .when(userRepository).deleteById(999L);

        // When/Then - Verify exception is thrown
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userRepository.deleteById(999L));
        assertEquals("Delete operation failed", exception.getMessage());

        // PILLAR 3: VERIFICATION - Verify deleteById was called
        verify(userRepository).deleteById(999L);
    }

    @Test
    @DisplayName("Advanced Stubbing: Void Method with doNothing")
    void testVoidMethodStubbing_DoNothing() {
        // PILLAR 2: STUBBING - Void methods that should do nothing
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When - Execute delete operation
        userService.deleteUser(1L);

        // Then - No exception thrown
        // PILLAR 3: VERIFICATION - Verify both methods were called
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Advanced Stubbing: Conditional Stubbing with Argument Matchers")
    void testConditionalStubbing_ArgumentMatchers() {
        // PILLAR 2: STUBBING - Different behavior based on arguments
        // any() matches any Long value
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        // Specific value takes precedence over any()
        when(userRepository.findById(eq(1L)))
                .thenReturn(Optional.of(testUser1));

        // When - Call with different arguments
        Optional<User> specificResult = userService.findUserById(1L);
        Optional<User> anyResult = userService.findUserById(999L);

        // Then - Verify conditional behavior
        assertTrue(specificResult.isPresent());
        assertEquals("Michael Scott", specificResult.get().getName());

        assertFalse(anyResult.isPresent());

        // PILLAR 3: VERIFICATION - Verify with argument matchers
        verify(userRepository, times(2)).findById(any(Long.class));
        verify(userRepository).findById(eq(1L));
    }

    // =========================================================================
    // ADVANCED VERIFICATION PATTERNS
    // =========================================================================

    @Test
    @DisplayName("Advanced Verification: Verification Modes (times, never, atLeast, atMost)")
    void testVerificationModes() {
        // Given - Setup test data
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        // When - Execute multiple calls
        userService.findUserById(1L);
        userService.findUserById(1L);
        userService.findUserById(1L);

        // PILLAR 3: VERIFICATION - Different verification modes
        verify(userRepository, times(3)).findById(1L);        // Exactly 3 times
        verify(userRepository, atLeast(2)).findById(1L);      // At least 2 times
        verify(userRepository, atMost(5)).findById(1L);       // At most 5 times
        verify(userRepository, never()).findById(999L);       // Never called with 999L
    }

    @Test
    @DisplayName("Advanced Verification: ArgumentCaptor for Complex Verification")
    void testArgumentCaptor_ComplexVerification() {
        // Given - Setup successful save
        when(userRepository.findByEmail(anyString())).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return user;
        });

        // When - Create new user
        User createdUser = userService.createUser("Dwight Schrute", "dwight@dundermifflin.com");

        // PILLAR 3: VERIFICATION - Capture and verify the saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNotNull(capturedUser);
        assertEquals("Dwight Schrute", capturedUser.getName());
        assertEquals("dwight@dundermifflin.com", capturedUser.getEmail());
        assertNotNull(capturedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Advanced Verification: Multiple ArgumentCaptors")
    void testMultipleArgumentCaptors() {
        // Given - Setup for multiple save operations
        when(userRepository.findByEmail(anyString())).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(System.currentTimeMillis());
            return user;
        });

        // When - Create multiple users
        userService.createUser("Stanley Hudson", "stanley@dundermifflin.com");
        userService.createUser("Kevin Malone", "kevin@dundermifflin.com");
        userService.createUser("Angela Martin", "angela@dundermifflin.com");

        // PILLAR 3: VERIFICATION - Capture all saved users
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(3)).save(userCaptor.capture());

        List<User> capturedUsers = userCaptor.getAllValues();
        assertEquals(3, capturedUsers.size());
        assertEquals("Stanley Hudson", capturedUsers.get(0).getName());
        assertEquals("Kevin Malone", capturedUsers.get(1).getName());
        assertEquals("Angela Martin", capturedUsers.get(2).getName());
    }

    @Test
    @DisplayName("Advanced Verification: Method Call Ordering with InOrder")
    void testMethodCallOrdering() {
        // Given - Setup mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When - Execute operations in specific order
        userService.findUserById(1L);
        userService.updateUser(1L, "Michael Scarn", "michael.scarn@dundermifflin.com");

        // PILLAR 3: VERIFICATION - Verify method call ordering
        var inOrder = inOrder(userRepository);
        inOrder.verify(userRepository, times(2)).findById(1L);     // Called twice total
        inOrder.verify(userRepository).save(any(User.class));      // Then save
    }

    @Test
    @DisplayName("Advanced Verification: Complex Argument Matching with argThat")
    void testComplexArgumentMatching() {
        // Given - Setup mock behavior
        when(userRepository.findByEmail(anyString())).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(200L);
            return user;
        });

        // When - Create user
        userService.createUser("Ryan Howard", "ryan@dundermifflin.com");

        // PILLAR 3: VERIFICATION - Verify with custom argument matcher
        verify(userRepository).save(argThat(user ->
                user.getName().equals("Ryan Howard") &&
                user.getEmail().equals("ryan@dundermifflin.com") &&
                user.getCreatedAt() != null
        ));
    }

    @Test
    @DisplayName("Complete Intermediate Triangle: Multiple Mocks Working Together")
    void testCompleteIntermediateTriangle() {
        // PILLAR 2: STUBBING - Multiple stub behaviors
        when(userRepository.findAll())
                .thenReturn(Arrays.asList(testUser1, testUser2));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser1));

        // When - Execute multiple operations
        List<User> allUsers = userService.findAllUsers();
        Optional<User> specificUser = userService.findUserById(1L);

        // Then - Verify results
        assertEquals(2, allUsers.size());
        assertTrue(specificUser.isPresent());
        assertEquals("Michael Scott", specificUser.get().getName());

        // PILLAR 3: VERIFICATION - Verify all interactions
        verify(userRepository).findAll();
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    // =========================================================================
    // INJECTION PATTERN DEMONSTRATIONS
    // =========================================================================

    @Test
    @DisplayName("Mock Creation: @InjectMocks Constructor Injection Pattern")
    void testInjectMocksPattern() {
        // This test demonstrates that @InjectMocks automatically injects
        // the @Mock annotated userRepository into the UserService constructor

        // Given - @InjectMocks has already created userService with injected mock
        assertNotNull(userService);
        assertNotNull(userRepository);

        // PILLAR 2: STUBBING
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean exists = userService.userExists(1L);

        // Then
        assertTrue(exists);

        // PILLAR 3: VERIFICATION
        verify(userRepository).existsById(1L);
    }
}