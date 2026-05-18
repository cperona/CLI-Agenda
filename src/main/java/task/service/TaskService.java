package task.service;

import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.model.Task;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskService {
    TaskResponseDto toDto(Task t);
    Task fromRequest(TaskRequestDto dto);
    TaskResponseDto createTask(TaskRequestDto dto);
    Optional<TaskResponseDto> findById(int id);
    TaskResponseDto updateTask(int id, TaskRequestDto dto);
    void deleteTask(int id);
    void titleValidation(String title);
    void descriptionValidation(String description);
    void validateDeadline(LocalDateTime deadline);
    Priority defaultPriorityIfNull(Priority priority);
}
