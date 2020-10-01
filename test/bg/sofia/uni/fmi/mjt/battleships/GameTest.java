package bg.sofia.uni.fmi.mjt.battleships;

import bg.sofia.uni.fmi.mjt.battleships.enums.ShipType;
import bg.sofia.uni.fmi.mjt.battleships.server.Game;
import bg.sofia.uni.fmi.mjt.battleships.server.Player;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

import static org.junit.Assert.*;

public class GameTest {
    private static final String COORDINATES_FIRST_PLAYER = "A1 A5\n" +
            "B1 B4\n" +
            "C1 C4\n" +
            "D1 D3\n" +
            "E1 E3\n" +
            "F1 F3\n" +
            "G1 G2\n" +
            "H1 H2\n" +
            "I1 I2\n" +
            "J1 J2\n";

    private static final char[][] BOARD_FIRST_PLAYER = {{'*', '*', '*', '*', '*', '_', '_', '_', '_', '_'},
            {'*', '*', '*', '*', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '*', '*', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '*', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '*', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '*', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '_', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '_', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '_', '_', '_', '_', '_', '_', '_', '_'},
            {'*', '*', '_', '_', '_', '_', '_', '_', '_', '_'}};

    private static final String COORDINATES_SECOND_PLAYER = "A1 A5\n" +
            "B1 B4\n" +
            "C1 C4\n" +
            "D1 D3\n" +
            "E1 E3\n" +
            "F1 F3\n" +
            "G3 G4\n" +
            "H6 H7\n" +
            "I5 I6\n" +
            "J9 J10\n";

    private static final String CELL_STRING = "A2";
    private static final String INPUT_COORDINATE = "A1 A5";
    private static final int SHIP_COUNT_OF_CELLS = 5;
    private static final Cell CELL = new Cell(0, 1);
    private static final Ship SHIP = new Ship(ShipType.FIVE_CELLS, new Cell(0, 0), new Cell(0, 4));

    private Game game = new Game("myGame", "Maria");
    private BufferedReader in = new BufferedReader(new StringReader(""));
    private PrintWriter out = new PrintWriter(System.out);

    @Test
    public void testPrintGameStatisticsZeroPlayers() {
        String expectedOutput = "No players in the game room: \n" +
                "Player 1: \n" +
                "Player 2: \n";
        assertEquals(expectedOutput, game.printGameStatistics());
    }

    @Test
    public void testPrintGameStatisticsOnePlayer() {
        Game game1 = new Game("myGame", "Maria");
        game1.addPlayerOne(new Player("Dari", new Socket(), in, out));
        String expectedOutput = "One player in the game room: \n" +
                "Player 1: " + game1.getPlayerOne().getName() + "\n" +
                "Player 2: ";
        assertEquals(expectedOutput, game1.printGameStatistics());
    }

    @Test
    public void testPrintGameStatisticsTwoPlayers() {
        Game game1 = new Game("myGame", "Maria");
        game1.addPlayerOne(new Player("Dari", new Socket(), in, out));
        game1.addPlayerTwo(new Player("Moni", new Socket(), in, out));
        String expectedOutput = "Two players in the game room: \n" +
                "Player 1: " + game1.getPlayerOne().getName() + "\n" +
                "Player 2: " + game1.getPlayerTwo().getName();
        assertEquals(expectedOutput, game1.printGameStatistics());
    }

    @Test
    public void testConvertInputCoordinatesToCell() {
        assertEquals(CELL, game.convertInputCoordinatesToCell(CELL_STRING));
    }

    @Test
    public void testValidInputCoordinateTrue() {
        assertTrue(game.validInputCoordinate(CELL_STRING));
    }

    @Test
    public void testValidInputCoordinateFalse() {
        String testString = "Z8";
        assertFalse(game.validInputCoordinate(testString));
    }

    @Test
    public void testValidInputShipCoordinatesTrue() {
        assertTrue(game.validInputShipCoordinates(INPUT_COORDINATE, SHIP_COUNT_OF_CELLS));
    }

    @Test
    public void testValidInputShipCoordinatesFalse() {
        String testString = "abcd";
        assertFalse(game.validInputShipCoordinates(testString, SHIP_COUNT_OF_CELLS));
    }

    @Test
    public void testReadShips() {
        BufferedReader input = new BufferedReader(new StringReader(COORDINATES_FIRST_PLAYER));
        Board playerBoard = new Board();
        playerBoard.setShipsOnBoard(game.readShips(input, out));
        assertArrayEquals(BOARD_FIRST_PLAYER, playerBoard.getBoard());
    }

    @Test
    public void testSelectHitField() {
        BufferedReader input = new BufferedReader(new StringReader("A4"));
        Cell expectedResult = new Cell(0, 3);
        assertEquals(expectedResult, game.selectHitField(input, out));
    }

    @Test
    public void testSelectHitFieldNull() {
        BufferedReader input = new BufferedReader(new StringReader("save-game"));
        assertEquals(null, game.selectHitField(input, out));
    }

    @Test
    public void testPlay() {
        Game testGame = new Game("myGame", "Dari");
        BufferedReader in1 = new BufferedReader(new StringReader(COORDINATES_FIRST_PLAYER + "A1\n" +
                "save-game\n"));
        BufferedReader in2 = new BufferedReader(new StringReader(COORDINATES_SECOND_PLAYER + "A5\n"));
        testGame.addPlayerOne(new Player("Dari", new Socket(), in1, out));
        testGame.addPlayerTwo(new Player("Moni", new Socket(), in2, out));
        testGame.setInputAndOutput();
        assertEquals(Game.SAVED_GAME, testGame.play());
    }

}