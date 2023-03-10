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

    public void deleteById(long id) throws SQLException {
        String sql = "DELETE FROM %s WHERE id = ?".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.execute();
            getConnection().commit();
        }
    }

    public void deleteByEntity(T entity) throws SQLException {
        String sql = "DELETE FROM %s WHERE id = ?".formatted(tableName);
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setLong(1, entity.getId());

            statement.execute();
            getConnection().commit();
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM %s".formatted(tableName);;
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(sql);
            getConnection().commit();
        }
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
        }

        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
