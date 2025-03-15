package org.example.socialnetworkfx.repository;

import javafx.util.Pair;
import org.example.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.domain.validation.Validation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDBRepository extends AbstractDBRepository<Long, User> {

    public UserDBRepository(Validation<User> validation, Connection connection) {
        super(validation, connection);
    }

    @Override
    protected String getTableName() {
        return "\"User\"";
    }

    @Override
    protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String firstName = resultSet.getString("firstname");
        String lastName = resultSet.getString("lastname");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String cryptedp = resultSet.getString("cryptedp");

        User user = new User(firstName, lastName, username, password, cryptedp);
        user.setID(id);
        return user;
    }

    @Override
    protected void setPreparedStatementParametersForInsert(PreparedStatement statement, User entity) throws SQLException {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getUsername());
        statement.setString(4, entity.getPassword());
        statement.setString(5, entity.getCryptedP());
    }

    @Override
    protected void setPreparedStatementParametersForUpdate(PreparedStatement statement, User entity) throws SQLException {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getUsername());
        statement.setString(4, entity.getPassword());
        statement.setString(5, entity.getCryptedP());
        statement.setLong(6, entity.getID());
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        String sql = "SELECT * FROM \"User\"";
        List<User> users = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public Optional<User> save(User user) {
        String sql = "INSERT INTO \"User\" (firstname, lastname, username, password, cryptedp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPreparedStatementParametersForInsert(stmt, user);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setID(generatedKeys.getLong(1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user to DB: " + e.getMessage(), e);
        }
        return Optional.of(user);
    }

    public Optional<User> findUser(String username) {
        String sql = "SELECT id, firstname, lastname, username, password, cryptedp FROM \"User\" WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String password = rs.getString("password");
                String cryptedp = rs.getString("cryptedp");

                User user = new User(firstName, lastName, username, password, cryptedp);
                user.setID(id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long userId) {
        String sql = "SELECT * FROM \"User\" WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + e.getMessage(), e);
        }
    }

    public List<User> findAll(int page, int pageSize) {
        String sql = "SELECT * FROM \"User\" LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users with pagination: " + e.getMessage(), e);
        }
        return users;
    }

    public Pair<String, String> getUserDetails(Long userId) {
        String sql = "SELECT bio, fromd FROM \"UserDetail\" WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String bio = rs.getString("bio");
                String from = rs.getString("fromd");
                return new Pair<>(bio, from);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user details for user ID " + userId, e);
        }
        return null;
    }

}