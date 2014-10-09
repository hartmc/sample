package com.chess.board;

import static com.chess.board.Color.Black;

public enum  Rank {
    first, second, third, fourth, fifth, sixth, seventh, eighth;
    
    public Rank getRelativeRank(int rankOffset) {
        int newRank = ordinal() + rankOffset;
        if (newRank < 0 || newRank > 7) {
            return null;
        }
        return values()[newRank];
    }
    
    public int number() {
        return ordinal() + 1;
    }
    
    public static Rank[] reverseValues() {
        return new Rank[] { eighth, seventh, sixth, fifth, fourth, third, second, first };
    }
    
    /**
     * for white, returns this. For black, returns the ranks in opposite order, ie, the 8th is the 1st rank from black's
     * perspective, the 7th rank is the 2nd rank from black's perspective, etc.
     */
    public Rank fromPerspective(Color color) {
        if (color == Black) {
            return values()[7 - ordinal()];
        }
        
        return this;
    }
    
    public int distanceFromCenter() {
        if (ordinal() < 4)
            return 3 - ordinal();
        else
            return ordinal() - 4;
    }
}
