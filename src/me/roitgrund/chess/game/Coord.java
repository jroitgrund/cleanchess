package me.roitgrund.chess.game;

import com.google.common.base.Preconditions;

import static me.roitgrund.chess.game.Piece.Color.WHITE;

public class Coord {

    private final int row;
    private final int col;

    public Coord(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static Coord fromNotation(String notation) {
        Preconditions.checkArgument(notation.length() == 2);
        return new Coord(
                Integer.parseInt(notation.substring(1)) - 1,
                notation.toLowerCase().codePointAt(0) - 97);
    }

    public static Coord oneInFrontOf(Coord coord, Piece.Color color) {
        return new Coord(color == WHITE ? coord.row + 1 : coord.row - 1,
                coord.col);
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    public boolean isValid() {
        return row >= 0 && row <= 8 && col >= 0 && col <= 8;
    }

    public boolean isAdjacentColumn(Coord coord) {
        return Math.abs(col - coord.col) == 1;
    }

    public boolean isOneInFrontOf(Coord coord, Piece.Color color) {
        return equals(oneInFrontOf(coord, color));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coord coord = (Coord) o;

        return row == coord.row && col == coord.col;
    }

    public boolean isSameColAs(Coord coord) {
        return coord.col == col;
    }

    public boolean isSameRowAs(Coord coord) {
        return coord.row == row;
    }

    public boolean isNextRowFrom(Coord coord, Piece.Color color) {
        return color == WHITE ? coord.row == row - 1 : coord.row == row + 1;
    }

    public boolean isEnPassantStart(Piece.Color color) {
        return color == WHITE ? row == 4 : row == 3;
    }

    public boolean isPawnStart(Piece.Color color) {
        return color == WHITE ? row == 1 : row == 6;
    }

    public boolean isSameDiagonalAs(Coord coord) {
        return Math.abs(coord.getRow() - row) == Math.abs(coord.getCol() - col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRowDirection(Coord to) {
        return to.getRow() > getRow() ? 1 : -1;
    }

    public int getColDirection(Coord to) {
        return to.getCol() > getCol() ? 1 : -1;
    }

    public Coord next(int rowDirection, int colDirection) {
        return new Coord(row + rowDirection, col + colDirection);
    }

    public boolean isInSquareAround(Coord coord) {
        return Math.abs(coord.getRow() - row) <= 1 &&
                Math.abs(coord.getCol() - col) <= 1;
    }
}
