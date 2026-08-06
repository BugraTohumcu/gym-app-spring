package org.bugra.shared;

public interface ValidationMessages {

    String USERNAME_REQUIRED            = "Username is required";
    String PASSWORD_REQUIRED            = "Password is required";
    String FIRST_NAME_REQUIRED          = "First name is required";
    String LAST_NAME_REQUIRED           = "Last name is required";
    String DOB_PAST                     = "Date of birth should be in past date";
    String TRAINING_DATE_FUTURE         = "Training date should be in future date";
    String SPECIALIZATION_REQUIRED      = "Specialization is required";
    String TRAINING_NAME_REQUIRED       = "Training name is required";
    String TRAINING_TYPE_NAME_REQUIRED  = "Training type name is required";
    String TRAINING_DURATION            = "Training duration is required";
    String STATUS_REQUIRED              = "Status is required" ;

    String FIRST_NAME_SIZE              = "First name should be between 2 - 50 chars";
    String LAST_NAME_SIZE               = "Last name should be between 2 - 50 chars";
    String ADDRESS_SIZE                 = "Address length can not pass 100 chars";
    String SPECIALIZATION_SIZE          = "Specialization length should be between 2- 50 chars";
    String USERNAME_SIZE                = "Username must be between 3 and 50 characters";
    String TRAINER_LIST_SIZE            = "You should add at least one trainer username";
    String DATE_INTERVAL                = "From date must not be after toDate";
    String TRAINING_NAME_SIZE           = "Training name must be between 2-20 chars";
    String TRAINING_TYPE_NAME_SIZE      = "Training type name must be between 2-20 chars";
    String TRAINING_DURATION_VALUE      = "Training duration neither can be zero or negative value";
    String PASSWORD_SIZE                =  "Password must be between 2 and 20 characters";
}