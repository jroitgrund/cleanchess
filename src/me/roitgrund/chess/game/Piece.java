package me.roitgrund.chess.game;

import java.util.stream.Stream;

public class Piece {

    private final Type type;
    private final Color color;
    private final String stringRepresentation;

    public Piece(Type type, Color color) {
        this.type = type;
        this.color = color;
        this.stringRepresentation =
                String.format("%s%s", color.getLetter(), type.getLetter());
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    /**
     * Valid here is defined as a move the particular piece is allowed to
     * make, regardless of base rules such as not endangering the king.
     */
    public boolean isValidMove(Board board, Coord from, Coord to) {
        return type.isValidMove(board, from, to, this);
    }

    public Color getColor() {
        return color;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Piece piece = (Piece) o;

        return type == piece.type && color == piece.color;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + stringRepresentation.hashCode();
        return result;
    }

    public enum Color {
        BLACK('B'), WHITE('W');

        private final char letter;

        Color(char letter) {
            this.letter = letter;
        }

        public static Color other(Color color) {
            return color == BLACK ? WHITE : BLACK;
        }

        public char getLetter() {
            return letter;
        }
    }

    public enum Type {
        PAWN('P') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                if (isValidTake(board, from, to, me)) {
                    return true;
                } else if (!board.getPiece(to).isPresent()) {
                    Coord oneInFrontOf = Coord.oneInFrontOf(from, me.color);
                    return to.equals(oneInFrontOf) ||
                            !board.getPiece(oneInFrontOf).isPresent() &&
                                    to.isOneInFrontOf(oneInFrontOf, me.color) &&
                                    from.isPawnStart(me.color);
                }

                return false;
            }

            private boolean isValidTake(Board board,
                                        Coord from,
                                        Coord to,
                                        Piece me) {
                return to.isAdjacentColumn(from) &&
                        to.isNextRowFrom(from, me.color) &&
                        (board.getPiece(to).isPresent() ||
                                isValidEnPassant(board, from, to, me));
            }

            private boolean isValidEnPassant(Board board,
                                             Coord from,
                                             Coord to,
                                             Piece me) {
                return board.canEnPassant(to) &&
                        from.isEnPassantStart(me.color);
            }
        },

        ROOK('R') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                return from.isSameRowAs(to) &&
                        board.nothingOnRowBetween(from, to) ||
                        from.isSameColAs(to) &&
                                board.nothingOnColBetween(from, to);
            }
        },

        KNIGHT('C') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                int rowDiff = Math.abs(to.getRow() - from.getRow());
                int colDiff = Math.abs(to.getCol() - from.getCol());
                return rowDiff == 1 && colDiff == 2 ||
                        rowDiff == 2 && colDiff == 1;
            }
        },

        BISHOP('B') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                return from.isSameDiagonalAs(to) &&
                        board.nothingOnDiagonalBetween(from, to);
            }
        },

        KING('K') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                if (Stream.of(Castle.values()).anyMatch(
                        c -> c.canCastle(from, to, board, me.color))) {
                    return true;
                }
                return to.isInSquareAround(from);
            }
        },

        QUEEN('Q') {
            @Override
            public boolean isValidMove(Board board,
                                       Coord from,
                                       Coord to,
                                       Piece me) {
                return from.isSameDiagonalAs(to) &&
                        board.nothingOnDiagonalBetween(from, to) ||
                        from.isSameRowAs(to) &&
                                board.nothingOnRowBetween(from, to) ||
                        from.isSameColAs(to) &&
                                board.nothingOnColBetween(from, to);
            }
        };

        private final char letter;

        Type(char letter) {
            this.letter = letter;
        }

        public char getLetter() {
            return letter;
        }

        public abstract boolean isValidMove(Board board,
                                            Coord from,
                                            Coord to,
                                            Piece me);
    }
}
