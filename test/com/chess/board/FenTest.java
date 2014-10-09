package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Piece.BlackBishop;
import static com.chess.board.Piece.BlackKing;
import static com.chess.board.Piece.BlackKnight;
import static com.chess.board.Piece.BlackPawn;
import static com.chess.board.Piece.BlackRook;
import static com.chess.board.Piece.WhiteBishop;
import static com.chess.board.Piece.WhiteKing;
import static com.chess.board.Piece.WhiteKnight;
import static com.chess.board.Piece.WhitePawn;
import static com.chess.board.Piece.WhiteRook;
import static com.chess.board.Square.a1;
import static com.chess.board.Square.a2;
import static com.chess.board.Square.a6;
import static com.chess.board.Square.a8;
import static com.chess.board.Square.b2;
import static com.chess.board.Square.b4;
import static com.chess.board.Square.b5;
import static com.chess.board.Square.b7;
import static com.chess.board.Square.c2;
import static com.chess.board.Square.c5;
import static com.chess.board.Square.d4;
import static com.chess.board.Square.e1;
import static com.chess.board.Square.e2;
import static com.chess.board.Square.e3;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.e5;
import static com.chess.board.Square.e6;
import static com.chess.board.Square.e7;
import static com.chess.board.Square.e8;
import static com.chess.board.Square.f2;
import static com.chess.board.Square.f3;
import static com.chess.board.Square.f4;
import static com.chess.board.Square.f5;
import static com.chess.board.Square.f6;
import static com.chess.board.Square.g1;
import static com.chess.board.Square.g2;
import static com.chess.board.Square.g6;
import static com.chess.board.Square.g8;
import static com.chess.board.Square.h1;
import static com.chess.board.Square.h3;
import static com.chess.board.Square.h7;
import static com.chess.board.Square.h8;

import com.chess.board.SerializationTest;

public class FenTest extends SerializationTest<Fen> {
    
    public void testCountHalfMoveDiff() throws Exception {
        Fen start = Fen.getDefaultFen();
        Fen halfMoveLater = new Board().move(e2, e4).getFen();
        
        assertEquals(1, start.getHalfMoveDifferenceTo(halfMoveLater));
        assertEquals(-1, halfMoveLater.getHalfMoveDifferenceTo(start));
        assertEquals(0, start.getHalfMoveDifferenceTo(start));
        
        Fen twoFullMovesLater = new Board().move(e2, e4).move(e7, e5).move(f2, f4).move(e5, f4).getFen();
        
        assertEquals(4, start.getHalfMoveDifferenceTo(twoFullMovesLater));
        assertEquals(-4, twoFullMovesLater.getHalfMoveDifferenceTo(start));
        
        Fen threeHalfMovesLater = new Board().move(e2, e4).move(e7, e5).move(f2, f4).getFen();
        assertEquals(3, start.getHalfMoveDifferenceTo(threeHalfMovesLater));
        assertEquals(-3, threeHalfMovesLater.getHalfMoveDifferenceTo(start));
        
        Fen blackToMove = new Board().move(e2, e4).getFen();
        Fen whiteToMove = new Board().move(e2, e4).move(e7, e5).getFen();
        assertEquals(1, blackToMove.getHalfMoveDifferenceTo(whiteToMove));
        assertEquals(-1, whiteToMove.getHalfMoveDifferenceTo(blackToMove));
    }
    
    public void testFenDefaultToMoveOne() throws Exception {
        Fen fen = new Fen("[FEN \"2kr4/p4R1R/2p1r1p1/1Np4b/2P4P/8/PP1p2PK/8 w - -\"]\n");
        assertEquals(new Fen("2kr4/p4R1R/2p1r1p1/1Np4b/2P4P/8/PP1p2PK/8 w - - 0 1"), fen);
    }
    
    public void testAnotherChessDotComFenFormat() {
        Fen fen = new Fen("[FEN \"2kr4/p4R1R/2p1r1p1/1Np4b/2P4P/8/PP1p2PK/8 w - - 0 1\"]\n");
        assertEquals(new Fen("2kr4/p4R1R/2p1r1p1/1Np4b/2P4P/8/PP1p2PK/8 w - - 0 1"), fen);
    }
    
