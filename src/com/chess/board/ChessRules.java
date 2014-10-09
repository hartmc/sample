package com.chess.board;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.File.*;
import static com.chess.board.PieceType.King;
import static com.chess.board.PieceType.Pawn;

public class ChessRules implements Rules {

    private static final long serialVersionUID = 1L;
    private static final List<Point> knightMoves = Arrays.asList(new Point(-2, -1), new Point(-2, 1), new Point(-1, -2), new Point(
            -1,
            2), new Point(1, -2), new Point(1, 2), new Point(2, -1), new Point(2, 1));

    private static final List<Point> kingMoves = Arrays.asList(new Point(-1, -1), new Point(-1, 0), new Point(-1, 1), new Point(
            0,
            -1), new Point(0, 1), new Point(1, -1), new Point(1, 0), new Point(1, 1));

    @Override
    public void assertLegal(Move move, Board board) throws IllegalMoveException {
        Position position = board.getPosition();
        Color turn = board.getTurn();
        Piece startPiece = position.get(move.getStartSquare());
        if (startPiece == null) {
            throw new IllegalMoveException("There is no piece on " + move.getStartSquare() + "!");
        }

        if (startPiece.getColor() != turn) {
            throw new IllegalMoveException("It is " + turn.toString().toLowerCase() + "'s turn to move!");
        }

        if (move.getStartSquare() == move.getEndSquare()) {
            fail(move);
        }

        // eliminate all moves that attempt to capture a piece of our own color
        Piece endPiece = position.get(move.getEndSquare());
        if (endPiece != null && endPiece.getColor() == turn) {
            fail(move);
        }

        switch (startPiece.getPieceType()) {
            case Queen:
                assertLegalQueenMove(move, position);
                break;
            case Rook:
                assertLegalRookMove(move, position);
                break;
            case Bishop:
                assertLegalBishopMove(move, position);
                break;
            case Knight:
                assertLegalKnightMove(move);
                break;
            case Pawn:
                assertLegalPawnMove(move, position, turn, board.getEnPassantSquare());
                break;
            case King:
                assertLegalKingMove(move, board);
                break;
        }

        // make sure the king will not be in check after we make this move
        Position result = move(move, position);
        if (result.isInCheck(turn)) {
            throw new IllegalMoveException("The king would be in check on " + move.getEndSquare() + "!");
        }
    }

    private void assertLegalQueenMove(Move move, Position position) throws IllegalMoveException {
        try {
            assertLegalRookMove(move, position);
        }
        catch (IllegalMoveException ime) {
            assertLegalBishopMove(move, position);
        }
    }

    private void assertLegalRookMove(Move move, Position position) throws IllegalMoveException {
        int fileDiff = Math.abs(move.getStartSquare().getFile().ordinal() - move.getEndSquare().getFile().ordinal());
        int rankDiff = Math.abs(move.getStartSquare().getRank().ordinal() - move.getEndSquare().getRank().ordinal());

        if (fileDiff != 0 && rankDiff != 0) {
            fail(move);
        }

        for (Square square : move.getStartSquare().getSquaresBetween(move.getEndSquare())) {
            if (position.get(square) != null) {
                throw new IllegalMoveException("Rooks cannot jump over pieces.");
            }
        }
    }

    private void assertLegalBishopMove(Move move, Position position) throws IllegalMoveException {
        int fileDiff = Math.abs(move.getStartSquare().getFile().ordinal() - move.getEndSquare().getFile().ordinal());
        int rankDiff = Math.abs(move.getStartSquare().getRank().ordinal() - move.getEndSquare().getRank().ordinal());

        if (fileDiff != rankDiff) {
            fail(move);
        }

        for (Square square : move.getStartSquare().getSquaresBetween(move.getEndSquare())) {
            if (position.get(square) != null) {
                throw new IllegalMoveException("Bishops cannot jump over pieces.");
            }
        }
    }

    private void assertLegalKnightMove(Move move) throws IllegalMoveException {
        int fileDiff = Math.abs(move.getStartSquare().getFile().ordinal() - move.getEndSquare().getFile().ordinal());
        int rankDiff = Math.abs(move.getStartSquare().getRank().ordinal() - move.getEndSquare().getRank().ordinal());

        if (fileDiff == 2 && rankDiff == 1 || fileDiff == 1 && rankDiff == 2) {
            // legal move, do nothing.
        }
        else {
            fail(move);
        }
    }

