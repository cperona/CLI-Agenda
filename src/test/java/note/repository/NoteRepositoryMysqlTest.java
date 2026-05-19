package note.repository;

import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import note.model.Note;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Not;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class NoteRepositoryMysqlTest {
    @BeforeEach
    public void deleteAll() {
        NoteRepository noteRepository = new NoteRepositoryMysql();
        noteRepository.deleteAll();
        TaskRepository taskRepository = new TaskRepositoryMysql();
        taskRepository.deleteAll();
    }

    @Test
    public void insertNoteTest(){
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("Description", LocalDate.now(), 123);
        Note inserted = noteRepository.save(note);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Description", found.get().getDescription());
        Assertions.assertEquals(LocalDate.now(), found.get().getCreated_at());
        Assertions.assertEquals(123, found.get().getTask_id());
    }

    @Test
    public void updateNoteTest(){
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note inserted = noteRepository.save(note);
        inserted.setDescription("modified");
        noteRepository.update(inserted);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("modified",found.get().getDescription());
    }

    @Test
    public void findByIdTest() {
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note inserted = noteRepository.save(note);
        Optional<Note> found = noteRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("description", found.get().getDescription());
        Assertions.assertEquals(LocalDate.now(), found.get().getCreated_at());
    }

    @Test
    public void findAllTest()
    {
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note noteB = new Note("description2", LocalDate.now(),456);
        noteRepository.save(note);
        noteRepository.save(noteB);
        List<Note> notes = noteRepository.findAll();
        Assertions.assertEquals(2,notes.size());
        Assertions.assertEquals("description",notes.get(0).getDescription());
        Assertions.assertEquals("description2",notes.get(1).getDescription());
    }

    @Test
    public void existsByIdTest()
    {
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note inserted = noteRepository.save(note);
        boolean found = noteRepository.existsById(inserted.getId());
        Assertions.assertTrue(found);
    }

    @Test
    public void deleteTest()
    {
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note inserted = noteRepository.save(note);
        noteRepository.delete(inserted.getId());
        boolean existing = noteRepository.existsById(inserted.getId());
        Assertions.assertFalse(existing);
    }

    @Test
    public void findAllByTaskIdAfterTest(){
        NoteRepository noteRepository = new NoteRepositoryMysql();
        Note note = new Note("description", LocalDate.now(),123);
        Note noteB = new Note("description2", LocalDate.now(), 123);
        noteRepository.save(note);
        noteRepository.save(noteB);
        List<Note> notes = noteRepository.findByTaskId(123);
        Assertions.assertEquals(2,notes.size());
        Assertions.assertEquals("description",notes.get(0).getDescription());
        Assertions.assertEquals("description2",notes.get(1).getDescription());
    }


}
