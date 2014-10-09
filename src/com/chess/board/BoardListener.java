package com.chess.board;

public interface BoardListener extends MoveHistoryListener {
    
    public void gameDrawn(Board board, DrawType draw);
    
    public void checkMate(Board board);
    
    public void offerDraw(Board board);
    
    public void resign(Board board);
}
