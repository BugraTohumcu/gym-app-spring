package org.bugra.enums;

public enum StorageType {
    TRAINEE(StorageType.Constants.TRAINEE_NAME),
    TRAINER(StorageType.Constants.TRAINER_NAME),
    TRAINING(StorageType.Constants.TRAINING_NAME);

    private final String beanName;

    StorageType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public static class Constants {
        public static final String TRAINEE_NAME = "TRAINEE";
        public static final String TRAINER_NAME = "TRAINER";
        public static final String TRAINING_NAME = "TRAINING";
    }
}
