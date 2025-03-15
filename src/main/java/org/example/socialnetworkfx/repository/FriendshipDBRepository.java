package org.example.socialnetworkfx.repository;

import org.example.socialnetworkfx.domain.Friendship;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.domain.validation.Validation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepository extends AbstractDBRepository<Long, Friendship> {

    public FriendshipDBRepository(Validation<Friendship> validation, Connection connection) {
        super(validation, connection);
    }

    @Override
    protected String getTableName() {
        return "\"Friendship\"";
    }

    @Override
    protected Friendship mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long idFriendship = resultSet.getLong("id");
        Long idUser1 = resultSet.getLong("iduser1");
        Long idUser2 = resultSet.getLong("iduser2");
        Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
        LocalDateTime friendsFrom = (timestamp != null) ? timestamp.toLocalDateTime() : null;

        User user1 = findUserById(idUser1).orElseThrow(() -> new SQLException("User with ID " + idUser1 + " not found."));
        User user2 = findUserById(idUser2).orElseThrow(() -> new SQLException("User with ID " + idUser2 + " not found."));

        Friendship friendship = new Friendship(user1, user2, friendsFrom);
        friendship.setID(idFriendship);
        return friendship;
    }

    @Override
    protected void setPreparedStatementParametersForInsert(PreparedStatement statement, Friendship entity) throws SQLException {
        statement.setLong(1, entity.getFirstFriend().getID());
        statement.setLong(2, entity.getSecondFriend().getID());
    }

    @Override
    protected void setPreparedStatementParametersForUpdate(PreparedStatement statement, Friendship entity) throws SQLException {
        statement.setLong(1, entity.getFirstFriend().getID());
        statement.setLong(2, entity.getSecondFriend().getID());
        statement.setLong(3, entity.getID());
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM \"Friendship\"";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                friendships.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all friendships: " + e.getMessage(), e);
        }
        return friendships;
    }

    @Override
    public Optional<Friendship> save(Friendship friendship) {
        String sql = "INSERT INTO \"Friendship\" (iduser1, iduser2) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, friendship.getFirstFriend().getID());
            stmt.setLong(2, friendship.getSecondFriend().getID());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    friendship.setID(generatedKeys.getLong(1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving friendship to DB: " + e.getMessage(), e);
        }
        return Optional.of(friendship);
    }

    public List<User> findFriends(Long userId, int page, int pageSize) {
        String sql = "SELECT u.id, u.firstname, u.lastname, u.username, u.password, u.cryptedp " +
                "FROM \"User\" u " +
                "JOIN \"Friendship\" f ON (u.id = f.iduser1 OR u.id = f.iduser2) " +
                "WHERE (f.iduser1 = ? OR f.iduser2 = ?) AND u.id != ? " +
                "LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            stmt.setLong(3, userId);
            stmt.setInt(4, pageSize);
            stmt.setInt(5, (page - 1) * pageSize);
            ResultSet rs = stmt.executeQuery();

            List<User> friends = new ArrayList<>();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String cryptedP = rs.getString("cryptedp");

                User user = new User(firstName, lastName, username, password, cryptedP);
                user.setID(id);
                friends.add(user);
            }
            return friends;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding friends for user with ID " + userId + ": " + e.getMessage(), e);
        }
    }

    public int countFriends(Long userId) {
        List<User> friends = findFriends(userId, 1, Integer.MAX_VALUE);
        return friends.size();
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

    public Optional<Friendship> delete(Long ID) {
        String sql = "DELETE FROM \"Friendship\" WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, ID);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Friendship with ID " + ID + " deleted successfully.");
                return Optional.empty();
            } else {
                System.out.println("No friendship found with ID " + ID + ".");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting friendship with ID " + ID + ": " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}