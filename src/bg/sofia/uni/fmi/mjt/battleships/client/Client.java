package bg.sofia.uni.fmi.mjt.battleships.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final int SERVER_PORT = 4444;
    private static boolean exit = false;

    public static void main(String[] args) throws IOException {
        Scanner scn = new Scanner(System.in);

        Socket socket = new Socket("localhost", SERVER_PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // sendMessage thread
        Thread sendMessage = new Thread(() -> {
            while (true) {

                String message = scn.nextLine();
                out.println(message);
                if (message.equals("disconnect")) {
                    exit = true;
                    return;
                }
                //System.out.println("Message sent to the server.");
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    if (exit) {
                        return;
                    }
                    String message = in.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    System.out.println("Could not read the message sent to this client.");
                    e.printStackTrace();
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}
