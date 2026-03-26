package com.example;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example).
 *
 * This version of the program creates a new thread for
 * every connection request.
 */
public class ChatServerWithThreads {

    public static final int LISTENING_PORT = 6081;

    public static void main(String[] args) {

        ServerSocket listener;  
        Socket connection;      

        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                connection = listener.accept();
            
                ConnectionHandler h = new ConnectionHandler(connection);
                h.start();
                 
            }
        }
        catch (Exception e) {
            System.out.println("server shut down");
            System.out.println("error:  " + e);
            return;
        }

    }

    private static class ConnectionHandler extends Thread {
        private static volatile ArrayList<ConnectionHandler> handlers = new ArrayList<ConnectionHandler>(); 
        private static int nextClientId = 0;
        private int clientId;
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        ConnectionHandler(Socket socket) {
            client = socket;
            this.clientId = ++nextClientId;
            try{
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());

            synchronized (handlers) {
            handlers.add(this);
            }}
            catch(Exception e){}
           
        }
        public void run() {
            String clientAddress = client.getInetAddress().toString();
            while(true) {
                try {
                    String message = (String)ois.readObject();
                    if(!message.equals("disconnect")){
                        System.out.println("Client " + clientId + ": " + message);
                        
                        synchronized (handlers) {
                            for (ConnectionHandler h : handlers){
                                try{
                                    h.oos.writeObject("Client " + clientId + ": " + message);
                                    h.oos.flush();
                                }
                                catch (IOException e){
                                }
                            }
                        }
                    }
                    else{
                        System.out.println("Client " + clientId + " closing connection");
                        break;
                       
                    }
                }
                catch(EOFException e){
                    System.out.println("Client " + clientId + " disconnected: " + clientAddress);
                    synchronized (handlers) {
                    handlers.remove(this);
                    }
                    break;
                }
                catch (Exception e){
                    System.out.println("Error on connection with Client " + clientId + ": "
                            + clientAddress + ": " + e);
                }
            }
        }
    }


}
