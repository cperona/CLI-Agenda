package event.cli;

import event.dto.EventRequestDTO;
import event.dto.EventResponseDTO;
import event.service.EventService;
import task.dto.TaskRequestDto;
import task.dto.TaskResponseDto;
import task.model.Priority;
import task.service.TaskServiceImpl;

import javax.swing.text.DateFormatter;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class EventMenu {
    private final EventService eventService;
    private final Scanner scanner;

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public EventMenu(EventService eventService)
    {
        this.eventService = eventService;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("""
                    ===========TASK=MENU==========
                    1. Create event
                    2. Edit event
                    3. Delete event
                    4. Find event by id
                    5. List all events
                    6. List events by Upcoming   
                    0. Back
                    ==============================
                    """);

            System.out.print("-Select an option: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> createEvent();
      /*          case "2" -> editEvent();
                case "3" -> deleteEvent();
                case "4" -> findByEventId();
                case "5" -> listEvents(eventService.listAll());
                case "6" -> listEvents(eventService.listUpcoming());*/
                case "0" -> back = true;
                default -> System.out.println("  Invalid option.");
            }


        }
    }

    public void createEvent() {
        try {
            System.out.println("Creating event, insert...");
            String title = readTitle(true);
            String description = readDescription(true);
            LocalDate event_date = readEventDate();
            boolean recurring = readRecurring();

            EventResponseDTO created = eventService.insertEvent(new EventRequestDTO(title,description,event_date,recurring));
            System.out.println("  • Event created with id: " + created.id());
            pressEnterToContinue();
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating task \n"+ e.getMessage());
        }
    }

    private boolean readRecurring(){
        while (true) {
            System.out.print("Recurring: y/n ");
            String input = scanner.nextLine().trim();
            if(!input.isEmpty())
            {
                input = input.toLowerCase();
                if(input.charAt(0)=='y')
                {
                    return true;
                }
                else if(input.charAt(0)=='n')
                {
                    return false;
                }
                else
                {
                    System.out.println("Bad option.");
                }
            }
        }
    }

    private String readTitle(boolean required) {
        while (true) {
            System.out.print("Title:  ");
            String input = scanner.nextLine().trim();
            try {
                if(!required && input.isEmpty()) return input;
                eventService.titleValidation(input);
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
                eventService.descriptionValidation(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + "\n Try again.");
            }
        }
    }

    private LocalDate readEventDate() {
        while (true) {
            System.out.println("Use format: " + DATE_PATTERN);
            System.out.print("Event date: ");
            String input = scanner.nextLine().trim();
            try {
                LocalDate eventDate = LocalDate.parse(input, DATE_TIME_FORMATTER);
                eventService.validateeventDate(eventDate);
                return eventDate;
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid format, try again.");
            }
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

}
