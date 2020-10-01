package bg.sofia.uni.fmi.mjt.battleships.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 4444;
    private static final int MAX_EXECUTOR_THREADS = 10;

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static List<ClientHandler> onlineClients = new CopyOnWriteArrayList<>();
    private static List<Game> games = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started and is listening for requests.");
        } catch (IOException e) {
            System.out.println("Exception while creating server socket.");
            e.printStackTrace();
        }

        int clientID = 1;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                ClientHandler currentClient = new ClientHandler(clientSocket, onlineClients, games);
                onlineClients.add(currentClient);
                executor.execute(currentClient);
                System.out.println("Client with id: " + clientID + " is connected.");
                clientID++;

            } catch (IOException e) {
                System.out.println("Client could not be connected.");
                e.printStackTrace();
            }
        }
    }
}
