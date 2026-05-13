package common.exception;

public class EventIdDoesNotExists extends RuntimeException{
    public EventIdDoesNotExists()
    {
        super("El evento con esta id no existe.");
    }
}
