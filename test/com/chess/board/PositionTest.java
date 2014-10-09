package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Piece.BlackBishop;
import static com.chess.board.Piece.BlackKing;
import static com.chess.board.Piece.BlackKnight;
import static com.chess.board.Piece.BlackQueen;
import static com.chess.board.Piece.BlackRook;
import static com.chess.board.Piece.WhiteBishop;
import static com.chess.board.Piece.WhiteKing;
import static com.chess.board.Piece.WhiteKnight;
import static com.chess.board.Piece.WhitePawn;
import static com.chess.board.Piece.WhiteQueen;
import static com.chess.board.Piece.WhiteRook;
import static com.chess.board.Square.a1;
import static com.chess.board.Square.a2;
import static com.chess.board.Square.a5;
import static com.chess.board.Square.b3;
import static com.chess.board.Square.b7;
import static com.chess.board.Square.c4;
import static com.chess.board.Square.d4;
import static com.chess.board.Square.d5;
import static com.chess.board.Square.d7;
import static com.chess.board.Square.e1;
import static com.chess.board.Square.e2;
import static com.chess.board.Square.e3;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.e7;
import static com.chess.board.Square.e8;
import static com.chess.board.Square.f1;
import static com.chess.board.Square.f2;
import static com.chess.board.Square.f3;
import static com.chess.board.Square.g2;
import static com.chess.board.Square.g7;
import static com.chess.board.Square.h1;
import static com.chess.board.Square.h3;
import static com.chess.board.Square.h7;
import static com.chess.board.Square.h8;

import java.util.Collection;

import com.chess.util.SerializationTest;

public class PositionTest extends SerializationTest<Position> {
    
    public void testSerializable() throws Exception {
        Position position = new Board().getPosition();
        assertSerialize(position);
    }
    
    public void testPutPieceOnMultipleSquares() throws Exception {
        Position position = new Position();
        Position result = position.put(WhitePawn, a2, b3, c4, d4);
        assertEquals(WhitePawn, result.get(a2));
        assertEquals(WhitePawn, result.get(b3));
        assertEquals(WhitePawn, result.get(c4));
        assertEquals(WhitePawn, result.get(d4));
        
        assertNull(position.get(a2));
        assertNull(position.get(b3));
        assertNull(position.get(c4));
        assertNull(position.get(d4));
    }
    
    public void testCloneIsIndepedent() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        
        Position copy = position.clone();
        copy.put(e2, WhiteQueen);
        
        assertNull(position.get(e2));
        position = position.put(e7, BlackQueen);
        assertNull(copy.get(e7));
    }
    
    public void testGetPieceLocations() throws Exception {
        Position position = new Position();
        position = position.put(e4, WhiteKing);
        position = position.put(h7, WhiteKnight);
        position = position.put(e2, WhiteBishop);
        position = position.put(h3, WhitePawn);
        position = position.put(e1, BlackRook);
        position = position.put(d7, BlackKing);
        
        Collection<PieceLocation> pieceLocations = position.getPieceLocations();
        assertEquals(6, pieceLocations.size());
        for (PieceLocation location : pieceLocations) {
            switch (location.getPiece()) {
                case WhiteKing:
                    assertEquals(e4, location.getSquare());
                    break;
                case WhiteKnight:
                    assertEquals(h7, location.getSquare());
                    break;
                case WhiteBishop:
                    assertEquals(e2, location.getSquare());
                    break;
                case WhitePawn:
                    assertEquals(h3, location.getSquare());
                    break;
                case BlackRook:
                    assertEquals(e1, location.getSquare());
                    break;
                case BlackKing:
                    assertEquals(d7, location.getSquare());
                    break;
                default:
                    fail("unknown piece " + location.getPiece());
            }
        }
    }
    
    public void testIsInCheckFromRook() throws Exception {
        Position position = new Position();
        position = position.put(e4, WhiteKing);
        position = position.put(e1, BlackRook);
        position = position.put(d7, BlackKing);
        position = position.put(h7, WhiteRook);
        
        assertTrue(position.isInCheck(White));
        assertTrue(position.isInCheck(Black));
    }
    
    public void testIsInCheckFromKnight() throws Exception {
        Position position = new Position();
        position = position.put(e3, WhiteKing);
        position = position.put(f1, BlackKnight);
        position = position.put(b3, BlackKing);
        position = position.put(a5, WhiteKnight);
        
        assertTrue(position.isInCheck(White));
        assertTrue(position.isInCheck(Black));
    }
    
    public void testIsInCheckFromBishop() throws Exception {
        Position position = new Position();
        position = position.put(h8, WhiteKing);
        position = position.put(a1, BlackBishop);
        position = position.put(d5, BlackKing);
        position = position.put(g2, WhiteBishop);
        
        assertTrue(position.isInCheck(White));
        assertTrue(position.isInCheck(Black));
    }
    
    public void testInInCheckFromQueen() throws Exception {
        Position position = new Position();
        position = position.put(g7, WhiteKing);
        position = position.put(g2, BlackQueen);
        position = position.put(f3, WhiteQueen);
        position = position.put(b7, BlackKing);
        
        assertTrue(position.isInCheck(White));
        assertTrue(position.isInCheck(Black));
    }
    
    public void testClone() throws Exception {
        Position first = new Position();
        first = first.put(e2, WhiteBishop);
        
        Position second = new Position();
        second = second.put(e2, WhiteBishop);
        second = second.put(e1, WhiteKing);
        second = second.put(e8, BlackKing);
        second = second.put(f2, WhiteKnight);
        
        assertFalse(first.equals(second));
        
        Position copy = second.clone();
        assertEquals(second, copy);
        assertEquals(second.hashCode(), copy.hashCode());
        
        copy = copy.put(f3, Piece.WhiteQueen);
        assertFalse(second.equals(copy));
    }
    
    // this test threw an NPE when written
    public void testPutNullRemovesFromMap() throws Exception {
        Position position = new Position();
        Position result = position = position.put(e2, WhitePawn);
        result = position = position.put(e2, null);
        
        try {
            assertFalse(result.isInCheck(White));
            fail("no king should throw exception");
        }
        catch (IllegalStateException ise) {
        }
    }
    
    // this test is ensuring that checking the "i2" for pawns is not a problem
    public void testNPEForKingCheckOnEdgeOfBoard() throws Exception {
        Position position = new Position();
        position = position.put(h1, Piece.WhiteKing);
        assertFalse(position.isInCheck(White));
    }
}
