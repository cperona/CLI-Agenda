package task.service;

import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    TaskResponseDto toDto(Task t);
    Task fromRequest(TaskRequestDto dto);

    TaskResponseDto createTask(TaskRequestDto dto);

    Optional<TaskResponseDto> findById(int id);

    TaskResponseDto updateTask(int id, TaskRequestDto dto);

    void deleteTask(int id);

    void markCompleted(int id);

    List<TaskResponseDto> listAll();

    List<TaskResponseDto> listByPriority(Priority priority);

    List<TaskResponseDto> listPending();

    List<TaskResponseDto> listCompleted();

    List<TaskResponseDto> listUpcoming();

    List<TaskResponseDto> listByEvent(int eventId);
}
