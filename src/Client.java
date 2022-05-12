
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable{
    private JTextField textFieldHost;
    private JTextField textFieldPort;
    private JPanel jPanelEnter;
    private JPanel jPanelMessage;

    private JTextField jFieldSend;
    private JTextField message;
    private JLabel hostValue;
    private JLabel portValue;

    private String portReceiver;
    private String portSender;
    private String hostReceiver;
    private String messageNumber;

    private DatagramSocket datagramSocket = null;
    private Thread thread;
    public Client(){

    }

    public DatagramSocket getDatagramSocket(){
        return this.datagramSocket;
    }

    public void setPortReceiver(String portReceiver) {
        this.portReceiver = portReceiver;
    }
    public String getPortReceiver(){
        return this.portReceiver;
    }

    public void setPortSender(String portSender){
        this.portSender = portSender;
    }
    public String getPortSender(){
        return this.portSender;
    }

    public void setHostReceiver(String host){
        this.hostReceiver = host;
    }

    public String getHostReceiver(){
        return this.hostReceiver;
    }

    public void setMessageNumber(String messageNumber){
        this.messageNumber = messageNumber;
    }

    public String getMessageNumber(){
        return this.messageNumber;
    }

    public Client(JFrame jFrame){
        Client client = new Client();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Client");
        jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);

        jPanelEnter = new JPanel();
        jPanelMessage = new JPanel();
        jPanelEnter.setVisible(true);
        jPanelMessage.setVisible(false);


        JPanel host = new JPanel(new GridLayout(1, 2));
        JPanel port = new JPanel(new GridLayout(1, 2));
        JPanel button = new JPanel(new GridLayout(1, 1));

        jPanelEnter.setBorder(BorderFactory.createEmptyBorder(100,60,90,60));
        jPanelEnter.setLayout(new GridLayout(6,0));
        jFrame.add(jPanelEnter,BorderLayout.CENTER);

        textFieldHost = new JTextField();
        JLabel labelHost = new JLabel("Trimite mesaj: ");
        textFieldPort = new JTextField();
        JLabel labelPort = new JLabel("La portul: ", SwingConstants.RIGHT);
        JButton jButton = new JButton("Enter");

        jPanelEnter.add(host);
        jPanelEnter.add(port);
        jPanelEnter.add(button);

        host.add(labelHost);
        host.add(textFieldHost);

        port.add(labelPort);
        port.add(textFieldPort);

        button.add(jButton);

        JLabel jLabelHost = new JLabel("Trimite: ");
        hostValue = new JLabel();
        JLabel jLabelPortReceiver = new JLabel("La portul nr: ");
        JLabel jLabelPortSender = new JLabel("Trimis de la port: ");
        jLabelPortSender.setForeground(Color.blue);
        jFieldSend = new JTextField();
        jFieldSend.setForeground(Color.blue);
        portValue = new JLabel();
        JLabel jLabelMessage = new JLabel("Introduceti numarul pentru a calcula logaritmul:");
        message = new JTextField();
        JButton jButtonSend = new JButton("Trimite numarul");
        JButton buttonBack = new JButton("Schimbi destinatarul?");
        jPanelMessage.setBorder(BorderFactory.createEmptyBorder(100,40,90,40));
        jPanelMessage.setLayout(new GridLayout(6,0,2,2));

        JPanel jPanel1 = new JPanel(new GridLayout(2, 3));
        JPanel jPanel2 = new JPanel(new GridLayout(2, 3));
        JPanel jPanel3 = new JPanel(new GridLayout(2, 2));
        JPanel jPanel4 = new JPanel(new GridLayout(2, 2));
        JPanel jPanel5 = new JPanel(new GridLayout(2, 0));
        JPanel jPanel6 = new JPanel(new GridLayout(2, 0));

        jPanelMessage.add(jPanel1);
        jPanelMessage.add(jPanel2);
        jPanelMessage.add(jPanel4);
        jPanelMessage.add(jPanel3);
        jPanelMessage.add(jPanel5);
        jPanelMessage.add(jPanel6);

        jPanel1.add(jLabelHost);
        jPanel1.add(hostValue);

        jPanel2.add(jLabelPortReceiver);
        jPanel2.add(portValue);

        jPanel3.add(jLabelMessage);
        jPanel3.add(message);

        jPanel4.add(jLabelPortSender);
        jPanel4.add(jFieldSend);

        jPanel5.add(jButtonSend);
        jPanel6.add(buttonBack);

        CharInputRestriction restrict = new CharInputRestriction();
        textFieldPort.addKeyListener(restrict);
        jFieldSend.addKeyListener(restrict);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isValidPort(textFieldPort.getText()) || !isValidHost(textFieldHost.getText())){
                    return;
                }
                client.setHostReceiver(textFieldHost.getText());
                client.setPortReceiver(textFieldPort.getText());

                jPanelEnter.setVisible(false);
                jFrame.remove(jPanelEnter);
                jFrame.add(jPanelMessage, BorderLayout.CENTER);
                jPanelMessage.setVisible(true);
                hostValue.setText(textFieldHost.getText());
                hostValue.setForeground(Color.red);
                portValue.setText(textFieldPort.getText());
                portValue.setForeground(Color.red);
                jFrame.pack();

            }
        });

        jButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isValidPort(jFieldSend.getText())){
                    return;
                }
                client.setPortSender(jFieldSend.getText());
                client.setMessageNumber(message.getText());
                thread = new Thread(client);
                thread.start();
            }
        });

        buttonBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanelMessage.setVisible(false);
                jFrame.remove(jPanelMessage);
                jFrame.add(jPanelEnter,BorderLayout.CENTER);
                jPanelEnter.setVisible(true);
                textFieldHost.setText("");
                textFieldPort.setText("");
                jFrame.pack();
            }
        });
        jFrame.pack();
    }

    public Boolean isValidHost(String host){
        if(host.length()==0) {
            JOptionPane.showMessageDialog(null,"Trebuie completat!");
            return false;
        }
        return true;
    }

    public Boolean isValidPort(String port){
        try{
            if(port.length() == 0){
                JOptionPane.showMessageDialog(jPanelEnter,"Numărul portului de destinație!");
                return false;
            }
            if(Integer.parseInt(port)<1024) {
                JOptionPane.showMessageDialog(null, "Va rugam sa introduceti un numar mai mare decât 1024 ");
                return false;
            }
        } catch (final NumberFormatException exception){
            JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
            return false;
        }
        try{
            if(Integer.parseInt(port) > 65535){
                JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
                return false;
            }
        }catch (final NumberFormatException exception){
            System.out.print(exception);
            JOptionPane.showMessageDialog(null, "Numarul maxim de porturi este 65535, portul pe care l-ati introdus nu exista!");
            return false;
        }
        return true;
    }

    public class CharInputRestriction extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent keyEvent){

            if (!Character.isDigit(keyEvent.getKeyChar())) {
                keyEvent.consume();
            }
        }
    }

    public void run() {
        try {
            datagramSocket = this.getDatagramSocket();
            datagramSocket = new DatagramSocket(Integer.valueOf(this.getPortSender()));
            System.out.println(this.getMessageNumber());
            byte[] message = this.getMessageNumber().trim().getBytes(StandardCharsets.US_ASCII);
            System.out.println(new String(message,StandardCharsets.US_ASCII));
            InetAddress aHost = InetAddress.getByName(this.getHostReceiver());
            int serverPort = Integer.valueOf(this.getPortReceiver()).intValue();
            DatagramPacket request = new DatagramPacket(message, message.length, aHost, serverPort);
            System.out.println(new String(request.getData()).trim());
            datagramSocket.send(request);
            System.out.println("Sent");
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(reply);
            JOptionPane.showMessageDialog(jPanelMessage, "Server a raspuns: "+ new String(reply.getData()).trim());
        } catch (SocketException e) {
            System.out.println("Socket " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O " + e.getMessage());
        } finally {
            if (datagramSocket!= null) {
                this.setMessageNumber("");
                datagramSocket.close();
            }
        }
    }

    public static void main(String [] args){
        JFrame jFrame = new JFrame();
        new Client(jFrame);
    }
}

