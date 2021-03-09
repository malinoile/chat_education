import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame {

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;

    private final JPanel blockMessages = new JPanel();
    private final JPanel userPanel = new JPanel();
    private final JPanel loginPanel = new JPanel();
    private final JTextArea messageArea = new JTextArea();
    private final JTextArea clientsInformation = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(messageArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JScrollPane scrollClients = new JScrollPane(clientsInformation, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private final ClientNetwork clientNetwork = new ClientNetwork();

    public ClientGUI() {
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Chat");
        this.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientNetwork.sendMessage("@end");
                super.windowClosing(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                clientNetwork.sendMessage("@end");
                super.windowClosed(e);
            }
        });

        addCallbacks();
        renderLoginJPanel();

        blockMessages.setLayout(new FlowLayout(FlowLayout.CENTER));
        blockMessages.setBorder(BorderFactory.createTitledBorder("Chat messages"));
        blockMessages.setPreferredSize(new Dimension(WINDOW_WIDTH - 50, WINDOW_HEIGHT - 150));

        messageArea.setEditable(false);
        clientsInformation.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 220));
        scrollClients.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 470));
        blockMessages.add(scrollClients);
        blockMessages.add(scrollPane);

        userPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        userPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 400));

        JTextField userMessage = new JTextField();
        userMessage.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 470));

        JButton button = new JButton("Отправить");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = userMessage.getText();
                if (!msg.isEmpty()) {
                    clientNetwork.sendMessage(msg);
                    userMessage.setText("");
                }
            }
        });

        userPanel.add(new JLabel("Введите ваше сообщение"));
        userPanel.add(userMessage);
        userPanel.add(button);

        add(blockMessages);
        add(userPanel);

        userPanel.setVisible(false);
        blockMessages.setVisible(false);
        setVisible(true);
    }

    private void renderLoginJPanel() {
        loginPanel.setBackground(Color.white);
        loginPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        loginPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - 100, WINDOW_HEIGHT - 350));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Authorization"));
        JTextField login = new JTextField();
        JLabel loginLabel = new JLabel("Username : ");
        JLabel passwordLabel = new JLabel("Password : ");
        JPasswordField password = new JPasswordField();
        login.setPreferredSize(new Dimension(WINDOW_WIDTH - 250, 25));
        password.setPreferredSize(new Dimension(WINDOW_WIDTH - 250, 25));
        loginPanel.add(loginLabel);
        loginPanel.add(login);
        loginPanel.add(passwordLabel);
        loginPanel.add(password);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - 200, 35));
        buttonPanel.setBackground(Color.WHITE);
        JButton button = new JButton("Login");
        buttonPanel.add(button);
        button.addActionListener(e -> {
            clientNetwork.sendMessage("@login " + login.getText() + " " + String.valueOf(password.getPassword()));
            login.setText("");
            password.setText("");
        });
        loginPanel.add(buttonPanel);
        loginPanel.setVisible(true);
        this.add(loginPanel);
    }

    private void addCallbacks() {
        clientNetwork.setCallOnMessageReceived(message -> messageArea.append(message + "\n"));
        clientNetwork.setCallOnChangeClientList(clientsList -> clientsInformation.setText(clientsList));
        clientNetwork.setCallOnLogIn(str -> {
            loginPanel.setVisible(false);
            userPanel.setVisible(true);
            blockMessages.setVisible(true);
        });
        clientNetwork.setCallOnError(error -> JOptionPane.showMessageDialog(null, error, "Error message",
                JOptionPane.ERROR_MESSAGE));
        clientNetwork.setCallOnClose(isClose -> {
            if(isClose) {
                this.dispose();
            }
        });
    }
}
