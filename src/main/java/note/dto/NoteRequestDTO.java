package note.dto;

import java.time.LocalDate;

public record NoteRequestDTO(
        String description,
        LocalDate created_at,
        int task_id) {
}
