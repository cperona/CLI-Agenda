package note.repository;

import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import note.model.Note;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.model.Priority;
import task.model.Task;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class NoteRepositoryMysqlTest {
    private NoteRepository noteRepository;
    private TaskRepository taskRepository;
    private EventRepository eventRepository;

    private int createEventAndTask() {
        Event event = new Event("titulo", "descripcion", LocalDate.now(), false);
        Event insertedEvent = eventRepository.save(event);
        int idEvent = insertedEvent.getId();
        Task task = new Task();
        task.setTitle("Test Save Task");
        task.setDescription("testing");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.HIGH);
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(idEvent);
        Task saved = taskRepository.save(task);
        return saved.getId();
    }

    @BeforeEach
    public void deleteAll() {
        noteRepository = new NoteRepositoryMysql();
        noteRepository.deleteAll();
        taskRepository = new TaskRepositoryMysql();
        taskRepository.deleteAll();
        eventRepository = new EventRepositoryMysql();
        eventRepository.deleteAll();
    }

    @Test
    public void insertNoteTest() {
        int idTask = createEventAndTask();
        Note note = new Note("Description", LocalDate.now(), idTask);
        Note inserted = noteRepository.save(note);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Description", found.get().getDescription());
        Assertions.assertEquals(idTask, found.get().getTask_id());
    }

    @Test
    public void updateNoteTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note inserted = noteRepository.save(note);
        inserted.setDescription("modified");
        noteRepository.update(inserted);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("modified", found.get().getDescription());
        Assertions.assertEquals(idTask, found.get().getTask_id());
    }

    @Test
    public void findByIdTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note inserted = noteRepository.save(note);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("description", found.get().getDescription());
        Assertions.assertEquals(idTask, found.get().getTask_id());
    }

    @Test
    public void findAllTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note noteB = new Note("description2", LocalDate.now(), idTask);
        noteRepository.save(note);
        noteRepository.save(noteB);
        List<Note> notes = noteRepository.findAll();
        Assertions.assertEquals(2, notes.size());
        Assertions.assertEquals("description", notes.get(0).getDescription());
        Assertions.assertEquals("description2", notes.get(1).getDescription());
        Assertions.assertEquals(idTask, notes.get(0).getTask_id());
        Assertions.assertEquals(idTask, notes.get(1).getTask_id());
    }

    @Test
    public void existsByIdTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note inserted = noteRepository.save(note);
        boolean found = noteRepository.existsById(inserted.getId());
        Assertions.assertTrue(found);
    }

    @Test
    public void deleteTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note inserted = noteRepository.save(note);
        noteRepository.delete(inserted.getId());
        boolean existing = noteRepository.existsById(inserted.getId());
        Assertions.assertFalse(existing);
    }

    @Test
    public void findAllByTaskIdAfterTest() {
        int idTask = createEventAndTask();
        Note note = new Note("description", LocalDate.now(), idTask);
        Note noteB = new Note("description2", LocalDate.now(), idTask);
        noteRepository.save(note);
        noteRepository.save(noteB);
        List<Note> notes = noteRepository.findByTaskId(idTask);
        Assertions.assertEquals(2, notes.size());
        Assertions.assertEquals("description", notes.get(0).getDescription());
        Assertions.assertEquals("description2", notes.get(1).getDescription());
        Assertions.assertEquals(idTask, notes.get(0).getTask_id());
        Assertions.assertEquals(idTask, notes.get(1).getTask_id());
    }
}
