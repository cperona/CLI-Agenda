package event.cli;

import common.exception.EventIdDoesNotExists;
import common.exception.TaskNotFoundException;
import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.service.EventServiceImpl;
import task.cli.TaskMenu;
import task.dto.TaskResponseDto;
import task.service.TaskServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EventMenu {

    private final EventServiceImpl eventServiceImpl;
    private final TaskServiceImpl taskServiceImpl;
    private final Scanner scanner;

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public EventMenu(EventServiceImpl eventServiceImpl, TaskServiceImpl taskServiceImpl) {
        this.eventServiceImpl = eventServiceImpl;
        this.taskServiceImpl = taskServiceImpl;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    ===========EVENT=MENU==========
                    1. Create event
                    2. Edit event
                    3. Delete event
                    4. Find event by id
                    5. List all event
                    6. List event by Upcoming days
                    7. List event by after date
                    8. List all task for an event
                    9. Assign a task to event
                    
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createEvent();
                case "2" -> editEvent();
                case "3" -> deleteEvent();
                case "4" -> findEventById();
                case "5" -> listEvents(eventServiceImpl.selectAllEvents());
                case "6" -> listEvents(eventServiceImpl.findByUpcoming(readDays()));
                case "7" -> listEvents(eventServiceImpl.findAllByDateAfter(readEventDate(true).get()));
                case "8" -> listTasksByEvent();
                case "9" -> assignTaskToEvent();
                case "0" -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    public void createEvent() {
        try {
            System.out.println("Creating Event, insert...");
            String title = readTitle(true);
            String description = readDescription();
            LocalDate eventDate = readEventDate(true).get();
            boolean recurring = eventIsRecurring(true).get();

            EventResponseDTO created = eventServiceImpl.insertEvent(
                    new EventRequestDTO(title, description, eventDate, recurring)
            );
            System.out.println("  • Event created with id: " + created.id());
            pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating Event \n"+ e.getMessage());
        }
    }

    public void editEvent() {
        System.out.println("Edit event, insert...");
        int id = readId();
        Optional<EventResponseDTO> found = eventServiceImpl.findById(id);
        if (found.isEmpty()) {
            System.out.println("  x Event not found.");
            pressEnterToContinue();
            return;
        }

        EventResponseDTO existing = found.get();
        System.out.println(" • Event found, Leave blank to keep current value.");

        String title = readTitle(false);
        if (title.isBlank()) title = existing.title();

        String desc = readDescription();
        if (desc.isBlank()) desc = existing.description();

        Optional<LocalDate> eventDate = readEventDate(false);
        LocalDate newEventDate = eventDate.orElse(existing.event_date());

        Optional<Boolean> recurring = eventIsRecurring(false);
        boolean newRecurring = recurring.orElse(existing.recurring());

        eventServiceImpl.updateEvent(new EventRequestDTO(title, desc, newEventDate, newRecurring), id);
        System.out.println("  • Event updated.");
        pressEnterToContinue();
    }

    public void deleteEvent() {
        System.out.println("Delete event, insert...");
        int id = readId();

        System.out.print("Are you sure you want to delete this task? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("  • Deletion cancelled.");
            pressEnterToContinue();
            return;
        }

        try {
            eventServiceImpl.deleteById(id);
            System.out.println("  • Event deleted.");
        } catch (EventIdDoesNotExists e) {
            System.out.println("  x " + e.getMessage());
        }
        pressEnterToContinue();
    }

    public void findEventById() {
        System.out.println("Find event by id, insert...");
        int id = readId();
        Optional<EventResponseDTO> found = eventServiceImpl.findById(id);
        if (found.isEmpty()) {
            System.out.println("  No events found.");
        } else {
            List<TaskResponseDto> tasks = taskServiceImpl.listByEvent(id);
            printEvent(found.get(), tasks);
        }
        pressEnterToContinue();
    }

    private void printEvent(EventResponseDTO event, List<TaskResponseDto> tasks) {
        System.out.printf("  [%d] %s%n", event.id(), event.title());
        System.out.println("      " + event.description());
        System.out.println("      Event Date: " + event.event_date().format(DATE_TIME_FORMATTER));
        if(!tasks.isEmpty()) {
            System.out.print("      Tasks id: ");
            for (TaskResponseDto task : tasks) {
                System.out.print(" [" + task.id() + "]");
            }
            System.out.println();
        }
    }

    private void listEvents(List<EventResponseDTO> events) {
        System.out.println();
        if (events.isEmpty()) {
            System.out.println("  No events found.");
        } else {
            System.out.println("=======================================");
            events.forEach(e -> printEvent(e, taskServiceImpl.listByEvent(e.id())));
            System.out.println("=======================================");
        }
        pressEnterToContinue();
    }

    public void assignTaskToEvent() {
        System.out.println("Assign task to event, insert...");
        System.out.println("Event...");

        int eventId = readId();
        Optional<EventResponseDTO> found = eventServiceImpl.findById(eventId);
        if (found.isEmpty()) {
            System.out.println("  x Event not found.");
            pressEnterToContinue();
            return;
        }

        System.out.println("Task...");
        int taskId = readId();
        Optional<TaskResponseDto> task = taskServiceImpl.findById(taskId);
        if (task.isEmpty()) {
            System.out.println("  x Task not found.");
            pressEnterToContinue();
            return;
        }

        try {
            taskServiceImpl.assignToEvent(taskId, eventId);
        } catch (EventIdDoesNotExists | TaskNotFoundException e) {
            System.out.println("  x " + e.getMessage());
        }
        System.out.println("  Task ["+ taskId + "] assigned to event [" + eventId + "]");
        pressEnterToContinue();
    }

    public void listTasksByEvent() {
        System.out.println("Listing tasks by event, insert...");
        System.out.println("Event...");
        int id = readId();

        Optional<EventResponseDTO> found = eventServiceImpl.findById(id);
        if (found.isEmpty()) {
            System.out.println("  x Event not found.");
            pressEnterToContinue();
            return;
        }
        List<TaskResponseDto> tasks = taskServiceImpl.listByEvent(id);
        if (tasks.isEmpty()) {
            System.out.println("  x This event has no tasks assigned");
            pressEnterToContinue();
            return;
        }

        System.out.println("===============EVENT==================");
        printEvent(found.get(), new ArrayList<>());
        System.out.println("===============TASKS==================");
        for (TaskResponseDto task : tasks) {
            TaskMenu.printTask(task);
        }
        System.out.println("=======================================");
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    //---------------Reading - Helpers---------------

    private String readTitle(boolean required) {
        while (true) {
            System.out.print("Title:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                eventServiceImpl.titleValidation(input);
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
                eventServiceImpl.descriptionValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private Optional<LocalDate> readEventDate(boolean required) {
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

    private Optional<Boolean> eventIsRecurring(boolean required) {
        while(true) {
            System.out.println("Event recurring yearly? (yes/no)");
            System.out.print("Recurring?: ");
            String recurring = scanner.nextLine().trim().toLowerCase();

            if(recurring.isBlank() && !required) return Optional.empty();
            if(recurring.equalsIgnoreCase("yes")) return Optional.of(true);
            if(recurring.equalsIgnoreCase("no")) return Optional.of(false);
            System.out.println("    Invalid input, try again");
        }
    }

    private Integer readId() {
        while (true) {
            System.out.print("ID: ");
            try {
                Integer id = Integer.parseInt(scanner.nextLine().trim());
                return id;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid ID, try again.");
            }
        }
    }

    private int readDays() {
        while (true) {
            System.out.print("Upcoming days: ");
            try {
                int days = Integer.parseInt(scanner.nextLine().trim());
                if(days < 0) throw new IllegalArgumentException("Days can't be negative");
                return days;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input, try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid input, try again. " + e.getMessage());
            }
        }
    }
}
