package event.service;

import common.exception.EventIdDoesNotExists;
import event.dto.EventMapper;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.model.Event;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import event.service.EventService;
import event.service.EventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

   @Test
    public void insertEventShouldSaveAndReturnDto(){
       EventRequestDTO request = new EventRequestDTO("Concierto","Descripcion",LocalDate.now(),true);
       Event saved = EventMapper.toEntity(request);
       saved.setId(1);
       when(eventRepository.save(any(Event.class))).thenReturn(saved);
       EventResponseDTO result = eventService.insertEvent(request);

       Assertions.assertNotNull(result);
       Assertions.assertEquals(1,result.id());
       Assertions.assertEquals("Concierto",result.title());
       verify(eventRepository,times(1)).save(any(Event.class));
   }

   @Test
    public void updateEventShouldUpdateAndReturnDto(){
       int id = 1;
       EventRequestDTO request = new EventRequestDTO("Editado","Nueva descripcion",LocalDate.of(2026,1,1),false);
       Event existing = new Event();
       existing.setId(id);
       existing.setTitle("Original");
       existing.setDescription("Descripcion");
       existing.setEvent_date(LocalDate.now());
       existing.setRecurring(true);
       Event updated = EventMapper.toEntity(request);
       updated.setId(id);

       doNothing().when(eventRepository).update(any(Event.class));
       EventResponseDTO result = eventService.updateEvent(request,id);
       Assertions.assertNotNull(result);
       Assertions.assertEquals(id,result.id());
       Assertions.assertEquals("Editado",result.title());
       Assertions.assertEquals("Nueva descripcion",result.description());
       Assertions.assertEquals(LocalDate.of(2026,1,1),result.event_date());

       verify(eventRepository,times(1)).update(any(Event.class));
   }

   @Test
    public void selectAllEventsShouldReturnDtoList(){
       Event e1 = new Event();
       e1.setId(1);
       e1.setTitle("A");
       e1.setDescription("D1");
       e1.setEvent_date(LocalDate.of(2026,6,1));
       e1.setRecurring(false);

       Event e2 = new Event();
       e2.setId(2);
       e2.setTitle("B");
       e2.setDescription("D2");
       e2.setEvent_date(LocalDate.of(2026,6,2));
       e2.setRecurring(true);

       when(eventRepository.findAll()).thenReturn(List.of(e1,e2));
       List<EventResponseDTO> result = eventService.selectAllEvents();
       Assertions.assertEquals(2,result.size());
       Assertions.assertEquals("A",result.get(0).title());
       Assertions.assertEquals("B",result.get(1).title());
       verify(eventRepository,times(1)).findAll();
   }

   @Test
    public void selectEventByIdShouldReturnDtoWhenExists(){
       int id = 1;
       Event existing = new Event();
       existing.setId(id);
       existing.setTitle("Evento");
       existing.setDescription("Descripcion");
       existing.setEvent_date(LocalDate.of(2026,6,10));
       existing.setRecurring(false);
       when(eventRepository.findById(id)).thenReturn(Optional.of(existing));
       EventResponseDTO result = eventService.selectEventById(id);
       Assertions.assertNotNull(result);
       Assertions.assertEquals(id,result.id());
       Assertions.assertEquals("Evento", result.title());
       verify(eventRepository,times(1)).findById(id);
   }

   @Test
    public void selectEventByIdShouldThrowExceptionWhenNotFound(){
       int id = 999;
       when(eventRepository.findById(id)).thenReturn(Optional.empty());
       Assertions.assertThrows(EventIdDoesNotExists.class,()->eventService.selectEventById(id));
       verify(eventRepository,times(1)).findById(id);
   }

   @Test
    public void deleteByIdShouldDelegateToRepository(){
       int id = 1;
       doNothing().when(eventRepository).delete(id);
       eventService.deleteById(id);
       verify(eventRepository,times(1)).delete(id);
   }

   @Test
    public void existsByIdShouldReturnRepositoryValues(){
       int id = 1;
       when(eventRepository.existsById(id)).thenReturn(true);
       boolean result = eventService.existsById(id);
       Assertions.assertTrue(result);
       verify(eventRepository,times(1)).existsById(id);
   }

   @Test
    public void findAllByDateAfterShouldMapListToDto(){
       LocalDate date = LocalDate.of(2026,6,1);
       Event event = new Event();
       event.setId(1);
       event.setTitle("Evento");
       event.setDescription("Descripcion");
       event.setEvent_date(LocalDate.of(2026,6,10));
       event.setRecurring(false);
       when(eventRepository.findAllByDateAfter(date)).thenReturn(List.of(event));
       List<EventResponseDTO> result = eventService.findAllByDateAfter(date);
       Assertions.assertEquals(1,result.size());
       Assertions.assertEquals("Evento",result.get(0).title());
       verify(eventRepository,times(1)).findAllByDateAfter(date);
   }
}
