package org.example.socialnetworkfx.repository;

import org.example.socialnetworkfx.domain.FriendshipRequest;
import org.example.socialnetworkfx.domain.Status;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.domain.validation.Validation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipRequestDBRepository extends AbstractDBRepository<Long, FriendshipRequest> {

    public FriendshipRequestDBRepository(Validation<FriendshipRequest> validation, Connection connection) {
        super(validation, connection);
    }

    @Override
    protected String getTableName() {
        return "\"FriendshipRequest\"";
    }

    @Override
    protected FriendshipRequest mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long idRequest = resultSet.getLong("id");
        Long idSender = resultSet.getLong("idsender");
        Long idRecipient = resultSet.getLong("idrecipient");
        String statusStr = resultSet.getString("status");

        User sender = findUserById(idSender).orElseThrow(() -> new SQLException("Sender with ID " + idSender + " not found."));
        User recipient = findUserById(idRecipient).orElseThrow(() -> new SQLException("Recipient with ID " + idRecipient + " not found."));

        Status status = Status.valueOf(statusStr.toUpperCase());

        FriendshipRequest request = new FriendshipRequest(recipient, sender, status);
        request.setID(idRequest);

        return request;
    }

    @Override
    protected void setPreparedStatementParametersForInsert(PreparedStatement statement, FriendshipRequest entity) throws SQLException {
        statement.setLong(1, entity.getSender().getID());
        statement.setLong(2, entity.getRecipient().getID());
        statement.setString(3, entity.getStatus().name());
    }

    @Override
    protected void setPreparedStatementParametersForUpdate(PreparedStatement statement, FriendshipRequest entity) throws SQLException {
        statement.setLong(1, entity.getSender().getID());
        statement.setLong(2, entity.getRecipient().getID());
        statement.setString(3, entity.getStatus().name());
        statement.setLong(4, entity.getID());
    }

    @Override
    public Optional<FriendshipRequest> update(FriendshipRequest entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        List<FriendshipRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM \"FriendshipRequest\"";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FriendshipRequest request = mapResultSetToEntity(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all friendship requests: " + e.getMessage(), e);
        }
        return requests;
    }

    @Override
    public Optional<FriendshipRequest> save(FriendshipRequest request) {
        String sql = "INSERT INTO \"FriendshipRequest\" (idsender, idrecipient, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, request.getSender().getID());
            stmt.setLong(2, request.getRecipient().getID());
            stmt.setString(3, request.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    request.setID(generatedKeys.getLong(1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving friendship request to DB: " + e.getMessage(), e);
        }
        return Optional.of(request);
    }

    public List<User> getFriendRequests(Long userId) {
        String sql = "SELECT u.id, u.firstname, u.lastname, u.username, u.password, u.cryptedp " +
                "FROM \"User\" u " +
                "JOIN \"FriendshipRequest\" fr ON u.id = fr.idsender OR u.id = fr.idrecipient " +
                "WHERE (fr.idrecipient = ? OR fr.idsender = ?) AND fr.status = 'PENDING'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();

            List<User> friendRequests = new ArrayList<>();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String cryptedP = rs.getString("cryptedp");

                User user = new User(firstName, lastName, username, password, cryptedP);
                user.setID(id);
                friendRequests.add(user);
            }
            return friendRequests;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding friend requests for user with ID " + userId + ": " + e.getMessage(), e);
        }
    }

    public List<User> getFriendRequestsUser(Long userId) {
        String sql = "SELECT u.id, u.firstname, u.lastname, u.username, u.password, u.cryptedp " +
                "FROM \"User\" u " +
                "JOIN \"FriendshipRequest\" fr ON u.id = fr.idsender " +
                "WHERE fr.idrecipient = ? AND fr.status = 'PENDING'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<User> friendRequests = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String cryptedP = rs.getString("cryptedp");

                User user = new User(firstName, lastName, username, password, cryptedP);
                user.setID(id);
                friendRequests.add(user);
            }
            return friendRequests;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding friend requests for user with ID " + userId + ": " + e.getMessage(), e);
        }
    }

    public void sendFriendRequest(Long userId, Long friendId) {
        String sql = "INSERT INTO \"FriendshipRequest\" (idsender, idrecipient, status) VALUES (?, ?, 'PENDING')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, friendId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error sending friend request from user with ID " + userId + " to user with ID " + friendId + ": " + e.getMessage(), e);
        }
    }

    public Optional<FriendshipRequest> findRequestBySenderAndRecipient(Long senderId, Long recipientId) {
        String sql = "SELECT * FROM \"FriendshipRequest\" WHERE idsender = ? AND idrecipient = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, senderId);
            stmt.setLong(2, recipientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding friendship request: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void updateFR(FriendshipRequest entity) {
        String sql = "UPDATE \"FriendshipRequest\" SET idsender = ?, idrecipient = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementParametersForUpdate(stmt, entity);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating friendship request: " + e.getMessage(), e);
        }
    }

    private Optional<User> findUserById(Long userId) {
        String sql = "SELECT * FROM \"User\" WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String cryptedP = rs.getString("cryptedp");

                User user = new User(firstName, lastName, username, password, cryptedP);
                user.setID(id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}