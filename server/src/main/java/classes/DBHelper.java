package classes;

import java.sql.*;

public class DBHelper implements AutoCloseable{

    private static DBHelper instance;
    private static Connection connection;

    private static PreparedStatement selectStatement;
    private static PreparedStatement updateStatement;

    private DBHelper() {}

    public static DBHelper getInstance() {
        if(instance == null) {
            initConnection();
            initPreparedStatements();

            instance = new DBHelper();
        }

        return instance;
    }

    private static void initConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Возникла ошибка при подключении");
            e.printStackTrace();
        }
    }

    private static void initPreparedStatements() {
        try {
            selectStatement = connection.prepareStatement("SELECT username, password FROM users " +
                    "WHERE LOWER(username) = LOWER(?) AND password = ?");
            updateStatement = connection.prepareStatement("UPDATE users SET username = LOWER(?) WHERE LOWER(username) = LOWER(?)");
        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации PreparedStatements");
            e.printStackTrace();
        }
    }

    public static String authorization(String username, String password) {
        try {
            selectStatement.setString(1, username);
            selectStatement.setString(2, password);
            ResultSet resultSet = selectStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int updateUsername(String oldUsername, String newUsername) {
        try {
            updateStatement.setString(1, newUsername);
            updateStatement.setString(2, oldUsername);
            return updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void close() throws Exception {
        connection.close();
        selectStatement.close();
        updateStatement.close();
    }
}
