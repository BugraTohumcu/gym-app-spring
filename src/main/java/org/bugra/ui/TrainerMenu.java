package org.bugra.ui;

import org.bugra.dto.ChangePassword;
import org.bugra.dto.TrainerTrainingFilter;
import org.bugra.dto.UserResponse;
import org.bugra.facade.GymFacade;
import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import org.bugra.model.TrainingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainerMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TrainerMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu(UserResponse currentUser) {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Trainer Operations ──────────────────");
            System.out.println("│ 1. Get my profile");
            System.out.println("│ 2. Update my profile");
            System.out.println("│ 3. Change password");
            System.out.println("│ 4. Activate / De-activate my profile");
            System.out.println("│ 5. Get my trainings");
            System.out.println("│ 6. Add training");
            System.out.println("│ 0. Logout");
            System.out.println("└─────────────────────────────────────────");
            System.out.print("Select: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> getTrainerProfileFlow(currentUser);
                case "2" -> updateTrainerFlow(currentUser);
                case "3" -> changePasswordFlow();
                case "4" -> toggleTrainerActiveFlow(currentUser);
                case "5" -> getTrainerTrainingsFlow(currentUser);
                case "6" -> addTrainingFlow(currentUser);
                case "0" -> { printSuccess("Logged out."); back = true; }
                default -> printError("Invalid option!");
            }
        }
    }

    private void getTrainerProfileFlow(UserResponse currentUser) {
        try {
            Trainer t = gymFacade.getTrainerByUsername(currentUser.username());
            printTrainer(t);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void updateTrainerFlow(UserResponse currentUser) {
        System.out.println("\n--- Update Trainer ---");
        try {
            Trainer existing = gymFacade.getTrainerByUsername(currentUser.username());
            printTrainer(existing);

            System.out.println("\nEnter new values (leave blank to keep current):");

            System.out.print("First Name     [" + existing.getUser().getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name      [" + existing.getUser().getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Specialization [" + existing.getSpecialization().getTrainingTypeName() + "]: ");
            String specialization = scanner.nextLine().trim();

            if (!firstName.isBlank() && firstName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+"))
                existing.getUser().setFirstName(firstName);
            if (!lastName.isBlank() && lastName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+"))
                existing.getUser().setLastName(lastName);
            if (!specialization.isBlank())
                existing.getSpecialization().setTrainingTypeName(specialization);

            Trainer updated = gymFacade.updateTrainer(existing);
            printSuccess("Trainer updated successfully!");
            printTrainer(updated);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void toggleTrainerActiveFlow(UserResponse currentUser) {
        try {
            gymFacade.toggleTrainerActive(currentUser.username());
            printSuccess("Active status toggled.");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void getTrainerTrainingsFlow(UserResponse currentUser) {
        System.out.println("\n--- My Trainings ---");
        System.out.println("Leave blank to skip criteria");

        System.out.print("From Date (YYYY-MM-DD) : ");
        String from = scanner.nextLine().trim();

        System.out.print("To Date   (YYYY-MM-DD) : ");
        String to = scanner.nextLine().trim();

        System.out.print("Trainee Name           : ");
        String traineeName = scanner.nextLine().trim();

        try {
            TrainerTrainingFilter filter = new TrainerTrainingFilter(
                    currentUser.username(),
                    from.isBlank() ? null : LocalDate.parse(from),
                    to.isBlank() ? null : LocalDate.parse(to),
                    traineeName.isBlank() ? null : traineeName
            );

            List<Training> trainings = gymFacade.getTrainerTrainings(filter);
            if (trainings.isEmpty()) {
                System.out.println("No trainings found.");
                return;
            }
            trainings.forEach(this::printTraining);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void addTrainingFlow(UserResponse currentUser) {
        System.out.println("\n--- Add Training ---");

        long traineeId  = readPositiveLong("Trainee ID");
        String name     = readNonBlank("Training Name");
        String typeName = readNonBlank("Training Type");
        LocalDate date  = readFutureOrTodayDate("Training Date");
        int duration    = readPositiveInt("Duration (mins)");

        try {
            Trainer trainer = gymFacade.getTrainerByUsername(currentUser.username());
            Trainee trainee = gymFacade.getTrainee(traineeId);

            TrainingType trainingType = new TrainingType();
            trainingType.setTrainingTypeName(typeName);

            Training training = new Training();
            training.setTrainer(trainer);
            training.setTrainee(trainee);
            training.setTrainingName(name);
            training.setTrainingType(trainingType);
            training.setTrainingDate(date);
            training.setTrainingDuration(duration);

            Training saved = gymFacade.createTraining(training);
            printSuccess("Training added successfully!");
            printTraining(saved);
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