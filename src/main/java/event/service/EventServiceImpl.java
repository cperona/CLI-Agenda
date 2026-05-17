package event.service;

import common.exception.EventIdDoesNotExists;
import event.dto.EventMapper;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.model.Event;
import event.repository.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EventServiceImpl implements EventService{
    private EventRepository eventRepository;
    public EventServiceImpl(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventResponseDTO insertEvent(EventRequestDTO event)
    {

        return EventMapper.toDTO(eventRepository.save(EventMapper.toEntity(event)));
    }

    @Override
    public EventResponseDTO updateEvent(EventRequestDTO event, int id) {
        Event eventEntity = EventMapper.toEntity(event);
        eventEntity.setId(id);
        eventRepository.update(eventEntity);
        return EventMapper.toDTO(eventEntity);
    }

    @Override
    public List<EventResponseDTO> selectAllEvents()
    {
        return eventRepository.findAll().stream().map(EventMapper::toDTO).toList();
    }

    @Override
    public EventResponseDTO selectEventById(int id)
    {
        Optional<Event> result = eventRepository.findById(id);
        if(result.isEmpty())
        {
            throw new EventIdDoesNotExists();
        }
        return EventMapper.toDTO(result.get());
    }

    @Override
    public void deleteById(int id)
    {
        eventRepository.delete(id);
    }

    @Override
    public boolean existsById(int id) {
        return eventRepository.existsById(id);
    }

    @Override
    public List<EventResponseDTO> findAllByDateAfter(LocalDate date) {
        List<Event> events = eventRepository.findAllByDateAfter(date);
        return events.stream().map(EventMapper::toDTO).toList();
    }
}
