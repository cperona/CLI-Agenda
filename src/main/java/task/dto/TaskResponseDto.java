package task.dto;

import task.model.Priority;
import java.time.LocalDateTime;

public record TaskResponseDto(
        int id,
        String title,
        String description,
        LocalDateTime deadline,
        Priority priority,
        boolean isCompleted,
        LocalDateTime createdAt,
        Integer eventId
) {}