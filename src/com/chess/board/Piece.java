package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.PieceType.Bishop;
import static com.chess.board.PieceType.King;
import static com.chess.board.PieceType.Knight;
import static com.chess.board.PieceType.Pawn;
import static com.chess.board.PieceType.Queen;
import static com.chess.board.PieceType.Rook;

public enum Piece {
    BlackPawn(Black, Pawn, "p"),
    BlackRook(Black, Rook, "r"),
    BlackKnight(Black, Knight, "n"),
    BlackBishop(Black, Bishop, "b"),
    BlackQueen(Black, Queen, "q"),
    BlackKing(Black, King, "k"),
    WhitePawn(White, Pawn, "P"),
    WhiteRook(White, Rook, "R"),
    WhiteKnight(White, Knight, "N"),
    WhiteBishop(White, Bishop, "B"),
    WhiteQueen(White, Queen, "Q"),
    WhiteKing(White, King, "K");
    
    private final Color color;
    private final PieceType pieceType;
    private final String fenEncoding;
    
    private Piece(Color color, PieceType pieceType, String fenEncoding) {
        this.color = color;
        this.pieceType = pieceType;
        this.fenEncoding = fenEncoding;
    }
    
    public Color getColor() {
        return color;
    }
    
    public PieceType getType() {
        return getPieceType();
    }
    
    public PieceType getPieceType() {
        return pieceType;
    }
    
    public static Piece getPiece(Color turn, PieceType pieceType) {
        return Piece.valueOf(turn.toString() + pieceType.toString());
    }
    
    public boolean isWhite() {
        return color == White;
    }
    
    public boolean isBlack() {
        return color == Black;
    }
    
    public String getFenEncoding() {
        return fenEncoding;
    }
    
    public static Piece pieceFromFenEncoding(char pieceChar) {
        for (Piece piece : Piece.values()) {
            if (piece.getFenEncoding().charAt(0) == pieceChar) {
                return piece;
            }
        }
        
        return null;
    }
}
