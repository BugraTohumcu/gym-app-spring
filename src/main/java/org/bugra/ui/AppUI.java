package org.bugra.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppUI extends BaseUI {

    private ConfigurableApplicationContext context;
    private final TraineeMenu traineeMenu;
    private final TrainerMenu trainerMenu;
    private final TrainingMenu trainingMenu;

    public AppUI(TraineeMenu traineeMenu, TrainerMenu trainerMenu, TrainingMenu trainingMenu) {
        this.traineeMenu = traineeMenu;
        this.trainerMenu = trainerMenu;
        this.trainingMenu = trainingMenu;
    }

    public void run() {

        // Gracefully shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("System is closing...");
            context.close();
        }));

        boolean running = true;
        printBanner();

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> traineeMenu.showMenu();
                case "2" -> trainerMenu.showMenu();
                case "3" -> trainingMenu.showMenu();
                case "0" -> {
                    running = false;
                }
                default -> printError("Invalid option!");
            }
        }
        System.out.println("\nGoodbye!");
    }

    private void printBanner() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║    GYM MANAGEMENT SYSTEM         ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    private void printMainMenu() {
        System.out.println("\n┌─── Main Menu ───────────────────");
        System.out.println("│ 1. Trainee Operations");
        System.out.println("│ 2. Trainer Operations");
        System.out.println("│ 3. Training Operations");
        System.out.println("│ 0. Exit");
        System.out.println("└──────────────────────────────────");
        System.out.print("Select: ");
    }

    @Autowired
    public void setContext(ConfigurableApplicationContext context) {
        this.context = context;
    }
}