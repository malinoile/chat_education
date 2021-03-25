package classes;

import interfaces.AuthService;

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
        try(
                ServerSocket serverSocket = new ServerSocket(8098);
                DBHelper dbHelper = DBHelper.getInstance()
        ) {
            System.out.println("Server wait on port 8098");
            for(;;) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.err.println("ServerSocket не был получен");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Подключение к базе данных не удалось");
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
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
        broadcastMessage(sb.toString());
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastUsers();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastUsers();
    }

}
