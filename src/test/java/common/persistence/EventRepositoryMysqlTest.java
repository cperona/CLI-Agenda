package common.persistence;
import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EventRepositoryMysqlTest {
    @BeforeEach
    public void deleteAll()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        eventRepository.deleteAll();

    }

    @Test
    public void insertEventTest()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event inserted = eventRepository.save(event);
        Assertions.assertNotNull(inserted.getId());
        Optional<Event> found = eventRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("titulo",found.get().getTitle());
        Assertions.assertEquals("descripcion",found.get().getDescription());
    }

    @Test
    public void updateEventTest(){
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event inserted = eventRepository.save(event);
        inserted.setTitle("Modificado");
        eventRepository.update(inserted);
        Optional<Event> found = eventRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Modificado",found.get().getTitle());
        Assertions.assertEquals("descripcion",found.get().getDescription());
    }

    @Test
    public void findByIdTest()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event inserted = eventRepository.save(event);
        Optional<Event> found = eventRepository.findById(inserted.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("titulo",found.get().getTitle());
        Assertions.assertEquals("descripcion",found.get().getDescription());
    }

    @Test
    public void findAllTest()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event eventB = new Event("titulo2","descripcion2", LocalDate.now(),false);
        eventRepository.save(event);
        eventRepository.save(eventB);
        List<Event> events = eventRepository.findAll();
        Assertions.assertEquals(2,events.size());
        Assertions.assertEquals("titulo",events.get(0).getTitle());
        Assertions.assertEquals("titulo2",events.get(1).getTitle());

    }

    @Test
    public void existsByIdTest()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event inserted = eventRepository.save(event);
        boolean found = eventRepository.existsById(inserted.getId());
        Assertions.assertTrue(found);
    }

    @Test
    public void deleteTest()
    {
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.now(),false);
        Event inserted = eventRepository.save(event);
        eventRepository.delete(inserted.getId());
        boolean existing = eventRepository.existsById(inserted.getId());
        Assertions.assertFalse(existing);
    }


    @Test
    public void findAllByDateAfterTest(){
        EventRepository eventRepository = new EventRepositoryMysql();
        Event event = new Event("titulo","descripcion", LocalDate.of(2026,1,1),false);
        Event inserted = eventRepository.save(event);
        List<Event> events = eventRepository.findAllByDateAfter(LocalDate.of(2025,1,1));
        Assertions.assertEquals(1,events.size());
        Assertions.assertEquals("titulo",events.get(0).getTitle());
    }
}
