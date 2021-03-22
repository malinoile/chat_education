import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonConnection {

    private static Connection connection;

    private SingletonConnection() {}

    public static Connection getDBConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        }

        return connection;
    }
}
