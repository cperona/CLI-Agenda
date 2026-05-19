package task.service;

import common.exception.TaskNotFoundException;
import common.persistence.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.model.Task;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskRequestDto requestDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1);
        task.setTitle("Test task");
        task.setDescription("Test description");
        task.setDeadline(LocalDateTime.of(2027, 1, 10, 12, 0));
        task.setPriority(Priority.HIGH);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2027, 1, 1, 10, 0));
        task.setEventId(5);

        requestDto = new TaskRequestDto("Test task", "Test description", LocalDateTime.of(2027, 1, 10, 12, 0), Priority.HIGH, 5);
    }

    @Test
    void createTaskShouldSaveAndReturnDto() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(1);
            return t;
        });

        TaskResponseDto result = taskService.createTask(requestDto);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("Test task", result.title());
        assertEquals("Test description", result.description());
        assertEquals(Priority.HIGH, result.priority());
        assertFalse(result.isCompleted());
        assertEquals(5, result.eventId());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertEquals("Test task", saved.getTitle());
        assertEquals("Test description", saved.getDescription());
        assertEquals(Priority.HIGH, saved.getPriority());
        assertFalse(saved.isCompleted());
        assertEquals(5, saved.getEventId());
    }

    @Test
    void findByIdShouldReturnDtoWhenExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        Optional<TaskResponseDto> result = taskService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().id());
        assertEquals("Test task", result.get().title());
        verify(taskRepository).findById(1);
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        Optional<TaskResponseDto> result = taskService.findById(1);

        assertTrue(result.isEmpty());
        verify(taskRepository).findById(1);
    }

    @Test
    void updateTaskShouldModifyAndCallRepositoryUpdate() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskRequestDto updateDto = new TaskRequestDto("Updated title", "Updated description", LocalDateTime.of(2027, 2, 1, 12, 0), Priority.LOW, 7);

        TaskResponseDto result = taskService.updateTask(1, updateDto);

        assertNotNull(result);
        assertEquals("Updated title", result.title());
        assertEquals("Updated description", result.description());
        assertEquals(Priority.LOW, result.priority());
        assertEquals(7, result.eventId());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).update(captor.capture());
        Task updated = captor.getValue();
        assertEquals(1, updated.getId());
        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(Priority.LOW, updated.getPriority());
        assertEquals(7, updated.getEventId());
    }

    @Test
    void deleteTaskShouldThrowWhenTaskDoesNotExist() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1));

        verify(taskRepository).findById(1);
        verify(taskRepository, never()).delete(anyInt());
    }

    @Test
    void markCompletedShouldSetTaskCompletedAndUpdate() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.markCompleted(1);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).update(captor.capture());
        assertTrue(captor.getValue().isCompleted());
    }

    @Test
    void listAllShouldReturnMappedDtos() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listAll();

        assertEquals(1, result.size());
        assertEquals("Test task", result.get(0).title());
        verify(taskRepository).findAll();
    }

    @Test
    void listByPriorityShouldFilterAndMapDtos() {
        when(taskRepository.findByPriority(Priority.HIGH)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listByPriority(Priority.HIGH);

        assertEquals(1, result.size());
        assertEquals(Priority.HIGH, result.get(0).priority());
        verify(taskRepository).findByPriority(Priority.HIGH);
    }

    @Test
    void listPendingShouldUseCompletedFalse() {
        when(taskRepository.findByCompleted(false)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listPending();

        assertEquals(1, result.size());
        verify(taskRepository).findByCompleted(false);
    }

    @Test
    void listCompletedShouldUseCompletedTrue() {
        task.setIsCompleted(true);
        when(taskRepository.findByCompleted(true)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listCompleted();

        assertEquals(1, result.size());
        verify(taskRepository).findByCompleted(true);
    }

    @Test
    void listUpcomingShouldReturnMappedDtos() {
        when(taskRepository.findUpcoming()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listUpcoming();

        assertEquals(1, result.size());
        verify(taskRepository).findUpcoming();
    }

    @Test
    void listByEventShouldReturnMappedDtos() {
        when(taskRepository.findByEventId(5)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.listByEvent(5);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).eventId());
        verify(taskRepository).findByEventId(5);
    }
}