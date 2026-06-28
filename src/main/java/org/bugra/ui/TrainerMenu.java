package org.bugra.ui;

import org.bugra.facade.GymFacade;
import org.bugra.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMenu extends BaseUI {

    private final GymFacade gymFacade;

    public TrainerMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            printSubMenu("TRAINER");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> createTrainerFlow();
                case "2" -> getTrainerFlow();
                case "3" -> updateTrainerFlow();
                case "0" -> back = true;
                default -> printError("Invalid option!");
            }
        }
    }

    private void createTrainerFlow() {
        System.out.println("\n--- Create Trainer ---");
        String firstName = readName("First Name");
        String lastName  = readName("Last Name");
        String specialization = readNonBlank("Specialization");

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);

        try {
            Trainer saved = gymFacade.createTrainer(trainer);
            printSuccess("Trainer created successfully!");
            printTrainer(saved);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void getTrainerFlow() {
        System.out.println("\n--- Get Trainer ---");
        try {
            long id = readPositiveLong("Trainer ID");
            Trainer t = gymFacade.getTrainer(id);
            printTrainer(t);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void updateTrainerFlow() {
        System.out.println("\n--- Update Trainer ---");
        try {
            long id = readPositiveLong("Trainer ID");
            Trainer existing = gymFacade.getTrainer(id);
            printTrainer(existing);

            System.out.println("\nEnter new values (leave blank to keep current):");
            System.out.print("First Name     [" + existing.getFirstName() + "]: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name      [" + existing.getLastName() + "]: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Specialization [" + existing.getSpecialization() + "]: ");
            String specialization = scanner.nextLine().trim();

            if (!firstName.isBlank()) {
                if (firstName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) existing.setFirstName(firstName);
                else printError("Invalid first name, keeping current value.");
            }
            if (!lastName.isBlank()) {
                if (lastName.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) existing.setLastName(lastName);
                else printError("Invalid last name, keeping current value.");
            }
            if (!specialization.isBlank()) existing.setSpecialization(specialization);

            Trainer updated = gymFacade.updateTrainer(existing);
            printSuccess("Trainer updated successfully!");
            printTrainer(updated);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}