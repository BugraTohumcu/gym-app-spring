package org.bugra.ui;

import org.bugra.facade.GymFacade;
import org.bugra.model.Trainee;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TraineeMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TraineeMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printSubMenu("TRAINEE");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createTraineeFlow();
                case "2" -> getTraineeFlow();
                case "3" -> updateTraineeFlow();
                case "4" -> deleteTraineeFlow();
                case "0" -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void createTraineeFlow() {
        System.out.println("\n--- Create Trainee ---");
        String firstName = readName("First Name");
        String lastName  = readName("Last Name");
        LocalDate birthDate = readDate("Birth Date");
        String address = readAddress("Address");

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
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

    private void getTraineeFlow() {
        System.out.println("\n--- Get Trainee ---");
        try {
            long id = readPositiveLong("Trainee ID");
            Trainee t = gymFacade.getTrainee(id);
            printTrainee(t);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void updateTraineeFlow() {
        System.out.println("\n--- Update Trainee ---");
        try {
            long id = readPositiveLong("Trainee ID");
            Trainee existing = gymFacade.getTrainee(id);
            printTrainee(existing);

            System.out.println("\nEnter new values (leave blank to keep current):");
            System.out.print("First Name [" + existing.getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name  [" + existing.getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Address    [" + existing.getAddress() + "]: ");
            String address = scanner.nextLine().trim();

            if (!firstName.isBlank()) {
                if (firstName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) existing.setFirstName(firstName);
                else printError("Invalid first name, keeping current value.");
            }
            if (!lastName.isBlank()) {
                if (lastName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) existing.setLastName(lastName);
                else printError("Invalid last name, keeping current value.");
            }
            if (!address.isBlank()) {
                if (address.matches("[a-zA-Z0-9ğüşıöçĞÜŞİÖÇ\\s,.-]+")) existing.setAddress(address);
                else printError("Invalid address, keeping current value.");
            }

            Trainee updated = gymFacade.updateTrainee(existing);
            printSuccess("Trainee updated successfully!");
            printTrainee(updated);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void deleteTraineeFlow() {
        System.out.println("\n--- Delete Trainee ---");
        try {
            long id = readPositiveLong("Trainee ID");
            boolean deleted = gymFacade.deleteTrainee(id);
            if (deleted) printSuccess("Trainee with ID " + id + " deleted successfully.");
            else printError("Trainee with ID " + id + " not found.");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}