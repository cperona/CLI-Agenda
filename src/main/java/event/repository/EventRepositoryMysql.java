package event.repository;

import common.exception.EventIdDoesNotExists;
import common.exception.EventSQLException;
import common.persistence.DatabaseConnection;
import event.model.Event;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryMysql implements EventRepository {
    private DatabaseConnection databaseConnection;

    public EventRepositoryMysql() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Event save(Event event) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "INSERT INTO event(title,description,event_date,recurring) VALUES(?,?,?,?);";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getEvent_date()));
            pstmt.setBoolean(4, event.getRecurring());
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                throw new EventSQLException("Insert fallado no hay filas afectadas.");
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                Event result = new Event();
                if (keys.next()) {
                    result.setId(keys.getInt(1));
                    result.setTitle(event.getTitle());
                    result.setDescription(event.getDescription());
                    result.setEvent_date(event.getEvent_date());
                    result.setRecurring(event.getRecurring());
                } else {
                    throw new EventSQLException("Insert ok pero no hay id.");
                }
                return result;
            }
        } catch (SQLException e) {
            throw new EventSQLException("Error alta evento.");
        }
    }

    @Override
    public void update(Event event) {
        try (Connection connection = databaseConnection.getConnection()) {
            if (!(existsById(event.getId()))) {
                throw new EventIdDoesNotExists();
            }
            String sql = "UPDATE event SET title =?,description=?,event_date=?,recurring=? WHERE id=?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, event.getTitle());
                pstmt.setString(2, event.getDescription());
                pstmt.setDate(3, Date.valueOf(event.getEvent_date()));
                pstmt.setBoolean(4, event.getRecurring());
                pstmt.setInt(5, event.getId());
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error update event."+ex.getMessage());
        }
    }


    @Override
    public List<Event> findAll() {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            ArrayList<Event> events = new ArrayList<>();
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,title,description,event_date,recurring FROM event;")) {
                ResultSet rs = prepared.executeQuery();
                while (rs.next()) {
                    events.add(new Event(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getBoolean(5)));
                }
                return events;
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error leyendo eventos");
        }
    }

    @Override
    public Optional<Event> findById(int id) {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            Event event;
            Optional<Event> result;
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,title,description,event_date,recurring FROM event WHERE id=?;")) {
                prepared.setInt(1, id);
                ResultSet rs = prepared.executeQuery();
                if (rs.next()) {
                    event = new Event(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getBoolean(5));
                    result = Optional.of(event);
                } else {
                    result = Optional.empty();
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error leyendo evento");
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = databaseConnection.getConnection()) {
            if (findById(id).isEmpty()) {
                throw new EventIdDoesNotExists();
            }
            String sql = "DELETE FROM event WHERE id=?;";
            try (PreparedStatement prest = connection.prepareStatement(sql)) {
                prest.setInt(1, id);
                prest.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error deleting event.");
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "DELETE FROM event;";
            try (PreparedStatement prest = connection.prepareStatement(sql)) {
                prest.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error deleting event.");
        }
    }

    @Override
    public boolean existsById(int id) {
        Optional<Event> event = findById(id);
        if(event.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public List<Event> findAllByDateAfter(LocalDate date) {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            Event event;
            List<Event> result=new ArrayList<>();
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,title,description,event_date,recurring FROM event WHERE event_date>=?;")) {
                prepared.setDate(1, Date.valueOf(date));
                ResultSet rs = prepared.executeQuery();
                while (rs.next()) {
                    result.add(new Event(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getBoolean(5)));
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error leyendo evento");
        }
    }

    @Override
    public List<Event> findUpcoming(int days) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = """
                SELECT * FROM event
                WHERE (recurring = false
                       AND event_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? DAY))
                   OR (recurring = true
                       AND DAYOFYEAR(event_date) BETWEEN DAYOFYEAR(NOW())
                           AND DAYOFYEAR(DATE_ADD(NOW(), INTERVAL ? DAY)))
                ORDER BY event_date ASC
                """;
            try (PreparedStatement prepared = connection.prepareStatement(sql)) {
                prepared.setInt(1, days);
                prepared.setInt(2, days);
                ResultSet rs = prepared.executeQuery();
                List<Event> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new Event(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getBoolean(5)));
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error finding upcoming events");
        }
    }
}