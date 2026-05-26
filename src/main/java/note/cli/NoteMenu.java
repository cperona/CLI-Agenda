package note.cli;

import common.exception.*;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;
import note.service.NoteService;
import note.service.NoteServiceImpl;
import task.dto.TaskResponseDto;
import task.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class NoteMenu {
    private final NoteService noteServiceImpl;
    private final Scanner scanner;
    private final TaskService taskServiceImpl;
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);


    public NoteMenu(NoteService noteServiceImpl,TaskService taskServiceImpl)
    {
        this.noteServiceImpl = noteServiceImpl;
        this.taskServiceImpl = taskServiceImpl;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    ===========NOTE=MENU==========
                    1. Create note
                    2. Edit note
                    3. Delete note
                    4. Find note by id
                    5. List notes by task_id
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createNote();
                case "2" -> editNote();
                case "3" -> deleteNote();
                case "4" -> findByNoteId();
                case "5" -> listNotesByTaskId();

                case "0" -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private void listNotesByTaskId()
    {
        System.out.println("List notes by task id.");
        int taskId = readId();
        try {
                TaskResponseDto task = taskServiceImpl.findById(taskId);

                List<NoteResponseDTO> notes = noteServiceImpl.findByTaskId(taskId);
                for (NoteResponseDTO note : notes) {
                    printNote(note);
                }
        }
        catch(TaskNotFoundException | NoteSQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        pressEnterToContinue();
    }

    private void findByNoteId() {
        System.out.println("Find note by id, insert...");
        int id = readId();
        try
        {
            NoteResponseDTO found = noteServiceImpl.findById(id);
            printNote(found);
        }
        catch(NoteIdDoesNotExists | NoteSQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        pressEnterToContinue();
    }

    private void printNote(NoteResponseDTO note) {
        System.out.println(note.id());
        System.out.println("      " + note.description());
        System.out.println("      Created at: " + note.created_at().format(DATE_TIME_FORMATTER));
        System.out.println("      Task Id:" + note.task_id());
    }

    private void editNote(){
        System.out.println("Editing Note, update...");
        int id = readId();
        try {
            NoteResponseDTO found = noteServiceImpl.findById(id);

            System.out.println(" • Note found, Leave blank to keep current value.");

            String desc = readDescription();
            if (desc.isBlank()) desc = found.description();

            Optional<LocalDate> noteDate = readNoteDate(false);
            LocalDate newNoteDate = noteDate.orElse(found.created_at());

            Optional<Integer> taskId = readTaskId(false);
            int newTaskId = taskId.orElse(found.task_id());

            noteServiceImpl.updateNote(new NoteRequestDTO(desc, newNoteDate, newTaskId), id);
            System.out.println("  • Note updated.");
        }
        catch(NoteIdDoesNotExists | NoteSQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        pressEnterToContinue();

    }

    private Optional<LocalDate> readNoteDate(boolean required) {
        while (true) {
            System.out.print("Use format: " + DATE_PATTERN + " , date: ");
            String input = scanner.nextLine().trim();
            if(!required && input.isBlank()) return Optional.empty();
            try {
                return Optional.of(LocalDate.parse(input, DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid format, try again.");
            }
        }
    }

    private int readId()
    {
        while(true)
        {
            System.out.println("Insert id:");
            try
            {
                int id = Integer.parseInt(scanner.nextLine().trim());
                return id;
            }
            catch(NumberFormatException ex)
            {
                System.out.println("It must be a number.");
            }
        }
    }

    private void createNote() {
        try {
            System.out.println("Creating Event, insert...");
            String title = readTitle(true);
            String description = readDescription();
            int taskId = readTaskId(true).get();
            LocalDate noteDate = LocalDate.now();


            NoteResponseDTO created = noteServiceImpl.insertNote(
                    new NoteRequestDTO(description, noteDate, taskId)
            );
            System.out.println("  • Note created with id: " + created.id());
            pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating Event \n"+ e.getMessage());
        }
    }

    private Optional<Integer> readTaskId(boolean required)
    {
        while(true) {
            System.out.println("Task id: ");
            String input = scanner.nextLine().trim();
            if(!required && input.isBlank())
            {
                return Optional.empty();
            }
            try {
                int taskId = Integer.parseInt(input);
                try{
                    TaskResponseDto task = taskServiceImpl.findById(taskId);
                    return Optional.of(taskId);
                }
                catch(TaskNotFoundException | TaskSQLException ex)
                {
                    ex.getMessage();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Insert only numbers.");
            }
            catch(TaskNotFoundException ex){
                System.out.println(ex.getMessage());
            }
        }

    }

    private String readTitle(boolean required) {
        while (true) {
            System.out.print("Title:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                noteServiceImpl.titleValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private String readDescription() {
        while (true) {
            System.out.print("Description:  ");
            String input = scanner.nextLine().trim();
            try {
                noteServiceImpl.descriptionValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private void deleteNote() {
        System.out.println("Delete note, insert...");
        int id = readId();
        boolean exists = noteServiceImpl.existsById(id);
        if(exists)
        {
            System.out.print("Are you sure you want to delete this note? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                System.out.println("  • Deletion cancelled.");
                pressEnterToContinue();
                return;
            }

            try {
                noteServiceImpl.deleteById(id);
                System.out.println("  • Note deleted.");
            } catch (EventIdDoesNotExists | EventSQLException e) {
                System.out.println("  x " + e.getMessage());
            }
        }
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
