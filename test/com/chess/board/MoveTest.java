package com.chess.board;

import static com.chess.board.Promotion.Bishop;
import static com.chess.board.Promotion.Knight;
import static com.chess.board.Promotion.Queen;
import static com.chess.board.Promotion.Rook;
import static com.chess.board.Square.a1;
import static com.chess.board.Square.a2;
import static com.chess.board.Square.d1;
import static com.chess.board.Square.d2;
import static com.chess.board.Square.d7;
import static com.chess.board.Square.d8;
import static com.chess.board.Square.e2;
import static com.chess.board.Square.e3;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.g7;
import static com.chess.board.Square.g8;
import static com.chess.board.Square.h8;

import com.chess.application.ApplicationProperties;
import com.chess.util.SerializationTest;

public class MoveTest extends SerializationTest<Move> {
    
    private Promotion promotion;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        promotion = ApplicationProperties.getPromotionPiece();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ApplicationProperties.setPromotionPiece(promotion);
    }
    
    public void testLongAlgebraicIncludesPromotionPiece() throws Exception {
        assertEquals("d7d8Q", new Move(d7, d8).setPromotion(Queen).getLongAlgebraicNotation());
        
        assertEquals("d7d8N", new Move(d7, d8).setPromotion(Knight).getLongAlgebraicNotation());
        
        assertEquals("d7d8B", new Move(d7, d8).setPromotion(Bishop).getLongAlgebraicNotation());
        
        assertEquals("d7d8R", new Move(d7, d8).setPromotion(Rook).getLongAlgebraicNotation());
    }
    
    public void testPromotionDefaultsToNull() throws Exception {
        assertNull(new Move(e2, e4).getPromotionPiece());
    }
    
    public void testSerializable() throws Exception {
        Move move = new Move(g7, g8);
        assertSerialize(move);
    }
    
    public void testMoveEqualityComparesPromotionPiece() {
        assertFalse(new Move(g7, g8).setPromotion(Queen).equals(new Move(g7, g8).setPromotion(Bishop)));
        assertTrue(new Move(g7, g8).setPromotion(Queen).equals(new Move(g7, g8).setPromotion(Queen)));
        assertTrue(new Move(g7, g8).equals(new Move(g7, g8)));
    }
    
    public void testLongAlgebraic() {
        Move move = new Move(d1, e3);
        assertEquals("d1e3", move.getLongAlgebraicNotation());
    }
    
    public void testParseLongAlgebraic() {
        assertNull(Move.parseLongAlgebraicNotation("O-O"));
        assertEquals(new Move(a1, h8), Move.parseLongAlgebraicNotation("a1h8+"));
        assertEquals(new Move(a1, h8), Move.parseLongAlgebraicNotation("a1h8"));
        assertEquals(new Move(a2, a1).setPromotion(Bishop), Move.parseLongAlgebraicNotation("a2a1B"));
    }
    
    public void testEquality() {
        Move first = new Move(e2, e4);
        Move second = new Move(e2, e4);
        Move third = new Move(e2, e3);
        Move fourth = new Move(e3, e4);
        
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        
        assertFalse(first.equals(third));
        assertFalse(first.equals(fourth));
    }
    
    public void testSetPromotionPiece() {
        Move move = new Move(d2, d1).setPromotion(Knight);
        assertEquals(Knight, move.getPromotionPiece());
    }
}
