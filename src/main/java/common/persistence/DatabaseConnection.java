package common.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("agendadb.properties")) {

            if (input == null) {
                throw new RuntimeException("agendadb.properties not found in classpath");
            }

            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Could not load agendadb.properties: " + e.getMessage(), e);
        }

        try {
            this.connection = DriverManager.getConnection(
                    props.getProperty("agendadb.url"),
                    props.getProperty("agendadb.user"),
                    props.getProperty("agendadb.password")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to the database: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            //if (connection == null || connection.isClosed()) {
                // Si la conexión se cerró, recarga las propiedades y reconecta
                Properties props = new Properties();
                try (InputStream input = DatabaseConnection.class
                        .getClassLoader()
                        .getResourceAsStream("agendadb.properties")) {
                    props.load(input);
                }
                connection = DriverManager.getConnection(
                        props.getProperty("agendadb.url"),
                        props.getProperty("agendadb.user"),
                        props.getProperty("agendadb.password")
                );
           // }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Lost database connection: " + e.getMessage(), e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Warning: could not close DB connection: " + e.getMessage());
        }
    }
}