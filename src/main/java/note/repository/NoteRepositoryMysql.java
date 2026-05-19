package note.repository;

import common.exception.EventIdDoesNotExists;
import common.exception.EventSQLException;
import common.exception.NoteIdDoesNotExists;
import common.exception.NoteSQLException;
import common.persistence.DatabaseConnection;
import event.model.Event;
import note.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteRepositoryMysql implements NoteRepository{
    private DatabaseConnection databaseConnection;

    public NoteRepositoryMysql() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Note save(Note note) {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "INSERT INTO note(description,created_at,task_id) VALUES(?,?,?);";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, note.getDescription());
            pstmt.setDate(2, Date.valueOf(note.getCreated_at()));
            pstmt.setInt(3, note.getTask_id());
            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                throw new EventSQLException("Insert fallado no hay filas afectadas.");
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                Note result = new Note();
                if (keys.next()) {
                    result.setId(keys.getInt(1));
                    result.setDescription(note.getDescription());
                    result.setCreated_at(note.getCreated_at());
                    result.setTask_id(note.getTask_id());
                } else {
                    throw new EventSQLException("Insert ok pero no hay id.");
                }
                return result;
            }
        } catch (SQLException e) {
            throw new NoteSQLException("Error alta nota.");
        }
    }

    @Override
    public void update(Note note) {
        try (Connection connection = databaseConnection.getConnection()) {
            if (!(existsById(note.getId()))) {
                throw new NoteIdDoesNotExists();
            }
            String sql = "UPDATE note SET description =?,created_at=?,task_id=? WHERE id=?;";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1,note.getDescription());
                pstmt.setDate(2, Date.valueOf(note.getCreated_at()));
                pstmt.setInt(3, note.getTask_id());
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new NoteSQLException("Error update note."+ex.getMessage());
        }
    }

    @Override
    public List<Note> findAll() {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            ArrayList<Note> notes = new ArrayList<>();
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,description,created_at,task_id FROM note;")) {
                ResultSet rs = prepared.executeQuery();
                while (rs.next()) {
                    notes.add(new Note(rs.getInt(1), rs.getString(2), rs.getDate(3).toLocalDate(), rs.getInt(4)));
                }
                return notes;
            }
        } catch (SQLException ex) {
            throw new EventSQLException("Error leyendo notas");
        }
    }

    @Override
    public Optional<Note> findById(int id) {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            Note note;
            Optional<Note> result;
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,description,created_at,task_id FROM note WHERE id=?;")) {
                prepared.setInt(1, id);
                ResultSet rs = prepared.executeQuery();
                if (rs.next()) {
                    note = new Note(rs.getInt(1), rs.getString(2), rs.getDate(3).toLocalDate(), rs.getInt(4));
                    result = Optional.of(note);
                } else {
                    result = Optional.empty();
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new NoteSQLException("Error leyendo nota");
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = databaseConnection.getConnection()) {
            if (findById(id).isEmpty()) {
                throw new NoteIdDoesNotExists();
            }
            String sql = "DELETE FROM note WHERE id=?;";
            try (PreparedStatement prest = connection.prepareStatement(sql)) {
                prest.setInt(1, id);
                prest.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new NoteSQLException("Error deleting note.");
        }
    }

    @Override
    public void deleteAll() {
        try (Connection connection = databaseConnection.getConnection()) {
            String sql = "DELETE FROM note;";
            try (PreparedStatement prest = connection.prepareStatement(sql)) {
                prest.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new NoteSQLException("Error deleting note.");
        }
    }

    @Override
    public boolean existsById(int id) {
        Optional<Note> note = findById(id);
        if(note.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public List<Note> findByTaskId(int taskId) {
        try (Connection connection = databaseConnection.getConnection()) {
            Statement stmt;
            List<Note> result = new ArrayList<>();
            try (PreparedStatement prepared = connection.prepareStatement("SELECT id,description,created_at,task_id FROM note WHERE task_id=?;")) {
                prepared.setInt(1, taskId);
                ResultSet rs = prepared.executeQuery();
                while(rs.next()) {
                    result.add(new Note(rs.getInt(1), rs.getString(2), rs.getDate(3).toLocalDate(), rs.getInt(4)));
                }
                return result;
            }
        } catch (SQLException ex) {
            throw new NoteSQLException("Error leyendo notas");
        }
    }
}
