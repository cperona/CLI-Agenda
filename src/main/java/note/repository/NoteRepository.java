package note.repository;

import note.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    Note save(Note note);
    void update(Note note);
    List<Note> findAll();
    Optional<Note> findById(int id);
    void delete(int id);
    void deleteAll();
    boolean existsById(int id);
    List<Note> findByTaskId(int taskId);
}
