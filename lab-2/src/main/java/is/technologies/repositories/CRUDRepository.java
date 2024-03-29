package is.technologies.repositories;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface of CRUD repository
 * @param <T> is type of model of certain implementation
 */
public interface CRUDRepository<T> {
    T save(T entity) throws SQLException;

    void deleteById(long id) throws SQLException;

    void deleteByEntity(T entity) throws SQLException;

    void deleteAll() throws SQLException;

    T update(T entity) throws SQLException;

    T getById(long id) throws SQLException;

    List<T> getAll() throws SQLException;
}
