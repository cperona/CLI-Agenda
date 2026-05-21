package note.service;

import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoteService {
    NoteResponseDTO insertNote(NoteRequestDTO note);

    NoteResponseDTO updateNote(NoteRequestDTO note,int id);

    List<NoteResponseDTO> selectAllNotes();

    NoteResponseDTO selectNoteById(int id);

    void deleteById(int id);

    boolean existsById(int id);

    List<NoteResponseDTO> findByTaskId(int taskId);

    void titleValidation(String title);

    void descriptionValidation(String description);

    Optional<NoteResponseDTO> findById(int id);
}
