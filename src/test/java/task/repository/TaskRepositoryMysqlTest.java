package task.repository;

import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import note.repository.NoteRepository;
import note.repository.NoteRepositoryMysql;
import org.junit.jupiter.api.*;
import task.model.Priority;
import task.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryMysqlTest {

    private TaskRepository repository;
    private EventRepository eventRepository;
    private NoteRepository noteRepository;

    @BeforeEach
    public void deleteAll() {
        noteRepository = new NoteRepositoryMysql();
        noteRepository.deleteAll();
        repository = new TaskRepositoryMysql();
        repository.deleteAll();
        eventRepository = new EventRepositoryMysql();
        eventRepository.deleteAll();
    }

    private int createEvent() {
        Event event = new Event("titulo", "descripcion", LocalDate.now(), false);
        Event insertedEvent = eventRepository.save(event);
        int idEvent = insertedEvent.getId();
        return idEvent;
    }

    @Test
    void shouldSaveTask() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Test Save Task");
        task.setDescription("testing");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.HIGH);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        Task saved = repository.save(task);

        assertNotNull(saved.getId());
        assertEquals("Test Save Task", saved.getTitle());
        assertEquals(idEvent, saved.getEventId());
    }

    @Test
    void shouldFindTaskById() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Find by Id");
        task.setDescription("Testing findById");
        LocalDateTime deadline = LocalDateTime.now().plusDays(2).withNano(0);
        task.setDeadline(deadline);
        task.setPriority(Priority.MEDIUM);
        task.setIsCompleted(false);
        task.setEventId(idEvent);
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
        assertEquals(idEvent, result.get().getEventId());
    }

    @Test
    void shouldUpdateTask() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Old title");
        task.setDescription("Old description");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);

        Task saved = repository.save(task);

        saved.setTitle("New title");

        repository.update(saved);

        Optional<Task> updated = repository.findById(saved.getId());

        assertTrue(updated.isPresent());
        assertEquals("New title", updated.get().getTitle());
        assertEquals("Old description", updated.get().getDescription());
        assertEquals(idEvent, updated.get().getEventId());
    }

    @Test
    void shouldDeleteTask() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Delete me");
        task.setDescription("Delete test");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);

        Task saved = repository.save(task);

        repository.delete(saved.getId());

        Optional<Task> deleted = repository.findById(saved.getId());

        assertFalse(deleted.isPresent());
    }

    @Test
    void findByEventIdShouldReturnTasks() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Task1");
        task.setDescription("Description1");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        task = new Task();
        task.setTitle("Task2");
        task.setDescription("Description2");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        List<Task> tasks = repository.findByEventId(idEvent);
        assertEquals(2, tasks.size());
        assertEquals("Task1", tasks.get(0).getTitle());
        assertEquals("Task2", tasks.get(1).getTitle());
    }

    @Test
    void findUpcomingShouldReturnTasks() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Task1");
        task.setDescription("Description1");
        task.setDeadline(LocalDateTime.of(2027, 1, 2, 12, 0, 0));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2027, 1, 1, 12, 0, 0));
        task.setEventId(idEvent);
        repository.save(task);
        task = new Task();
        task.setTitle("Task2");
        task.setDescription("Description2");
        task.setDeadline(LocalDateTime.of(2027, 1, 3, 12, 0, 0));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2027, 1, 2, 12, 0, 0));
        task.setEventId(idEvent);
        repository.save(task);
        List<Task> tasks = repository.findUpcoming();
        assertEquals(2, tasks.size());
        assertEquals("Task1", tasks.get(0).getTitle());
        assertEquals("Task2", tasks.get(1).getTitle());
    }

    @Test
    void findByIsCompletedShouldReturnTasks() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Task1");
        task.setDescription("Description1");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(true);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        task = new Task();
        task.setTitle("Task2");
        task.setDescription("Description2");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(true);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        List<Task> tasks = repository.findByCompleted(true);
        assertEquals(2, tasks.size());
        assertEquals("Task1", tasks.get(0).getTitle());
        assertEquals("Task2", tasks.get(1).getTitle());
    }

    @Test
    public void findByPriorityTest() {
        int idEvent = createEvent();
        Task task = new Task();
        task.setTitle("Task1");
        task.setDescription("Description1");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(true);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        task = new Task();
        task.setTitle("Task2");
        task.setDescription("Description2");
        task.setDeadline(LocalDateTime.now().plusDays(5));
        task.setPriority(Priority.LOW);
        task.setIsCompleted(true);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        repository.save(task);
        List<Task> tasks = repository.findByPriority(Priority.LOW);
        assertEquals(2, tasks.size());
        assertEquals("Task1", tasks.get(0).getTitle());
        assertEquals("Task2", tasks.get(1).getTitle());
    }


}