package note.model;

import java.time.LocalDate;

public class Note {
    private int id;
    private String description;
    private LocalDate created_at;
    private int task_id;

    public Note(int id, String description, LocalDate created_at, int task_id) {
        this.id = id;
        this.description = description;
        this.created_at = created_at;
        this.task_id = task_id;
    }

    public Note(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }
}
