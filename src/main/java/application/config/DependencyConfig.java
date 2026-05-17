package application.config;

import common.persistence.DatabaseConnection;
import task.cli.TaskMenu;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;
import task.service.TaskService;
import java.sql.Connection;

public class DependencyConfig {

    Connection connection;

    private final TaskService  taskService;
//    private final NoteService noteService;
//    private final EventService eventService;

    public DependencyConfig() {

        connection = DatabaseConnection.getInstance().getConnection();

        TaskRepository taskRepository  = new TaskRepositoryMysql();
//        NoteRepository noteRepository  = new NoteRepositoryMysql();
//        EventRepository eventRepository = new EventRepositoryMysql();


        this.taskService  = new TaskService(taskRepository);
//        this.noteService  = new NoteService(noteRepository);
//        this.eventService = new EventService(eventRepository);

    }

    public TaskMenu  buildTaskMenu()      { return new TaskMenu(taskService); }
//    public NoteMenu  buildNoteMenu()      { return new NoteMenu(noteService); }
//    public EventMenu buildEventMenu()     { return new EventMenu(eventService, taskService); }
}
