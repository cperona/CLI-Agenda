package application;

import application.config.DependencyConfig;
import application.menu.MainMenu;
import common.persistence.DatabaseConnection;

public class AgendaApp {

    static void main(String[] args) {
        System.out.println("|==============================|");
        System.out.println("|       CLI-AGENDA  v1.0       |");
        System.out.println("|==============================|");

        try {
            DependencyConfig config = new DependencyConfig();
            new MainMenu(config).run();

        } catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println("\n  x Fatal error: " + e.getMessage());
            System.err.println("  Make sure the Docker container is running: docker compose up -d");
        } finally {
            DatabaseConnection.getInstance().close();
        }
    }
}