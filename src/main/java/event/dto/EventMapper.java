package event.dto;

import event.model.Event;

public class EventMapper {
    public static EventResponseDTO toDTO(Event event){
        return new EventResponseDTO(event.getId(), event.getTitle(), event.getDescription(),event.getEvent_date(),event.getRecurring());
    }

    public static Event toEntity(EventRequestDTO dto){
        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setEvent_date(dto.event_date());
        event.setRecurring(dto.recurring());
        return event;
    }

    public static void updateEntity(Event event,EventRequestDTO dto){
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setEvent_date(dto.event_date());
        event.setRecurring(dto.recurring());
    }
}
