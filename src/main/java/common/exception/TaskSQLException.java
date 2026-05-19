package common.exception;

public class TaskSQLException extends RuntimeException {
    public TaskSQLException(String message, Throwable cause) {
        super(message);
    }
}
