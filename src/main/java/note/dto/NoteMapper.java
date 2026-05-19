package note.dto;

import note.model.Note;

public class NoteMapper {
    public static NoteResponseDTO toDTO (Note note)
    {
        return new NoteResponseDTO(note.getId(),note.getDescription(),note.getCreated_at(),note.getTask_id());
    }

    public static Note toEntity(NoteRequestDTO dto)
    {
        Note note = new Note();
        note.setDescription(dto.description());
        note.setCreated_at(dto.created_at());
        note.setTask_id(dto.task_id());
        return note;
    }

    public static void updateEntity(Note note,NoteRequestDTO dto)
    {
        note.setDescription(dto.description());
        note.setCreated_at(dto.created_at());
        note.setTask_id(dto.task_id());
    }
}
