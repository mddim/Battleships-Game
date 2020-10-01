package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.Board;
import bg.sofia.uni.fmi.mjt.battleships.Ship;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {

    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Board board;
    private Ship[] ships;

    public Player(String name, Socket socket, BufferedReader in, PrintWriter out) {
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
        board = new Board();
    }

    public String getName() {
        return name;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public Board getBoard() {
        return board;
    }

    public Ship[] getShips() {
        return ships;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }
}
