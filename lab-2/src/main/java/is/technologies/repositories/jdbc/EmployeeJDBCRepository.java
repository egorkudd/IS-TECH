package is.technologies.repositories.jdbc;

import is.technologies.models.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeJDBCRepository extends JDBCRepository<Employee> {
    public EmployeeJDBCRepository(String url, String user, String password) {
        super(url, user, password);
        this.tableName = "employees";
    }

    @Override
    public Employee save(Employee entity) throws SQLException {
        String sql = "INSERT INTO %s (name, birthday) VALUE(?, ?)".formatted(tableName);;
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getBirthday().toString());
            statement.executeUpdate();
        }

        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            resultSet.next();
            getConnection().commit();

            Employee savedEntity = new Employee();
            savedEntity.setId(resultSet.getLong(1));
            savedEntity.setName(entity.getName());
            savedEntity.setBirthday(entity.getBirthday());

            return savedEntity;
        }

    }

    @Override
    public Employee update(Employee entity) throws SQLException {
        String sql = "UPDATE %s SET name = ? , birthday = ? WHERE id = ?".formatted(tableName);;
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getBirthday().toString());
            statement.setLong(3, entity.getId());

            statement.execute();
            getConnection().commit();

            return entity;
        }
    }

    @Override
    public Employee getById(long id) throws SQLException {
        String sql = "SELECT * FROM %s WHERE id = ?".formatted(tableName);;
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            getConnection().commit();

            if (!resultSet.next()) {
                return null;
            }

            return createEmployee(resultSet);

        }
    }

    @Override
    public List<Employee> getAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();

        String sql = "SELECT * FROM %s".formatted(tableName);;
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            getConnection().commit();

            while (resultSet.next()) {
                employees.add(createEmployee(resultSet));
            }

            return employees;

        }
    }

    private Employee createEmployee(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Date birthday = resultSet.getDate("birthday");

        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setBirthday(birthday);

        return employee;
    }
}
