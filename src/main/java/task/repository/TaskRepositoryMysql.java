package task.repository;

import common.exception.TaskSQLException;
import common.persistence.DatabaseConnection;
import task.model.Priority;
import task.model.Task;

import java.sql.Connection;
import java.sql.*;
import java.util.Optional;

public class TaskRepositoryMysql implements TaskRepository {

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        Timestamp deadline = rs.getTimestamp("deadline");
        task.setDeadline(deadline != null ? deadline.toLocalDateTime() : null);

        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setIsCompleted(rs.getBoolean("is_completed"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        int eventId = rs.getInt("event_id");
        task.setEventId(rs.wasNull() ? null : eventId);

        return task;
    }

    @Override
    public Task save(Task task) {
        String sql = """
                INSERT INTO task (title, description, deadline, priority, is_completed, created_at, event_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            if (task.getDeadline() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }
            pstmt.setString(4, task.getPriority().name());
            pstmt.setBoolean(5, task.isCompleted());
            pstmt.setTimestamp(6, Timestamp.valueOf(task.getCreatedAt()));
            if (task.getEventId() != null) {
                pstmt.setInt(7, task.getEventId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) task.setId(keys.getInt(1));

            return task;
        } catch (SQLException e) {
            throw new TaskSQLException("Error saving task", e);
        }
    }

    @Override
    public void update(Task task) {
        String sql = """
                UPDATE task
                SET title = ?, description = ?, deadline = ?,
                    priority = ?, is_completed = ?, event_id = ?
                WHERE id = ?
                """;
        try (PreparedStatement pstmt = connection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            if (task.getDeadline() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }
            pstmt.setString(4, task.getPriority().name());
            pstmt.setBoolean(5, task.isCompleted());
            if (task.getEventId() != null) {
                pstmt.setInt(6, task.getEventId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setInt(7, task.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TaskSQLException("Error updating task", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM task WHERE id = ?";
        try (PreparedStatement pstmt = connection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TaskSQLException("Error deleting task", e);
        }
    }

    public Optional<Task> findById(int id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        try (PreparedStatement pstmt = connection().prepareStatement(sql)) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new TaskSQLException("Error finding task by id", e);
        }
    }
}
