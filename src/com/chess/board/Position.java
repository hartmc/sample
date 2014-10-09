package com.chess.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.chess.board.PieceType.*;

public class Position implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<Square, Piece> pieceLocations = new HashMap<Square, Piece>();

    public Piece get(Square square) {
        return pieceLocations.get(square);
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Position clone() {
        Position copy = new Position();
        synchronized (pieceLocations) {
            copy.pieceLocations.putAll(this.pieceLocations);
        }
        return copy;
    }

    @Override
    public int hashCode() {
        return pieceLocations.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position) obj;
        if (!pieceLocations.equals(other.pieceLocations)) {
            return false;
        }
        return true;
    }

    public boolean isInCheck(Color turn) {
        Square kingSquare = findKing(turn);

        // see if there is a pawn that can capture the king
        Square[] pawnSquares = new Square[] { kingSquare.getRelativeSquare(1, turn.multiplier()),
                                             kingSquare.getRelativeSquare(-1, turn.multiplier()) };
        for (Square square : pawnSquares) {
            Piece maybePawn = get(square); // null square is ok, we just get null back
            if (maybePawn != null && maybePawn.getPieceType() == Pawn && maybePawn.getColor() == turn.getOppositeColor()) {
                return true;
            }
        }

        // see if there is a knight a knight-hop away
        Square[] knightSquares = new Square[] { kingSquare.getRelativeSquare(2, 1), kingSquare.getRelativeSquare(1, 2),
                                               kingSquare.getRelativeSquare(2, -1), kingSquare.getRelativeSquare(1, -2),
                                               kingSquare.getRelativeSquare(-1, 2), kingSquare.getRelativeSquare(-2, 1),
                                               kingSquare.getRelativeSquare(-2, -1), kingSquare.getRelativeSquare(-1, -2) };
        for (Square square : knightSquares) {
            Piece maybeKnight = get(square);
            if (maybeKnight != null && maybeKnight.getPieceType() == Knight && maybeKnight.getColor() == turn.getOppositeColor()) {
                return true;
            }
        }

        // see if there is a bishop / queen on a diagonal leading to the king
        final PieceType[] diagonalPieces = new PieceType[] { Queen, Bishop };
        if (canReachSquare(kingSquare, turn.getOppositeColor(), -1, 1, diagonalPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), 1, 1, diagonalPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), -1, -1, diagonalPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), 1, -1, diagonalPieces)) {
            return true;
        }

        // see if there is a rook / queen on a rank / file leading to the king
        final PieceType[] majorPieces = new PieceType[] { Queen, Rook };
        if (canReachSquare(kingSquare, turn.getOppositeColor(), -1, 0, majorPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), 1, 0, majorPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), 0, -1, majorPieces)
            || canReachSquare(kingSquare, turn.getOppositeColor(), 0, 1, majorPieces)) {
            return true;
        }

        // see if the enemy king can capture us!
        Piece enemyKing = Piece.getPiece(turn.getOppositeColor(), PieceType.King);
        if (get(kingSquare.getRelativeSquare(-1, -1)) == enemyKing || get(kingSquare.getRelativeSquare(-1, 0)) == enemyKing
            || get(kingSquare.getRelativeSquare(-1, 1)) == enemyKing || get(kingSquare.getRelativeSquare(0, 1)) == enemyKing
            || get(kingSquare.getRelativeSquare(0, -1)) == enemyKing || get(kingSquare.getRelativeSquare(1, -1)) == enemyKing
            || get(kingSquare.getRelativeSquare(1, 1)) == enemyKing || get(kingSquare.getRelativeSquare(1, 0)) == enemyKing)
            return true;

        return false;
    }

    private Square findKing(Color turn) {
        synchronized (pieceLocations) {
            for (Square square : pieceLocations.keySet()) {
                Piece piece = pieceLocations.get(square);
                if (piece.getPieceType() == King && piece.getColor() == turn) {
                    return square;
                }
            }
        }

        throw new IllegalStateException("No " + turn.toString().toLowerCase() + " king found!");
    }

    private boolean canReachSquare(Square square, Color color, int xInc, int yInc, PieceType[] pieceTypes) {
        return searchForSquare(square, color, xInc, yInc, pieceTypes) != null;
    }

    // searches for a piece of the given type and color, that can reach the given square with no pieces in between.
    // xInc and yInc define the movement of the piece to that square, ie, -1, 0 would search along the rank of
    // "square" to the left. 1,1 would search the diagonal to the top right of the given square, etc.
    //
    // returns the Square of the first piece matching the search (ie, "checking" the square in question),
    // or null if no such piece is found.
    private Square searchForSquare(Square square, Color color, int xInc, int yInc, PieceType[] pieceTypes) {
        for (int i = 1; i < 8; i++) {
            int nextFile = square.getFile().ordinal() + xInc * i;
            if (nextFile < 0 || nextFile > 7) {
                return null;
            }

            int nextRank = square.getRank().ordinal() + yInc * i;
            if (nextRank < 0 || nextRank > 7) {
                return null;
            }

            final Square nextSquare = Square.getSquare(File.values()[nextFile], Rank.values()[nextRank]);
            Piece maybeEnemy = get(nextSquare);
            if (maybeEnemy != null) {
                if (maybeEnemy.getColor() != color) {
                    return null;
                }

                for (PieceType type : pieceTypes) {
                    if (type == maybeEnemy.getPieceType()) {
                        return nextSquare;
                    }
                }

                return null;
            }
        }

        return null;
    }

    public Collection<PieceLocation> getPieceLocations() {
        Collection<PieceLocation> result = new ArrayList<PieceLocation>();
        synchronized (pieceLocations) {
            for (Entry<Square, Piece> entry : pieceLocations.entrySet()) {
                result.add(new PieceLocation(entry.getValue(), entry.getKey()));
            }
        }

        return result;
    }

    public Position put(Square square, Piece piece) {
        Position result = clone();
        synchronized (pieceLocations) {
            if (piece == null) {
                result.pieceLocations.remove(square);
            }
            else {
                result.pieceLocations.put(square, piece);
            }
        }

        return result;
    }

    public Position put(Piece piece, Square... squares) {
        Position copy = clone();
        for (Square square : squares) {
            copy.pieceLocations.put(square, piece);
        }
        return copy;
    }
}
