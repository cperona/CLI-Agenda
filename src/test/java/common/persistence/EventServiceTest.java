package common.persistence;

import event.model.Event;
import event.repository.EventRepositoryMysql;
import event.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class EventServiceTest {
    @Test
    public void insertEventTest()
    {
        EventService eventService = new EventService(new EventRepositoryMysql());
        Event event = new Event("titulo","description", LocalDate.now(),false);
        Event inserted = eventService.insertEvent(event);
        Assertions.assertNotNull(inserted.getId());
        eventService.close();
    }
}
