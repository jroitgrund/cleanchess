package me.roitgrund.chess.game;

import com.google.common.base.Preconditions;
import me.roitgrund.chess.game.Piece.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static me.roitgrund.chess.game.Piece.Color.BLACK;
import static me.roitgrund.chess.game.Piece.Color.WHITE;
import static me.roitgrund.chess.game.Piece.Type.BISHOP;
import static me.roitgrund.chess.game.Piece.Type.KING;
import static me.roitgrund.chess.game.Piece.Type.KNIGHT;
import static me.roitgrund.chess.game.Piece.Type.PAWN;
import static me.roitgrund.chess.game.Piece.Type.QUEEN;
import static me.roitgrund.chess.game.Piece.Type.ROOK;

/**
 * Represents a chess board including where the pieces are, whose turn it is,
 * and the en-passant and castling states for both players.
 */
public class Board {
    private final Piece[][] board;
    private Optional<Integer> enPassant;
    private Coord whiteKing;
    private Coord blackKing;
    private Piece.Color turn;
    private Map<Coord, Boolean> castlingPermissions;

    public Board() {
        board = new Piece[8][8];

        setupPieces(0, WHITE);
        setupPieces(7, BLACK);
        blackKing = new Coord(7, 4);
        whiteKing = new Coord(0, 4);
        turn = WHITE;
        castlingPermissions = Castle.getInitialCastlingPermissions();
    }

    private Board(
            Piece[][] board,
            Optional<Integer> enPassant,
            Coord whiteKing,
            Coord blackKing,
            Color turn,
            Map<Coord, Boolean> castlingPermissions) {
        this.board = board;
        this.enPassant = enPassant;
        this.whiteKing = whiteKing;
        this.blackKing = blackKing;
        this.turn = turn;
        this.castlingPermissions = castlingPermissions;
    }

    public boolean canCastleTo(Coord coord) {
        return castlingPermissions.get(coord);
    }

    public void preventCastlingTo(Coord coord) {
        castlingPermissions.put(coord, false);
    }

    public GameState move(Coord from, Coord to) {
        if (isLegalMove(from, to)) {
            actuallyMove(from, to);
            return getGameState();
        }

        return GameState.ILLEGAL_MOVE;
    }

    public boolean canEnPassant(Coord to) {
        return enPassant.isPresent() && enPassant.get() == to.getCol();
    }

    public boolean nothingOnRowBetween(Coord from, Coord to) {
        Preconditions.checkArgument(from.isSameRowAs(to));
        return IntStream.range(Math.min(from.getCol(), to.getCol()) + 1,
                Math.max(from.getCol(), to.getCol()))
                .noneMatch(col -> getPiece(new Coord(from.getRow(),
                        col)).isPresent());
    }

    public Optional<Piece> getPiece(Coord coord) {
        return Optional.ofNullable(board[coord.getRow()][coord.getCol()]);
    }

    public boolean nothingOnColBetween(Coord from, Coord to) {
        Preconditions.checkArgument(from.isSameColAs(to));
        return IntStream
                .range(
                        Math.min(from.getRow(), to.getRow()) + 1,
                        Math.max(from.getRow(), to.getRow()))
                .noneMatch(row -> getPiece(
                        new Coord(row, from.getCol())).isPresent());
    }

    public boolean nothingOnDiagonalBetween(Coord from, Coord to) {
        Preconditions.checkArgument(from.isSameDiagonalAs(to));
        int rowDirection = from.getRowDirection(to);
        int colDirection = from.getColDirection(to);

        Coord curr = from.next(rowDirection, colDirection);

        while (!curr.equals(to)) {
            if (getPiece(curr).isPresent()) {
                return false;
            }

            curr = curr.next(rowDirection, colDirection);
        }

        return true;
    }

    public GameState move(String from, String to) {
        return move(Coord.fromNotation(from), Coord.fromNotation(to));
    }

    public boolean squareVulnerableFromColor(Coord square, Color color) {
        return !falseForAllCoords(
                potentialDangerFrom -> getPiece(potentialDangerFrom)
                        .map(piece -> piece.getColor() == color &&
                                piece.isValidMove(
                                        this,
                                        potentialDangerFrom,
                                        square))
                        .orElse(false));
    }

    public GameState getGameState() {
        if (falseForAllCoords(
                from -> falseForAllCoords(to -> isLegalMove(from, to)))) {
            return getWinnerOrStaleMate();
        }

        return GameState.PLAYING;
    }

