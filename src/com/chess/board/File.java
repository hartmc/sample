package com.chess.board;

import static com.chess.board.Side.Kingside;
import static com.chess.board.Side.Queenside;

public enum File {
    a, b, c, d, e, f, g, h;
    
    public File getRelativeFile(int fileOffset) {
        int newFile = ordinal() + fileOffset;
        if (newFile < 0 || newFile > 7) {
            return null;
        }
        
        return values()[newFile];
    }
    
    public Side getSide() {
        return ordinal() >= e.ordinal() ? Kingside : Queenside;
    }
    
    public int distanceFromCenter() {
        if (ordinal() < 4)
            return 3 - ordinal();
        else
            return ordinal() - 4;
    }
}
