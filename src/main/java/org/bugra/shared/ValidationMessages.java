package org.bugra.shared;

public interface ValidationMessages {
    String FIRST_NAME_REQUIRED = "First name is required";
    String LAST_NAME_REQUIRED = "Last name is required";
    String DOB_PAST = "Date of birth should be in past date";


    String FIRST_NAME_SIZE = "First name should be between 2 - 50 chars";
    String LAST_NAME_SIZE = "Last name should be between 2 - 50 chars";
    String ADDRESS_SIZE = "Address length can not pass 100 chars";
}