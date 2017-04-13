package com.phonecompany.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

/**
 * Base interface for CRUD operations on entities
 *
 * @param <T> entity type that CRUD operations are performed on
 */
public interface CrudDao<T> {
    /**
     * Inserts entity
     *
     * @param entity entity to be saved
     * @return saved entity
     */
    T insert(T entity) throws SQLException;

    /**
     * Updates entity
     *
     * @param entity entity to be saved
     * @return saved entity
     */
    T update(T entity) throws SQLException;

    /**
     * Gets entity by its id
     *
     * @param id entity identifier
     * @return entity found by the provided identifier
     */
    T getById(Long id) throws SQLException;

    /**
     * Deletes entity by the given id
     *
     * @param id identifier of the entity that has to be deleted
     */
    void delete(Long id) throws SQLException;

    /**
     * Gets all the entities
     *
     * @return all entities
     */
    List<T> getAll() throws SQLException;
}
