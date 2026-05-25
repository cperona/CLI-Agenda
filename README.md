# CLI-Agenda

CLI-Agenda is a command line application made in Java that centralises the management of relevant information for the user.
It allows the user to create, edit, list, and remove tasks and notes, as well as register and receive notifications of important events.
This utility offers the option to filter and order by priority, state, date, and it supports the repetition of events to be annually or personalized.

## 🛠 Technologies
- Java
- MySQL
- JDBC
- Gradle
- JUnit and Mockito for testing


## 🚀 Set up and install
1. Clone this repository: `git clone github.com/cperona/CLI-Agenda`
2. Run: `sudo docker compose up -d --remove-orphans `

## 🧩 Required
- Docker
- Java-25

## 🧪 Tests
1. Run: `sudo docker compose -f docker-compose-test.yml up --remove-orphans`
2. To run all the tests: `runAllTests` gradle run task

## Project structure

A typical structure for this project may include:

```text
├── build.gradle.kts
├── docker-compose-test.yml
├── docker-compose.yml
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── README.md
├── settings.gradle.kts
└── src
    ├── main
    │   ├── java
    │   │   ├── application
    │   │   │   ├── AgendaApp.java
    │   │   │   ├── config
    │   │   │   │   └── DependencyConfig.java
    │   │   │   └── menu
    │   │   │       └── MainMenu.java
    │   │   ├── common
    │   │   │   ├── exception
    │   │   │   │   ├── EventIdDoesNotExists.java
    │   │   │   │   ├── EventSQLException.java
    │   │   │   │   ├── NoteIdDoesNotExists.java
    │   │   │   │   ├── NoteSQLException.java
    │   │   │   │   ├── TaskNotFoundException.java
    │   │   │   │   └── TaskSQLException.java
    │   │   │   ├── persistence
    │   │   │   │   └── DatabaseConnection.java
    │   │   │   └── utils
    │   │   ├── event
    │   │   │   ├── cli
    │   │   │   │   └── EventMenu.java
    │   │   │   ├── dto
    │   │   │   │   ├── EventMapper.java
    │   │   │   │   ├── EventRequestDTO.java
    │   │   │   │   └── EventResponseDTO.java
    │   │   │   ├── model
    │   │   │   │   └── Event.java
    │   │   │   ├── Observer
    │   │   │   │   ├── EventNotificationConsole.java
    │   │   │   │   ├── EventObserver.java
    │   │   │   │   └── EventSubject.java
    │   │   │   ├── repository
    │   │   │   │   ├── EventRepository.java
    │   │   │   │   └── EventRepositoryMysql.java
    │   │   │   └── service
    │   │   │       ├── EventServiceImpl.java
    │   │   │       └── EventService.java
    │   │   ├── infrastructure
    │   │   │   └── sql
    │   │   │       └── structure.sql
    │   │   ├── note
    │   │   │   ├── cli
    │   │   │   │   └── NoteMenu.java
    │   │   │   ├── dto
    │   │   │   │   ├── NoteMapper.java
    │   │   │   │   ├── NoteRequestDTO.java
    │   │   │   │   └── NoteResponseDTO.java
    │   │   │   ├── model
    │   │   │   │   └── Note.java
    │   │   │   ├── repository
    │   │   │   │   ├── NoteRepository.java
    │   │   │   │   └── NoteRepositoryMysql.java
    │   │   │   └── service
    │   │   │       ├── NoteServiceImpl.java
    │   │   │       └── NoteService.java
    │   │   └── task
    │   │       ├── cli
    │   │       │   └── TaskMenu.java
    │   │       ├── dto
    │   │       │   ├── TaskRequestDto.java
    │   │       │   └── TaskResponseDto.java
    │   │       ├── model
    │   │       │   ├── Priority.java
    │   │       │   └── Task.java
    │   │       ├── repository
    │   │       │   ├── TaskRepository.java
    │   │       │   └── TaskRepositoryMysql.java
    │   │       └── service
    │   │           ├── TaskServiceImpl.java
    │   │           └── TaskService.java
    │   └── resources
    │       └── agendadb.properties
    └── test
        ├── java
        │   ├── common
        │   │   └── persistence
        │   │       └── DatabaseConnectionTest.java
        │   ├── event
        │   │   ├── repository
        │   │   │   └── EventRepositoryMysqlTest.java
        │   │   └── service
        │   │       └── EventServiceImplTest.java
        │   ├── note
        │   │   ├── repository
        │   │   │   └── NoteRepositoryMysqlTest.java
        │   │   └── service
        │   │       └── NoteServiceImplTest.java
        │   └── task
        │       ├── repository
        │       │   └── TaskRepositoryMysqlTest.java
        │       └── service
        │           └── TaskServiceImplTest.java
        └── resources
            └── agendadb.properties
```

# Java Task, Event, and Note Manager

A console-based Java application for managing tasks, events, and notes using **MySQL** with **JDBC**. The project is designed around a layered architecture with terminal input, exception handling, and database persistence.

## Overview

