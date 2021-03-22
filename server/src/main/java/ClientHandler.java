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

    public ClientHandler(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
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
                            username = server.getAuthService().getNicknameByLoginAndPassword(tokens[1], tokens[2]);

                            if (username != null) {
                                isAuthorize = true;
                                sendMessage("@login_ok");
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
                                System.out.println(arr[1]);
                                if(server.getAuthService().changeNickname(username, arr[1])) {
                                    username = arr[1];
                                    server.broadcastUsers();
                                } else {
                                    sendMessage("@error Изменить имя пользователя не удалось.\nУкажите другое имя пользователя или попробуйте позднее");
                                }
                            }
                        } else {
                            server.broadcastMessages(username + ": " + msg);
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

    void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
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

    public String getUsername() {
        return username;
    }
}
