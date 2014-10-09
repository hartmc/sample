package com.chess.board;

public interface MoveHistoryListener {
    
    public void moveHistoryChanged(MoveHistory moveHistory);
    
    public void moveHistorySelectionChanged(MoveHistory moveHistory);
}
