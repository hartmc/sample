package com.chess.board;

public class SimpleHistoryListener implements MoveHistoryListener {
    
    private int historyChangedCount = 0;
    private int historySelectionChangedCount;
    
    @Override
    public void moveHistoryChanged(MoveHistory history) {
        historyChangedCount++;
    }
    
    @Override
    public void moveHistorySelectionChanged(MoveHistory moveHistory) {
        historySelectionChangedCount++;
    }
    
    public int getHistorySelectionChangedCount() {
        return historySelectionChangedCount;
    }
    
    public int getHistoryChangedCount() {
        return historyChangedCount;
    }
}
