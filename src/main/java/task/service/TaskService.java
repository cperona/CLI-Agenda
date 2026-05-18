package task.service;

import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    public TaskResponseDto toDto(Task t);
    public Task fromRequest(TaskRequestDto dto);

    public TaskResponseDto createTask(TaskRequestDto dto);

    public Optional<TaskResponseDto> findById(int id);

    public TaskResponseDto updateTask(int id, TaskRequestDto dto);

    public void deleteTask(int id);

    public void markCompleted(int id);

    public List<TaskResponseDto> listAll();

    public List<TaskResponseDto> listByPriority(Priority priority);

    public List<TaskResponseDto> listPending();

    public List<TaskResponseDto> listCompleted();

    public List<TaskResponseDto> listUpcoming();

    public List<TaskResponseDto> listByEvent(int eventId);
}
