package event.dto;

import java.time.LocalDate;

public record EventRequestDTO(
    String title,
    String description,
    LocalDate event_date,
    Boolean recurring){
}
