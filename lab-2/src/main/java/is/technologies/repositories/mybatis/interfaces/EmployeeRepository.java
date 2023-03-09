package is.technologies.repositories.mybatis.interfaces;

import is.technologies.models.Employee;
import is.technologies.repositories.CRUDRepository;
import org.apache.ibatis.annotations.*;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRepository extends CRUDRepository<Employee> {
    @Override
    @Insert("INSERT INTO employees(name, birthday) VALUE(#{name}, #{birthday})")
    @Options(useGeneratedKeys=true, keyProperty="id")
    Employee save(Employee entity) throws SQLException;

    @Override
    @Delete("DELETE FROM employees WHERE id = #{id}")
    void deleteById(long id) throws SQLException;

    @Override
    @Delete("DELETE FROM employees WHERE id = #{id}")
    void deleteByEntity(Employee entity) throws SQLException;

    @Override
    @Delete("DELETE FROM employees")
    void deleteAll() throws SQLException;

    @Override
    @Update("UPDATE employees SET name = #{name}, birthday = #{birthday} WHERE id = #{id}")
    Employee update(Employee entity) throws SQLException;

    @Override
    @Select("SELECT * FROM employees WHERE id = #{id}")
    Employee getById(long id) throws SQLException;

    @Override
    @Select("SELECT * FROM employees")
    List<Employee> getAll() throws SQLException;
}
