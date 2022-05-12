import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server implements Runnable {

    private JTextField jTextField;
    private JPanel jPanelEnter;
    private JPanel jPanelListen;
    private JProgressBar jProgressBarr;
    private JLabel portValue;
    private Thread thread;
    private String port;
    private DatagramSocket datagramSocket = null;

    public void createSocket(int port) {
        try {
            this.datagramSocket = new DatagramSocket(port);
        } catch (SocketException exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    public DatagramSocket getSocket() {
        return this.datagramSocket;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return this.port;
    }

    public Server() {
        DatagramSocket datagramSocket = null;
    }

    public Server(JFrame jFrame) {
        Server server = new Server();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Server");
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);

        jPanelEnter = new JPanel();
        jPanelListen = new JPanel();
        jPanelEnter.setVisible(true);
        jPanelListen.setVisible(false);

        jTextField = new JTextField();
        JLabel jLabel = new JLabel("Numarul portului este : ");
        JButton jButton = new JButton("Enter");
        jPanelEnter.setBorder(BorderFactory.createEmptyBorder(100, 70, 100, 70));
        jPanelEnter.setLayout(new GridLayout(0, 3));
        jFrame.add(jPanelEnter, BorderLayout.CENTER);
        jPanelEnter.add(jLabel);
        jPanelEnter.add(jTextField);
        jPanelEnter.add(jButton);
        jTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) {
                    e.consume();
                }
            }
        });
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if (jTextField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Introduceti numarul la port!");
                    return;
                }
                try {
                    if (Integer.parseInt(jTextField.getText()) < 1024) {
                        JOptionPane.showMessageDialog(null, "Va rugam sa introduceti un numar mai mare decât 1024");
                        return;
                    }
                } catch (final NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
                    return;
                }
                try {
                    if (Integer.parseInt(jTextField.getText()) > 65535) {
                        JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
                        return;
                    }
                } catch (final NumberFormatException exception) {
                    System.out.print(exception);
                    JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
                    return;
                }
                String port = jTextField.getText();
                server.setPort(port);
                jFrame.remove(jPanelEnter);
                jFrame.add(jPanelListen, BorderLayout.CENTER);
                jPanelListen.setVisible(true);
                portValue.setText(portValue.getText() + port);
                int socket = Integer.parseInt(server.getPort());
                server.createSocket(socket);
                thread = new Thread(server);
                thread.start();
            }
        });

        jProgressBarr = new JProgressBar();
        JButton newPort = new JButton("Schimbati portul?");
        jPanelListen.setBorder(BorderFactory.createEmptyBorder(70, 70, 100, 70));
        jPanelListen.setLayout(new GridLayout(3, 0));
        jPanelListen.add(jProgressBarr);
        final Timer t = new Timer(35, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jProgressBarr.setValue(jProgressBarr.getValue() + 1);
                if (jProgressBarr.getValue() == 100) {
                    jProgressBarr.setValue(0);
                }
            }
        });
        t.start();

        portValue = new JLabel("Portul curent:  ", SwingConstants.CENTER);
        portValue.setFont(new Font("Arial", Font.BOLD, 15));
        jPanelListen.add(portValue);
        jPanelListen.add(newPort);
        newPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.interrupt();
                jTextField.setText("");
                portValue.setText("Portul curent: ");
                jPanelListen.setVisible(false);
                jFrame.remove(jPanelListen);
                jFrame.add(jPanelEnter, BorderLayout.CENTER);
                jPanelEnter.setVisible(true);
                server.getSocket().close();
            }
        });
        jFrame.pack();
    }

    public void run() {
        try {
            System.out.println(this.getPort());
            byte[] buffer = new byte[500];

            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Se executa...");
                this.getSocket().receive(request);
                try {
                    String number = "";
                    System.out.println("Cererea:" + new String(request.getData()).trim());

                    if (new String(request.getData()).trim().length() != request.getLength()) {
                        number = new String(request.getData()).trim().substring(0, request.getLength());
                    } else {
                        number = new String(request.getData()).trim();
                    }
                    BigInteger messageNumber = new BigInteger(number);
                    if (messageNumber.compareTo(BigInteger.ZERO) < 0) {
                        byte message[] = ("Nu exista").getBytes(StandardCharsets.US_ASCII);
                        DatagramPacket reply = new DatagramPacket(message, message.length, request.getAddress(), request.getPort());
                        this.getSocket().send(reply);
                        continue;
                    }
                    String result = String.valueOf(Math.log(messageNumber.doubleValue()));
                    byte[] message = ("Logaritmul numarul: " + number + " este " + result).getBytes(StandardCharsets.US_ASCII);
                    DatagramPacket reply = new DatagramPacket(message, message.length, request.getAddress(), request.getPort());
                    this.getSocket().send(reply);
                } catch (NumberFormatException exception) {
                    byte[] message = ("Nu este un numar").getBytes(StandardCharsets.US_ASCII);
                    DatagramPacket reply = new DatagramPacket(message, message.length, request.getAddress(), request.getPort());
                    this.getSocket().send(reply);
                    continue;
                }
            }
        } catch (SocketException exception) {
            System.out.println("Socket-ul pe portul nr. " + this.getPort() + " a fost inchis");
            this.setPort("");
            return;
        } catch (IOException IOexception) {
            JOptionPane.showMessageDialog(null, "Eroare " + IOexception.getMessage());

        } finally {
            if (this.getSocket() != null) {
                System.out.println("Executat");
            }
        }
        System.out.println("Iesire");
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        new Server(jFrame);
    }
}