import java.sql.*;
import java.util.HashMap;

public class DBAuthService implements AuthService {
    HashMap<String, String> users = new HashMap<>();

    public DBAuthService() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try(
                Connection connection = SingletonConnection.getDBConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users")
                ) {
            while(resultSet.next()) {
                users.put(resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        if(users.containsKey(login)&&users.get(login).equals(password)) {
            return login;
        }
        return null;

    }
}
