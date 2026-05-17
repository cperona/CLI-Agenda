package common.persistence;

import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.repository.EventRepositoryMysql;
import event.service.EventService;
import event.service.EventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class EventServiceTest {
    @Test
    public void insertEventTest()
    {
        EventService eventService = new EventServiceImpl(new EventRepositoryMysql());
        EventRequestDTO event = new EventRequestDTO("titulo","descripcion",LocalDate.now(),false);

        EventResponseDTO inserted = eventService.insertEvent(event);
        Assertions.assertNotNull(inserted.id());
        eventService.close();
    }
}
