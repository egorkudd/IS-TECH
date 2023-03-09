package is.technologies.repositories;

import java.sql.SQLException;
import java.util.List;

public interface ChildEntityRepository<T> extends CRUDRepository<T> {
    List<T> getAllByParentId(long id) throws SQLException;
}