    public void testAnotherFENNotation() {
        Fen fen = new Fen("FEN \"8/3bq2p/2pp1kp1/7P/3P1Q2/1B6/1P4P1/5K2 b - - 0 1\"");
        assertEquals(new Fen("8/3bq2p/2pp1kp1/7P/3P1Q2/1B6/1P4P1/5K2 b - - 0 1"), fen);
    }
    
    public void testChessDotComFenFormat() {
        Fen fen = new Fen("[FEN \"8/3bq2p/2pp1kp1/7P/3P1Q2/1B6/1P4P1/5K2 b - - 0 1\"]");
        assertEquals(new Fen("8/3bq2p/2pp1kp1/7P/3P1Q2/1B6/1P4P1/5K2 b - - 0 1"), fen);
    }
    
    public void testCannotTurnOnCastlingIfCastlingIsIllegal() {
        Fen fen = new Fen("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
        fen = fen.setCanBlackCastleKingside(true);
        fen = fen.setCanBlackCastleQueenside(true);
        fen = fen.setCanWhiteCastleKingside(true);
        fen = fen.setCanWhiteCastleQueenside(true);
        
        assertEquals("4k3/8/8/8/8/8/8/4K3 w - - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalCastling() throws Exception {
        Fen fen = new Fen("1r2r1k1/p1q2pb1/3pn1pp/2p4n/2P1PP2/P4NPP/RBQN3K/4R3 b KQkq - 0 1");
        assertEquals("1r2r1k1/p1q2pb1/3pn1pp/2p4n/2P1PP2/P4NPP/RBQN3K/4R3 b - - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalWhiteKingsideCastleDueToRookPosition() throws Exception {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K1R1 w KQkq - 0 1");
        assertEquals("r3k2r/8/8/8/8/8/8/R3K1R1 w Qkq - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalWhiteKingsideCastleDueToKingPosition() throws Exception {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R4K1R w Kkq - 0 1");
        assertEquals("r3k2r/8/8/8/8/8/8/R4K1R w kq - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalWhiteQueensideCastleDueToRookPosition() throws Exception {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/1R2K2R w KQkq - 0 1");
        assertEquals("r3k2r/8/8/8/8/8/8/1R2K2R w Kkq - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalWhiteQueensideCastleDueToKingPosition() throws Exception {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R4K1R w Qkq - 0 1");
        assertEquals("r3k2r/8/8/8/8/8/8/R4K1R w kq - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalBlackKingsideCastleDueToRookPosition() throws Exception {
        Fen fen = new Fen("r3k1r1/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        assertEquals("r3k1r1/8/8/8/8/8/8/R3K2R w KQq - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalBlackKingsideCastleDueToKingPosition() throws Exception {
        Fen fen = new Fen("r4k1r/8/8/8/8/8/8/R3K2R w KQk - 0 1");
        assertEquals("r4k1r/8/8/8/8/8/8/R3K2R w KQ - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalBlackQueensideCastleDueToRookPosition() throws Exception {
        Fen fen = new Fen("1r2k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        assertEquals("1r2k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1", fen.toString());
    }
    
    public void testCorrectIllegalBlackQueensideCastleDueToKingPosition() throws Exception {
        Fen fen = new Fen("r4k1r/8/8/8/8/8/8/R3K2R w KQq - 0 1");
        assertEquals("r4k1r/8/8/8/8/8/8/R3K2R w KQ - 0 1", fen.toString());
    }
    
    public void testSerialization() throws Exception {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        assertSerialize(fen);
    }
    
    public void testBuildPartialFenFromPosition() throws Exception {
        Board board = new Board();
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8);
        board.move(e2, e4);
        
        assertEquals(
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"),
                Fen.buildPartialFenFromPosition(board.getPosition()));
    }
    
    public void testSetPosition() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setPosition(new Position().put(WhiteKing, e1));
        assertEquals("8/8/8/8/8/8/8/4K3 w Qk - 0 15", result.getFenString());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", fen.getFenString());
    }
    
    public void testSetEnPassantSquare() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setEnPassantSquare(e3);
        assertEquals(e3, result.getEnPassantSquare());
        assertNull(fen.getEnPassantSquare());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk e3 0 15", result.getFenString());
        
        Fen nullSquare = result.setEnPassantSquare(null);
        assertEquals(fen, nullSquare);
    }
    
    public void testSetFullMoveCounter() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setFullMoveCounter(7);
        assertEquals(7, result.getFullMoveCount());
        assertEquals(15, fen.getFullMoveCount());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 7", result.getFenString());
    }
    
    public void testSetHalfMovesToDraw() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setDrawHalfMoveCounter(7);
        assertEquals(7, result.getHalfMovesSinceLastPawnMoveOrCapture());
        assertEquals(0, fen.getHalfMovesSinceLastPawnMoveOrCapture());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 7 15", result.getFenString());
    }
    
    public void testSetToMove() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setTurn(Black);
        assertEquals(Black, result.getTurn());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R b Qk - 0 15", result.getFenString());
        result = fen.setTurn(White);
        assertEquals(White, result.getTurn());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", result.getFenString());
    }
    
    public void testSetBlackCastleKingside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setCanBlackCastleKingside(false);
        assertFalse(result.canBlackCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Q - 0 15", result.getFenString());
        result = fen.setCanBlackCastleKingside(true);
        assertTrue(result.canBlackCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", result.getFenString());
    }
    
    public void testSetBlackCastleQueenside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setCanBlackCastleQueenside(true);
        assertTrue(result.canBlackCastleQueenside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qkq - 0 15", result.getFenString());
        result = fen.setCanBlackCastleQueenside(false);
        assertFalse(result.canBlackCastleQueenside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", result.getFenString());
    }
    
    public void testSetWhiteCastleQueenside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setCanWhiteCastleQueenside(false);
        assertFalse(result.canWhiteCastleQueenside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w k - 0 15", result.getFenString());
        result = fen.setCanWhiteCastleQueenside(true);
        assertTrue(result.canWhiteCastleQueenside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", result.getFenString());
    }
    
    public void testSetCastleKingsideWithOtherOptionsEnabled() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15");
        Fen result = fen.setCanWhiteCastleKingside(true);
        assertFalse(fen.canWhiteCastleKingside());
        assertTrue(result.canWhiteCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQk - 0 15", result.getFenString());
        
        result = fen.setCanWhiteCastleKingside(false);
        assertFalse(result.canWhiteCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w Qk - 0 15", result.getFenString());
    }
    
    public void testSetCastleKingside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        Fen result = fen.setCanWhiteCastleKingside(true);
        assertFalse(fen.canWhiteCastleKingside());
        assertTrue(result.canWhiteCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w K - 0 15", result.getFenString());
        
        result = fen.setCanWhiteCastleKingside(false);
        assertFalse(result.canWhiteCastleKingside());
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15", result.getFenString());
    }
    
    public void testFenWithNoMoveNumber() {
        Fen fen = new Fen("r5k1/1b5p/p3r1p1/1pB1pn2/1P1p4/5P1P/RPP1N1P1/4R1K1 w - -");
        assertEquals(White, fen.getTurn());
        assertNull(fen.getEnPassantSquare());
        assertEquals(1, fen.getFullMoveCount());
        assertEquals(0, fen.getHalfMovesSinceLastPawnMoveOrCapture());
        
        Position position = new Position();
        position = position.put(WhitePawn, h3, g2, f3, c2, b2, b4);
        position = position.put(BlackPawn, h7, g6, e5, d4, b5, a6);
        position = position.put(WhiteRook, e1, a2);
        position = position.put(WhiteKnight, e2);
        position = position.put(WhiteBishop, c5);
        position = position.put(WhiteKing, g1);
        position = position.put(BlackRook, e6, a8);
        position = position.put(BlackKnight, f5);
        position = position.put(BlackBishop, b7);
        position = position.put(BlackKing, g8);
        assertEquals(position, fen.getPosition());
    }
    
    public void testBadFormat() {
        try {
            new Fen("");
            fail("exception should be thrown!");
        }
        catch (FENFormatException ffe) {
        }
    }
    
    public void testEqualsIgnoreMoveCount() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 2 16");
        
        assertFalse(fen.equals(fen2));
        assertTrue(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountNoticesCastlingChange() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQk - 0 15");
        assertFalse(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountNoticesEnPassantChange() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQk e6 0 15");
        assertFalse(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountIgnoresGameCounter() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 16");
        assertTrue(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountIgnoresDrawCounter() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 1 15");
        assertTrue(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountNoticesTurnChange() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 15");
        assertFalse(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testEqualsIgnoreMoveCountNoticesPositionChange() {
        Fen fen = new Fen("r6k/8/8/8/8/8/8/R6K w - - 0 15");
        Fen fen2 = new Fen("r6k/8/8/8/8/8/8/r6K w - - 1 15");
        assertFalse(fen.equalsIgnoreMoveCount(fen2));
    }
    
    public void testGetPosition() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        
        Position expectedPosition = new Position();
        expectedPosition = expectedPosition.put(a8, BlackRook);
        expectedPosition = expectedPosition.put(h8, BlackRook);
        expectedPosition = expectedPosition.put(e8, BlackKing);
        expectedPosition = expectedPosition.put(a1, WhiteRook);
        expectedPosition = expectedPosition.put(e1, WhiteKing);
        expectedPosition = expectedPosition.put(h1, WhiteRook);
        
        assertEquals(expectedPosition, fen.getPosition());
    }
    
    public void testGetDefaultPosition() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        
        Fen fen = new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        assertEquals(board.getPosition(), fen.getPosition());
        assertEquals(board.getFen(), fen);
    }
    
    public void testGetEnPassantSquare() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        assertNull(fen.getEnPassantSquare());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - e6 0 15");
        assertEquals(e6, fen.getEnPassantSquare());
    }
    
    public void testGetTurn() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        assertEquals(White, fen.getTurn());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R b - e6 0 15");
        assertEquals(Black, fen.getTurn());
    }
    
    public void testConstructor() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15", fen.getFenString());
    }
    
    public void testEquality() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        Fen fen2 = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 15");
        
        assertEquals(fen, fen2);
        assertEquals(fen.hashCode(), fen2.hashCode());
        assertFalse(fen.equals(new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 0 16")));
        assertFalse(fen.equals(new Fen("r3k2r/8/8/8/8/8/8/R3K2R w - - 1 15")));
    }
    
    public void testCanWhiteCastleKingside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Qkq - 0 15");
        assertFalse(fen.canWhiteCastleKingside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w K - 0 15");
        assertTrue(fen.canWhiteCastleKingside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        assertTrue(fen.canWhiteCastleKingside());
    }
    
    public void testCanWhiteCastleQueenside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Kkq - 0 15");
        assertFalse(fen.canWhiteCastleQueenside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Q - 0 15");
        assertTrue(fen.canWhiteCastleQueenside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        assertTrue(fen.canWhiteCastleQueenside());
    }
    
    public void testCanBlackCastleKingside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQq - 0 15");
        assertFalse(fen.canBlackCastleKingside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w k - 0 15");
        assertTrue(fen.canBlackCastleKingside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        assertTrue(fen.canBlackCastleKingside());
    }
    
    public void testCanBlackCastleQueenside() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQk - 0 15");
        assertFalse(fen.canBlackCastleQueenside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w q - 0 15");
        assertTrue(fen.canBlackCastleQueenside());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 15");
        assertTrue(fen.canBlackCastleQueenside());
    }
    
    public void testGetHalfMovesSinceLastPawnMoveOrCapture() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Kkq - 0 15");
        assertEquals(0, fen.getHalfMovesSinceLastPawnMoveOrCapture());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Q - 13 7");
        assertEquals(13, fen.getHalfMovesSinceLastPawnMoveOrCapture());
    }
    
    public void testGetFullMoveCounter() {
        Fen fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Kkq - 0 15");
        assertEquals(15, fen.getFullMoveCount());
        
        fen = new Fen("r3k2r/8/8/8/8/8/8/R3K2R w Q - 13 7");
        assertEquals(7, fen.getFullMoveCount());
    }
}
