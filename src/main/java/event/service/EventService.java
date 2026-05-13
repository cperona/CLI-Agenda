package event.service;

import common.exception.EventIdDoesNotExists;
import event.model.Event;
import event.repository.EventRepository;

import java.util.List;
import java.util.Optional;

public class EventService {
    private EventRepository eventRepository;
    public EventService(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    public Event insertEvent(Event event)
    {
        return eventRepository.save(event);
    }

    public List<Event> selectAllEvents()
    {
        return eventRepository.findAll();
    }

    public Event selectEventById(int id)
    {
        Optional<Event> result = eventRepository.findById(id);
        if(result.isEmpty())
        {
            throw new EventIdDoesNotExists();
        }
        return result.get();
    }

    public void close()
    {
        eventRepository.close();
    }
}
