package application.menu;

import application.config.DependencyConfig;

import java.util.Scanner;

public class MainMenu {

    private final DependencyConfig config;

    public MainMenu(DependencyConfig config) {
        this.config = config;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.println(" -----------Main-Menu----------");
            System.out.println("  1. Tasks");
            System.out.println("  2. Notes");
            System.out.println("  3. Events");
            System.out.println("  0. Exit");

            System.out.print("-Select an option: ");
            String option = sc.nextLine().trim();
            switch (option) {
                case "1" -> config.buildTaskMenu().showMenu();
//                case "2" -> config.buildNoteMenu().showMenu();
//                case "3" -> config.buildEventMenu().showMenu();
                case "0" -> running = false;
                default -> System.out.println("  Invalid option.");
            }
        }

        System.out.println("\n  Goodbye!\n");
    }
}
