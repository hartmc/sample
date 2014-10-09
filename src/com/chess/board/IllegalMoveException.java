package com.chess.board;

public class IllegalMoveException extends Exception {
    
    public IllegalMoveException(String message) {
        super(message);
    }
    
    private static final long serialVersionUID = 1L;
    
}
