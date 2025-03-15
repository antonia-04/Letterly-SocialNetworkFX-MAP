package org.example.socialnetworkfx.repository;

import org.example.socialnetworkfx.domain.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository {
    private final Connection connection;

    public MessageDBRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Message message) {
        String sql = "INSERT INTO \"Message\" (text, timestamp, sender_id, recipient_id, reply_to) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, message.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(message.getTimestamp()));
            stmt.setLong(3, message.getSenderId());
            stmt.setLong(4, message.getRecipientId());
            if (message.getReplyTo() != null) {
                stmt.setLong(5, message.getReplyTo().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                message.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving message: " + e.getMessage(), e);
        }
    }

    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        String sql = "SELECT * FROM \"Message\" WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) ORDER BY timestamp";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId1);
            stmt.setLong(2, userId2);
            stmt.setLong(3, userId2);
            stmt.setLong(4, userId1);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                        rs.getString("text"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getLong("sender_id"),
                        rs.getLong("recipient_id")
                );
                message.setId(rs.getLong("id"));
                Long replyToId = rs.getLong("reply_to");
                if (!rs.wasNull()) {
                    message.setReplyTo(findById(replyToId));
                }
                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage(), e);
        }
        return messages;
    }

    private Message findById(Long id) {
        String sql = "SELECT * FROM \"Message\" WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Message(
                        rs.getString("text"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getLong("sender_id"),
                        rs.getLong("recipient_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding message by id: " + e.getMessage(), e);
        }
        return null;
    }
}