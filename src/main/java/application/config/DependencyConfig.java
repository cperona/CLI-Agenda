package application.config;

import common.persistence.DatabaseConnection;
import task.cli.TaskMenu;
import task.repository.TaskRepository;
import task.repository.TaskRepositoryMysql;
import task.service.TaskServiceImpl;
import java.sql.Connection;

public class DependencyConfig {

    Connection connection;

    private final TaskServiceImpl taskServiceImpl;
//    private final NoteService noteService;
//    private final EventService eventService;

    public DependencyConfig() {

        connection = DatabaseConnection.getInstance().getConnection();

        TaskRepository taskRepository  = new TaskRepositoryMysql();
//        NoteRepository noteRepository  = new NoteRepositoryMysql();
//        EventRepository eventRepository = new EventRepositoryMysql();


        this.taskServiceImpl = new TaskServiceImpl(taskRepository);
//        this.noteService  = new NoteService(noteRepository);
//        this.eventService = new EventService(eventRepository);

    }

    public TaskMenu  buildTaskMenu()      { return new TaskMenu(taskServiceImpl); }
//    public NoteMenu  buildNoteMenu()      { return new NoteMenu(noteService); }
//    public EventMenu buildEventMenu()     { return new EventMenu(eventService, taskService); }
}
