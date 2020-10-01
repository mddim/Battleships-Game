package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.Board;
import bg.sofia.uni.fmi.mjt.battleships.Cell;
import bg.sofia.uni.fmi.mjt.battleships.Ship;
import bg.sofia.uni.fmi.mjt.battleships.enums.ShipType;
import bg.sofia.uni.fmi.mjt.battleships.exceptions.IllegalShipPlacementException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class Game {

    private static final int SHIPS_AMOUNT = 10;
    private static final int FIRST_PLAYER_TURN = 1;
    private static final int SECOND_PLAYER_TURN = 2;
    public static final int GAME_ENDED = 1;
    public static final int SAVED_GAME = 2;

    private String gameName;
    private String creator;
    private String status;
    private int turn;

    private Player playerOne;
    private Player playerTwo;

    private Board boardPlayerOne;
    private Board boardPlayerTwo;

    private int countOfLoggedPlayers;

    private PrintWriter playerOneOut;
    private BufferedReader playerOneIn;
    private PrintWriter playerTwoOut;
    private BufferedReader playerTwoIn;

    public Game(String gameName, String creator) {
        this.gameName = gameName;
        this.creator = creator;
        this.status = "pending";
        this.countOfLoggedPlayers = 0;

        this.turn = FIRST_PLAYER_TURN;
    }

    public void addPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
        this.countOfLoggedPlayers = 1;
        this.boardPlayerOne = playerOne.getBoard();
    }

    public void addPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
        this.countOfLoggedPlayers = 2;
        this.boardPlayerTwo = playerTwo.getBoard();
    }

    public void setInputAndOutput() {
        playerOneOut = playerOne.getOut();
        playerOneIn = playerOne.getIn();
        playerTwoOut = playerTwo.getOut();
        playerTwoIn = playerTwo.getIn();
    }

    public String getGameName() {
        return gameName;
    }

    public int getCountOfLoggedPlayers() {
        return countOfLoggedPlayers;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public String getCreator() {
        return creator;
    }

    public String getStatus() {
        return status;
    }

    public int getCurrentTurn() {
        return turn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Board getBoardPlayerOne() {
        return boardPlayerOne;
    }

    public Board getBoardPlayerTwo() {
        return boardPlayerTwo;
    }

    private void changeTurn() {
        turn = (turn == FIRST_PLAYER_TURN) ? SECOND_PLAYER_TURN : FIRST_PLAYER_TURN;
    }

    public String printGameStatistics() {
        String output = "";
        if (countOfLoggedPlayers == 0) {
            output = "No players in the game room: \n" +
                    "Player 1: \n" +
                    "Player 2: \n";
        } else if (countOfLoggedPlayers == 1) {
            output = "One player in the game room: \n" +
                    "Player 1: " + playerOne.getName() + "\n" +
                    "Player 2: ";
        } else if (countOfLoggedPlayers == 2) {
            output = "Two players in the game room: \n" +
                    "Player 1: " + playerOne.getName() + "\n" +
                    "Player 2: " + playerTwo.getName();
        }
        return output;
    }

    public Cell convertInputCoordinatesToCell(String input) {
        int x, y;
        String[] rowAndCol = input.split("(?<=\\D)(?=\\d)");
        x = switch (rowAndCol[0]) {
            case "A" -> 0;
            case "B" -> 1;
            case "C" -> 2;
            case "D" -> 3;
            case "E" -> 4;
            case "F" -> 5;
            case "G" -> 6;
            case "H" -> 7;
            case "I" -> 8;
            case "J" -> 9;
            default -> -1;
        };
        y = Integer.parseInt(rowAndCol[1]) - 1;
        return new Cell(x, y);
    }

    public boolean validInputCoordinate(String input) {
        if (Pattern.matches("^[A-J][1-9]$", input) || Pattern.matches("^[A-J]10$", input)) {
            String[] rowAndCol = input.split("(?<=\\D)(?=\\d)");
            char row = rowAndCol[0].charAt(0);
            int column = Integer.parseInt(rowAndCol[1]);
            return row >= 'A' && row <= 'J' && column >= 1 && column <= 10;
        }
        return false;
    }

    public boolean validInputShipCoordinates(String input, int countOfCells) {
        if (Pattern.matches("..\\s..", input) || Pattern.matches("...\\s...", input) ||
                Pattern.matches("...\\s..", input) || Pattern.matches("..\\s...", input)) {
            String[] shipCoordinates = input.split("\\s");
            if (!validInputCoordinate(shipCoordinates[0]) || !validInputCoordinate(shipCoordinates[1])) {
                return false;
            }
            Cell startCell = convertInputCoordinatesToCell(shipCoordinates[0]);
            Cell endCell = convertInputCoordinatesToCell(shipCoordinates[1]);
            Ship ship = new Ship(ShipType.values()[countOfCells - 2], startCell, endCell);
            return ship.isShipValid() && validCoordinates(startCell) && validCoordinates(endCell);
        }
        return false;
    }

    private boolean validCoordinates(Cell cell) {
        return cell.getX() >= 0 && cell.getX() <= 9 && cell.getY() >= 0 && cell.getY() <= 9;
    }

    public Ship[] setShipFromCategory(int countOfCells, BufferedReader in, PrintWriter out) {
        String input = "";
        Ship[] ships = new Ship[6 - countOfCells];
        out.println(countOfCells + " cells long ships: ");
        out.flush();
        for (int i = 1; i <= 6 - countOfCells; i++) {
            out.print("#" + i + ": \n");
            out.flush();
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("I/O exception while reading ship coordinates.");
            }
            while (!validInputShipCoordinates(input, countOfCells)) {
                out.println("Invalid ship coordinates! Select again.");
                out.flush();
                try {
                    input = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("I/O exception while reading ship coordinates.");
                }
            }
            //TO DO: validate ships overlapping
            //out.println(input);
            String[] shipCoordinates = input.split(" ");
            Cell startCell = convertInputCoordinatesToCell(shipCoordinates[0]);
            Cell endCell = convertInputCoordinatesToCell(shipCoordinates[1]);
            ships[i - 1] = new Ship(ShipType.values()[countOfCells - 2], startCell, endCell);
        }
        return ships;
    }

    public Ship[] readShips(BufferedReader in, PrintWriter out) {
        Ship[] ships = new Ship[SHIPS_AMOUNT];
        out.println("Please select your ships! Write first and last cell of the ship. (e.g. A1 A5)");
        int i = 0;
        for (int j = 5; j > 1; j--) {
            Ship[] shipsOfType = setShipFromCategory(j, in, out);
            for (Ship ship : shipsOfType) {
                ships[i] = ship;
                i++;
            }
        }
        return ships;
    }

    public Cell selectHitField(BufferedReader in, PrintWriter out) {
        out.println("Please select hit position!");
        String input = "";
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("I/O exception while reading hit field.");
        }
        if (input.equals("save-game")) {
            return null;
        }
        while (!validInputCoordinate(input)) {
            out.println("Invalid input! Please select again.");
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("I/O exception while reading hit field.");
            }
        }
        Cell field = convertInputCoordinatesToCell(input);
        if (!validCoordinates(field)) {
            throw new IllegalShipPlacementException("Hit coordinates are not valid!");
        }
        return field;
    }

    public void welcome(Player player) {
        setInputAndOutput();

        PrintWriter playerOut = player.getOut();

        playerOut.println("Lets play!");
    }

    private int gameLogicLoop() {

        while (!boardPlayerOne.checkLost() && !boardPlayerTwo.checkLost()) {
            if (turn == FIRST_PLAYER_TURN) {
                playerTwoOut.println(playerOne.getName() + "'s turn.");
                Cell field = selectHitField(playerOneIn, playerOneOut);
                if (field == null) {
                    return SAVED_GAME;
                }
                while (boardPlayerTwo.checkFieldAlreadySelected(field)) {
                    playerOneOut.println("Position already selected. Select again.");
                    field = selectHitField(playerOneIn, playerOneOut);
                }
                boardPlayerTwo.hitCell(field);
                playerOneOut.println("\t  YOUR BOARD");
                boardPlayerOne.printBoard(playerOneOut);
                playerOneOut.println("\n\t  ENEMY BOARD");
                boardPlayerTwo.printBoardForOpponent(playerOneOut);

            } else if (turn == SECOND_PLAYER_TURN) {
                playerOneOut.println(playerTwo.getName() + "'s turn.");
                Cell field = selectHitField(playerTwoIn, playerTwoOut);
                if (field == null) {
                    return SAVED_GAME;
                }
                while (boardPlayerOne.checkFieldAlreadySelected(field)) {
                    playerTwoOut.println("Position already selected. Select again.");
                    field = selectHitField(playerTwoIn, playerTwoOut);
                }
                boardPlayerOne.hitCell(field);
                playerTwoOut.println("\t  YOUR BOARD");
                boardPlayerTwo.printBoard(playerTwoOut);
                playerTwoOut.println("\n\t  ENEMY BOARD");
                boardPlayerOne.printBoardForOpponent(playerTwoOut);
            }
            changeTurn();
        }
        String winner = "";
        if (boardPlayerTwo.checkLost()) {
            winner = playerOne.getName() + "is the winner.";
        } else if (boardPlayerOne.checkLost()) {
            winner = playerTwo.getName() + "is the winner.";
        }
        playerOneOut.println(winner);
        playerTwoOut.println(winner);
        return GAME_ENDED;
    }

    public int play() {

        //set ships on boards
        Thread t1 = new Thread(() -> {
            boardPlayerOne.printBoard(playerOneOut);
            playerOne.setShips(readShips(playerOneIn, playerOneOut));
            boardPlayerOne.setShipsOnBoard(playerOne.getShips());
        });

        Thread t2 = new Thread(() -> {
            boardPlayerTwo.printBoard(playerTwoOut);
            playerTwo.setShips(readShips(playerTwoIn, playerTwoOut));
            boardPlayerTwo.setShipsOnBoard(playerTwo.getShips());
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return gameLogicLoop();
    }

    public int continueGame(Board board1, Board board2, int currentTurn) {

        boardPlayerOne = board1;
        boardPlayerTwo = board2;
        turn = currentTurn;

        return gameLogicLoop();

    }

}
