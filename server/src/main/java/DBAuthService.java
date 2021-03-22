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

        //System.out.println(users);
        if(users.containsKey(login)&&users.get(login).equals(password)) {
            return login;
        }
        return null;

    }

    @Override
    public boolean changeNickname(String oldNickname, String newNickname) {

        for (String key : users.keySet()) {
            if(key.equals(newNickname)) {
                return false;
            }
        }

        try(
                Connection connection = SingletonConnection.getDBConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ? WHERE username = ?");
        ) {
            statement.setString(1, newNickname);
            statement.setString(2, oldNickname);

            int count = statement.executeUpdate();

            if(count > 0) {
                users.put(newNickname, users.remove(oldNickname));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
