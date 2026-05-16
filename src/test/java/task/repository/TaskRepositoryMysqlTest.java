package task.repository;

import common.persistence.DatabaseConnection;
import org.junit.jupiter.api.*;
import task.model.Priority;
import task.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryMysqlTest {

    private TaskRepositoryMysql repository;

    @BeforeEach
    void initRepository() {
        repository = new TaskRepositoryMysql();
    }

    // Clear the Task table before tests while preserving resulting test data for manual verification.
    @BeforeAll
    static void setUp() throws SQLException {
        Connection conn = DatabaseConnection
                .getInstance()
                .getConnection();

        try (PreparedStatement ps =
                     conn.prepareStatement("DELETE FROM task")) {

            ps.executeUpdate();
        }
    }

    @Test
    void shouldSaveTask() {
        Task task = new Task();
        task.setTitle("Test Save Task");
        task.setDescription("testing");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.HIGH);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = repository.save(task);

        assertNotNull(saved.getId());
        assertEquals("Test Save Task", saved.getTitle());
    }

    @Test
    void shouldFindTaskById() {
        Task task = new Task();
        task.setTitle("Find by Id");
        task.setDescription("Testing findById");
        LocalDateTime deadline = LocalDateTime.now().plusDays(2).withNano(0);
        task.setDeadline(deadline);
        task.setPriority(Priority.MEDIUM);
        task.setIsCompleted(false);
        LocalDateTime createdAt = LocalDateTime.now().withNano(0);
        task.setCreatedAt(createdAt);

        Task saved = repository.save(task);

        Optional<Task> result = repository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Find by Id", result.get().getTitle());
        assertEquals("Testing findById", result.get().getDescription());
        assertEquals(deadline, result.get().getDeadline());
        assertEquals(Priority.MEDIUM, result.get().getPriority());
        assertFalse(result.get().isCompleted());
        assertEquals(createdAt, result.get().getCreatedAt());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task();
        task.setTitle("Old title");
        task.setDescription("Old description");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = repository.save(task);

        saved.setTitle("New title");

        repository.update(saved);

        Optional<Task> updated = repository.findById(saved.getId());

        assertTrue(updated.isPresent());
        assertEquals("New title", updated.get().getTitle());
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task();
        task.setTitle("Delete me");
        task.setDescription("Delete test");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = repository.save(task);

        repository.delete(saved.getId());

        Optional<Task> deleted = repository.findById(saved.getId());

        assertFalse(deleted.isPresent());
    }
}