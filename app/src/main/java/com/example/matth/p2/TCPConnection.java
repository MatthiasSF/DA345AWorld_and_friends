package com.example.matth.p2;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class that handles the communication with the server
 *
 * @author Matthias Falk
 */
public class TCPConnection {
    private Socket socket;
    private InetAddress inetAddress;
    private String ip;
    private int port;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private ThreadPool pool;
    private TCPConnectionListener listener;
    private TCPReceiver receiver;

    /**
     * Constructor
     *
     * @param ip             - the ip the application will connect to
     * @param connectionPort - the port that the application will connect to
     * @param listener       - The listener the class will send the recieved messages to
     */
    public TCPConnection(String ip, int connectionPort, TCPConnectionListener listener) {
        this.ip = ip;
        this.port = connectionPort;
        this.listener = listener;
        this.pool = new ThreadPool(2);
    }

    /**
     * Connects the apllication to the server
     */
    public void connect() {
        pool.execute(new Connect());
        pool.start();
    }

    /**
     * Disconnects from the server
     */
    public void disconnect() {
        pool.execute(new Disconnect());
    }

    /**
     * Sends an message to the server
     *
     * @param message - the String that will be sent
     */
    public void send(String message) {
        pool.execute(new Send(message));
    }

    /**
     * An receiver that receives an message from the server
     */
    private class TCPReceiver extends Thread {
        public void run() {
            String stringMessage;
            JsonParser parser = new JsonParser();
            JsonObject jsonMessage;
            try {
                while (receiver != null) {
                    stringMessage = inputStream.readUTF();
                    Log.i("Message", stringMessage);
                    jsonMessage = (JsonObject) parser.parse(stringMessage);
                    listener.receive(jsonMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                receiver = null;
            }
        }
    }

    /**
     * Connects to server and starts the receiver
     */
    private class Connect implements Runnable {
        public void run() {
            try {
                inetAddress = InetAddress.getByName(ip);
                socket = new Socket(inetAddress, port);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.flush();
                receiver = new TCPReceiver();
                receiver.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Disconnects from the server and stops the threadpool
     */
    public class Disconnect implements Runnable {
        public void run() {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                pool.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an message to server
     */
    public class Send implements Runnable {
        private String message;

        public Send(String message) {
            this.message = message;
        }

        public void run() {
            try {
                outputStream.writeUTF(message);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
