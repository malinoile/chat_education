import java.util.HashMap;

public class DBAuthService implements AuthService {
    HashMap<String, String> users = new HashMap<>();

    public DBAuthService(){
        users.put("Vasya", "password");
        users.put("Toto", "pass");
        users.put("Foo", "foopass");
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {

        if(users.containsKey(login)&&users.get(login).equals(password)) {
            return login;
        }
        return null;

    }
}
