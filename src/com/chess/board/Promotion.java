package com.chess.board;

public enum Promotion {
    Queen(PieceType.Queen), Knight(PieceType.Knight), Bishop(PieceType.Bishop), Rook(PieceType.Rook);
    
    private PieceType type;
    
    private Promotion(PieceType type) {
        this.type = type;
    }
    
    public PieceType getPieceType() {
        return type;
    }
    
    public static Promotion fromChar(char character) {
        switch (character) {
            case ('Q'):
            case ('q'):
                return Queen;
            case ('N'):
            case ('n'):
                return Knight;
            case ('R'):
            case ('r'):
                return Rook;
            case ('b'):
            case ('B'):
                return Bishop;
        }
        
        return null;
    }
}