    @Override
    public Position move(Move move, Position position) {
        Position copy = position.put(move.getEndSquare(), position.get(move.getStartSquare()));
        copy = copy.put(move.getStartSquare(), null);

        if (move.getEndSquare().isBackRank() && position.get(move.getStartSquare()).getPieceType() == PieceType.Pawn) {
            copy = copy.put(
                    move.getEndSquare(),
                    Piece.getPiece(position.get(move.getStartSquare()).getColor(), move.getPromotionPiece().getPieceType()));
        }

        // if we are castling, move the rook
        if (position.get(move.getStartSquare()).getPieceType() == King) {
            if (move.getStartSquare().getFile() == e) {
                if (move.getEndSquare().getFile() == g) {
                    final Square rookSquare = Square.getSquare(h, move.getStartSquare().getRank());
                    copy = copy.put(Square.getSquare(f, move.getStartSquare().getRank()), copy.get(rookSquare));
                    copy = copy.put(rookSquare, null);
                }
                else if (move.getEndSquare().getFile() == c) {
                    final Square rookSquare = Square.getSquare(a, move.getStartSquare().getRank());
                    copy = copy.put(Square.getSquare(d, move.getStartSquare().getRank()), copy.get(rookSquare));
                    copy = copy.put(rookSquare, null);
                }
            }
        }

        // if we are capturing en passant, remove the enemy pawn
        if (position.get(move.getStartSquare()).getPieceType() == Pawn && position.get(move.getEndSquare()) == null
            && move.getStartSquare().getFile() != move.getEndSquare().getFile()) {
            copy = copy.put(Square.getSquare(move.getEndSquare().getFile(), move.getStartSquare().getRank()), null);
        }

        return copy;
    }

    private void assertLegalKingMove(Move move, Board board) throws IllegalMoveException {
        // (usually) cannot move more then 1 file left or right
        final int fileDiff = Math.abs(move.getEndSquare().getFile().ordinal() - move.getStartSquare().getFile().ordinal());
        if (fileDiff > 1) {
            // check for castling
            if (fileDiff == 2 && move.getStartSquare().getFile() == e
                && move.getStartSquare().getRank() == board.getTurn().getBackRank()) {
                // make sure we aren't in check
                if (board.getPosition().isInCheck(board.getTurn())) {
                    throw new IllegalMoveException("Castling out of check is illegal.");
                }

                // could be castling... see if the king or rook have moved yet
                // or if we are in check, or moving through check (ending up in check is covered later)
                if (!board.canCastleTo(move.getEndSquare().getFile().getSide())) {
                    throw new IllegalMoveException("Castling is illegal in this position.");
                }

                // make sure we aren't moving through check
                Square inBetween = Square.getSquare(move.getEndSquare().getFile() == g ? f : d, move.getStartSquare().getRank());
                if (board.getPosition().get(inBetween) != null) {
                    throw new IllegalMoveException("Castling is illegal with a piece between the king and rook.");
                }
                Position copy = board.getPosition().put(inBetween, board.getPosition().get(move.getStartSquare()));
                copy = copy.put(move.getStartSquare(), null);
                if (copy.isInCheck(board.getTurn())) {
                    throw new IllegalMoveException("Castling through check is illegal.");
                }
            }
            else {
                fail(move);
            }
        }

        // cannot move more then 1 rank up or down
        if (Math.abs(move.getEndSquare().getRank().ordinal() - move.getStartSquare().getRank().ordinal()) > 1) {
            fail(move);
        }
    }

