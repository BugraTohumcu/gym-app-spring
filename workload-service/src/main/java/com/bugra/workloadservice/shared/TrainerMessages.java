package com.bugra.workloadservice.shared;

public interface TrainerMessages {

    String USERNAME_REQUIRED            = "Username is required";
    String FIRST_NAME_REQUIRED          = "First name is required";
    String LAST_NAME_REQUIRED           = "Last name is required";
    String TRAINING_DATE_REQUIRED       = "Training date is required";
    String TRAINING_DATE_FUTURE         = "Training date should be in future date";
    String TRAINING_DURATION            = "Training duration is required";
    String ACTION_TYPE_REQUIRED         = "Action type is required" ;


    String FIRST_NAME_SIZE              = "First name should be between 2 - 50 chars";
    String LAST_NAME_SIZE               = "Last name should be between 2 - 50 chars";
    String USERNAME_SIZE                = "Username must be between 3 and 50 characters";
    String TRAINING_DURATION_VALUE      = "Training duration neither can be zero or negative value";
}
