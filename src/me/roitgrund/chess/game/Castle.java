package me.roitgrund.chess.game;

import com.google.common.base.Preconditions;
import me.roitgrund.chess.game.Piece.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public enum Castle {
    WHITE_KING(
            "e1",
            "g1",
            "h1",
            "f1"),
    WHITE_QUEEN(
            "e1",
            "c1",
            "a1",
            "d1"),
    BLACK_KING(
            "e8",
            "g8",
            "h8",
            "f8"),
    BLACK_QUEEN(
            "e8",
            "c8",
            "a8",
            "d8");

    private Coord kingFrom;
    private Coord kingTo;
    private Coord rookFrom;
    private Coord rookTo;
    private List<Coord> vulnerableSquares;

    Castle(
            String kingFrom,
            String kingTo,
            String rookFrom,
            String rookTo) {
        this.kingFrom = Coord.fromNotation(kingFrom);
        this.kingTo = Coord.fromNotation(kingTo);
        this.rookFrom = Coord.fromNotation(rookFrom);
        this.rookTo = Coord.fromNotation(rookTo);
        this.vulnerableSquares = Arrays.asList(
                this.kingFrom,
                this.kingTo,
                this.rookTo);
    }

    public static Map<Coord, Boolean> getInitialCastlingPermissions() {
        HashMap<Coord, Boolean> castlingPermissions = new HashMap<>();
        Stream.of(Castle.values()).forEach(
                c -> castlingPermissions.put(c.kingTo, true));
        return castlingPermissions;
    }

    public static void handleCastling(
            Coord from, Coord to, Board board) {
        preventCastlingAfterMovingFrom(from, board);
        checkForCastlingAndCastle(from, to, board);
    }

    public boolean canCastle(Coord from, Coord to, Board board, Color color) {
        return from.equals(kingFrom) &&
                to.equals(kingTo) &&
                board.canCastleTo(kingTo) &&
                vulnerableSquares
                        .stream()
                        .noneMatch(square -> board.squareVulnerableFromColor(
                                square,
                                Color.other(color)));
    }

    private void setCastlingState(Board board, Coord to) {
        Optional<Piece> rook = board.getPiece(rookFrom);
        Preconditions.checkState(
                rook.isPresent(),
                "Castling allowed without rook present.");
        board.removePiece(rookFrom);
        board.setPiece(rookTo, rook.get());
    }

    private static void preventCastlingAfterMovingFrom(
            Coord from, Board board) {
        Stream.of(Castle.values())
                .filter(c -> c.kingFrom.equals(from) || c.rookFrom.equals(from))
                .forEach(c -> board.preventCastlingTo(c.kingTo));
    }

    private static void checkForCastlingAndCastle(
            Coord from, Coord to, Board board) {
        Stream.of(Castle.values())
                .filter(c -> c.kingFrom.equals(from) &&
                        c.kingTo.equals(to))
                .findFirst()
                .ifPresent(c -> c.setCastlingState(board, to));
    }
}
