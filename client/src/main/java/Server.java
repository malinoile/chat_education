import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private List<ClientHandler> clients;
    private AtomicInteger number = new AtomicInteger(1);

    public Server() {
        this.clients = new ArrayList<>();
        try(ServerSocket serverSocket = new ServerSocket(8180)) {
            System.out.println("Server was started");
            while(true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket, number.getAndIncrement());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for(ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder();
        sb.append("@clients ");
        for(ClientHandler client : clients) {
            sb.append(client.getUsername()).append(" / ");
        }
        broadcastMessage(sb.toString());
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

}
