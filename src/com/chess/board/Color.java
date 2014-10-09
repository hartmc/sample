package com.chess.board;

import static com.chess.board.Rank.eighth;
import static com.chess.board.Rank.first;

public enum Color {
    White(1, first) {

        @Override
        public Color getOppositeColor() {
            return Black;
        }
    },
    Black(-1, eighth) {

        @Override
        public Color getOppositeColor() {
            return White;
        }
    };

    private int multiplier;
    private Rank backRank;

    public abstract Color getOppositeColor();

    private Color(int multiplier, Rank backRank) {
        this.multiplier = multiplier;
        this.backRank = backRank;
    }

    // utility method for doing color independent math on the chess board
    public int multiplier() {
        return multiplier;
    }

    // utility method for
    public Rank getBackRank() {
        return backRank;
    }
}
