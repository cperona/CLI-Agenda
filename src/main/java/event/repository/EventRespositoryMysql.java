package event.repository;

import common.exception.EventSQLException;
import common.persistence.DatabaseConnection;
import event.model.Event;

import java.sql.*;

public class EventRespositoryMysql implements EventRepository{
    private DatabaseConnection databaseConnection;
    private Connection connection;
    public EventRespositoryMysql()
    {
        databaseConnection = DatabaseConnection.getInstance();
        connection = databaseConnection.getConnection();
    }

    @Override
    public Event insertEvent(Event event)
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
                    if(keys.next())
                    {
                        event.setId(keys.getInt(1));
                    }
                    else
                    {
                        throw new EventSQLException("Insert ok pero no hay id.");
                    }
                    return event;
                }
            } catch (SQLException ex) {
                throw new EventSQLException("Error alta evento.");
            }
    }

    @Override
    public void close()
    {
        databaseConnection.close();
    }
}
