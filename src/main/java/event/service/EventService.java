package event.service;

import common.exception.EventIdDoesNotExists;
import event.dto.EventMapper;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.model.Event;
import event.repository.EventRepository;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventResponseDTO insertEvent(EventRequestDTO event);

    List<EventResponseDTO> selectAllEvents();

    EventResponseDTO selectEventById(int id);

    void close();
}
