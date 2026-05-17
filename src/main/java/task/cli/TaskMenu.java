package task.cli;

import common.exception.TaskNotFoundException;
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
                    ===========TASK=MENU==========
                    1. Create task
                    2. Edit task
                    3. Delete task
                    4. Find task by id
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createTask();
                case "2" -> editTask();
                case "3" -> deleteTask();
                case "4" -> findTaskById();
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

            TaskResponseDto created = taskService.createTask(
                    new TaskRequestDto(title, description, deadline.orElse(null), priority.orElse(null), null)
            );
            System.out.println("  • Task created with id: " + created.id());
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
            System.out.println("  x Task not found.");
            pressEnterToContinue();
            return;
        }
        TaskResponseDto existing = found.get();
        System.out.println(" • Task found, Leave blank to keep current value.");

        String title = readTitle(false);
        if (title.isBlank()) title = existing.title();

        String desc = readDescription(false);
        if (desc.isBlank()) desc = existing.description();

        Optional<LocalDateTime> newDeadline = readDeadline();
        LocalDateTime deadline = newDeadline.orElse(existing.deadline());

        Optional<Priority> newPriority = readPriority();
        Priority priority =  newPriority.orElse(existing.priority());

        taskService.updateTask(id, new TaskRequestDto(title, desc, deadline, priority, existing.eventId()));
        System.out.println("  • Task updated.");
        pressEnterToContinue();
    }

    public void deleteTask() {
        System.out.println("Delete task, insert...");
        int id = readId();
        try {
            taskService.deleteTask(id);
            System.out.println("  • Task deleted.");
        } catch (TaskNotFoundException e) {
            System.out.println("  x " + e.getMessage());
        }
        pressEnterToContinue();
    }

    public void findTaskById() {
        System.out.println("Find task by id, insert...");
        int id = readId();
        Optional<TaskResponseDto> found = taskService.findById(id);
        if (found.isEmpty()) {
            System.out.println("  No tasks found.");
        } else {
            printTask(found.get());
        }
        pressEnterToContinue();
    }

    private void listTasks(List<TaskResponseDto> tasks) {
        System.out.println();
        if (tasks.isEmpty()) {
            System.out.println("  No tasks found.");
        } else {
            System.out.println("=======================================");
            tasks.forEach(this::printTask);
            System.out.println("=======================================");
        }
        pressEnterToContinue();
    }

    private void printTask(TaskResponseDto t) {
        System.out.printf("  [%d] %s  |  %s  |  %s%n",
                t.id(), t.title(), t.priority(), t.isCompleted() ? "• Completed" : "○ Pending");
        System.out.println("      " + t.description());
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
                System.out.println("  Invalid ID, try again.");
            }
        }
    }
}
