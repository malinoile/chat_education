package interfaces;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);

    boolean changeUsername(String oldUsername, String newUsername);
}
