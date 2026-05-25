package note.service;

import common.exception.NoteIdDoesNotExists;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;
import note.model.Note;
import note.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceImplTest {
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note note;
    private NoteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        note = new Note("description", LocalDate.of(2026, 5, 20), 10);
        note.setId(1);

        requestDTO = new NoteRequestDTO("description", LocalDate.of(2026, 5, 20), 10);
    }

    @Test
    void insertNoteShouldSaveAndReturnResponse() {
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note n = invocation.getArgument(0);
            n.setId(1);
            return n;
        });

        NoteResponseDTO result = noteService.insertNote(requestDTO);
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("description", result.description());
        assertEquals(LocalDate.of(2026, 5, 20), result.created_at());
        assertEquals(10, result.task_id());
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(captor.capture());
        Note saved = captor.getValue();
        assertEquals("description", saved.getDescription());
        assertEquals(LocalDate.of(2026, 5, 20), saved.getCreated_at());
        assertEquals(10, saved.getTask_id());
    }

    @Test
    void updateNoteShouldUpdateAndReturnResponse() {
        NoteRequestDTO updateDTO = new NoteRequestDTO(
                "modified",
                LocalDate.of(2026, 5, 21),
                10
        );

        NoteResponseDTO result = noteService.updateNote(updateDTO, 1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("modified", result.description());
        assertEquals(LocalDate.of(2026, 5, 21), result.created_at());
        assertEquals(10, result.task_id());

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).update(captor.capture());

        Note updated = captor.getValue();
        assertEquals(1, updated.getId());
        assertEquals("modified", updated.getDescription());
        assertEquals(LocalDate.of(2026, 5, 21), updated.getCreated_at());
        assertEquals(10, updated.getTask_id());
    }

    @Test
    void selectAllNotesShouldReturnMappedList() {
        when(noteRepository.findAll()).thenReturn(List.of(note));
        List<NoteResponseDTO> result = noteService.selectAllNotes();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals("description", result.get(0).description());
        verify(noteRepository).findAll();
    }

    @Test
    void selectNoteByIdShouldReturnMappedNote() {
        when(noteRepository.findById(1)).thenReturn(Optional.of(note));
        NoteResponseDTO result = noteService.selectNoteById(1);
        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals("description", result.description());
        assertEquals(LocalDate.of(2026, 5, 20), result.created_at());
        assertEquals(10, result.task_id());
        verify(noteRepository).findById(1);
    }

    @Test
    void deleteByIdShouldCallRepositoryDelete() {
        noteService.deleteById(1);
        verify(noteRepository).delete(1);
    }

    @Test
    void existsByIdShouldReturnTrueWhenExists() {
        when(noteRepository.existsById(1)).thenReturn(true);
        boolean result = noteService.existsById(1);
        assertTrue(result);
        verify(noteRepository).existsById(1);
    }

    @Test
    void existsByIdShouldReturnFalseWhenNotExists() {
        when(noteRepository.existsById(1)).thenReturn(false);
        boolean result = noteService.existsById(1);
        assertFalse(result);
        verify(noteRepository).existsById(1);
    }

    @Test
    void findByTaskIdShouldReturnMappedList() {
        when(noteRepository.findByTaskId(10)).thenReturn(List.of(note));
        List<NoteResponseDTO> result = noteService.findByTaskId(10);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).id());
        assertEquals(10, result.get(0).task_id());
        verify(noteRepository).findByTaskId(10);
    }

    @Test
    void findById_whenNoteExists_returnsNoteResponseDTO() {
        int id = 1;

        Note note = new Note();
        note.setId(id);
        note.setDescription("My note");
        note.setTask_id(999);

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));

        NoteResponseDTO result = noteService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("My note", result.description());
        assertEquals(999, result.task_id());
        verify(noteRepository).findById(id);
    }

    @Test
    void findById_whenNoteDoesNotExist_throwsNoteIdDoesNotExists() {
        int id = 99;

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteIdDoesNotExists.class,
                () -> noteService.findById(id));

        verify(noteRepository).findById(id);
    }

}
