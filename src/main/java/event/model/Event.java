package event.model;

import java.time.LocalDate;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDate event_date;
    private Boolean recurring;

    public Event(int id, String title, String description, LocalDate event_date, Boolean recurring) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.event_date = event_date;
        this.recurring = recurring;
    }

    public Event(String title, String description, LocalDate event_date, Boolean recurring) {
        this.title = title;
        this.description = description;
        this.event_date = event_date;
        this.recurring = recurring;
    }

    public Event()
    {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDate event_date) {
        this.event_date = event_date;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }
}
