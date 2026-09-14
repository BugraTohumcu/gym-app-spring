Feature: Training Workload Integration
  In order to keep trainer schedules synchronized,
  adding a new training session must automatically dispatch a workload update message.

  Scenario: Creating a training successfully triggers a workload message
    Given the ActiveMQ test queue is completely empty
    When a new training session of 60 minutes is created for trainer "jane.smith" and trainee "john.doe"
    Then the training should be saved successfully in the main database
    And a workload update message for "jane.smith" with 60 minutes should be sent to the ActiveMQ queue