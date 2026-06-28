package org.bugra.ui;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public abstract class BaseUI {

    protected static final Scanner scanner = new Scanner(System.in);

    // ─── INPUT HELPERS ───────────────────────────────────────

    protected String readNonBlank(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) return value;
            printError(fieldName + " cannot be blank. Please try again.");
        }
    }

    protected String readName(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (value.isBlank()) {
                printError(fieldName + " cannot be blank.");
                continue;
            }
            if (!value.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                printError(fieldName + " can only contain letters. No numbers or special characters.");
                continue;
            }
            return value;
        }
    }

    protected String readAddress(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (value.isBlank()) {
                printError(fieldName + " cannot be blank.");
                continue;
            }
            if (!value.matches("[a-zA-Z0-9ğüşıöçĞÜŞİÖÇ\\s,.-]+")) {
                printError(fieldName + " contains invalid characters.");
                continue;
            }
            if (!value.matches(".*[a-zA-ZğüşıöçĞÜŞİÖÇ].*")) {
                printError(fieldName + " must contain at least one letter.");
                continue;
            }
            return value;
        }
    }

    protected long readPositiveLong(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String input = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(input);
                if (value > 0) return value;
                printError(fieldName + " must be a positive number.");
            } catch (NumberFormatException e) {
                printError(fieldName + " must be a valid number.");
            }
        }
    }

    protected int readPositiveInt(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) return value;
                printError(fieldName + " must be a positive number.");
            } catch (NumberFormatException e) {
                printError(fieldName + " must be a valid number.");
            }
        }
    }

    protected LocalDate readDate(String fieldName) {
        while (true) {
            System.out.print(fieldName + " (YYYY-MM-DD) : ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                printError(fieldName + " cannot be blank.");
                continue;
            }
            if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                printError(fieldName + " must be in YYYY-MM-DD format. (e.g. 1990-05-15)");
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(input);
                if (!fieldName.equals("Training Date") && date.isAfter(LocalDate.now())) {
                    printError(fieldName + " cannot be in the future.");
                    continue;
                }
                if (date.isBefore(LocalDate.of(1900, 1, 1))) {
                    printError(fieldName + " cannot be before 1900-01-01.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                printError(fieldName + " is not a valid date. (e.g. month cannot be 13)");
            }
        }
    }

    // ─── PRINT HELPERS ───────────────────────────────────────

    protected void printTrainee(Trainee t) {
        System.out.println("\n┌─── Trainee ─────────────────────");
        System.out.println("│ ID         : " + t.getId());
        System.out.println("│ First Name : " + t.getFirstName());
        System.out.println("│ Last Name  : " + t.getLastName());
        System.out.println("│ Username   : " + t.getUsername());
        System.out.println("│ Password   : " + t.getPassword());
        System.out.println("│ Active     : " + t.isActive());
        System.out.println("│ Birth Date : " + t.getDateOfBirth());
        System.out.println("│ Address    : " + t.getAddress());
        System.out.println("└──────────────────────────────────");
    }

    protected void printTrainer(Trainer t) {
        System.out.println("\n┌─── Trainer ──────────────────────────");
        System.out.println("│ ID             : " + t.getId());
        System.out.println("│ First Name     : " + t.getFirstName());
        System.out.println("│ Last Name      : " + t.getLastName());
        System.out.println("│ Username       : " + t.getUsername());
        System.out.println("│ Password       : " + t.getPassword());
        System.out.println("│ Active         : " + t.isActive());
        System.out.println("│ Specialization : " + t.getSpecialization());
        System.out.println("└──────────────────────────────────────");
    }

    protected void printTraining(Training t) {
        System.out.println("\n┌─── Training ─────────────────────");
        System.out.println("│ ID         : " + t.getId());
        System.out.println("│ Trainee ID : " + t.getTraineeId());
        System.out.println("│ Trainer ID : " + t.getTrainerId());
        System.out.println("│ Name       : " + t.getTrainingName());
        System.out.println("│ Type       : " + t.getTrainingType().getTrainingTypeName());
        System.out.println("│ Date       : " + t.getTrainingDate());
        System.out.println("│ Duration   : " + t.getTrainingDuration() + " min");
        System.out.println("└──────────────────────────────────");
    }

    protected void printSubMenu(String entity) {
        System.out.println("\n┌─── " + entity + " ───────────────────");
        System.out.println("│ 1. Create");
        System.out.println("│ 2. Get");
        if (!entity.equals("TRAINING")) {
            System.out.println("│ 3. Update");
            if (entity.equals("TRAINEE")) {
                System.out.println("│ 4. Delete");
            }
        }
        System.out.println("│ 0. Back");
        System.out.println("└──────────────────────────────────");
        System.out.print("Select: ");
    }

    protected void printSuccess(String msg) { System.out.println("✓ " + msg); }
    protected void printError(String msg) { System.out.println("✗ Error: " + msg); }
}