package com.chess.board;

import static com.chess.board.File.a;
import static com.chess.board.File.b;
import static com.chess.board.File.d;
import static com.chess.board.File.e;
import static com.chess.board.File.f;
import static com.chess.board.File.h;
import static com.chess.board.Rank.eighth;
import static com.chess.board.Rank.fifth;
import static com.chess.board.Rank.first;
import static com.chess.board.Rank.seventh;
import static com.chess.board.Rank.sixth;
import static com.chess.board.Rank.third;
import static com.chess.board.Square.a1;
import static com.chess.board.Square.a2;
import static com.chess.board.Square.a3;
import static com.chess.board.Square.a4;
import static com.chess.board.Square.a5;
import static com.chess.board.Square.a6;
import static com.chess.board.Square.a7;
import static com.chess.board.Square.a8;
import static com.chess.board.Square.b1;
import static com.chess.board.Square.b2;
import static com.chess.board.Square.b4;
import static com.chess.board.Square.b6;
import static com.chess.board.Square.b7;
import static com.chess.board.Square.b8;
import static com.chess.board.Square.c1;
import static com.chess.board.Square.c3;
import static com.chess.board.Square.c4;
import static com.chess.board.Square.c5;
import static com.chess.board.Square.c6;
import static com.chess.board.Square.c7;
import static com.chess.board.Square.c8;
import static com.chess.board.Square.d1;
import static com.chess.board.Square.d2;
import static com.chess.board.Square.d3;
import static com.chess.board.Square.d4;
import static com.chess.board.Square.d6;
import static com.chess.board.Square.d7;
import static com.chess.board.Square.d8;
import static com.chess.board.Square.e1;
import static com.chess.board.Square.e3;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.e5;
import static com.chess.board.Square.e6;
import static com.chess.board.Square.e7;
import static com.chess.board.Square.e8;
import static com.chess.board.Square.f1;
import static com.chess.board.Square.f2;
import static com.chess.board.Square.f3;
import static com.chess.board.Square.f4;
import static com.chess.board.Square.f5;
import static com.chess.board.Square.f6;
import static com.chess.board.Square.f8;
import static com.chess.board.Square.g1;
import static com.chess.board.Square.g2;
import static com.chess.board.Square.g3;
import static com.chess.board.Square.g5;
import static com.chess.board.Square.g6;
import static com.chess.board.Square.g7;
import static com.chess.board.Square.g8;
import static com.chess.board.Square.h1;
import static com.chess.board.Square.h2;
import static com.chess.board.Square.h4;
import static com.chess.board.Square.h6;
import static com.chess.board.Square.h8;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class SquareTest extends TestCase {
    
    public void testGetSquareWithInvalidOrdinal() {
        assertNull(Square.getSquare(a, null));
        assertNull(Square.getSquare(null, c4.getRank()));
    }
    
    public void testNoSquaresBetweenDisjointSquares() {
        assertEquals(0, c4.getSquaresBetween(e1).length);
        assertEquals(0, b6.getSquaresBetween(d7).length);
        assertEquals(0, h1.getSquaresBetween(b6).length);
        assertEquals(0, e4.getSquaresBetween(g3).length);
        assertEquals(0, e4.getSquaresBetween(e5).length);
        assertEquals(0, e4.getSquaresBetween(f5).length);
        assertEquals(0, e4.getSquaresBetween(f4).length);
    }
    
    public void testGetSquaresInBetweenOnFile() {
        assertArrayEquals(new Square[] { c4, c5, c6, c7 }, c3.getSquaresBetween(c8));
        assertArrayEquals(new Square[] { a2, a3, a4, a5, a6, a7 }, a1.getSquaresBetween(a8));
        assertArrayEquals(new Square[] { c7, c6, c5, c4 }, c8.getSquaresBetween(c3));
    }
    
    public void testGetSquaresInBetweenOnRank() {
        assertArrayEquals(new Square[] { c4, d4, e4 }, b4.getSquaresBetween(f4));
        assertArrayEquals(new Square[] { b6, c6, d6, e6, f6, g6 }, a6.getSquaresBetween(h6));
        assertArrayEquals(new Square[] { e4, d4, c4 }, f4.getSquaresBetween(b4));
    }
    
    public void testGetSquaresInBetweenOnDiagonal() {
        assertArrayEquals(new Square[] { e3, d4, c5 }, f2.getSquaresBetween(b6));
        assertArrayEquals(new Square[] { b2, c3, d4, e5, f6, g7 }, a1.getSquaresBetween(h8));
        assertArrayEquals(new Square[] { c5, d4, e3 }, b6.getSquaresBetween(f2));
    }
    
    private void assertArrayEquals(Square[] squares, Square[] squares2) {
        assertEquals(squares.length, squares2.length);
        for (int i = 0; i < squares.length; i++) {
            assertEquals(squares[i], squares2[i]);
        }
    }
    
    public void testGetSquareByOffset() {
        assertEquals(a1, e3.getRelativeSquare(-4, -2));
        assertEquals(f4, e3.getRelativeSquare(1, 1));
        
        assertNull(g2.getRelativeSquare(1, -2));
        assertNull(f3.getRelativeSquare(4, 1));
        assertNull(b7.getRelativeSquare(-2, 1));
        assertNull(b2.getRelativeSquare(1, -3));
    }
    
    public void testIsDarkSquare() throws Exception {
        List<Square> darkSquares = Arrays.asList(new Square[] { a1, a3, a5, a7, b2, b4, b6, b8, c1, c3, c5, c7, d2, d4, d6, d8, e1,
                                                               e3, e5, e7, f2, f4, f6, f8, g1, g3, g5, g7, h2, h4, h6, h8 });
        for (Square square : Square.values()) {
            if (darkSquares.contains(square)) {
                assertTrue(square.isDarkSquare());
                assertFalse(square.isLightSquare());
            }
            else {
                assertTrue(square.isLightSquare());
                assertFalse(square.isDarkSquare());
            }
        }
    }
    
    public void testGetRelativeSquare() throws Exception {
        assertEquals(d3, e1.getRelativeSquare(-1, 2));
        assertNull(e1.getRelativeSquare(0, -1));
        assertEquals(d2, d2.getRelativeSquare(0, 0));
        assertNull(e6.getRelativeSquare(1, 8));
        assertEquals(h8, a1.getRelativeSquare(7, 7));
        assertEquals(a1, h8.getRelativeSquare(-7, -7));
        assertEquals(a8, h1.getRelativeSquare(-7, 7));
    }
    
    public void testGetSquare() throws Exception {
        assertEquals(a1, Square.getSquare(a, first));
        assertEquals(a8, Square.getSquare(a, eighth));
        assertEquals(h1, Square.getSquare(h, first));
        assertEquals(h8, Square.getSquare(h, eighth));
        assertEquals(d3, Square.getSquare(d, third));
        assertEquals(f5, Square.getSquare(f, fifth));
        assertEquals(e6, Square.getSquare(e, sixth));
        assertEquals(b7, Square.getSquare(b, seventh));
    }
    
    public void testIsBackRank() throws Exception {
        assertTrue(a1.isBackRank());
        assertTrue(b1.isBackRank());
        assertTrue(c1.isBackRank());
        assertTrue(d1.isBackRank());
        assertTrue(e1.isBackRank());
        assertTrue(f1.isBackRank());
        assertTrue(g1.isBackRank());
        assertTrue(h1.isBackRank());
        assertTrue(a8.isBackRank());
        assertTrue(b8.isBackRank());
        assertTrue(c8.isBackRank());
        assertTrue(d8.isBackRank());
        assertTrue(e8.isBackRank());
        assertTrue(f8.isBackRank());
        assertTrue(g8.isBackRank());
        assertTrue(h8.isBackRank());
        assertFalse(a2.isBackRank());
        assertFalse(a3.isBackRank());
        assertFalse(a4.isBackRank());
        assertFalse(a5.isBackRank());
        assertFalse(a6.isBackRank());
        assertFalse(a7.isBackRank());
    }
}
