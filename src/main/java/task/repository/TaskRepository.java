package task.repository;

import task.model.Priority;
import task.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(int id);

    void update(Task task);

    void delete(int id);

    List<Task> findAll();

    List<Task> findByPriority(Priority priority);

    List<Task> findByCompleted(boolean isCompleted);

    List<Task> findUpcoming();

    List<Task> findByEventId(int eventId);
}
