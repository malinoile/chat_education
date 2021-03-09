import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<ClientHandler> clients;
    private AuthService authService = new DBAuthService();

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        this.clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(8911)) {
            System.out.println("Server was started");
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessages(String message) {
        for(ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("@clients ");
        for(ClientHandler client : clients) {
            sb.append(client.getUsername()).append("/");
        }
        broadcastMessages(sb.toString());
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastUsers();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastUsers();
    }
}
