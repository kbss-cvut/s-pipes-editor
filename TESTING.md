# Testing Conventions

This document outlines the testing conventions used in our project.

## Test Class Naming

- **Pattern**: `{ClassName}Test`
- **Examples**:
  - `ExecutionController` → `ExecutionControllerTest`
  - `FormService` → `FormServiceTest`

## Test Method Naming

### For Service Methods
- **Pattern**: `{methodName}{condition}{expected}`
- **Examples**:
  - `findUserByIdWhenUserExistsReturnsUser`
  - `saveUserWhenEmailIsInvalidThrowsValidationException`

## Test Structure (Given-When-Then)

Always structure tests using the Given-When-Then pattern:

### Basic Structure
```java
@Test
@DisplayName("Descriptive test name only if method is too complex")
void methodName_condition_expected() {
    // Given - setup test data and preconditions
    
    // When - perform the action being tested
    
    // Then - verify the results
}
```

### Example: Service Test (GWT parts are delimited by newline)
```java
@Test
void findUserByIdWhenUserExistsReturnsUser() {
    User expectedUser = new User("1", "test@example.com");
    when(userRepository.findById("1")).thenReturn(Optional.of(expectedUser));
    
    User result = userService.findUserById("1");
    
    assertThat(result).isEqualTo(expectedUser);
    verify(userRepository).findById("1");
}
```

### Example: One-liner that is too simple to split into GWT parts
```java
@Test
void findById_returnsEmpty_whenNotFound() {
    assertThat(repository.findById("nonexistent")).isEmpty();
}
```

## Best Practices

1. **One Assertion Per Test**: Each test should verify one behavior
2. **Descriptive Names**: Test names should clearly describe the scenario
3. **Test Isolation**: Each test should be independent of others
4. **Readability**: Use empty lines to separate GWT sections for better readability
5. **Use @DisplayName**: For more descriptive test output only if it is hard to understand from the method name
6. **Nested Tests**: Use `@Nested` to group related test cases
