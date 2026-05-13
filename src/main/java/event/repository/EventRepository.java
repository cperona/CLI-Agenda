package event.repository;

import event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    List<Event> findAll();

    Optional<Event> findById(int id);
    void close();
}
