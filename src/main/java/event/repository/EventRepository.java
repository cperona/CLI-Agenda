package event.repository;

import event.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    void update(Event event);
    List<Event> findAll();
    Optional<Event> findById(int id);
    void delete(int id);
    void deleteAll();
    boolean existsById(int id);
    List<Event> findAllByDateAfter(LocalDate date);
    //boolean titleExists(String title);
    //List<Event> findByTitle(String title);
}
