package com.example;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class SocketClientExample {
    /*
     * Modify this example so that it opens a dialogue window using java swing,
     * takes in a user message and sends it
     * to the server. The server should output the message back to all connected clients
     * (you should see your own message pop up in your client as well when you send it!).
     *  We will build on this project in the future to make a full fledged server based game,
     *  so make sure you can read your code later! Use good programming practices.
     *  ****HINT**** you may wish to have a thread be in charge of sending information
     *  and another thread in charge of receiving information.
    */
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 6081); 
        ObjectOutputStream   oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        JFrame frame = new JFrame("Client Chat");
            JTextArea serverText = new JTextArea();
            JTextField clientText = new JTextField();

            serverText.setEditable(false);
            clientText.setEditable(true);

            frame.setLayout(new BorderLayout());
            frame.setSize(400,400);
            frame.add(serverText, BorderLayout.CENTER);
            frame.add(clientText, BorderLayout.SOUTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        oos.writeObject("disconnect");
                        oos.flush();
                    } catch (IOException ex) {
                    }
                }
            });

            Thread t = new Thread(() -> {
                boolean running = true;
                while (running) {
                    try {
                        String message = (String)ois.readObject();
                        serverText.append(message + "\n");
                    } catch (Exception e) {
                        serverText.append("disconnected\n");
                        running = false;
                    }
                }
            });
            t.start();

            clientText.addActionListener(e -> {
            String input = clientText.getText();
            clientText.setText("");

try{
oos.writeObject(input);
            oos.flush();
}
catch (IOException etwo){
}   
        });
    }
}
