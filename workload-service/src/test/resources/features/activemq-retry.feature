Feature: ActiveMQ Message Retry and Default DLQ Routing
  Scenario: A valid message causing internal error and send to dlq
    Given the default DLQ "ActiveMQ.DLQ" is empty
    When a valid message causing an internal error
    Then the system should retry the message
    And the message should finally be routed to the default dlq "ActiveMQ.DLQ"