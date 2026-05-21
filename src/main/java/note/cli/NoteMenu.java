package note.cli;

import event.cli.EventMenu;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;
import note.service.NoteService;
import note.service.NoteServiceImpl;
import task.exceptions.TaskIdDoesNotExist;
import task.service.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                    ===========TASK=MENU==========
                    1. Create note
                    2. Edit note
                    3. Delete note
                    4. Find note by id
                    5. List all notes
                    6. List notes by task_id
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createNote();
                case "2" -> editNote();
       /*         case "3" -> deleteEvent();
                case "4" -> findByEventId();
                case "5" -> listEvents(eventService.listAll());
                case "6" -> listEvents(eventService.listUpcoming());*/
                case "0" -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    private void editNote(){
        System.out.println("Editint Note, update...");
        int id = readId();
        Optional<NoteResponseDTO> found = noteServiceImpl.findById(id);
        if (found.isEmpty()) {
            System.out.println("  x Note not found.");
            pressEnterToContinue();
            return;
        }
        NoteResponseDTO existing = found.get();
        System.out.println(" • Note found, Leave blank to keep current value.");

        String desc = readDescription();
        if (desc.isBlank()) desc = existing.description();

        Optional<LocalDate> noteDate = readNoteDate(false);
        LocalDate newNoteDate = noteDate.orElse(existing.created_at());

        Optional<Integer> taskId = readTaskId(false);
        int newTaskId = taskId.orElse(existing.task_id());

        noteServiceImpl.updateNote(new NoteRequestDTO(desc, newNoteDate, newTaskId), id);
        System.out.println("  • Event updated.");
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
            System.out.println("Insert event id:");
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
                if(taskServiceImpl.findById(taskId).isEmpty())
                {
                    throw new TaskIdDoesNotExist();
                }
                return Optional.of(taskId);
            } catch (NumberFormatException ex) {
                System.out.println("Introduce números no letras.");
            }
            catch(TaskIdDoesNotExist ex){
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



    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
