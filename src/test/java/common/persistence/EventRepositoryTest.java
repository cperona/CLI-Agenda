package common.persistence;
import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRespositoryMysql;
import event.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class EventRepositoryTest {
    @Test
    public void insertEventTest()
    {
        EventRepository eventRepository = new EventRespositoryMysql();
        Event event = new Event("titulo","description", LocalDate.now(),false);
        Event inserted = eventRepository.insertEvent(event);
        Assertions.assertNotNull(inserted.getId());
        eventRepository.close();
    }
}
