package common.exception;

public class NoteIdDoesNotExists extends RuntimeException{
    public NoteIdDoesNotExists()
    {
        super("No existe una nota con este id.");
    }
}
