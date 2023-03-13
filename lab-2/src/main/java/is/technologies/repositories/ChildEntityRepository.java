package is.technologies.repositories;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for repository of models with parent dependences
 * @param <T> is type of models
 */
public interface ChildEntityRepository<T> extends CRUDRepository<T> {
    List<T> getAllByParentId(long id) throws SQLException;
}
