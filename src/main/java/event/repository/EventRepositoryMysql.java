package event.repository;

import common.exception.EventIdDoesNotExists;
import common.exception.EventSQLException;
import common.persistence.DatabaseConnection;
import event.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryMysql implements EventRepository{
    private DatabaseConnection databaseConnection;
    private Connection connection;
    public EventRepositoryMysql()
    {
        databaseConnection = DatabaseConnection.getInstance();
        connection = databaseConnection.getConnection();
    }

    @Override
    public Event save(Event event)
    {
            String sql = "INSERT INTO event(title,description,event_date,recurring) VALUES(?,?,?,?)";
            try(PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
            {
                pstmt.setString(1, event.getTitle());
                pstmt.setString(2,event.getDescription());
                pstmt.setDate(3,Date.valueOf(event.getEvent_date()));
                pstmt.setBoolean(4,event.getRecurring());
                int affected = pstmt.executeUpdate();
                if(affected==0)
                {
                    throw new EventSQLException("Insert fallado no hay filas afectadas.");
                }
                try(ResultSet keys = pstmt.getGeneratedKeys()){
                    Event result = new Event();
                    if(keys.next())
                    {
                        result.setId(keys.getInt(1));
                        result.setTitle(event.getTitle());
                        result.setDescription(event.getDescription());
                        result.setEvent_date(event.getEvent_date());
                        result.setRecurring(event.getRecurring());
                    }
                    else
                    {
                        throw new EventSQLException("Insert ok pero no hay id.");
                    }
                    return result;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new EventSQLException("Error alta evento." + ex.getMessage());
            }
    }

    @Override
    public void update(Event event, int id) {
        if(findById(id).isEmpty())
        {
            throw new EventIdDoesNotExists();
        }
        String sql = "UPDATE event SET title =?,description=?,event_date=?,recurring=? WHERE id=?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, Date.valueOf(event.getEvent_date()));
            pstmt.setBoolean(4, event.getRecurring());
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            throw new EventSQLException("Error update event.");
        }
    }




    @Override
    public List<Event> findAll() {
        Statement stmt;

        ArrayList<Event> events = new ArrayList<>();
        try (PreparedStatement prepared = connection.prepareStatement("SELECT id,title,description,event_date,recurring FROM event;")){
            ResultSet rs = prepared.executeQuery();

            while (rs.next()) {
                events.add(new Event(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4).toLocalDate(),rs.getBoolean(5)));
            }
        } catch (SQLException ex) {
           throw new EventSQLException("Error leyendo eventos");
        }
        return events;
    }

    @Override
    public Optional<Event> findById(int id) {
        Statement stmt;
        Event event;
        Optional<Event> result;

        try (PreparedStatement prepared = connection.prepareStatement("SELECT id,title,description,event_date,recurring FROM event WHERE id=?;")){
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDate(4).toLocalDate(),rs.getBoolean(5));
                result = Optional.of(event);
            }
            else
            {
                result = Optional.empty();
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error leyendo eventos");
        }
        return result;
    }

    @Override
    public void close()
    {
        databaseConnection.close();
    }

    @Override
    public void delete(int id) {
        if(findById(id).isEmpty())
        {
            throw new EventIdDoesNotExists();
        }
        String sql = "DELETE FROM event WHERE id=?";
        try ( PreparedStatement prest = connection.prepareStatement(sql)) {
            prest.setInt(1, id);
            prest.executeUpdate();
        } catch (SQLException ex) {
           throw new EventSQLException("Error deleting event.");
        }
    }
}
