package is.technologies.repositories.jdbc;

import is.technologies.enums.TaskType;
import is.technologies.models.Task;
import is.technologies.repositories.ChildEntityRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository extends Repository<Task> implements ChildEntityRepository<Task> {
    public TaskRepository(String url, String user, String password) {
        super(url, user, password);
        this.tableName = "tasks";
    }

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

    @Override
    public List<Task> getAllByParentId(long id) throws SQLException {
        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM %s WHERE employee_id = ?".formatted(tableName);
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
