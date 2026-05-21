package event.service;

import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {
    EventResponseDTO insertEvent(EventRequestDTO event);

    EventResponseDTO updateEvent(EventRequestDTO event, int id);

    List<EventResponseDTO> selectAllEvents();

    EventResponseDTO selectEventById(int id);

    void deleteById(int id);

    Optional<EventResponseDTO> findById(int id);

    boolean existsById(int id);

    List<EventResponseDTO> findAllByDateAfter(LocalDate date);

    List<EventResponseDTO> findByUpcoming(int days);
}
