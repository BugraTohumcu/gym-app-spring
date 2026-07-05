package org.bugra.ui;

import org.bugra.model.Trainee;
import org.bugra.model.Trainer;
import org.bugra.model.Training;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public abstract class BaseUI {

    protected final Scanner scanner = new Scanner(System.in);

    // ─── INPUT HELPERS ────────────────────────────────────────

    protected String readNonBlank(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) return value;
            printError(fieldName + " cannot be blank.");
        }
    }

    protected String readName(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (value.isBlank()) { printError(fieldName + " cannot be blank."); continue; }
            if (!value.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                printError(fieldName + " can only contain letters.");
                continue;
            }
            return value;
        }
    }

    protected String readAddress(String fieldName) {
        while (true) {
            System.out.print(fieldName + " : ");
            String value = scanner.nextLine().trim();
            if (value.isBlank()) { printError(fieldName + " cannot be blank."); continue; }
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
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
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
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                printError(fieldName + " must be a positive number.");
            } catch (NumberFormatException e) {
                printError(fieldName + " must be a valid number.");
            }
        }
    }

    protected LocalDate readPastDate(String fieldName) {
        while (true) {
            System.out.print(fieldName + " (YYYY-MM-DD) : ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) { printError(fieldName + " cannot be blank."); continue; }
            if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                printError(fieldName + " must be in YYYY-MM-DD format.");
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isAfter(LocalDate.now())) {
                    printError(fieldName + " cannot be in the future.");
                    continue;
                }
                if (date.isBefore(LocalDate.of(1900, 1, 1))) {
                    printError(fieldName + " cannot be before 1900-01-01.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                printError(fieldName + " is not a valid date.");
            }
        }
    }

    protected LocalDate readFutureOrTodayDate(String fieldName) {
        while (true) {
            System.out.print(fieldName + " (YYYY-MM-DD) : ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) { printError(fieldName + " cannot be blank."); continue; }
            if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                printError(fieldName + " must be in YYYY-MM-DD format.");
                continue;
            }
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    printError(fieldName + " cannot be in the past.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                printError(fieldName + " is not a valid date.");
            }
        }
    }

    // ─── PRINT HELPERS ────────────────────────────────────────

    protected void printTrainee(Trainee t) {
        System.out.println("\n┌─── Trainee ─────────────────────");
        System.out.println("│ ID         : " + t.getId());
        System.out.println("│ First Name : " + t.getUser().getFirstName());
        System.out.println("│ Last Name  : " + t.getUser().getLastName());
        System.out.println("│ Username   : " + t.getUser().getUsername());
        System.out.println("│ Password   : " + t.getUser().getPassword());
        System.out.println("│ Active     : " + t.getUser().isActive());
        System.out.println("│ Birth Date : " + t.getDateOfBirth());
        System.out.println("│ Address    : " + t.getAddress());
        System.out.println("└──────────────────────────────────");
    }

    protected void printTrainer(Trainer t) {
        System.out.println("\n┌─── Trainer ──────────────────────────");
        System.out.println("│ ID             : " + t.getId());
        System.out.println("│ First Name     : " + t.getUser().getFirstName());
        System.out.println("│ Last Name      : " + t.getUser().getLastName());
        System.out.println("│ Username       : " + t.getUser().getUsername());
        System.out.println("│ Password       : " + t.getUser().getPassword());
        System.out.println("│ Active         : " + t.getUser().isActive());
        System.out.println("│ Specialization : " + t.getSpecialization().getTrainingTypeName());
        System.out.println("└──────────────────────────────────────");
    }

    protected void printTraining(Training t) {
        System.out.println("\n┌─── Training ─────────────────────");
        System.out.println("│ ID         : " + t.getId());
        System.out.println("│ Trainee    : " + t.getTrainee().getUser().getUsername());
        System.out.println("│ Trainer    : " + t.getTrainer().getUser().getUsername());
        System.out.println("│ Name       : " + t.getTrainingName());
        System.out.println("│ Type       : " + t.getTrainingType().getTrainingTypeName());
        System.out.println("│ Date       : " + t.getTrainingDate());
        System.out.println("│ Duration   : " + t.getTrainingDuration() + " min");
        System.out.println("└──────────────────────────────────");
    }

    protected void printSuccess(String msg) {
        System.out.println("✓ " + msg);
    }

    protected void printError(String msg) {
        System.out.println("✗ Error: " + msg);
    }
}