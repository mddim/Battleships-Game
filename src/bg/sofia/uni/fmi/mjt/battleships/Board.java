package bg.sofia.uni.fmi.mjt.battleships;

import java.io.PrintWriter;

public class Board {

    private static final char EMPTY_CELL = '_';
    private static final char SHIP_CELL = '*';
    private static final char HIT_SHIP_CELL = 'X';
    private static final char HIT_EMPTY_CELL = 'O';

    private char[][] startBoard;
    private char[][] board;
    private int boardSize;

    public Board() {
        this.boardSize = 10;
        this.board = new char[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    public char[][] getBoard() {
        return board;
    }

    private void setShipOnBoard(Ship ship) {
        Cell shipStartCell = ship.getStartCell();
        Cell shipEndCell = ship.getEndCell();
        int x = shipStartCell.getX();
        int y = shipStartCell.getY();
        for (int i = 0; i < ship.getNumberOfCells(); i++) {
            //if startCell = (X,_) && endCell = (X,_) -> horizontal ship
            if (shipStartCell.getX() == shipEndCell.getX()) {
                board[shipStartCell.getX()][y++] = SHIP_CELL;
            }
            //if startCell = (_,Y) && endCell = (_,Y) -> vertical ship
            if (shipStartCell.getY() == shipEndCell.getY()) {
                board[x++][shipStartCell.getY()] = SHIP_CELL;
            }
        }
    }

    public void setShipsOnBoard(Ship[] ships) {
        for (Ship ship : ships) {
            setShipOnBoard(ship);
        }
        this.startBoard = board;
    }

    public void hitCell(Cell cell) {
        int xCell = cell.getX();
        int yCell = cell.getY();
        if (board[xCell][yCell] != HIT_SHIP_CELL && board[xCell][yCell] == SHIP_CELL) {
            board[xCell][yCell] = HIT_SHIP_CELL;

        } else if (board[xCell][yCell] != HIT_EMPTY_CELL && board[xCell][yCell] == EMPTY_CELL) {
            board[xCell][yCell] = HIT_EMPTY_CELL;
            //EXCEPTION
        } else {
            System.out.println("This field has already been selected!");
        }
        //
    }

    public boolean checkLost() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (startBoard[i][j] == SHIP_CELL && board[i][j] != HIT_SHIP_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkFieldAlreadySelected(Cell cell) {
        return board[cell.getX()][cell.getY()] == HIT_SHIP_CELL || board[cell.getX()][cell.getY()] == HIT_EMPTY_CELL;
    }

    public synchronized void printBoard(PrintWriter out) {
        int printBoardRowSize = 10;
        int printBoardColSize = 20;
        char[] lettersForRows = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        out.println("   1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < printBoardRowSize; i++) {
            out.print(String.valueOf(lettersForRows[i]) + ' ');
            int countOfHorizontalLines = 0;
            for (int j = 0; j < printBoardColSize; j++) {
                if (j % 2 == 0) {
                    out.print("|");
                    countOfHorizontalLines++;
                } else {
                    out.print(board[i][j - countOfHorizontalLines]);
                }
            }
            out.print("|");
            switch (i) {
                case 2 -> out.println("\t\tLegend:");
                case 3 -> out.println("\t\t* - ship field");
                case 4 -> out.println("\t\tX - hit ship field");
                case 5 -> out.println("\t\tO - hit empty field");
                default -> out.println();
            }
        }
    }

    public synchronized void printBoardForOpponent(PrintWriter out) {
        int printBoardRowSize = 10;
        int printBoardColSize = 20;
        char[] lettersForRows = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        out.println("   1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < printBoardRowSize; i++) {
            out.print(String.valueOf(lettersForRows[i]) + ' ');
            int countOfHorizontalLines = 0;
            for (int j = 0; j < printBoardColSize; j++) {
                if (j % 2 == 0) {
                    out.print("|");
                    countOfHorizontalLines++;
                } else {
                    if (board[i][j - countOfHorizontalLines] != SHIP_CELL) {
                        out.print(board[i][j - countOfHorizontalLines]);
                    }
                    else {
                        out.print(EMPTY_CELL);
                    }
                }
            }
            out.println("|");
        }
    }
}
