package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.Board;

public class SavedGame {

    private String savedGameName;
    private Board boardPlayerOne;
    private Board boardPlayerTwo;
    private int turn;

    public SavedGame(String savedGameName, Board boardPlayerOne, Board boardPlayerTwo, int turn) {
        this.savedGameName = savedGameName;
        this.boardPlayerOne = boardPlayerOne;
        this.boardPlayerTwo = boardPlayerTwo;
        this.turn = turn;
    }

    public String getSavedGameName() {
        return savedGameName;
    }

    public Board getBoardPlayerOne() {
        return boardPlayerOne;
    }

    public Board getBoardPlayerTwo() {
        return boardPlayerTwo;
    }

    public int getTurn() {
        return turn;
    }
}
