package org.bugra.ui;

import org.bugra.dto.UserResponse;
import org.bugra.facade.GymFacade;
import org.bugra.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TrainingMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Training Lookup ─────────────────────");
            System.out.println("│ 1. Get Training by ID");
            System.out.println("│ 0. Back");
            System.out.println("└─────────────────────────────────────────");
            System.out.print("Select: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> getTrainingByIdFlow();
                case "0" -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void getTrainingByIdFlow() {
        long id = readPositiveLong("Enter Training ID");
        try {
            Training training = gymFacade.getTraining(id);
            if (training != null) {
                printTraining(training);
            } else {
                printError("Training not found with ID: " + id);
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}