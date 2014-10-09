package com.chess.board;

public enum PieceType {
    Pawn(""), Knight("N"), Bishop("B"), Rook("R"), Queen("Q"), King("K");
    
    private final String algebraicDescription;
    
    private PieceType(String algebraicDescription) {
        this.algebraicDescription = algebraicDescription;
    }
    
    public String getAlgebraicDescription() {
        return algebraicDescription;
    }
    
    public boolean isMoreValuableThan(PieceType pieceType) {
        return ordinal() > pieceType.ordinal();
    }
}
