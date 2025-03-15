package org.example.socialnetworkfx.repository;

import org.example.socialnetworkfx.domain.Entity;
import org.example.socialnetworkfx.domain.validation.Validation;

import java.sql.*;
import java.util.*;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected final Connection connection;
    protected final Validation<E> validator;
    protected final Map<ID, E> entities = new HashMap<>();  // In-memory cache

    public AbstractDBRepository(Validation<E> validator, Connection connection) {
        this.connection = connection;
        this.validator = validator;
    }

    // Abstract methods to be implemented in subclasses:
    protected abstract String getTableName();
    protected abstract E mapResultSetToEntity(ResultSet resultSet) throws SQLException;
    protected abstract void setPreparedStatementParametersForInsert(PreparedStatement statement, E entity) throws SQLException;
    protected abstract void setPreparedStatementParametersForUpdate(PreparedStatement statement, E entity) throws SQLException;
    public abstract Optional<E> update(E entity);
    //public abstract Iterable<E> findAll();
    public abstract Optional<E> save(E entity);

    @Override
    public Optional<E> findOne(ID id) {
        if (entities.containsKey(id)) {
            return Optional.of(entities.get(id));
        }
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                E entity = mapResultSetToEntity(rs);
                entities.put(id, entity);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding entity in DB: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = findOne(id);
        if (entity.isEmpty()) {
            throw new NoSuchElementException("Nu exista entitatea cu ID-ul " + id);
        }
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                entities.remove(id);
                return entity;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting entity from DB: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        Set<E> allEntities = new HashSet<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                E entity = mapResultSetToEntity(rs);

                // adaugam doar daca entitatea nu exista deja în allEntities
                allEntities.add(entity);

                if (!entities.containsKey(entity.getID())) {
                    entities.put(entity.getID(), entity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading all entities from DB: " + e.getMessage(), e);
        }
        return allEntities;
    }
}