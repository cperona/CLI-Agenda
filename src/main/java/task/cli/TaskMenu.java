package task.cli;

import common.exception.TaskNotFoundException;
import common.exception.TaskSQLException;
import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TaskMenu {

    private final TaskService taskServiceImpl;
    private final Scanner scanner;

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public TaskMenu(TaskService taskServiceImpl) {
        this.taskServiceImpl = taskServiceImpl;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    ===========TASK=MENU==========
                    1. Create task
                    2. Edit task
                    3. Delete task
                    4. Mark task as completed
                    5. Find task by id
                    6. List all tasks
                    7. List tasks by completed
                    8. List tasks by pending
                    9. List task by Upcoming
                    10. Filter by priority
                   
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createTask();
                case "2" -> editTask();
                case "3" -> deleteTask();
                case "4" -> markCompleted();
                case "5" -> findTaskById();
                case "6" -> listTasks(taskServiceImpl.listAll());
                case "7" -> listTasks(taskServiceImpl.listCompleted());
                case "8" -> listTasks(taskServiceImpl.listPending());
                case "9" -> listTasks(taskServiceImpl.listUpcoming());
                case "10" -> filterByPriority();
                case "0" -> back = true;
                default -> System.out.println("  Invalid option.");
            }
        }
    }

    public void createTask() {
        try {
            System.out.println("Creating task, insert...");
            String title = readTitle(true);
            String description = readDescription(true);
            System.out.println(" *  Leave blank for default: MEDIUM");
            Optional<Priority> priority = readPriority();
            System.out.println(" *  Leave blank for no deadline");
            Optional<LocalDateTime> deadline = readDeadline();

            TaskResponseDto created = taskServiceImpl.createTask(
                    new TaskRequestDto(title, description, deadline.orElse(null), priority.orElse(null), null)
            );
            System.out.println("  • Task created with id: " + created.id());
            pressEnterToContinue();
        } catch (TaskSQLException e) {
            System.out.println("Error creating task: " + e.getMessage());
            pressEnterToContinue();
        }
    }

    public void editTask() {
        System.out.println("Edit task, insert...");
        int id = readId();
        try {
            TaskResponseDto existing = taskServiceImpl.findById(id);

            System.out.println(" • Task found, Leave blank to keep current value.");

            String title = readTitle(false);
            if (title.isBlank()) title = existing.title();

            String desc = readDescription(false);
            if (desc.isBlank()) desc = existing.description();

            Optional<LocalDateTime> newDeadline = readDeadline();
            LocalDateTime deadline = newDeadline.orElse(existing.deadline());

            Optional<Priority> newPriority = readPriority();
            Priority priority =  newPriority.orElse(existing.priority());

            taskServiceImpl.updateTask(id, new TaskRequestDto(title, desc, deadline, priority, existing.eventId()));
            System.out.println("  • Task updated.");
            pressEnterToContinue();

        } catch (TaskNotFoundException | TaskSQLException e) {
            System.out.println("  x " + e.getMessage());
            pressEnterToContinue();
        }
    }

    public void deleteTask() {
        System.out.println("Delete task, insert...");
        int id = readId();
        TaskResponseDto task = taskServiceImpl.findById(id);
        System.out.print("Are you sure you want to delete this task? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("  • Deletion cancelled.");
            pressEnterToContinue();
            return;
        }

        try {
            taskServiceImpl.deleteTask(id);
            System.out.println("  • Task deleted.");
        } catch (TaskSQLException | TaskNotFoundException e) {
            System.out.println("  x " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void markCompleted() {
        try {
            System.out.println("Mark task as completed, insert...");
            taskServiceImpl.markCompleted(readId());
            System.out.println("  • Task marked as completed.");
        } catch (TaskSQLException e) {
            System.out.println("  x " + e.getMessage());
        }
        pressEnterToContinue();
    }

    public void findTaskById() {
        System.out.println("Find task by id, insert...");
        int id = readId();
        try {
            TaskResponseDto found = taskServiceImpl.findById(id);
            printTask(found);
        } catch (TaskSQLException | TaskNotFoundException e) {
            System.out.println("  x " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private void filterByPriority() {
        System.out.println("Filter by priority, insert...");
        while(true) {
            try {
                Priority input = readPriority().orElseThrow(IllegalArgumentException::new);
                listTasks(taskServiceImpl.listByPriority(input));
                return;
            } catch(IllegalArgumentException e) {
                System.out.println("  Priority can't be blank");
            }
        }
    }

    private void listTasks(List<TaskResponseDto> tasks) {
        System.out.println();
        if (tasks.isEmpty()) {
            System.out.println("  No tasks found.");
        } else {
            System.out.println("=======================================");
            tasks.forEach(TaskMenu::printTask);
            System.out.println("=======================================");
        }
        pressEnterToContinue();
    }

    public static void printTask(TaskResponseDto t) {
        System.out.printf("  [%d] %s  |  %s  |  %s%n",
                t.id(), t.title(), t.priority(), t.isCompleted() ? "• Completed" : "○ Pending");
        if(!t.description().isBlank()) System.out.println("      " + t.description());
        if (t.deadline() != null) {
            System.out.println("      Deadline: " + t.deadline().format(DATE_TIME_FORMATTER));
        }
        System.out.println("      Created at: " + t.createdAt().format(DATE_TIME_FORMATTER));
        if(t.eventId() != null) {
            System.out.println("      Event linked Id: " + t.eventId());
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    // -------------------- Reading - Helpers ------------------------

    private String readTitle(boolean required) {
        while (true) {
            System.out.print("Title:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                taskServiceImpl.titleValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private String readDescription(boolean required) {
        while (true) {
            System.out.print("Description:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                taskServiceImpl.descriptionValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " Try again.");
            }
        }
    }

    private Optional<Priority> readPriority() {
        while (true) {
            System.out.print("Priority (LOW / MEDIUM / HIGH): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if(input.isBlank()) return Optional.empty();
            try {
                return Optional.of(Priority.valueOf(input));
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid priority, try again.");
            }
        }
    }

    private Optional<LocalDateTime> readDeadline() {
        while (true) {
            System.out.println("Use format: " + DATE_PATTERN);
            System.out.print("Deadline: ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) return Optional.empty();
            try {
                LocalDateTime deadline = LocalDateTime.parse(input, DATE_TIME_FORMATTER);
                taskServiceImpl.validateDeadline(deadline);
                return Optional.of(deadline);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid format, try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage() + " Try again.");
            }
        }
    }

    private Integer readId() {
        while (true) {
            System.out.print("ID: ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Invalid ID, try again.");
            }
        }
    }
}
