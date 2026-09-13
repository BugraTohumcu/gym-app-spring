Feature: Workload Message Processing

  Scenario: Successfully processing a valid workload message from the queue
    Given the database and workload queue are completely empty
    When a valid workload message for trainer "jane.smith" with 60 minutes is sent to the queue
    Then the message should be processed without any errors
    And the database should reflect 60 minutes of workload for "jane.smith"