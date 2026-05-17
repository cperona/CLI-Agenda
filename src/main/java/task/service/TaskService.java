package task.service;

import common.exception.TaskNotFoundException;
import task.repository.TaskRepository;
import task.model.*;
import task.dto.*;

import java.time.LocalDateTime;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    private TaskResponseDto toDto(Task t) {
        return new TaskResponseDto(
                t.getId(), t.getTitle(), t.getDescription(),
                t.getDeadline(), t.getPriority(), t.isCompleted(),
                t.getCreatedAt(), t.getEventId()
        );
    }

    private Task fromRequest(TaskRequestDto dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDeadline(dto.deadline());
        task.setPriority(dto.priority());
        task.setIsCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setEventId(dto.eventId());
        return task;
    }

    public TaskResponseDto createTask(TaskRequestDto dto) {
        return toDto(taskRepository.save(fromRequest(dto)));
    }

    public Optional<TaskResponseDto> findById(int id) {
        return taskRepository.findById(id).map(this::toDto);
    }

    public TaskResponseDto updateTask(int id, TaskRequestDto dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setDeadline(dto.deadline());
        existing.setPriority(dto.priority());
        existing.setEventId(dto.eventId());

        taskRepository.update(existing);
        return toDto(existing);
    }

    public void deleteTask(int id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskRepository.delete(id);
    }
}
