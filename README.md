# Mockito POC

Just a simple proof of concept to learn all the core Mockito features.

## What's this about?

Learning Mockito by actually using it. No fluff, just hands-on practice focused on the **Mockito Triangle**:

1. **Mock Creation** - Creating mocks, spies, and using annotations
2. **Stubbing** - Setting up method behaviors, return values, exceptions
3. **Verification** - Checking interactions, argument matching, call counts

Plus diving into:
- Argument matchers
- ArgumentCaptor
- Advanced stubbing techniques
- Exception handling in tests

## Goal

Get comfortable with all the main Mockito features through practical examples and experimentation.

That's it. Simple POC for learning purposes.

## Tech Stack

- **Java 25** with preview features enabled
- **Spring Boot 3.5.6** for dependency injection and testing support
- **Mockito 5.7.0** for mocking framework
- **JUnit 5** for test framework
- **Maven** for build management

## Learning Path

### Level 1: Fundamentals
**Start with `fundamentals/MockitoTriangleTest`**
- Basic mock creation with `@Mock` and `mock()`
- Simple stubbing: `when().thenReturn()`, `when().thenThrow()`
- Basic verification: `verify()`
- Understanding the Create → Stub → Verify workflow

### Level 2: Intermediate Patterns
**Move to `intermediate/IntermediateTriangleTest`**
- Advanced mock creation with `@InjectMocks`
- Consecutive call stubbing
- Void method stubbing with `doThrow()` and `doNothing()`
- Verification modes: `times()`, `never()`, `atLeast()`, `atMost()`
- `ArgumentCaptor` for complex verification
- Method call ordering with `inOrder()`
- Argument matchers: `any()`, `eq()`, `argThat()`

**Spring Boot Integration: `intermediate/SpringBootMockBeanTest`**
- `@MockBean` for Spring context integration
- Testing real service logic with mocked dependencies
- Understanding when to use `@MockBean` vs `@Mock`

### Level 3: Advanced (Coming Soon)
- Spies and partial mocks
- Static method mocking
- Custom argument matchers
- thenAnswer() for dynamic responses

## Development

### What I Actually Learned From Building This

So I went through building the `MockitoTriangleTest` and honestly, it was way more enlightening than expected. Here's the real deal:

**The Triangle Actually Works**: That Create → Stub → Verify flow isn't just theory - it genuinely helps you think through your tests. Every single test followed this pattern and it feels natural.

**Mock Creation is Flexible**: Started with `@Mock` annotations (which is clean and readable), but also tried the programmatic `mock()` approach. Both work fine, but annotations feel more organized.

**Stubbing is Where the Magic Happens**:
- You can stub different methods with different behaviors in the same test
- Some returning values (`when().thenReturn()`), others throwing exceptions (`when().thenThrow()`).

**Verification Caught Real Issues**: Turns out `verify()` is pretty useful for catching when your code isn't doing what you think it's doing with dependencies.

**Key Insight**: The triangle approach forces you to think about what your code should actually be doing with its dependencies, not just what it should return.

---

**Update: Intermediate Patterns** - After diving into the intermediate stuff, holy crap this is powerful:

**@InjectMocks saves so much boilerplate** - no more manual constructor calls in every test. Just annotate and it injects everything automatically.

**ArgumentCaptor is a game changer** - you can verify complex objects were passed correctly without writing custom matchers. Just capture and inspect.

**Consecutive stubbing is powerful** - simulating state changes across multiple calls is surprisingly easy. First call returns X, second returns Y, etc.

**InOrder verification catches subtle bugs** - ensures operations happen in the right sequence. Found this super useful for testing workflow logic.

**@MockBean vs @Mock matters** - understanding when you need Spring context vs pure unit tests is crucial. @MockBean for integration, @Mock for unit tests. Different tools for different jobs.

**Verification modes are essential** - `times()`, `never()`, `atLeast()` let you be specific about interactions. Way better than just "it was called."

**Argument matchers make tests flexible** - `any()`, `argThat()` let your tests be flexible without being too loose. You can match patterns instead of exact values.


