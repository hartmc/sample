package com.chess.board;

public class PieceLocation {
    
    private final Piece piece;
    private final Square square;
    
    public PieceLocation(Piece piece, Square square) {
        this.piece = piece;
        this.square = square;
    }
    
    public Piece getPiece() {
        return piece;
    }
    
    public Square getSquare() {
        return square;
    }
    
    @Override
    public String toString() {
        return piece + " on " + square;
    }
}
