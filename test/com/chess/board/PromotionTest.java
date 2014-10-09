package com.chess.board;

import static com.chess.board.Promotion.Bishop;
import static com.chess.board.Promotion.Knight;
import static com.chess.board.Promotion.Queen;
import static com.chess.board.Promotion.Rook;
import junit.framework.TestCase;

public class PromotionTest extends TestCase {
    
    public void testPromotionFromChar() {
        assertNull(Promotion.fromChar('x'));
        assertEquals(Bishop, Promotion.fromChar('b'));
        assertEquals(Bishop, Promotion.fromChar('B'));
        assertEquals(Queen, Promotion.fromChar('q'));
        assertEquals(Queen, Promotion.fromChar('Q'));
        assertEquals(Rook, Promotion.fromChar('r'));
        assertEquals(Rook, Promotion.fromChar('R'));
        assertEquals(Knight, Promotion.fromChar('n'));
        assertEquals(Knight, Promotion.fromChar('N'));
    }
}
