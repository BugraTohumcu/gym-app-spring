package org.bugra.ui;

import org.bugra.dto.LoginUser;
import org.bugra.dto.UserResponse;
import org.bugra.enums.UserRole;
import org.bugra.facade.GymFacade;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.TrainingType;
import org.bugra.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AppUI extends BaseUI {

    private final GymFacade gymFacade;
    private final TraineeMenu traineeMenu;
    private final TrainerMenu trainerMenu;

    public AppUI(GymFacade gymFacade, TraineeMenu traineeMenu, TrainerMenu trainerMenu) {
        this.gymFacade = gymFacade;
        this.traineeMenu = traineeMenu;
        this.trainerMenu = trainerMenu;
    }

    public void run() {
        printBanner();
        boolean running = true;

        while (running) {
            printMainMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> createTraineeFlow();
                case "2" -> createTrainerFlow();
                case "3" -> loginFlow();
                case "0" -> running = false;
                default -> printError("Invalid option!");
            }
        }
        System.out.println("\nGoodbye!");
    }

    // ─── AUTH ─────────────────────────────────────────────────

    private void loginFlow() {
        System.out.println("\n--- Login ---");
        String username = readNonBlank("Username");
        String password = readNonBlank("Password");

        try {
            UserResponse currentUser = gymFacade.login(new LoginUser(username, password));
            printSuccess("Welcome, " + currentUser.fullName() + "!");

            if (currentUser.userRole() == UserRole.TRAINEE) {
                traineeMenu.showMenu(currentUser);
            } else {
                trainerMenu.showMenu(currentUser);
            }
        } catch (Exception e) {
            printError("Invalid username or password.");
        }
    }

    // ─── CREATE FLOWS (PRE-LOGIN) ─────────────────────────────

    private void createTraineeFlow() {
        System.out.println("\n--- Create Trainee ---");

        String firstName = readName("First Name");
        String lastName  = readName("Last Name");
        LocalDate birthDate = readPastDate("Birth Date");
        String address = readAddress("Address");

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(birthDate);
        trainee.setAddress(address);

        try {
            Trainee saved = gymFacade.createTrainee(trainee);
            printSuccess("Trainee created successfully!");
            printTrainee(saved);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void createTrainerFlow() {
        System.out.println("\n--- Create Trainer ---");

        String firstName = readName("First Name");
        String lastName  = readName("Last Name");
        String typeName  = readNonBlank("Specialization (Training Type)");

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(typeName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        try {
            Trainer saved = gymFacade.createTrainer(trainer);
            printSuccess("Trainer created successfully!");
            printTrainer(saved);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // ─── APP UI HELPERS ───────────────────────────────────────

    private void printBanner() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║    GYM MANAGEMENT SYSTEM         ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    private void printMainMenu() {
        System.out.println("\n┌─── Main Menu ───────────────────");
        System.out.println("│ 1. Register as Trainee");
        System.out.println("│ 2. Register as Trainer");
        System.out.println("│ 3. Login");
        System.out.println("│ 0. Exit");
        System.out.println("└──────────────────────────────────");
        System.out.print("Select: ");
    }
}