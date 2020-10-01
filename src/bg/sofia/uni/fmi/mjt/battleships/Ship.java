package bg.sofia.uni.fmi.mjt.battleships;

import bg.sofia.uni.fmi.mjt.battleships.enums.ShipType;

import java.util.ArrayList;
import java.util.List;

public class Ship {

    private ShipType shipType;
    private int numberOfCells;
    private Cell startCell;
    private Cell endCell;

    public Ship(ShipType shipType, Cell startCell, Cell endCell) {
        this.shipType = shipType;
        numberOfCells = switch (shipType) {
            case TWO_CELLS -> 2;
            case THREE_CELLS -> 3;
            case FOUR_CELLS -> 4;
            case FIVE_CELLS -> 5;
        };
        this.startCell = startCell;
        this.endCell = endCell;
    }

    public boolean isShipValid() {
        return ((startCell.getX() == endCell.getX() && startCell.getY() + (numberOfCells - 1) == endCell.getY()) ||
                (startCell.getY() == endCell.getY() && startCell.getX() + (numberOfCells - 1) == endCell.getX()));
    }

    public int getNumberOfCells() {
        return numberOfCells;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }

    public List<Cell> getShipsCells() {
        List<Cell> cells = new ArrayList<>();
        if (startCell.getX() == endCell.getX()) {
            for (int i = startCell.getY(); i <= endCell.getY(); i++) {
                cells.add(new Cell(startCell.getX(), i));
            }
        } else if (startCell.getY() == endCell.getY()) {
            for (int i = startCell.getX(); i <= endCell.getX(); i++) {
                cells.add(new Cell(i, startCell.getY()));
            }
        }
        return cells;
    }
}