    private void assertLegalPawnMove(Move move, Position position, Color turn, Square enPassantSquare) throws IllegalMoveException {
        File startFile = move.getStartSquare().getFile();
        File endFile = move.getEndSquare().getFile();

        Rank startRank = move.getStartSquare().getRank();
        Rank endRank = move.getEndSquare().getRank();

        // we can never move more then one file away from the start square
        if (Math.abs(startFile.ordinal() - endFile.ordinal()) > 1) {
            fail(move);
        }

        // pawns only move forward
        if ((startRank.ordinal() - endRank.ordinal()) * turn.multiplier() >= 0) {
            fail(move);
        }

        // pawns can never move more then two ranks
        if ((endRank.ordinal() - startRank.ordinal()) * turn.multiplier() > 2) {
            fail(move);
        }

        // if we move two ranks, there cannot be a piece directly in front of us
        if ((endRank.ordinal() - startRank.ordinal()) * turn.multiplier() == 2) {
            Piece oneSquareInFront = position.get(Square.getSquare(startFile, startRank.getRelativeRank(turn.multiplier())));
            if (oneSquareInFront != null)
                fail(move);
        }

        // pawns can only move two ranks from their starting position
        // and then they can only move in a straight line
        if ((endRank.ordinal() - startRank.ordinal()) * turn.multiplier() == 2) {
            if (startFile != endFile) {
                fail(move);
            }

            if (turn == Black && startRank.ordinal() != 6) {
                fail(move);
            }
            else if (turn == White && startRank.ordinal() != 1) {
                fail(move);
            }
        }

        // pawns can only move diagonally when they are capturing a piece
        Piece endPiece = position.get(move.getEndSquare());
        if (endPiece != null && startFile == endFile) {
            throw new IllegalMoveException("Pawns can only capture diagonally.");
        }

        // if we are moving diagonally, we must capture a piece
        if (startFile != endFile && endPiece == null && move.getEndSquare() != enPassantSquare) {
            fail(move);
        }

        if (move.getEndSquare().isBackRank() && move.getPromotionPiece() == null)
            fail(move, "No promotion piece was specified for this move!");
    }

    private void fail(Move move) throws IllegalMoveException {
        fail(move, move + " is not a legal move.");
    }

    private void fail(Move move, String reason) throws IllegalMoveException {
        throw new IllegalMoveException(move + ": " + reason);
    }

    @Override
    public boolean isLegal(Move move, Board board) {
        try {
            assertLegal(move, board);
            return true;
        }
        catch (IllegalMoveException ime) {
            return false;
        }
    }

    @Override
    public boolean canAnyLegalMoveStartOn(Square square, Board board) {
        return getAnyLegalMoveStartingOn(square, board) != null;
    }

    private Move buildLegalKnightMove(Square square, Board board) {
        return buildLegalPieceMove(square, knightMoves, board);
    }

    private Move buildLegalPieceMove(Square square, List<Point> moves, Board board) {
        for (Point point : moves) {
            Move move = buildLegalMove(square, point.x, point.y, board);
            if (move != null)
                return move;
        }

        return null;
    }

    private Move buildLegalKingMove(Square square, Board board) {
        return buildLegalPieceMove(square, kingMoves, board);
    }

    private Move buildLegalRookMove(Square square, Board board) {
        for (int i = 0; i < 8; i++) {
            Square top = square.getRelativeSquare(0, i);
            if (top != null && isLegal(new Move(square, top), board)) {
                return new Move(square, top);
            }

            Square left = square.getRelativeSquare(-i, 0);
            if (left != null && isLegal(new Move(square, left), board)) {
                return new Move(square, left);
            }

            Square right = square.getRelativeSquare(i, 0);
            if (right != null && isLegal(new Move(square, right), board)) {
                return new Move(square, right);
            }

            Square bottom = square.getRelativeSquare(0, -i);
            if (bottom != null && isLegal(new Move(square, bottom), board)) {
                return new Move(square, bottom);
            }
        }

        return null;
    }

    private Move buildLegalBishopMove(Square square, Board board) {
        for (int i = 0; i < 8; i++) {
            Square topRight = square.getRelativeSquare(i, i);
            if (topRight != null && isLegal(new Move(square, topRight), board)) {
                return new Move(square, topRight);
            }

            Square topLeft = square.getRelativeSquare(-i, i);
            if (topLeft != null && isLegal(new Move(square, topLeft), board)) {
                return new Move(square, topLeft);
            }

            Square bottomRight = square.getRelativeSquare(i, -i);
            if (bottomRight != null && isLegal(new Move(square, bottomRight), board)) {
                return new Move(square, bottomRight);
            }

            Square bottomLeft = square.getRelativeSquare(-i, -i);
            if (bottomLeft != null && isLegal(new Move(square, bottomLeft), board)) {
                return new Move(square, bottomLeft);
            }
        }

        return null;
    }