This application allows users to manage three main entities:

- **Tasks**: create, edit, delete, complete, list, and filter tasks.
- **Events**: create, edit, delete, search, list, and associate tasks with events.
- **Notes**: create, edit, delete, search, and list notes linked to tasks.

The system runs entirely in the terminal and focuses on practicing core backend concepts such as:

- JDBC database access
- MySQL relational modeling
- Layered architecture (repository, service, UI)
- DTOs and mappers
- Custom exceptions and error handling
- CRUD operations and filtering

## Features

### Main menu

```text
-----------Main-Menu----------
1. Tasks
2. Notes
3. Events
0. Exit
```

### Task management

```text
===========TASK MENU==========
1. Create task
2. Edit task
3. Delete task
4. Mark task as completed
5. Find task by id
6. List all tasks
7. List tasks by completed
8. List tasks by pending
9. List task by upcoming
10. Filter by priority
0. Back
==============================
```

Task features include:

- Creating new tasks
- Editing existing tasks
- Deleting tasks
- Marking tasks as completed
- Searching by ID
- Listing all tasks
- Listing completed and pending tasks
- Listing upcoming tasks
- Filtering by priority

### Event management

```text
===========EVENT MENU==========
1. Create event
2. Edit event
3. Delete event
4. Find event by id
5. List all events
6. List events by upcoming days
7. List events after date
8. List all tasks for an event
9. Assign a task to an event
0. Back
===============================
```

Event features include:

- Creating and editing events
- Deleting events
- Searching by ID
- Listing all events
- Filtering events by date-related criteria
- Viewing tasks associated with an event
- Assigning tasks to events

### Note management

```text
===========NOTE MENU==========
1. Create note
2. Edit note
3. Delete note
4. Find note by id
5. List notes by task_id
0. Back
==============================
```

Note features include:

- Creating notes linked to tasks
- Editing and deleting notes
- Searching notes by ID
- Listing notes that belong to a specific task

## Database schema

The project uses a relational MySQL schema with three related tables: `event`, `task`, and `note`.

### Event table

```sql
CREATE TABLE IF NOT EXISTS event (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_date DATETIME NOT NULL,
    recurring BOOLEAN
);
```

### Task table

```sql
CREATE TABLE IF NOT EXISTS task (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    deadline DATETIME NULL,
    priority ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    is_completed TINYINT(1) NOT NULL,
    created_at DATETIME NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_id INT UNSIGNED NULL,
    FOREIGN KEY (event_id) REFERENCES event(id)
);
```

### Note table

```sql
CREATE TABLE IF NOT EXISTS note (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    task_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task(id)
);
```

## Entity relationships

- An **event** can be associated with multiple tasks.
- A **task** can belong to one event or remain unassigned.
- A **task** can have multiple notes.
- A **note** must always belong to a task.

## Error handling

The application includes exception handling and input validation to make terminal interaction safer and easier to manage. This helps detect invalid IDs, database errors, and incorrect user input without crashing the application.

Examples of handled situations:

- Entity not found by ID
- Invalid numeric input in menus
- SQL execution errors
- Invalid business operations

## Testing

The project can include two main testing approaches:

- **Repository integration tests** for JDBC and MySQL behavior
- **Service unit tests** using Mockito to isolate business logic

This combination helps validate both persistence and application logic.

## Learning goals

This project is suitable for practicing:

- Java OOP and clean code principles
- JDBC with prepared statements
- MySQL schema design and foreign keys
- Service and repository patterns
- DTO mapping
- Unit and integration testing
- Exception-driven error handling

## Design Patterns

This project applies several classic design patterns to keep the code organized, reusable, and easier to maintain.

### Builder Pattern for Menus

The menu system was implemented using the **Builder** pattern. This approach makes it easier to create terminal menus step by step, separating menu construction from its final representation and improving readability when defining multiple options and sections.

Using Builder for the console UI helps avoid large, repetitive blocks of menu creation code. It also makes the menus easier to extend when new options or submenus are added in the future.

### Singleton Pattern for Database Access

Database access was implemented using the **Singleton** pattern. This ensures that the application uses a single shared database connection manager or configuration instance, which is a common use case for Singleton in Java applications.

Using Singleton for the database layer helps centralize connection management and prevents unnecessary duplication of shared resources. It also provides a single access point for JDBC-related operations across repositories and services.

### Observer Pattern for Upcoming Events

The application uses the **Observer** pattern to display upcoming events when the program starts. In this design, one component acts as the subject and notifies subscribed observers when startup event checks are triggered.

This pattern is useful because it decouples the event-checking logic from the components responsible for displaying notifications in the terminal. As a result, the startup behavior can be extended more easily without tightly coupling the main application flow to the notification logic.

## Possible improvements

- Add transactions for multi-step operations
- Add pagination for long lists
- Validate dates and business rules more strictly
- Add logging
- Add a REST API version with Spring Boot
- Add Docker support for MySQL and the application

## Author

This project was created as a practice application to work with Java, JDBC, MySQL, and layered backend design.
