package task.exceptions;

public class TaskIdDoesNotExist extends RuntimeException{
    public TaskIdDoesNotExist()
    {
        super("El id de la tarea no existe.");
    }
}
