package note.service;

import common.exception.EventIdDoesNotExists;
import common.exception.NoteIdDoesNotExists;
import event.dto.EventMapper;
import event.model.Event;
import event.repository.EventRepository;
import note.dto.NoteMapper;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;
import note.model.Note;
import note.repository.NoteRepository;

import java.util.List;
import java.util.Optional;

public class NoteServiceImpl implements NoteService{
    private NoteRepository noteRespository;
    public NoteServiceImpl(NoteRepository noteRespository)
    {
        this.noteRespository = noteRespository;
    }

    @Override
    public NoteResponseDTO insertNote(NoteRequestDTO note) {
        return NoteMapper.toDTO(noteRespository.save(NoteMapper.toEntity(note)));
    }

    @Override
    public NoteResponseDTO updateNote(NoteRequestDTO note, int id) {
        Note noteEntity = NoteMapper.toEntity(note);
        noteEntity.setId(id);
        noteRespository.update(noteEntity);
        return NoteMapper.toDTO(noteEntity);
    }

    @Override
    public List<NoteResponseDTO> selectAllNotes() {
        return noteRespository.findAll().stream().map(NoteMapper::toDTO).toList();
    }

    @Override
    public NoteResponseDTO selectNoteById(int id) {
        Optional<Note> result = noteRespository.findById(id);
        if(result.isEmpty())
        {
            throw new NoteIdDoesNotExists();
        }
        return NoteMapper.toDTO(result.get());
    }

    @Override
    public void deleteById(int id) {
        noteRespository.delete(id);
    }

    @Override
    public boolean existsById(int id) {
        return noteRespository.existsById(id);
    }

    @Override
    public List<NoteResponseDTO> findByTaskId(int taskId) {
        return noteRespository.findByTaskId(taskId).stream().map(NoteMapper::toDTO).toList();
    }
}
