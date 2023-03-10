package is.technologies.repositories.mybatis.interfaces;

import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;
import org.apache.ibatis.annotations.*;

import java.sql.SQLException;
import java.util.List;

public interface TaskMyBatisRepository extends ChildEntityRepository<Task> {
    @Override
    @Insert("INSERT INTO tasks(name, dead_line, description, type, employee_id)"
            + "VALUE(#{name}, #{deadLine}, #{description}, #{type}, #{employeeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Task save(Task entity) throws SQLException;

    @Override
    @Delete("DELETE FROM tasks WHERE id = #{id}")
    void deleteById(long id) throws SQLException;

    @Override
    @Delete("DELETE FROM tasks WHERE id = #{id}")
    void deleteByEntity(Task entity) throws SQLException;

    @Override
    @Delete("DELETE FROM tasks")
    void deleteAll() throws SQLException;

    @Override
    @Update("UPDATE tasks SET name = #{name}, dead_line = #{deadLine}, "
            + "description = #{description}, type = #{type}"
            + "WHERE id = #{id}")
    Task update(Task entity) throws SQLException;

    @Override
    @Select("SELECT * FROM tasks WHERE id = #{id}")
    Task getById(long id) throws SQLException;

    @Override
    @Select("SELECT * FROM tasks")
    List<Task> getAll() throws SQLException;

    @Override
    @Select("SELECT * FROM tasks WHERE employee_id = #{id} limit 5")
    List<Task> getAllByParentId(long id) throws SQLException;
}