package org.bugra.ui;

import org.bugra.dto.ChangePassword;
import org.bugra.dto.TraineeTrainingFilter;
import org.bugra.dto.UserResponse;
import org.bugra.facade.GymFacade;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class TraineeMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TraineeMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu(UserResponse currentUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Trainee Operations ──────────────────");
            System.out.println("│ 1. Get my profile");
            System.out.println("│ 2. Update my profile");
            System.out.println("│ 3. Change password");
            System.out.println("│ 4. Activate / De-activate my profile");
            System.out.println("│ 5. Delete my profile");
            System.out.println("│ 6. Get my trainings");
            System.out.println("│ 7. Get available trainers");
            System.out.println("│ 8. Update my trainers list");
            System.out.println("│ 0. Logout");
            System.out.println("└─────────────────────────────────────────");
            System.out.print("Select: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> getTraineeProfileFlow(currentUser);
                case "2" -> updateTraineeFlow(currentUser);
                case "3" -> changePasswordFlow();
                case "4" -> toggleTraineeActiveFlow(currentUser);
                case "5" -> { deleteTraineeFlow(currentUser); back = true; }
                case "6" -> getTraineeTrainingsFlow(currentUser);
                case "7" -> getAvailableTrainersFlow(currentUser);
                case "8" -> updateTraineeTrainersFlow(currentUser);
                case "0" -> { printSuccess("Logged out."); back = true; }
                default -> printError("Invalid option!");
            }
        }
    }

    private void getTraineeProfileFlow(UserResponse currentUser) {
        try {
            Trainee t = gymFacade.getTraineeByUsername(currentUser.username());
            printTrainee(t);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void updateTraineeFlow(UserResponse currentUser) {
        System.out.println("\n--- Update Trainee ---");
        try {
            Trainee existing = gymFacade.getTraineeByUsername(currentUser.username());
            printTrainee(existing);

            System.out.println("\nEnter new values (leave blank to keep current):");

            System.out.print("First Name  [" + existing.getUser().getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name   [" + existing.getUser().getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Address     [" + existing.getAddress() + "]: ");
            String address = scanner.nextLine().trim();

            if (!firstName.isBlank() && firstName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+"))
                existing.getUser().setFirstName(firstName);
            if (!lastName.isBlank() && lastName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+"))
                existing.getUser().setLastName(lastName);
            if (!address.isBlank() && address.matches("[a-zA-Z0-9ğüşıöçĞÜŞİÖÇ\\s,.-]+")
                    && address.matches(".*[a-zA-ZğüşıöçĞÜŞİÖÇ].*"))
                existing.setAddress(address);

            Trainee updated = gymFacade.updateTrainee(existing);
            printSuccess("Trainee updated successfully!");
            printTrainee(updated);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void toggleTraineeActiveFlow(UserResponse currentUser) {
        try {
            gymFacade.toggleTraineeActive(currentUser.username());
            printSuccess("Active status toggled.");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void deleteTraineeFlow(UserResponse currentUser) {
        System.out.println("\n--- Delete My Profile ---");
        System.out.print("Are you sure? This will also delete your trainings. (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            try {
                gymFacade.deleteTraineeByUsername(currentUser.username());
                printSuccess("Profile deleted. Goodbye!");
            } catch (Exception e) {
                printError(e.getMessage());
            }
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void getTraineeTrainingsFlow(UserResponse currentUser) {
        System.out.println("\n--- My Trainings ---");
        System.out.println("Leave blank to skip criteria");

        System.out.print("From Date (YYYY-MM-DD) : ");
        String from = scanner.nextLine().trim();

        System.out.print("To Date   (YYYY-MM-DD) : ");
        String to = scanner.nextLine().trim();

        System.out.print("Trainer Name           : ");
        String trainerName = scanner.nextLine().trim();

        System.out.print("Training Type          : ");
        String trainingType = scanner.nextLine().trim();

        try {
            TraineeTrainingFilter filter = new TraineeTrainingFilter(
                    currentUser.username(),
                    from.isBlank() ? null : LocalDate.parse(from),
                    to.isBlank() ? null : LocalDate.parse(to),
                    trainerName.isBlank() ? null : trainerName,
                    trainingType.isBlank() ? null : trainingType
            );

            List<Training> trainings = gymFacade.getTraineeTrainings(filter);
            if (trainings.isEmpty()) {
                System.out.println("No trainings found.");
                return;
            }
            trainings.forEach(this::printTraining);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void getAvailableTrainersFlow(UserResponse currentUser) {
        try {
            List<Trainer> trainers = gymFacade.getTrainersNotAssignedToTrainee(currentUser.username());
            if (trainers.isEmpty()) {
                System.out.println("No available trainers found.");
                return;
            }
            trainers.forEach(this::printTrainer);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void updateTraineeTrainersFlow(UserResponse currentUser) {
        System.out.println("\n--- Update My Trainers List ---");
        System.out.println("Enter trainer usernames separated by comma (e.g. john.doe,jane.smith):");
        System.out.print("Usernames : ");
        String input = scanner.nextLine().trim();

        if (input.isBlank()) {
            printError("At least one trainer username required.");
            return;
        }

        List<String> trainerUsernames = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        try {
            gymFacade.updateTraineeTrainers(currentUser.username(), trainerUsernames);
            printSuccess("Trainers list updated successfully!");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void changePasswordFlow() {
        System.out.println("\n--- Change Password ---");
        String current = readNonBlank("Current Password");
        String newPass  = readNonBlank("New Password");

        try {
            gymFacade.changePassword(new ChangePassword(current, newPass));
            printSuccess("Password changed successfully!");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}