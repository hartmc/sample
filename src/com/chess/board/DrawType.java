package com.chess.board;

public enum DrawType {
    Stalemate("stalemate"), FiftyMove("50 move rule"), ThreeMoveRepetition("3 move repetition"), InsufficientMatingMaterial(
            "insufficient material"), MutuallyAgreed("agreement");
    
    private String description;
    
    private DrawType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
