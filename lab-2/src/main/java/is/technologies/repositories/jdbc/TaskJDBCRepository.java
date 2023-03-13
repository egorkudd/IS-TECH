package is.technologies.repositories.jdbc;

import is.technologies.enums.TaskType;
import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task repository with JDBC
 */
public class TaskJDBCRepository extends JDBCRepository<Task> implements ChildEntityRepository<Task> {
    public TaskJDBCRepository(String url, String user, String password) {
        super(url, user, password);
        this.tableName = "tasks";
    }

    /**
     * Method to save model to database
     * @param entity is model to save
     * @return saved entity
     */
    @Override
    public Task save(Task entity) throws SQLException {
        String sql = "INSERT INTO %s (name, dead_line, description, type, employee_id)"
                .concat(" VALUE(?, ?, ?, ?, ?)").formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDeadLine().toString());
            statement.setString(3, entity.getDescription());
            statement.setString(4, entity.getType().toString());
            statement.setLong(5, entity.getEmployeeId());

            statement.execute();
        }

        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
            resultSet.next();
            getConnection().commit();

            Task savedTask = new Task();
            savedTask.setId(resultSet.getLong(1));
            savedTask.setName(entity.getName());
            savedTask.setDeadLine(entity.getDeadLine());
            savedTask.setDescription(entity.getDescription());
            savedTask.setType(entity.getType());
            savedTask.setEmployeeId(entity.getEmployeeId());
            return savedTask;
        }
    }

    /**
     * Method for updating model in database
     * @param entity is model to update
     * @return updated entity
     */
    @Override
    public Task update(Task entity) throws SQLException {
        String sql = "UPDATE %s SET name = ?, dead_line = ?, description = ?, "
                .concat("type = ?, employee_id = ? WHERE id = ?").formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDeadLine().toString());
            statement.setString(3, entity.getDescription());
            statement.setString(4, entity.getType().toString());
            statement.setLong(5, entity.getEmployeeId());
            statement.setLong(6, entity.getId());

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
    public Task getById(long id) throws SQLException {
        String sql = "SELECT * FROM %s WHERE id = ?".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            getConnection().commit();

            if (!resultSet.next()) {
                return null;
            }

            return createTask(resultSet);
        }
    }

    /**
     * Method to get all models of one type
     * @return list of models
     */
    @Override
    public List<Task> getAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM %s".formatted(tableName);
        try (Statement statement = getConnection().createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            getConnection().commit();

            while (resultSet.next()) {
                tasks.add(createTask(resultSet));
            }

            return tasks;
        }
    }

    /**
     * Method to get all tasks of one employee
     * @param id is id of parent model
     * @return list of tasks of one employee
     */
    @Override
    public List<Task> getAllByParentId(long id) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM %s WHERE employee_id = ? limit 5".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            getConnection().commit();

            while (resultSet.next()) {
                tasks.add(createTask(resultSet));
            }

            return tasks;
        }
    }
    /**
     * Method to transact task from database to model class
     * @param resultSet is set of information of employee from database
     * @return employee's model
     * @throws SQLException if some fields are not valid
     */
    private Task createTask(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Date deadLine = resultSet.getDate("dead_line");
        String description = resultSet.getString("description");
        String type = resultSet.getString("type");
        long employeeId = resultSet.getLong("employee_id");

        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDeadLine(deadLine);
        task.setDescription(description);
        task.setType(TaskType.valueOf(type));
        task.setEmployeeId(employeeId);
        return task;
    }
}
