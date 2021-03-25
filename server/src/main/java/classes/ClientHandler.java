package classes;

import interfaces.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private String username;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                boolean isChatting = true;
                String msg = null;
                boolean isAuthorize = false;
                try {
                    while (!isAuthorize && isChatting) {
                        msg = in.readUTF();

                        if (msg.equalsIgnoreCase("@end")) {
                            sendMessage(msg);
                            isChatting = false;
                        }

                        if (msg.startsWith("@login ")) {
                            String tokens[] = msg.split("\\s");
                            username = server.getAuthService().getUsernameByLoginAndPassword(tokens[1], tokens[2]);

                            if (username != null) {
                                isAuthorize = true;
                                sendMessage("@login_ok " + username);
                                server.subscribe(this);
                            } else {
                                sendMessage("@error Данные для входа указаны некорректно");
                            }
                        }
                    }

                    while (isChatting) {
                        msg = in.readUTF();
                        System.out.println(msg);
                        if (msg.startsWith("@")) {
                            if (msg.equalsIgnoreCase("@end")) {
                                isChatting = false;
                                sendMessage(msg);
                            } else if (msg.startsWith("@change")) {
                                String arr[] = msg.split("\\s", 2);
                                if(server.getAuthService().changeUsername(username, arr[1])) {
                                    username = arr[1];
                                    server.broadcastUsers();
                                    server.broadcastMessage("@change "+username);
                                } else {
                                    sendMessage("@error Изменить имя пользователя не удалось.\nУкажите другое имя пользователя или попробуйте позднее");
                                }
                            }
                        } else {
                            server.broadcastMessage(username + ": " + msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
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

    private void disconnect() {
        server.unsubscribe(this);
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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
