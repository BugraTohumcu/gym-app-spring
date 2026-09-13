Feature: ActiveMQ DLQ Routing

  Scenario: Routing a message with missing required fields to the DLQ
    Given the DLQ is completely empty
    When an invalid message with an empty username is sent to the main workload queue
    Then the message should not be processed by the main system
    And the message should be routed to the DLQ
    And the message in the DLQ should contain the header "X-DLQ-Reason"