    private Move buildLegalMove(Square square, int fileOffset, int rankOffset, Board board) {
        Square endSquare = square.getRelativeSquare(fileOffset, rankOffset);
        if (endSquare == null) {
            return null;
        }

        Move move = Move.buildMove(square, endSquare, board);
        if (isLegal(move, board))
            return move;
        else
            return null;
    }

    @Override
    public boolean canAnyLegalMoveEndOn(Square square, Board board) {
        return getLegalMovesEndingOn(square, board).size() > 0;
    }

    @Override
    public Collection<Move> getLegalMovesEndingOn(Square square, Board board) {
        Collection<Move> result = new ArrayList<Move>();
        for (PieceLocation pieceLocation : board.getPosition().getPieceLocations()) {
            final Move move = Move.buildMove(pieceLocation.getSquare(), square, board);
            if (board.isLegal(move)) {
                result.add(move);
            }
        }

        return result;
    }

    @Override
    public Collection<Move> getLegalMovesStartingOn(Square startSquare, Board board) {
        List<Move> result = new ArrayList<Move>();

        // not a very efficient implementation, but it works...
        // if speed is a problem, we could check the piece type on the start square and
        // then only search squares that are plausible destinations for that piece.
        for (Square square : Square.values()) {
            Move move = Move.buildMove(startSquare, square, board);
            if (isLegal(move, board)) {
                result.add(move);
            }
        }
        return result;
    }

    @Override
    public DrawType getDrawType(Board board) {
        // see if we have gone 50 full moves without a pawn move or capture
        if (board.getHalfMovesSinceLastPawnMoveOrCapture() >= 100) {
            return DrawType.FiftyMove;
        }

        // see if the side to move is in stalemate
        if (!currentPlayerHasLegalMove(board) && !board.getPosition().isInCheck(board.getTurn())) {
            return DrawType.Stalemate;
        }

        // see if we are repeating the same position for the third (or more) time
        if (board.getMoveHistory().isThreeMoveRepetition()) {
            return DrawType.ThreeMoveRepetition;
        }

        return null;
    }

    private boolean currentPlayerHasLegalMove(Board board) {
        return getAnyLegalMove(board) != null;
    }

    @Override
    public Move getAnyLegalMoveEndingOn(Square square, Board board) {
        return null;
    }

    @Override
    public Move getAnyLegalMoveStartingOn(Square square, Board board) {
        Piece piece = board.getPieceAt(square);
        Color turn = board.getTurn();
        if (piece == null || piece.getColor() != turn) {
            return null;
        }

        switch (piece.getPieceType()) {
            case Pawn:
                return buildLegalPawnMove(square, board, turn);
            case Bishop:
                return buildLegalBishopMove(square, board);
            case Knight:
                return buildLegalKnightMove(square, board);
            case Rook:
                return buildLegalRookMove(square, board);
            case Queen:
                Move bishopMove = buildLegalBishopMove(square, board);
                if (bishopMove != null)
                    return bishopMove;
                return buildLegalRookMove(square, board);
            default:
                return buildLegalKingMove(square, board);
        }
    }

    private Move buildLegalPawnMove(Square square, Board board, Color turn) {
        Move stepForward = buildLegalMove(square, 0, turn.multiplier(), board);
        if (stepForward != null)
            return stepForward;

        Move captureRight = buildLegalMove(square, 1, turn.multiplier(), board);
        if (captureRight != null)
            return captureRight;

        Move captureLeft = buildLegalMove(square, -1, turn.multiplier(), board);
        if (captureLeft != null)
            return captureLeft;

        // if stepping 1 forward is illegal, stepping two forward will almost never be legal...
        // ... except when stepping two forward blocks a check on the king!
        Move stepTwoForward = buildLegalMove(square, 0, 2 * turn.multiplier(), board);
        if (stepTwoForward != null)
            return stepTwoForward;

        return null;
    }

    @Override
    public Move getAnyLegalMove(Board board) {
        for (PieceLocation pieceLocation : board.getPosition().getPieceLocations()) {
            if (pieceLocation.getPiece().getColor() == board.getTurn()) {
                Move move = getAnyLegalMoveStartingOn(pieceLocation.getSquare(), board);
                if (move != null)
                    return move;
            }
        }

        return null;
    }

    @Override
    public boolean isCheckMate(Board board) {
        return !currentPlayerHasLegalMove(board) && board.getPosition().isInCheck(board.getTurn());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
