package event.service;

import event.model.Event;
import event.repository.EventRepository;
public class EventService {
    private EventRepository eventRepository;
    public EventService(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    public Event insertEvent(Event event)
    {
        return eventRepository.insertEvent(event);
    }

    public void close()
    {
        eventRepository.close();
    }
}
