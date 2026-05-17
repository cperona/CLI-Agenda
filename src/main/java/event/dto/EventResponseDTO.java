package event.dto;

import java.time.LocalDate;

public record EventResponseDTO(
        int id,
        String title,
        String description,
        LocalDate event_date,
        Boolean recurring) {
}
