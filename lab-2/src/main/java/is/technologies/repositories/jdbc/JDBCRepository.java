package is.technologies.repositories.jdbc;

import is.technologies.models.Model;
import is.technologies.repositories.CRUDRepository;

import java.sql.*;

public abstract class JDBCRepository<T extends Model> implements CRUDRepository<T> {
    private final String url;
    private final String user;
    private final String password;
    protected static Connection connection;
    protected String tableName;

    protected JDBCRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Method to delete model from database by id
     * @param id of model to delete
     */
    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM %s WHERE id = ?".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
            getConnection().commit();
        }
    }

    /**
     * Method to delete model from database by model
     * @param entity is model to delete
     */
    public void deleteByEntity(T entity) throws SQLException {
        String sql = "DELETE FROM %s WHERE id = ?".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, entity.getId());

            statement.execute();
            getConnection().commit();
        }
    }

    /**
     * Method to delete all models of one type
     */
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM %s".formatted(tableName);;
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
            getConnection().commit();
        }
    }

    /**
     * Method to create and to get connection with database
     * @return connection
     * @throws SQLException if connection is unreal
     */
    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        }

        return connection;
    }

    /**
     * Method to close database connection
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
