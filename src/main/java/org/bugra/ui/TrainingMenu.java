package org.bugra.ui;

import org.bugra.facade.GymFacade;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TrainingMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TrainingMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printSubMenu("TRAINING");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createTrainingFlow();
                case "2" -> getTrainingFlow();
                case "0" -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void createTrainingFlow() {
        System.out.println("\n--- Create Training ---");
        long traineeId = readPositiveLong("Trainee ID");
        long trainerId = readPositiveLong("Trainer ID");
        String name    = readNonBlank("Training Name");
        String typeName = readNonBlank("Training Type");
        LocalDate date = readDate("Training Date");
        int duration   = readPositiveInt("Duration (mins)");

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(typeName);

        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(name);
        training.setTrainingType(trainingType);
        training.setTrainingDate(date);
        training.setTrainingDuration(duration);

        try {
            Training saved = gymFacade.createTraining(training);
            printSuccess("Training created successfully!");
            printTraining(saved);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void getTrainingFlow() {
        System.out.println("\n--- Get Training ---");
        try {
            long id = readPositiveLong("Training ID"); // "Trainee ID" yazıyordu, "Training ID" olarak güncelledim
            Training t = gymFacade.getTraining(id);
            printTraining(t);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}