package event.service;

import event.Observer.EventObserver;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventResponseDTO insertEvent(EventRequestDTO event);

    EventResponseDTO updateEvent(EventRequestDTO event, int id);

    List<EventResponseDTO> selectAllEvents();

    EventResponseDTO selectEventById(int id);

    void deleteById(int id);

    boolean existsById(int id);

    List<EventResponseDTO> findAllByDateAfter(LocalDate date);

    List<EventResponseDTO> findByUpcoming(int days);

    void titleValidation(String title);

    void descriptionValidation(String description);

    void validateeventDate(LocalDate eventDate);

    void addObserver(EventObserver observer);

    void removeObserver(EventObserver observer);

    boolean notifyObservers();
}
