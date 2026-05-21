package task.service;

import common.exception.TaskNotFoundException;
import task.repository.TaskRepository;
import task.model.*;
import task.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDto toDto(Task t) {
        return new TaskResponseDto(
                t.getId(), t.getTitle(), t.getDescription(),
                t.getDeadline(), t.getPriority(), t.isCompleted(),
                t.getCreatedAt(), t.getEventId()
        );
    }

    public Task fromRequest(TaskRequestDto dto) {
        Task task = new Task();

        titleValidation(dto.title());
        task.setTitle(dto.title());

        descriptionValidation(dto.description());
        task.setDescription(dto.description());

        validateDeadline(dto.deadline());
        task.setDeadline(dto.deadline());

        task.setPriority(defaultPriorityIfNull(dto.priority()));

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

        titleValidation(dto.title());
        existing.setTitle(dto.title());

        descriptionValidation(dto.description());
        existing.setDescription(dto.description());

        validateDeadline(dto.deadline());
        existing.setDeadline(dto.deadline());

        existing.setPriority(defaultPriorityIfNull(dto.priority()));
        existing.setEventId(dto.eventId());

        taskRepository.update(existing);
        return toDto(existing);
    }

    public void deleteTask(int id) {
        taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskRepository.delete(id);
    }

    public void markCompleted(int id) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        existing.setIsCompleted(true);
        taskRepository.update(existing);
    }

    public List<TaskResponseDto> listAll() {
        return taskRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<TaskResponseDto> listByPriority(Priority priority) {
        return taskRepository.findByPriority(priority).stream().map(this::toDto).toList();
    }

    public List<TaskResponseDto> listPending() {
        return taskRepository.findByCompleted(false).stream().map(this::toDto).toList();
    }

    public List<TaskResponseDto> listCompleted() {
        return taskRepository.findByCompleted(true).stream().map(this::toDto).toList();
    }

    public List<TaskResponseDto> listUpcoming() {
        return taskRepository.findUpcoming().stream().map(this::toDto).toList();
    }

    public List<TaskResponseDto> listByEvent(int eventId) {
        return taskRepository.findByEventId(eventId).stream().map(this::toDto).toList();
    }

    public void assignToEvent(int taskId, int eventId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + taskId));
        existing.setEventId(eventId);
        taskRepository.update(existing);
    }

    // ------------------- Validations -------------------------
    public void titleValidation(String title) {
        int maxLength = 80;
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title is empty.");
        if (title.length() > maxLength)
            throw new IllegalArgumentException("Title is too long. Max " + maxLength + " characters.");
    }

    public void descriptionValidation(String description) {
        int maxLength = 255;
        if (description.length() > maxLength)
            throw new IllegalArgumentException("Description is too long. Max " + maxLength + " characters.");
    }

    public void validateDeadline(LocalDateTime deadline) {
        if (deadline == null) return;
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "The deadline cannot be earlier than the current date and time."
            );
        }
    }

    public Priority defaultPriorityIfNull(Priority priority) {
        return priority == null ? Priority.MEDIUM : priority;
    }
}
