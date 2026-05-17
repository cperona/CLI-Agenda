package event.service;

import common.exception.EventIdDoesNotExists;
import event.dto.EventMapper;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.model.Event;
import event.repository.EventRepository;

import java.util.List;
import java.util.Optional;

public class EventServiceImpl implements EventService{
    private EventRepository eventRepository;
    public EventServiceImpl(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    public EventResponseDTO insertEvent(EventRequestDTO event)
    {

        return EventMapper.toDTO(eventRepository.save(EventMapper.toEntity(event)));
    }

    public List<EventResponseDTO> selectAllEvents()
    {
        return eventRepository.findAll().stream().map(EventMapper::toDTO).toList();
    }

    public EventResponseDTO selectEventById(int id)
    {
        Optional<Event> result = eventRepository.findById(id);
        if(result.isEmpty())
        {
            throw new EventIdDoesNotExists();
        }
        return EventMapper.toDTO(result.get());
    }

    public void close()
    {
        eventRepository.close();
    }
}
