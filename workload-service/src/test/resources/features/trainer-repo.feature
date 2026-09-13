Feature: Trainer Repository Database Operations
  Scenario: Successfully saving a new trainer to the database
    Given the database is completely empty
    When a new trainer with username "john.doe" and first name "John" is saved to the system
    Then the total number of trainers in the database should be 1
    And the trainer with username "john.doe" should be verified to have the first name "John"