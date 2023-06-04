import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
    private Socket socket;
    private int portNumber = 3457;
    private BufferedReader reader;
    private PrintWriter writer;

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        try {
            System.out.println("Connecting to server...");
            socket = new Socket("localhost", portNumber);
            System.out.println("Connection established!");

            GUI();
            Event();

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Event() {
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    writer.println(contentToSend);
                    writer.flush();
                    messageInput.setText("");
                }
            }
        });
    }

    private void GUI() {
        this.setTitle("Client Message [End]");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background colors
        Color darkGreen = new Color(29, 53, 43);
        Color lightGreen = new Color(96, 151, 119);
        Color white = new Color(255, 255, 255);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);

        // Set background colors
        heading.setOpaque(true);
        heading.setBackground(darkGreen);
        heading.setForeground(white);
        messageArea.setBackground(lightGreen);
        messageInput.setBackground(lightGreen);

        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader thread started.");
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null || message.equalsIgnoreCase("exit")) {
                        JOptionPane.showMessageDialog(this, "Connection is closed by server");
                        messageInput.setEnabled(false);
                        break;
                    }
                    messageArea.append("Server: " + message + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed by server.");
            }
        };
        new Thread(r1).start();
    }

    
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer thread started.");
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String content = br1.readLine();
                    if (content.equalsIgnoreCase("exit")) {
                        writer.println("exit");
                        socket.close();
                        break;
                    }
                    writer.println(content);
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is the client.");
        Client client = new Client();
        client.startReading();
        client.startWriting();
    }
}
