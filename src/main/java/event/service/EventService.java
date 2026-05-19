package event.service;

import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventResponseDTO insertEvent(EventRequestDTO event);

    EventResponseDTO updateEvent(EventRequestDTO event, int id);

    List<EventResponseDTO> selectAllEvents();

    EventResponseDTO selectEventById(int id);

    void deleteById(int id);

    boolean existsById(int id);

    List<EventResponseDTO> findAllByDateAfter(LocalDate date);

}
