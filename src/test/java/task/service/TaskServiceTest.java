package task.service;

import common.exception.TaskNotFoundException;
import common.persistence.DatabaseConnection;
import org.junit.jupiter.api.*;
import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {

        TaskRepository repository = new TaskRepositoryMysql();
        taskService = new TaskService(repository);
    }

    @BeforeAll
    static void clearTaskTable() throws SQLException {

        Connection conn = DatabaseConnection
                .getInstance()
                .getConnection();

        try (PreparedStatement ps1 =
                     conn.prepareStatement("DELETE FROM task");

             PreparedStatement ps2 =
                     conn.prepareStatement(
                             "ALTER TABLE task AUTO_INCREMENT = 1"
                     )) {

            ps1.executeUpdate();
            ps2.executeUpdate();
        }
    }

    @Test
    void shouldCreateTask() {

        TaskRequestDto dto = new TaskRequestDto(
                "Create task title",
                "Test description",
                LocalDateTime.now().plusDays(1),
                Priority.HIGH,
                null
        );

        TaskResponseDto result = taskService.createTask(dto);

        assertNotNull(result);
        assertEquals("Create task title", result.title());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {

        TaskRequestDto dto = new TaskRequestDto(
                "",
                "Description",
                LocalDateTime.now().plusDays(1),
                Priority.MEDIUM,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleTooLong() {

        String longTitle = "a".repeat(81);

        TaskRequestDto dto = new TaskRequestDto(
                longTitle,
                "Description",
                LocalDateTime.now().plusDays(1),
                Priority.MEDIUM,
                1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {

        String longDescription = "a".repeat(256);

        TaskRequestDto dto = new TaskRequestDto(
                "Valid title",
                longDescription,
                LocalDateTime.now().plusDays(1),
                Priority.LOW,
                null
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenDeadlineIsBeforeCurrentDate() {

        TaskRequestDto dto = new TaskRequestDto(
                "Task title",
                "Task description",
                LocalDateTime.now().minusDays(1),
                Priority.HIGH,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(dto)
        );

        assertEquals(
                "The deadline cannot be earlier than the current date and time.",
                exception.getMessage()
        );
    }

    @Test
    void shouldFindTaskById() {

        TaskRequestDto dto = new TaskRequestDto(
                "Find me",
                "Description",
                LocalDateTime.now().plusDays(1),
                Priority.HIGH,
                null
        );

        TaskResponseDto created = taskService.createTask(dto);

        Optional<TaskResponseDto> result = taskService.findById(created.id());

        assertTrue(result.isPresent());
        assertEquals(created.id(), result.get().id());
    }

    @Test
    void shouldDeleteTask() {

        TaskRequestDto dto = new TaskRequestDto(
                "Delete me",
                "Description",
                LocalDateTime.now().plusDays(1),
                Priority.LOW,
                null
        );

        TaskResponseDto created = taskService.createTask(dto);

        taskService.deleteTask(created.id());

        var deleted = taskService.findById(created.id());

        assertTrue(deleted.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTask(99999)
        );
    }

    @Test
    void shouldUpdateTask() {

        TaskRequestDto dto = new TaskRequestDto(
                "Old title",
                "Old description",
                LocalDateTime.now().plusDays(1),
                Priority.LOW,
                null
        );

        TaskResponseDto created = taskService.createTask(dto);

        TaskRequestDto updatedDto = new TaskRequestDto(
                "New title (edited)",
                "New description",
                LocalDateTime.now().plusDays(2),
                Priority.HIGH,
                null
        );

        TaskResponseDto updated =
                taskService.updateTask(created.id(), updatedDto);

        assertEquals("New title (edited)", updated.title());
        assertEquals("New description", updated.description());
        assertEquals(Priority.HIGH, updated.priority());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingTask() {

        TaskRequestDto dto = new TaskRequestDto(
                "Title",
                "Description",
                LocalDateTime.now().plusDays(1),
                Priority.MEDIUM,
                null
        );

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTask(99999, dto)
        );
    }

    @Test
    void shouldReturnMediumPriorityWhenPriorityIsNull() {

        TaskRequestDto dto = new TaskRequestDto(
                "Medium priority default",
                "Test description",
                LocalDateTime.now().plusDays(1),
                null,
                null
        );

        TaskResponseDto result = taskService.createTask(dto);

        assertNotNull(result);
        assertEquals(Priority.MEDIUM, result.priority());
    }
}