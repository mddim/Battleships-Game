package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler extends Thread {

    private final static String COMMANDS = "Choose a command: \n" +
            "username <username> - to set username\n" +
            "list-users - to list all online users \n" +
            "create-game <game-name> - to create a game \n" +
            "join-game <game-name> - to join game <game-name> \n" +
            "join-random-game - to join a random game \n" +
            "list-games - to list all games\n" +
            "start-game - to start the game if you are the creator of the room\n" +
            "list-saved-games - to list your saved games\n" +
            "load-game <game-name> - to load a saved game\n" +
            "delete-saved-game <game-name> - to delete a saved game\n" +
            "disconnect - to disconnect\n";

    private String clientName;
    private Socket socket;

    private final List<ClientHandler> onlineClients;
    private final List<Game> games;

    private PrintWriter out = null;
    private BufferedReader in = null;

    private Game activeGame;
    private Map<Game, SavedGame> savedGames;

    private boolean isGameRunning = false;

    public ClientHandler(Socket socket, List<ClientHandler> onlineClients, List<Game> games) {

        this.socket = socket;
        this.onlineClients = onlineClients;
        this.games = games;
        this.savedGames = new HashMap<>();

    }

    public String getClientName() {
        return clientName;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public Socket getSocket() {
        return socket;
    }

    public Map<Game, SavedGame> getSavedGames() {
        return savedGames;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
    }

    public void setSavedGames(Map<Game, SavedGame> savedGames) {
        this.savedGames = savedGames;
    }

    private void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    private void changeGameRunning(boolean isGameRunning) {
        synchronized (this) {
            for (ClientHandler client : onlineClients) {
                if (client.getClientName().equals(activeGame.getPlayerTwo().getName())) {
                    client.setGameRunning(isGameRunning);
                }
                if (client.getClientName().equals(activeGame.getPlayerOne().getName())) {
                    client.setGameRunning(isGameRunning);
                }
            }
        }
    }

    @Override
    public void run() {

        try {

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.print(COMMANDS);
            out.flush();

            while (true) {

                if (!isGameRunning) {
                    //after the game has been started, the second player is reading the first input line here :(
                    //the thread is already past the if
                    String line = in.readLine();
                    String command = line.split("\\s+")[0];

                    switch (command) {

                        case "username":

                            String name = line.split("\\s+")[1];
                            registerClient(name);

                            break;

                        case "list-users":

                            listUsers();

                            break;

                        case "create-game":

                            String gameName = line.split("\\s+")[1];
                            createGame(gameName);

                            break;

                        case "join-game":

                            gameName = line.split("\\s+")[1];
                            joinGame(gameName);

                            break;

                        case "join-random-game":

                            joinRandomGame();

                            break;

                        case "list-games":

                            listGames();

                            break;

                        case "start-game":

                            startGame();

                            break;

                        case "list-saved-games":

                            listSavedGames();

                            break;

                        case "load-game":

                            gameName = line.split("\\s+")[1];
                            loadGame(gameName);

                            break;

                        case "delete-saved-game":

                            gameName = line.split("\\s+")[1];
                            deleteSavedGame(gameName);

                            break;

                        case "disconnect":

                            disconnect();

                            break;

                        default:

                            out.print("Invalid command! \n" + COMMANDS);
                            out.flush();

                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("I/O exception while closing socket.");
            }
        }

    }

    private boolean doesUsernameAlreadyExist(String username) {
        for (ClientHandler client : onlineClients) {
            if (client.getClientName() != null) {
                if (client.getClientName().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void registerClient(String name) {

        boolean correct = false;
        while (!correct) {
            if (!onlineClients.isEmpty()) {
                synchronized (this) {
                    try {
                        if (doesUsernameAlreadyExist(name)) {
                            out.println("Username is taken. Please select again! Write username only.");
                            name = in.readLine();
                        } else {
                            correct = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("I/O exception while reading username.");
                    }
                }
            }
        }

        for (ClientHandler client : onlineClients) {
            if (client == this) {
                clientName = name;
                out.println("Username set to " + clientName);
                break;
            }
        }
    }

    private void listUsers() {
        for (ClientHandler client : onlineClients) {

            this.out.println(client.getClientName());

        }
    }

    public void createGame(String gameName) {

        boolean correct = false;
        while (!correct) {
            if (!onlineClients.isEmpty()) {
                synchronized (this) {
                    if (doesGameExist(gameName)) {
                        out.println("Game with given name already exists. Please select again! Write name only.");
                        try {
                            gameName = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("I/O exception while reading game name.");
                        }
                    } else {
                        correct = true;
                    }
                }
            }
        }

        games.add(new Game(gameName, this.getClientName()));
        out.println("Game " + gameName + " has been created.");
        //joinGame(gameName);

    }

    private void listGames() {
        for (Game game : games) {

            this.out.println(game.getGameName() + " -> " + "creator: " + game.getCreator() + " | status: " +
                    game.getStatus() + " | [" + game.getCountOfLoggedPlayers() + "/2]");

        }
    }

    private boolean doesGameExist(String gameName) {
        for (Game game : games) {
            if (game.getGameName().equals(gameName)) {
                return true;
            }
        }
        return false;
    }

    private String checkGameExist(String gameName) {
        if (!doesGameExist(gameName)) {
            out.println("This game does not exist! Do you want to create a new game with " +
                    "the given name or you want to join a random game? Select (create/random game)");
            String answer = null;
            try {
                answer = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("I/O exception while reading input.");
            }
            if ("create".equals(answer)) {
                createGame(gameName);
            } else if ("random game".equals(answer)) {
                synchronized (this) {
                    for (Game game : games) {
                        if (game.getCountOfLoggedPlayers() != 2) {
                            gameName = game.getGameName();
                            break;
                        }
                    }
                }
            }
        }
        return gameName;
    }

    private String checkFullRoom(String gameName) {
        boolean correct = false;
        while (!correct) {
            synchronized (this) {
                for (Game game : games) {
                    if (game.getGameName().equals(gameName)) {
                        if (game.getCountOfLoggedPlayers() != 2) {
                            correct = true;
                        } else {
                            out.println("This room is already full. Please select another one! Write room name only.");
                            listGames();
                            try {
                                gameName = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.err.println("I/O exception while reading input.");
                            }
                        }
                    }
                }
            }
        }
        return gameName;
    }

    public void joinGame(String gameName) {
        //check if the game doesn't exist
        gameName = checkGameExist(gameName);

        //check if the room is full
        gameName = checkFullRoom(gameName);

        if (!gameName.equals("")) {
            synchronized (this) {
                for (Game game : games) {
                    if (game.getGameName().equals(gameName)) {
                        if (game.getCountOfLoggedPlayers() == 0) {
                            Player playerOne = new Player(this.getClientName(), this.getSocket(),
                                    this.getIn(), this.getOut());
                            game.addPlayerOne(playerOne);
                            this.activeGame = game;
                        } else if (game.getCountOfLoggedPlayers() == 1) {
                            Player playerTwo = new Player(this.getClientName(), this.getSocket(),
                                    this.getIn(), this.getOut());
                            game.addPlayerTwo(playerTwo);
                            this.activeGame = game;
                        }
                    }
                }
            }
            out.println("You joined game " + gameName);
            out.println(activeGame.printGameStatistics());
        }

        String message1 = "Waiting for the creator to start the game...";
        String message2 = "Type start-game to start the game.";

        if (activeGame.getCountOfLoggedPlayers() == 2) {
            if (activeGame.getCreator().equals(activeGame.getPlayerTwo().getName())) {
                activeGame.getPlayerOne().getOut().println(message1);
                activeGame.getPlayerTwo().getOut().println(message2);
            } else if (activeGame.getCreator().equals(activeGame.getPlayerOne().getName())) {
                activeGame.getPlayerTwo().getOut().println(message1);
                activeGame.getPlayerOne().getOut().println(message2);
            }
        }
    }

    private void joinRandomGame() {
        synchronized (this) {
            for (Game game : games) {
                if (game.getCountOfLoggedPlayers() != 2) {
                    String gameName = game.getGameName();
                    joinGame(gameName);
                    break;
                }
            }
        }
    }

    private void startGame() {
        int gameStatus = 0;
        if (this.getClientName().equals(activeGame.getCreator())) {
            activeGame.welcome(activeGame.getPlayerOne());
            activeGame.welcome(activeGame.getPlayerTwo());
            activeGame.setStatus("in progress");
            changeGameRunning(true);
            gameStatus = activeGame.play();
        }
        if (gameStatus == Game.SAVED_GAME) {
            saveGame();
            changeGameRunning(false);
        } else if (gameStatus == Game.GAME_ENDED) {
            activeGame.setStatus("ended");
            changeGameRunning(false);
        }
    }

    public void saveGame() {
        SavedGame savedGame = new SavedGame(activeGame.getGameName(), activeGame.getBoardPlayerOne(),
                activeGame.getBoardPlayerTwo(), activeGame.getCurrentTurn());
        savedGames.put(activeGame, savedGame);
        synchronized (this) {
            for (ClientHandler client : onlineClients) {
                if (client.getClientName().equals(activeGame.getPlayerTwo().getName()) && client != this) {
                    client.setSavedGames(this.savedGames);
                }
                if (client.getClientName().equals(activeGame.getPlayerOne().getName()) && client != this) {
                    client.setSavedGames(this.savedGames);
                }
            }
        }
        activeGame.setStatus("saved");
        String message = "Game \"" + activeGame.getGameName() + "\" saved successfully.";
        activeGame.getPlayerOne().getOut().println(message);
        activeGame.getPlayerTwo().getOut().println(message);
        changeGameRunning(false);
    }

    private void listSavedGames() {
        synchronized (this) {
            for (Map.Entry<Game, SavedGame> entry : savedGames.entrySet()) {
                out.println(entry.getKey().getGameName());
            }
        }
    }

    private void loadGame(String gameName) {
        Board boardPlayerOne = new Board();
        Board boardPlayerTwo = new Board();
        int turn = 1;
        synchronized (this) {
            for (Map.Entry<Game, SavedGame> entry : savedGames.entrySet()) {
                if (entry.getKey().getGameName().equals(gameName)) {
                    activeGame = entry.getKey();
                    boardPlayerOne = entry.getValue().getBoardPlayerOne();
                    boardPlayerTwo = entry.getValue().getBoardPlayerTwo();
                    turn = entry.getValue().getTurn();
                }
            }
        }
        activeGame.setStatus("in progress");
        changeGameRunning(true);
        int gameStatus = activeGame.continueGame(boardPlayerOne, boardPlayerTwo, turn);
        if (gameStatus == Game.SAVED_GAME) {
            saveGame();
        } else if (gameStatus == Game.GAME_ENDED) {
            activeGame.setStatus("ended");
            changeGameRunning(false);
            return;
        }
    }

    private void deleteSavedGame(String gameName) {
        synchronized (this) {
            savedGames.remove(activeGame);
            synchronized (this) {
                for (ClientHandler client : onlineClients) {
                    if (client.getClientName().equals(activeGame.getPlayerTwo().getName())) {
                        client.getSavedGames().remove(activeGame);
                    }
                }
            }
        }
        String message = "Deleted saved game \"" + gameName + "\" from your saved games.";
        activeGame.setStatus("ended");
        activeGame.getPlayerOne().getOut().println(message);
        activeGame.getPlayerTwo().getOut().println(message);
    }

    private void disconnect() {

        System.out.println(clientName + " disconnected.");
        onlineClients.remove(this);
        out.println("Disconnected");

    }

}
