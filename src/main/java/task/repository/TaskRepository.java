package task.repository;

import task.model.Task;

import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(int id);

    void update(Task task);

    void delete(int id);
}
