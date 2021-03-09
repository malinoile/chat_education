import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientNetwork {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientNetwork() {
        connect();
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8180);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                boolean isChatting = true;
                while(isChatting) {
                    try {
                        String message = in.readUTF();
                        if(message.equalsIgnoreCase("@end")) {
                            isChatting = false;
                            closeConnect();
                        } else if(message.startsWith("@clients ")) {
                            System.out.println(message);
                        } else {
                            System.out.println(message);
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

    public boolean sendMessage(String message) {
        try {
            out.writeUTF(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnect() {
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
