package com.chess.board;

public class FENFormatException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public FENFormatException(String badFEN, RuntimeException re) {
        super("\"" + badFEN + "\" is not a properly formatted FEN string.", re);
        
    }
}
