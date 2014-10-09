package com.chess.board;

import static com.chess.board.File.a;
import static com.chess.board.File.b;
import static com.chess.board.File.c;
import static com.chess.board.File.d;
import static com.chess.board.File.e;
import static com.chess.board.File.f;
import static com.chess.board.File.g;
import static com.chess.board.File.h;
import static com.chess.board.Side.Kingside;
import static com.chess.board.Side.Queenside;
import junit.framework.TestCase;

public class FileTest extends TestCase {
    
    public void testDistanceFromCenter() {
        assertEquals(3, a.distanceFromCenter());
        assertEquals(2, b.distanceFromCenter());
        assertEquals(1, c.distanceFromCenter());
        assertEquals(0, d.distanceFromCenter());
        assertEquals(0, e.distanceFromCenter());
        assertEquals(1, f.distanceFromCenter());
        assertEquals(2, g.distanceFromCenter());
        assertEquals(3, h.distanceFromCenter());
    }
    
    public void testGetSide() {
        assertEquals(Kingside, e.getSide());
        assertEquals(Kingside, f.getSide());
        assertEquals(Kingside, g.getSide());
        assertEquals(Kingside, h.getSide());
        assertEquals(Queenside, a.getSide());
        assertEquals(Queenside, b.getSide());
        assertEquals(Queenside, c.getSide());
        assertEquals(Queenside, d.getSide());
    }
    
    public void testGetRelativeFile() throws Exception {
        assertEquals(a, b.getRelativeFile(-1));
        assertNull(b.getRelativeFile(-2));
        assertEquals(h, a.getRelativeFile(7));
        assertEquals(e, h.getRelativeFile(-3));
        assertNull(h.getRelativeFile(1));
    }
}
