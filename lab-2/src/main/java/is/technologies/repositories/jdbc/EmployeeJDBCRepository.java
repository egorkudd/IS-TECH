package is.technologies.repositories.jdbc;

import is.technologies.models.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee's repository with JDBC
 */
public class EmployeeJDBCRepository extends JDBCRepository<Employee> {
    public EmployeeJDBCRepository(String url, String user, String password) {
        super(url, user, password);
        this.tableName = "employees";
    }

    /**
     * Method to save model to database
     * @param entity is model to save
     * @return saved entity
     */
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

    /**
     * Method for updating model in database
     * @param entity is model to update
     * @return updated entity
     */
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

    /**
     * Method to get model from database
     * @param id is id of model to get
     * @return model from database
     */
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

    /**
     * Method to get all models of one type
     * @return list of models
     */
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

    /**
     * Method to transact employee from database to model class
     * @param resultSet is set of information of employee from database
     * @return employee's model
     * @throws SQLException if some fields are not valid
     */
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
