package application.config;

import common.persistence.DatabaseConnection;
import event.Observer.EventNotificationConsole;
import event.cli.EventMenu;
import event.repository.EventRepository;
import event.repository.EventRepositoryMysql;
import event.service.EventService;
import event.service.EventServiceImpl;
import note.repository.NoteRepository;
import note.repository.NoteRepositoryMysql;
import note.service.NoteService;
import note.service.NoteServiceImpl;
import task.cli.TaskMenu;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;
import task.service.TaskService;
import task.service.TaskServiceImpl;
import java.sql.Connection;

public class DependencyConfig {

    Connection connection;

    private final TaskService taskServiceImpl;
    private final NoteService noteServiceImpl;
    private final EventService eventServiceImpl;

    public DependencyConfig() {

        connection = DatabaseConnection.getInstance().getConnection();

        TaskRepository taskRepository  = new TaskRepositoryMysql();
        NoteRepository noteRepository  = new NoteRepositoryMysql();
        EventRepository eventRepository = new EventRepositoryMysql();

        this.taskServiceImpl = new TaskServiceImpl(taskRepository);
        this.noteServiceImpl  = new NoteServiceImpl(noteRepository);
        this.eventServiceImpl = new EventServiceImpl(eventRepository);

        this.eventServiceImpl.addObserver(new EventNotificationConsole());
    }

    public TaskMenu  buildTaskMenu()      { return new TaskMenu(taskServiceImpl); }

    public EventMenu buildEventMenu() { return new EventMenu(eventServiceImpl);};
//    public NoteMenu  buildNoteMenu()      { return new NoteMenu(noteService); }
//    public EventMenu buildEventMenu()     { return new EventMenu(eventService, taskService); }

    public boolean notifyUpcomingEvents() {
        return eventServiceImpl.notifyObservers();
    }
}
