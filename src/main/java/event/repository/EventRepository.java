package event.repository;

import event.model.Event;

public interface EventRepository {
    Event insertEvent(Event event);
    void close();
}