    public void setPiece(Coord coord, Piece piece) {
        board[coord.getRow()][coord.getCol()] = piece;
    }

    public void removePiece(Coord coord) {
        board[coord.getRow()][coord.getCol()] = null;
    }

    private boolean isLegalMove(Coord from, Coord to) {
        Optional<Piece> fromPiece = getPiece(from);
        if (!from.isValid() ||
                !to.isValid() ||
                to.equals(from) ||
                !fromPiece.isPresent() ||
                !(fromPiece.get().getColor() == turn)) {
            return false;
        }

        Optional<Piece> toPiece = getPiece(to);
        if (toPiece.isPresent() &&
                (toPiece.get().getColor() == fromPiece.get().getColor() ||
                        toPiece.get().getType() == KING)) {
            return false;
        }

        if (!fromPiece.get().isValidMove(this, from, to) ||
                kingEndangeredFromMove(from, to)) {
            return false;
        }
        return true;
    }

    private boolean falseForAllCoords(CoordPredicate coordPredicate) {
        return IntStream.range(0, 8).noneMatch(
                row -> IntStream.range(0, 8).anyMatch(
                        col -> coordPredicate.isTrue(new Coord(row, col))));
    }

    private GameState getWinnerOrStaleMate() {
        if (falseForAllCoords(from -> isLegalMove(
                from, turn == BLACK ? blackKing : whiteKing))) {
            return turn == BLACK ? GameState.WHITE_WINS : GameState.BLACK_WINS;
        }

        return GameState.STALEMATE;
    }

    private void setupPieces(int row, Piece.Color color) {
        board[row][0] = new Piece(ROOK, color);
        board[row][1] = new Piece(KNIGHT, color);
        board[row][2] = new Piece(BISHOP, color);
        board[row][3] = new Piece(QUEEN, color);
        board[row][4] = new Piece(KING, color);
        board[row][5] = new Piece(BISHOP, color);
        board[row][6] = new Piece(KNIGHT, color);
        board[row][7] = new Piece(ROOK, color);

        IntStream.rangeClosed(0, 7).forEach(col ->
                board[row + (color == WHITE ? 1 : -1)][col] =
                        new Piece(PAWN, color));
    }

    private void actuallyMove(Coord from, Coord to) {
        Piece fromPiece = getPiece(from).get();

        // Delete en passant piece.
        if (fromPiece.getType() == PAWN && !getPiece(to).isPresent()) {
            removePiece(Coord.oneInFrontOf(to, Piece.Color.other(turn)));
        }

        // Set new king position.
        if (fromPiece.getType() == KING) {
            if (fromPiece.getColor() == WHITE) {
                whiteKing = to;
            } else {
                blackKing = to;
            }
        }

        // Set possible en passant for next turn.
        if (fromPiece.getType() == PAWN &&
                to.isOneInFrontOf(Coord.oneInFrontOf(from, turn), turn)) {
            enPassant = Optional.of(from.getCol());
        } else {
            enPassant = Optional.empty();
        }

        // Handle castling.
        Castle.handleCastling(from, to, this);

        // Move piece.
        removePiece(from);
        removePiece(to);
        setPiece(to, fromPiece);

        // Promote pawn to queen.
        if (fromPiece.getType() == PAWN &&
                (to.getRow() == 0 || to.getCol() == 8)) {
            setPiece(to, new Piece(QUEEN, fromPiece.getColor()));
        }

        turn = Piece.Color.other(turn);
    }

    private boolean kingEndangeredFromMove(Coord from,
                                           Coord to) {
        Board boardCopy = copyOf(this);
        boardCopy.actuallyMove(from, to);
        return boardCopy.squareVulnerableFromColor(
                boardCopy.notPlayingKing(),
                boardCopy.turn);
    }

    private Coord notPlayingKing() {
        return turn == WHITE ? blackKing : whiteKing;
    }

    private static Board copyOf(Board from) {
        Piece[][] board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(from.board[i], 0, board[i], 0, 8);
        }
        return new Board(board,
                from.enPassant,
                from.whiteKing,
                from.blackKing,
                from.turn,
                new HashMap<>(from.castlingPermissions));
    }

    public enum GameState {
        WHITE_WINS,
        BLACK_WINS,
        STALEMATE,
        ILLEGAL_MOVE, PLAYING
    }

    @FunctionalInterface
    private interface CoordPredicate {
        boolean isTrue(Coord coord);
    }
}
