package task.cli;

import common.exception.TaskNotFoundException;
import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.IllegalFormatException;
import java.util.Optional;
import java.util.Scanner;

public class TaskMenu {

    private final TaskService taskService;
    private final Scanner scanner;

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public TaskMenu(TaskService taskService) {
        this.taskService = taskService;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    -------TASK-MENU-------
                    1. Create task"
                    2. Edit task
                    3. Delete task
                    4. Find task by id
                    0. Back
                    -----------------------
                    """);

            try {
                int option = scanner.nextInt();
                switch (option) {
                    case 1 -> createTask();
                    case 2 -> editTask();
                    case 3 -> deleteTask();
                    case 0 -> back = true;
                    default -> System.out.println("  Invalid option.");
                }
            } catch (IllegalFormatException e) {
                System.out.println("  Invalid format, only numbers are accepted.");
            }
        }
    }

    public void createTask() {
        try {
            System.out.println("Creating task, insert...");
            String title = readTitle(true);
            String description = readDescription(true);
            Priority priority = readPriority();
            Optional<LocalDateTime> deadline = readDeadline();

            TaskResponseDto created = taskService.createTask(
                    new TaskRequestDto(title, description, deadline.orElse(null), priority, null)
            );
            System.out.println("  ✓ Task created with id: " + created.id());
            pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating task \n"+ e.getMessage());
        }
    }

    public void editTask() {
        System.out.println("Edit task, insert...");
        Integer id = readId();
        Optional<TaskResponseDto> found = taskService.findById(id);
        if (found.isEmpty()) {
            System.out.println("  ✗ Task not found.");
            pressEnterToContinue();
            return;
        }
        TaskResponseDto existing = found.get();
        System.out.println("Task found, Leave blank to keep current value.");

        String title = readTitle(false);
        if (title.isBlank()) title = existing.title();

        String desc = readDescription(false);
        if (desc.isBlank()) desc = existing.description();

        Optional<LocalDateTime> newDeadline = readDeadline();
        LocalDateTime deadline = newDeadline.orElse(existing.deadline());

        Priority priority = readPriority();

        taskService.updateTask(id, new TaskRequestDto(title, desc, deadline, priority, existing.eventId()));
        System.out.println("  ✓ Task updated.");
        pressEnterToContinue();
    }

    public void deleteTask() {
        System.out.println("Delete task, insert...");
        int id = readId();
        try {
            taskService.deleteTask(id);
            System.out.println("  ✓ Task deleted.");
        } catch (TaskNotFoundException e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        pressEnterToContinue();
    }

    private String readTitle(boolean required) {
        while (true) {
            System.out.print("Title:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                taskService.titleValidation(input);
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
                taskService.descriptionValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private Priority readPriority() {
        while (true) {
            System.out.print("Priority (LOW / MEDIUM / HIGH) or leave blank for default: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                if (input.isBlank()) return Priority.MEDIUM;
                return Priority.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid priority – try again.");
            }
        }
    }

    private Optional<LocalDateTime> readDeadline() {
        while (true) {
            System.out.println("Use format: " + DATE_PATTERN + " or (leave blank for no deadline)");
            System.out.print("Deadline: ");
            String input = scanner.nextLine().trim();
            if (input.isBlank()) return Optional.empty();
            try {
                LocalDateTime deadline = LocalDateTime.parse(input, DATE_TIME_FORMATTER);
                taskService.validateDeadline(deadline);
                return Optional.of(deadline);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid format, try again.");
            }
        }
    }

    private Integer readId() {
        while (true) {
            System.out.print("ID: ");
            try {
                Integer id = Integer.parseInt(scanner.nextLine().trim());
                return id;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid ID. try again.");
            }
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
