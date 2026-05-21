package event.service;

import common.exception.EventIdDoesNotExists;
import common.exception.TaskNotFoundException;
import event.Observer.EventSubject;
import event.Observer.EventObserver;
import event.dto.EventMapper;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.model.Event;
import event.repository.EventRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EventServiceImpl implements EventService, EventSubject {
    private final EventRepository eventRepository;
    private final List<EventObserver> observers = new ArrayList<>();

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventResponseDTO insertEvent(EventRequestDTO event) {

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
    public List<EventResponseDTO> selectAllEvents() {
        return eventRepository.findAll().stream().map(EventMapper::toDTO).toList();
    }

    @Override
    public EventResponseDTO selectEventById(int id) {
        Optional<Event> result = eventRepository.findById(id);
        if (result.isEmpty()) {
            throw new EventIdDoesNotExists();
        }
        return EventMapper.toDTO(result.get());
    }

    @Override
    public void deleteById(int id) {
        eventRepository.findById(id)
                .orElseThrow(EventIdDoesNotExists::new);
        eventRepository.delete(id);
    }

    @Override
    public Optional<EventResponseDTO> findById(int id) {
        return eventRepository.findById(id).map(EventMapper::toDTO);
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

    @Override
    public List<EventResponseDTO> findByUpcoming(int days) {
        List<Event> events = eventRepository.findUpcoming(days);
        return events.stream().map(EventMapper::toDTO).toList();
    }

    // ----------------EVENT-VALIDATIONS----------------

    public void titleValidation(String title) {
        int maxLength = 80;
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title is empty.");
        if (title.length() > maxLength)
            throw new IllegalArgumentException("Title is too long. Max " + maxLength + " characters.");
    }

    public void descriptionValidation(String description) {
        int maxLength = 255;
        if (description.length() > maxLength)
            throw new IllegalArgumentException("Description is too long. Max " + maxLength + " characters.");
    }

    // -----------------EVENT-OBSERVER------------------

    @Override
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean notifyObservers() {
        List<EventResponseDTO> upcoming = findByUpcoming(7);
        if (upcoming.isEmpty()) return false;

        LocalDate today = LocalDate.now();
        List<Map.Entry<EventResponseDTO, Integer>> eventosConDias = new ArrayList<>();

        for (EventResponseDTO event : upcoming) {
            LocalDate eventDay = event.event_date();
            if (event.recurring()) {
                eventDay = eventDay.withYear(today.getYear());
                if (eventDay.isBefore(today)) {
                    eventDay = eventDay.plusYears(1);
                }
            }
            int daysUntil = (int) ChronoUnit.DAYS.between(today, eventDay);
            if (daysUntil >= 0 && daysUntil <= 7) {
                eventosConDias.add(Map.entry(event, daysUntil));
            }
        }
        // ASC order
        eventosConDias.sort(Comparator.comparingInt(Map.Entry::getValue));

        // Notify by order
        for (Map.Entry<EventResponseDTO, Integer> entry : eventosConDias) {
            for (EventObserver observer : observers) {
                observer.onEventAlert(entry.getKey(), entry.getValue());
            }
        }
        return true;
    }
}
