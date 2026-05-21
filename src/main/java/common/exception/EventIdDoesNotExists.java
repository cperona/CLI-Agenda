package common.exception;

public class EventIdDoesNotExists extends RuntimeException{
    public EventIdDoesNotExists()
    {
        super("Event Id does not exists");
    }
}
