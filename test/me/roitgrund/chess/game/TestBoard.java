package me.roitgrund.chess.game;

import me.roitgrund.chess.format.BoardFormatter;
import me.roitgrund.chess.game.Board.GameState;
import me.roitgrund.chess.game.Piece.Color;
import me.roitgrund.chess.game.Piece.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TestBoard {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testPawn() {
        move("e2", "e4");

        move("e7", "e5");

        failMove("e4", "e5");
        move("f2", "f4");

        move("e5", "f4");

        move("g2", "g4");

        failMove("f4", "e3");
        move("f4", "g3");

        failMove("e4", "e6");
        failMove("e4", "f5");
        failMove("e4", "d5");
        move("e4", "e5");
    }

    @Test
    public void testRook() {
        failMove("a1", "a2");
        failMove("a1", "a3");
        move("a2", "a4");

        move("a7", "a5");

        move("a1", "a3");

        move("a8", "a6");

        move("a3", "e3");

        move("a6", "e6");

        move("e3", "e4");

        failMove("e6", "e3");
        failMove("e6", "g5");
        move("e6", "e4");
    }

    @Test
    public void testBishop() {
        failMove("b1", "a2");
        move("d2", "d4");

        move("e7", "e5");

        move("c1", "f4");

        move("f8", "d6");

        failMove("f4", "f3");
        failMove("f4", "g4");
        failMove("f4", "d6");
        move("d4", "e5");

        move("d6", "e5");
    }

    @Test
    public void testKnight() {
        failMove("b1", "d2");
        failMove("b1", "a4");
        failMove("b1", "b3");
        move("b1", "a3");

        move("b8", "c6");

        move("a3", "c4");

        move("c6", "e5");

        move("c4", "e5");
    }

    @Test
    public void testKing() {
        move("e2", "e4");

        move("e7", "e5");

        move("e1", "e2");

        failMove("e8", "e6");
        move("d7", "d6");

        move("e2", "f3");

        move("c8", "g4");

        failMove("f3", "e2");
        failMove("a2", "a3");
        move("f3", "g4");
    }

    @Test
    public void testCastlingWorks() {
        setupCastlingBoard();

        move("e1", "g1");

        assertThat(board.getPiece(Coord.fromNotation("g1")).get()).isEqualTo(
                new Piece(Type.KING, Color.WHITE));
        assertThat(board.getPiece(Coord.fromNotation("f1")).get()).isEqualTo(
                new Piece(Type.ROOK, Color.WHITE));
        assertThat(board.getPiece(Coord.fromNotation("e1")).isPresent())
                .isFalse();
        assertThat(board.getPiece(Coord.fromNotation("h1")).isPresent())
                .isFalse();
    }

    @Test
    public void testCastlingNotPossibleIfKingMoved() {
        setupCastlingBoard();

        move("e1", "f1");

        move("a7", "a6");

        move("f1", "e1");

        move("a6", "a5");

        failMove("e1", "g1");
    }

    @Test
    public void testCastlingNotPossibleOnMovedRookSide() {
        setupCastlingBoard();

        move("h1", "g1");

        move("a7", "a6");

        move("g1", "h1");

        move("a6", "a5");

        failMove("e1", "g1");
        move("d2", "d4");

        move("a5", "a4");

        move("c1", "f4");

        move("a4", "a3");

        move("b1", "c3");

        move("b7", "b6");

        move("d1", "e2");

        move("b6", "b5");

        move("e1", "c1");
    }

    @Test
    public void testCastlingNotPossibleIfThreatened() {
        setupCastlingBoard();

        move("a2", "a3");

        move("c5", "f2");

        failMove("e1", "g1");
    }

    @Test
    public void testCastlingNotPossibleIfPathThreatened() {
        setupCastlingBoard();

        move("f3", "h4");

        move("a7", "a6");

        move("f2", "f3");

        failMove("e1", "g1");
    }

    @Test
    public void testKasparov() {
        move("d2", "d4");
        move("g8", "f6");

        move("c2", "c4");
        move("g7", "g6");

        move("b1", "c3");
        move("f8", "g7");

        move("e2", "e4");
        move("d7", "d6");

        move("f2", "f3");
        move("e8", "g8");

        move("c1", "e3");
        move("e7", "e5");

        move("g1", "e2");
        move("c7", "c6");

        move("d1", "d2");
        move("b8", "d7");

        move("a1", "d1");
        move("a7", "a6");

        move("d4", "e5");
        move("d7", "e5");

        move("b2", "b3");
        move("b7", "b5");

        move("c4", "b5");
        move("a6", "b5");

        move("d2", "d6");
        move("f6", "d7");

        move("f3", "f4");
        move("b5", "b4");

        move("c3", "b1");
        move("e5", "g4");

        move("e3", "d4");
        move("g7", "d4");

        move("d6", "d4");
        move("a8", "a2");

        move("h2", "h3");
        move("c6", "c5");

        move("d4", "g1");
        move("g4", "f6");

        move("e4", "e5");
        move("f6", "e4");

        move("h3", "h4");
        move("c5", "c4");

        move("e2", "c1");
        move("c4", "c3");

        move("c1", "a2");
        move("c3", "c2");

        move("g1", "d4");
        move("c2", "d1");

        move("e1", "d1");
        move("d7", "c5");

        move("d4", "d8");
        move("f8", "d8");

        move("d1", "c2");
        move("e4", "f2");
    }

    private void setupCastlingBoard() {
        move("e2", "e4");

        move("e7", "e5");

        move("f1", "c4");

        move("f8", "c5");

        move("g1", "f3");

        move("g8", "f6");
    }

    private void move(String from, String to) {
        try {
            assertThat(board.move(from, to)).isEqualTo(GameState.PLAYING);
        } catch (Throwable t) {
            System.out.println(new BoardFormatter().format(board));
            throw new RuntimeException(
                    String.format("Erroneously forbid move from %s to %s",
                            from,
                            to),
                    t);
        }
    }

    private void failMove(String from, String to) {
        try {
            assertThat(board.move(from, to)).isEqualTo(GameState.ILLEGAL_MOVE);
        } catch (Throwable t) {
            System.out.println(new BoardFormatter().format(board));
            throw new RuntimeException(
                    String.format("Erroneously allowed move from %s to %s",
                            from,
                            to),
                    t);
        }
    }
}
