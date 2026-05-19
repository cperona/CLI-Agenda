package note.dto;

import java.time.LocalDate;

public record NoteResponseDTO(
        int id,
        String description,
        LocalDate created_at,
        int task_id) {
}
