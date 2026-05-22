package common.exception;

public class NoteIdDoesNotExists extends RuntimeException{
    public NoteIdDoesNotExists()
    {
        super("Note id does not exists.");
    }
}
