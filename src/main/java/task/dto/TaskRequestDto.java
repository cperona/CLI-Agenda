package task.dto;

import task.model.Priority;
import java.time.LocalDateTime;

public record TaskRequestDto(
        String title,
        String description,
        LocalDateTime deadline,
        Priority priority,
        Integer eventId
) {}
