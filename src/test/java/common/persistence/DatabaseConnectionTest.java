package common.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @AfterEach
    void tearDown() {
        DatabaseConnection.getInstance().close();
    }

    @Test
    void shouldReturnSameSingletonInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        assertSame(instance1, instance2,
                "DatabaseConnection should return the same singleton instance");
    }

    @Test
    void shouldReturnValidConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        Connection connection = dbConnection.getConnection();

        assertNotNull(connection,
                "Connection should not be null");
    }

    @Test
    void shouldCloseConnection() throws Exception {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        Connection connection = dbConnection.getConnection();

        dbConnection.close();

        assertTrue(connection.isClosed(),
                "Connection should be closed");
    }
}