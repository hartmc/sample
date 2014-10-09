package com.chess.board;

import java.io.Serializable;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.File.a;
import static com.chess.board.File.h;
import static com.chess.board.Piece.*;
import static com.chess.board.Square.*;

/**
 * Fen after 1.e4: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1". Full move counter is incremented after each black
 * move
 */
public class Fen implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;
    private static Fen defaultFen = new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

    private Position position;
    private Color turn;
    private boolean whiteCastleKingside;
    private boolean whiteCastleQueenside;
    private boolean blackCastleKingside;
    private boolean blackCastleQueenside;
    private Square enPassantSquare;
    private int drawHalfMoveCounter;
    private int gameFullMoveCounter;

    public Fen(String fenString) throws FENFormatException {
        try {
            setDataFromFenString(fenString);
        }
        catch (RuntimeException re) {
            throw new FENFormatException(fenString, re);
        }
    }

    private void setDataFromFenString(String fenString) {
        if (fenString.startsWith("[FEN ")) {
            fenString = fenString.substring(6, fenString.lastIndexOf("\""));
        }
        else if (fenString.startsWith("FEN")) {
            fenString = fenString.substring(5, fenString.lastIndexOf("\""));
        }

        String positionDetails = fenString.substring(0, fenString.indexOf(" "));
        position = buildPositionFromString(positionDetails);
        turn = fenString.charAt(positionDetails.length() + 1) == 'w' ? White : Black;
        int castleStart = positionDetails.length() + 3;
        int castleEnd = fenString.indexOf(" ", castleStart);
        String castleString = fenString.substring(castleStart, castleEnd);
        whiteCastleKingside = castleString.contains("K") && position.get(e1) == WhiteKing && position.get(h1) == WhiteRook;
        whiteCastleQueenside = castleString.contains("Q") && position.get(e1) == WhiteKing && position.get(a1) == WhiteRook;
        blackCastleKingside = castleString.contains("k") && position.get(e8) == BlackKing && position.get(h8) == BlackRook;
        blackCastleQueenside = castleString.contains("q") && position.get(e8) == BlackKing && position.get(a8) == BlackRook;

        enPassantSquare = fenString.charAt(castleEnd + 1) == '-' ? null : Square.valueOf(fenString.substring(
                castleEnd + 1,
                castleEnd + 3));

        int lastSpace = fenString.lastIndexOf(" ");
        gameFullMoveCounter = Math.max(1, parseInt(fenString.substring(lastSpace + 1, fenString.length())));
        drawHalfMoveCounter = fenString.charAt(fenString.length() - 1) == '-' ? 0 : parseInt(fenString.substring(
                castleEnd + (enPassantSquare == null ? 3 : 4),
                lastSpace));
    }

    @Override
    public Fen clone() {
        try {
            return (Fen) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private int parseInt(String moveCounterString) {
        if (moveCounterString.equals("-"))
            return 0;
        return Integer.parseInt(moveCounterString);
    }

    public String getFenString() {
        StringBuilder buffer = getPositionBuffer();
        buffer.append(turn == White ? " w " : " b ");
        buffer.append(buildCastleString());
        buffer.append(enPassantSquare == null ? " - " : " " + enPassantSquare + " ");
        buffer.append(drawHalfMoveCounter).append(" ").append(gameFullMoveCounter);
        return buffer.toString();
    }

    private StringBuilder getPositionBuffer() {
        StringBuilder result = new StringBuilder();
        int emptySquares = 0;
        for (Rank rank : Rank.reverseValues()) {
            for (File file : File.values()) {
                Piece piece = position.get(Square.getSquare(file, rank));
                if (piece == null) {
                    emptySquares++;
                    continue;
                }
                else if (emptySquares > 0) {
                    result.append(emptySquares);
                    emptySquares = 0;
                }

                result.append(piece.getFenEncoding());
            }

            if (emptySquares > 0) {
                result.append(emptySquares);
                emptySquares = 0;
            }

            if (rank != Rank.first) {
                result.append("/");
            }
        }

        return result;
    }

    private String buildCastleString() {
        StringBuilder buffer = new StringBuilder();
        if (canWhiteCastleKingside())
            buffer.append("K");
        if (canWhiteCastleQueenside())
            buffer.append("Q");
        if (canBlackCastleKingside())
            buffer.append("k");
        if (canBlackCastleQueenside())
            buffer.append("q");

        if (buffer.length() == 0)
            return "-";
        else
            return buffer.toString();
    }

    public boolean equalsIgnoreMoveCount(Fen fen) {
        return position.equals(fen.position) && turn == fen.turn && whiteCastleKingside == fen.whiteCastleKingside
               && whiteCastleQueenside == fen.whiteCastleQueenside && blackCastleKingside == fen.blackCastleKingside
               && blackCastleQueenside == fen.blackCastleQueenside && enPassantSquare == fen.enPassantSquare;
    }

    public boolean canWhiteCastleKingside() {
        return whiteCastleKingside;
    }

    public boolean canWhiteCastleQueenside() {
        return whiteCastleQueenside;
    }

    public boolean canBlackCastleQueenside() {
        return blackCastleQueenside;
    }

    public boolean canBlackCastleKingside() {
        return blackCastleKingside;
    }

    public int getHalfMovesSinceLastPawnMoveOrCapture() {
        return drawHalfMoveCounter;
    }

    public int getFullMoveCount() {
        return gameFullMoveCounter;
    }

    public Square getEnPassantSquare() {
        return enPassantSquare;
    }

    public Color getTurn() {
        return turn;
    }

    public Position getPosition() {
        return position;
    }

    private Position buildPositionFromString(String positionDetails) {
        Position position = new Position();
        Square currentSquare = a8;
        for (int i = 0; i < positionDetails.length(); i++) {
            char nextChar = positionDetails.charAt(i);
            if (nextChar == '/')
                continue;

            Piece nextPiece = Piece.pieceFromFenEncoding(nextChar);
            if (nextPiece != null) {
                position = position.put(currentSquare, nextPiece);
            }
            else if (currentSquare.getFile() != h) {
                int emptyFiles = Integer.parseInt(String.valueOf(nextChar)) - 1;
                currentSquare = Square.getSquare(currentSquare.getFile().getRelativeFile(emptyFiles), currentSquare.getRank());
            }

            Square nextSquare = Square.getSquare(currentSquare.getFile().getRelativeFile(1), currentSquare.getRank());
            if (nextSquare == null) {
                currentSquare = Square.getSquare(a, currentSquare.getRank().getRelativeRank(-1));
            }
            else {
                currentSquare = nextSquare;
            }
        }

        return position;
    }

    @Override
    public String toString() {
        return getFenString();
    }

    public Fen setCanWhiteCastleKingside(boolean castle) {
        Fen clone = clone();
        clone.whiteCastleKingside = castle;
        return new Fen(clone.toString());
    }

    public Fen setCanWhiteCastleQueenside(boolean castle) {
        Fen clone = clone();
        clone.whiteCastleQueenside = castle;
        return new Fen(clone.toString());
    }

    public Fen setCanBlackCastleQueenside(boolean castle) {
        Fen clone = clone();
        clone.blackCastleQueenside = castle;
        return new Fen(clone.toString());
    }

    public Fen setCanBlackCastleKingside(boolean castle) {
        Fen clone = clone();
        clone.blackCastleKingside = castle;
        return new Fen(clone.toString());
    }

    public Fen setTurn(Color color) {
        Fen clone = clone();
        clone.turn = color;
        return clone;
    }

    public static Fen buildPartialFenFromPosition(Position position) {
        StringBuilder result = new StringBuilder();
        int emptySquares = 0;
        for (Rank rank : Rank.reverseValues()) {
            for (File file : File.values()) {
                Piece piece = position.get(Square.getSquare(file, rank));
                if (piece == null) {
                    emptySquares++;
                    continue;
                }
                else if (emptySquares > 0) {
                    result.append(emptySquares);
                    emptySquares = 0;
                }

                result.append(piece.getFenEncoding());
            }

            if (emptySquares > 0) {
                result.append(emptySquares);
                emptySquares = 0;
            }

            if (rank != Rank.first) {
                result.append("/");
            }
        }

        result.append(" w KQkq - 0 1");
        return new Fen(result.toString());
    }

    public Fen setFullMoveCounter(int totalFullMoves) {
        Fen fen = clone();
        fen.gameFullMoveCounter = totalFullMoves;
        return fen;
    }

    public Fen setDrawHalfMoveCounter(int halfMovesTowardDraw) {
        Fen fen = clone();
        fen.drawHalfMoveCounter = halfMovesTowardDraw;
        return fen;
    }

    public Fen setEnPassantSquare(Square enPassantSquare2) {
        Fen fen = clone();
        fen.enPassantSquare = enPassantSquare2;
        return fen;
    }

    public Fen setPosition(Position position) {
        Fen copy = clone();
        copy.position = position;
        return copy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (blackCastleKingside ? 1231 : 1237);
        result = prime * result + (blackCastleQueenside ? 1231 : 1237);
        result = prime * result + drawHalfMoveCounter;
        result = prime * result + ((enPassantSquare == null) ? 0 : enPassantSquare.hashCode());
        result = prime * result + gameFullMoveCounter;
        result = prime * result + ((position == null) ? 0 : position.hashCode());
        result = prime * result + ((turn == null) ? 0 : turn.hashCode());
        result = prime * result + (whiteCastleKingside ? 1231 : 1237);
        result = prime * result + (whiteCastleQueenside ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Fen other = (Fen) obj;
        if (blackCastleKingside != other.blackCastleKingside)
            return false;
        if (blackCastleQueenside != other.blackCastleQueenside)
            return false;
        if (drawHalfMoveCounter != other.drawHalfMoveCounter)
            return false;
        if (enPassantSquare == null) {
            if (other.enPassantSquare != null)
                return false;
        }
        else if (!enPassantSquare.equals(other.enPassantSquare))
            return false;
        if (gameFullMoveCounter != other.gameFullMoveCounter)
            return false;
        if (position == null) {
            if (other.position != null)
                return false;
        }
        else if (!position.equals(other.position))
            return false;
        if (turn == null) {
            if (other.turn != null)
                return false;
        }
        else if (!turn.equals(other.turn))
            return false;
        if (whiteCastleKingside != other.whiteCastleKingside)
            return false;
        if (whiteCastleQueenside != other.whiteCastleQueenside)
            return false;
        return true;
    }

    public static Fen getDefaultFen() {
        return defaultFen;
    }

    public int getHalfMoveDifferenceTo(Fen position) {
        if (position.before(this)) {
            return -1 * position.getHalfMoveDifferenceTo(this);
        }

        int halfMoves = 2 * (position.getFullMoveCount() - getFullMoveCount());
        if (getTurn() != position.getTurn())
            halfMoves += getTurn() == White ? 1 : -1;

        return halfMoves;
    }

    private boolean before(Fen fen) {
        return getFullMoveCount() < fen.getFullMoveCount()
               || (getFullMoveCount() == fen.getFullMoveCount() && getTurn() == White && fen.getTurn() == Black);
    }
}
