package classes;

import interfaces.AuthService;

public class DBAuthService implements AuthService {

    private final DBHelper dbHelper = DBHelper.getInstance();

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        return dbHelper.authorization(login, password);
    }

    @Override
    public boolean changeUsername(String oldUsername, String newUsername) {
        return dbHelper.updateUsername(oldUsername, newUsername) != 0;
    }
}
