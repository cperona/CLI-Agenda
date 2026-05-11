USE agenda;
CREATE TABLE event (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_date DATETIME NOT NULL,
    recurring BOOLEAN
);

CREATE TABLE task (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(80) NOT NULL,
    deadline DATETIME NOT NULL,
    priority ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    status TINYINT(1) NOT NULL,
    creation_date DATETIME NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event(id)
);

CREATE TABLE note (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    creation_date DATETIME,
    task_id INT UNSIGNED NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task(id)
);
