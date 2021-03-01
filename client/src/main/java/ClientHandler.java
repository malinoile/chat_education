import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public ClientHandler(Server server, Socket socket, Integer number) {
        try {
            this.server = server;
            this.socket = socket;
            this.username = "Client " + number;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                server.subscribe(this);
                boolean isChatting = true;
                while(isChatting) {
                    String message = null;
                    try {
                        message = in.readUTF();
                        if(message.startsWith("@")) {
                            if(message.equalsIgnoreCase("@end")) {
                                isChatting = false;
                                disconnect();
                            }
                        } else {
                            server.broadcastMessage(username + ": " + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
