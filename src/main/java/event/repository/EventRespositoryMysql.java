package event.repository;

import common.exception.EventSQLException;
import common.persistence.DatabaseConnection;
import event.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRespositoryMysql implements EventRepository{
    private DatabaseConnection databaseConnection;
    private Connection connection;
    public EventRespositoryMysql()
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
                        result.setTitle(keys.getString(2));
                        result.setDescription(keys.getString(3));
                        result.setEvent_date(keys.getDate(4).toLocalDate());
                        result.setRecurring(keys.getBoolean(5));
                    }
                    else
                    {
                        throw new EventSQLException("Insert ok pero no hay id.");
                    }
                    return result;
                }
            } catch (SQLException ex) {
                throw new EventSQLException("Error alta evento.");
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
}
