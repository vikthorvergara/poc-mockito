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

1. **Start with `MockitoTriangleTest`** - Complete examples of all three pillars
2. **Understand the workflow**: Create → Stub → Test → Verify
3. **Practice variations**: Different return types, exceptions, argument matching
4. **Build complexity gradually**: Start simple, add edge cases

## Development

### What I Actually Learned From Building This

So I went through building the `MockitoTriangleTest` and honestly, it was way more enlightening than expected. Here's the real deal:

**The Triangle Actually Works**: That Create → Stub → Verify flow isn't just theory - it genuinely helps you think through your tests. Every single test followed this pattern and it feels natural.

**Mock Creation is Flexible**: Started with `@Mock` annotations (which is clean and readable), but also tried the programmatic `mock()` approach. Both work fine, but annotations feel more organized.

**Stubbing is Where the Magic Happens**:
- You can stub different methods with different behaviors in the same test
- Some returning values ()`when().thenReturn()`), others throwing exceptions (`when().thenThrow()`).

**Verification Caught Real Issues**: Turns out `verify()` is pretty useful for catching when your code isn't doing what you think it's doing with dependencies.

**Key Insight**: The triangle approach forces you to think about what your code should actually be doing with its dependencies, not just what it should return.

Next up: probably diving into ArgumentCaptor and more advanced verification scenarios.


