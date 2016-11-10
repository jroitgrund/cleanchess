package me.roitgrund.chess.format;

import me.roitgrund.chess.game.Board;
import me.roitgrund.chess.game.Coord;
import me.roitgrund.chess.game.Piece;

import java.util.Optional;
import java.util.stream.IntStream;

import static me.roitgrund.chess.stream.Streams.reverseIntStream;

public class BoardFormatter {

    public String format(Board board) {
        StringBuffer boardRepresentation = new StringBuffer(29 * 11);
        boardRepresentation.append(" a  b  c  d  e  f  g  h\n");
        addBorder(boardRepresentation);
        addNewline(boardRepresentation);
        reverseIntStream(7, 0).forEach(row -> {
            boardRepresentation.append("|");
            IntStream.rangeClosed(0, 7).forEach(col -> {
                boardRepresentation.append(getRepresentation(board.getPiece(
                        new Coord(row, col))));
                boardRepresentation.append("|");
            });
            boardRepresentation.append(String.format(" %s", row + 1));
            addNewline(boardRepresentation);
        });
        addBorder(boardRepresentation);
        addNewline(boardRepresentation);

        return boardRepresentation.toString();
    }

    private static void addBorder(StringBuffer boardRepresentation) {
        IntStream.range(0, 25).forEach(i -> boardRepresentation.append("-"));
    }

    private static void addNewline(StringBuffer boardRepresentation) {
        boardRepresentation.append("\n");
    }

    private static String getRepresentation(Optional<Piece> piece) {
        return piece.map(Piece::getStringRepresentation).orElse("  ");
    }
}
