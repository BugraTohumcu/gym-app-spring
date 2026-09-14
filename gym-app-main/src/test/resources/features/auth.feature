Feature: User Authentication

  Scenario: Successful login with valid credentials
    Given a registered user exists with username "test.user" and password "secret123"
    When the user attempts to log in with username "test.user" and password "secret123"
    Then the system should return a successful response

  Scenario: Unsuccessful login with invalid credentials
    Given a registered user exists with username "test.user" and password "secret123"
    When the user attempts to log in with username "test.user" and password "wrongpassword"
    Then the system should return an unauthorized error response

  Scenario: Accessing a protected endpoint without a token
    When an unauthenticated user attempts to access a protected endpoint
    Then the system should return an unauthorized error response