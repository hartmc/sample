package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Piece.BlackBishop;
import static com.chess.board.Piece.BlackKing;
import static com.chess.board.Piece.BlackPawn;
import static com.chess.board.Piece.BlackQueen;
import static com.chess.board.Piece.BlackRook;
import static com.chess.board.Piece.WhiteBishop;
import static com.chess.board.Piece.WhiteKing;
import static com.chess.board.Piece.WhiteKnight;
import static com.chess.board.Piece.WhitePawn;
import static com.chess.board.Piece.WhiteRook;
import static com.chess.board.Promotion.Queen;
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
import static com.chess.board.Square.b3;
import static com.chess.board.Square.b4;
import static com.chess.board.Square.b5;
import static com.chess.board.Square.b6;
import static com.chess.board.Square.b7;
import static com.chess.board.Square.b8;
import static com.chess.board.Square.c1;
import static com.chess.board.Square.c2;
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
import static com.chess.board.Square.d5;
import static com.chess.board.Square.d6;
import static com.chess.board.Square.d7;
import static com.chess.board.Square.d8;
import static com.chess.board.Square.e1;
import static com.chess.board.Square.e2;
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
import static com.chess.board.Square.f7;
import static com.chess.board.Square.f8;
import static com.chess.board.Square.g1;
import static com.chess.board.Square.g2;
import static com.chess.board.Square.g3;
import static com.chess.board.Square.g4;
import static com.chess.board.Square.g5;
import static com.chess.board.Square.g6;
import static com.chess.board.Square.g7;
import static com.chess.board.Square.g8;
import static com.chess.board.Square.h1;
import static com.chess.board.Square.h2;
import static com.chess.board.Square.h3;
import static com.chess.board.Square.h4;
import static com.chess.board.Square.h5;
import static com.chess.board.Square.h6;
import static com.chess.board.Square.h7;
import static com.chess.board.Square.h8;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.chess.board.SerializationTest;

public class ChessRulesTest extends SerializationTest<ChessRules> {
    
    private ChessRules rules;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rules = new ChessRules();
    }
    
    public void testDoublePawnMoveToBlockCheckIsLegal() throws Exception {
        Board board = new Board(new Fen("r4bnr/pp1k1ppp/3p4/2pPq3/4P1Q1/8/PPP3PP/RN2K1NR b KQ - 1 11"));
        assertTrue(new ChessRules().isLegal(new Move(f7, f5), board));
        assertTrue(new ChessRules().canAnyLegalMoveStartOn(f7, board));
        assertTrue(new ChessRules().getLegalMovesStartingOn(f7, board).contains(new Move(f7, f5)));
    }
    
    public void testDoublePawnOverPieceIsIllegal() throws Exception {
        Board board = new Board(new Fen("r2q1rk1/ppp1ppbp/2np1np1/8/3PP3/2N1BQ1P/PPP2PP1/2KR1B1R w - - 5 9"));
        assertFalse(new ChessRules().isLegal(new Move(f2, f4), board));
        
        board = new Board(new Fen("rnbqkb1r/pppppppp/5n2/8/4PP2/8/PPPP2PP/RNBQKBNR b KQkq f3 0 2"));
        assertFalse(new ChessRules().isLegal(new Move(f7, f5), board));
    }
    
    public void testCheckmate() throws Exception {
        Board board = new Board(new Fen("3R4/4kp1p/p3pp1b/1p2n3/8/1P4P1/P3PPBP/RNr2K2 w - - 1 21"));
        assertFalse(new ChessRules().isCheckMate(board));
        
        board = new Board(new Fen("3Q4/5pkp/p3pp1b/1p2n3/8/1P4P1/P3PPBP/RNr2K2 w - - 1 21"));
        assertFalse(new ChessRules().isCheckMate(board));
        
        board = new Board(new Fen("7B/4kp1p/p3p1pb/1p6/4N3/1P2PPP1/P1q4P/r4K1R w - - 3 21"));
        assertFalse(new ChessRules().isCheckMate(board));
        
        board = new Board(new Fen("7Q/4kp1p/p3p1pb/1p6/4N3/1P2PPP1/P1q4P/r4K1R w - - 3 21"));
        assertFalse(new ChessRules().isCheckMate(board));
    }
    
    public void testPawnPromotionShowsUpInLegalMoves() throws Exception {
        Board board = new Board(new Fen("7k/P7/8/8/8/8/8/7K w - - 0 25"));
        Collection<Move> moves = board.getLegalMovesEndingOn(a8);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(a7, a8, Queen)));
        
        moves = board.getLegalMovesStartingOn(a7);
        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move(a7, a8, Queen)));
        
        assertTrue(board.canAnyLegalMoveBeginOn(a7));
        assertTrue(board.canAnyLegalMoveEndOn(a8));
    }
    
    public void testPawnMoveWithNullPromotionIsIllegal() throws Exception {
        Board board = new Board(new Fen("7k/P7/8/8/8/8/8/7K w - - 0 25"));
        try {
            board.move(new Move(a7, a8));
            fail("cannot move pawn to back rank without specifying a promotion piece");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testSerializable() throws Exception {
        assertSerialize(new ChessRules());
    }
    
    public void testGetLegalMovesStartingOnSquare() throws Exception {
        Board board = new Board();
        Collection<Move> moves = rules.getLegalMovesStartingOn(g1, board);
        assertTrue(moves.contains(new Move(g1, f3)));
        assertTrue(moves.contains(new Move(g1, h3)));
        
        assertEquals(0, rules.getLegalMovesStartingOn(a1, board).size());
        assertEquals(0, rules.getLegalMovesStartingOn(e7, board).size());
    }
    
    public void testBishopKnightCornerMate() throws Exception {
        Position position = new Position();
        position = position.put(h8, BlackKing);
        position = position.put(g6, WhiteKing);
        position = position.put(h6, WhiteKnight);
        position = position.put(d8, WhiteBishop);
        Board board = new Board(position, White);
        board.move(d8, f6);
        assertTrue(new ChessRules().isCheckMate(board));
    }
    
    public void testCastleQueensideMovesRook() throws Exception {
        Board board = new Board();
        board.move(new Move(d2, d4));
        board.move(new Move(d7, d5));
        board.move(new Move(b1, c3));
        board.move(new Move(b8, c6));
        board.move(new Move(c1, f4));
        board.move(new Move(c8, f5));
        board.move(new Move(d1, d2));
        board.move(new Move(d8, d7));
        
        Position position = new ChessRules().move(new Move(e1, c1), board.getPosition());
        assertEquals(WhiteRook, position.get(d1));
        
        board.move(new Move(e1, c1));
        position = new ChessRules().move(new Move(e8, c8), board.getPosition());
        assertEquals(BlackRook, position.get(d8));
    }
    
    public void testDrawByRepetition() throws Exception {
        Board board = new Board(); // start position #1
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8); // start position #2
        assertNull(listener.getDrawType());
        
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, g1);
        
        assertNull(listener.getDrawType());
        board.move(f6, g8); // start position #3
        assertEquals(DrawType.ThreeMoveRepetition, listener.getDrawType());
    }
    
    public void testDrawByRepetitionNoticesEnPassantIsDifferentPosition() throws Exception {
        Board board = new Board();
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(e2, e4);
        board.move(a7, a6);
        board.move(e4, e5);
        board.move(f7, f5); // en passant possible here
        
        board.move(g1, f3); // position #1
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8); // same position, en passant not possible
        
        board.move(g1, f3); // position #2
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8); // same position, en passant not possible
        
        assertNull(listener.getDrawType());
        
        board.move(g1, f3); // position #3
        assertEquals(DrawType.ThreeMoveRepetition, listener.getDrawType());
    }
    
    public void testDrawByRepetitionNoticesThatCastlingAbilityIsDifferentPosition() throws Exception {
        Board board = new Board();
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f1, e2);
        board.move(f8, e7); // castling possible here
        
        board.move(h1, g1); // position #1
        board.move(b8, c6);
        board.move(g1, h1);
        board.move(c6, b8); // same position, castle kingside illegal for white
        
        board.move(h1, g1); // position #2
        board.move(b8, c6);
        board.move(g1, h1);
        board.move(c6, b8); // same position, castle kingside illegal for white
        
        assertNull(listener.getDrawType());
        
        board.move(h1, g1); // position #3
        
        assertEquals(DrawType.ThreeMoveRepetition, listener.getDrawType());
    }
    
    public void testDrawByRepetitionIgnoresInBetweenMoves() throws Exception {
        Board board = new Board();
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8);
        board.move(b1, c3);
        board.move(b8, c6);
        board.move(c3, b1);
        assertNull(listener.getDrawType());
        
        board.move(c6, b8);
        assertEquals(DrawType.ThreeMoveRepetition, listener.getDrawType());
    }
    
    public void test50MoveDrawStartingWithBlackMove() throws Exception {
        Board board = new Board();
        board.setRules(new NoDrawRules());
        
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        
        // go 48 moves without a pawn move or capture
        for (int i = 0; i < 24; i++) {
            board.move(g8, f6);
            board.move(g1, f3);
            board.move(f6, g8);
            board.move(f3, g1);
        }
        
        board.move(b8, c6);
        board.move(b1, c3);
        board.move(c6, d4);
        
        assertFalse(listener.getDrawType() == DrawType.FiftyMove);
        board.move(c3, d5);
        assertEquals(DrawType.FiftyMove, listener.getDrawType());
    }
    
    public void test50MoveDraw() throws Exception {
        Board board = new Board();
        board.setRules(new NoDrawRules());
        
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        board.move(e5, f4);
        
        // go 48 moves without a pawn move or capture
        for (int i = 0; i < 24; i++) {
            board.move(g1, f3);
            board.move(g8, f6);
            board.move(f3, g1);
            board.move(f6, g8);
        }
        
        board.move(b1, c3);
        board.move(b8, c6);
        board.move(c3, d5);
        
        assertFalse(listener.getDrawType() == DrawType.FiftyMove);
        board.move(c6, d4);
        assertEquals(DrawType.FiftyMove, listener.getDrawType());
    }
    
    public void testDrawByStalemate() throws Exception {
        Position position = new Position();
        position = position.put(h2, WhitePawn);
        position = position.put(h3, BlackPawn);
        position = position.put(h1, WhiteKing);
        position = position.put(b6, BlackBishop);
        position = position.put(e8, BlackKing);
        
        Board board = new Board(position, White);
        assertEquals(DrawType.Stalemate, rules.getDrawType(board));
    }
    
    public void testEnPassantCaptureIsIllegalAfter1RankPawnMove() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(e5, WhitePawn);
        position = position.put(f7, BlackPawn);
        
        Board board = new Board(position, Black);
        board.move(f7, f5);
        
        rules.assertLegal(new Move(e5, f6), board);
        
        try {
            position = position.put(f7, null);
            position = position.put(f6, BlackPawn);
            board = new Board(position, Black);
            board.move(f6, f5);
            
            rules.assertLegal(new Move(e5, f6), board);
            fail("can't capture e.p. after single rank pawn move.");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnsCannotCaptureForward() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        
        try {
            rules.assertLegal(new Move(e4, e5), board);
            fail("pawns cannot capture forward!");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testGetLegalMovesEndingOnSquare() throws Exception {
        Collection<Move> moves = rules.getLegalMovesEndingOn(f3, new Board());
        assertTrue(moves.contains(new Move(f2, f3)));
        assertTrue(moves.contains(new Move(g1, f3)));
        assertEquals(2, moves.size());
    }
    
    public void testCheckForLegalMoves() throws Exception {
        Board board = new Board();
        assertTrue(rules.canAnyLegalMoveEndOn(f3, board));
        assertTrue(rules.canAnyLegalMoveEndOn(f4, board));
        assertFalse(rules.canAnyLegalMoveEndOn(f5, board));
    }
    
    public void testPawnsCannotMoveSideways() throws Exception {
        Board board = new Board();
        board.move(c2, c3);
        board.move(b7, b5);
        
        try {
            rules.assertLegal(new Move(c3, d3), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(b5, a5), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnsCanOnlyMoveForward() throws Exception {
        Board board = new Board();
        board.move(b2, b4);
        board.move(f7, f5);
        
        try {
            rules.assertLegal(new Move(b4, b3), board);
            fail("pawns can only move forward!");
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(b4, b5);
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(f5, f6), board);
            fail("pawns can only move forward!");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnsCannotMoveThreeSquares() throws Exception {
        Board board = new Board();
        try {
            rules.assertLegal(new Move(e2, e5), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e7, e4), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnsCanOnlyCaptureForward() throws Exception {
        Board board = new Board();
        board.move(b2, b4);
        board.move(a7, a5);
        board.move(b4, b5);
        board.move(a5, a4);
        
        try {
            rules.assertLegal(new Move(b5, a4), board);
            fail("pawns only capture forward");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(a4, b5), board);
            fail("pawns only capture forward");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnsOnlyMoveTwoRanksFromStartingPosition() throws Exception {
        Board board = new Board();
        board.move(d2, d3);
        board.move(h7, h6);
        
        try {
            rules.assertLegal(new Move(d3, d5), board);
            fail("pawns may only move 2 squares from the starting position");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(h6, h4), board);
            fail("pawns may only move 2 squares from the starting position");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testOnlyWhitePawnsCanMoveTwoSquaresFrom2ndRank() throws Exception {
        Board board = new Board();
        board.move(d2, d4);
        board.move(h7, h5);
        board.move(d4, d5);
        board.move(h5, h4);
        board.move(d5, d6);
        board.move(h4, h3);
        board.move(d6, c7);
        board.move(h3, g2);
        
        try {
            rules.assertLegal(new Move(c7, c5), board);
            fail("only black pawns may move two squares from 7th rank!");
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(c7, b8, Queen);
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(g2, g4), board);
            fail("only white pawns may move two squares from 2nd rank!");
        }
        catch (IllegalMoveException ime) {
        }
        
    }
    
    public void testPawnCannotCaptureWhileMovingTwoSquares() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(b8, c6);
        board.move(e4, e5);
        
        try {
            rules.assertLegal(new Move(d7, e5), board);
            fail("illegal capture, exception should have been thrown");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testLegalMoveWrongColorThrowsException() throws Exception {
        Board board = new Board();
        try {
            rules.assertLegal(new Move(e7, e5), board);
            fail("wrong color, illegal move");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testIllegalPawnCapture() throws Exception {
        Board board = new Board();
        try {
            rules.assertLegal(new Move(e2, f3), board);
            fail("illegal move exception should have been thrown");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testPawnCannotCaptureOwnPiece() throws Exception {
        Board board = new Board();
        board.move(g1, f3);
        board.move(e7, e5);
        
        try {
            rules.assertLegal(new Move(e2, f3), board);
            fail("pawns cannot capture pieces of same color");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testMoveFromSquareWithNoPieceThrowsException() throws Exception {
        Board board = new Board();
        try {
            rules.assertLegal(new Move(e3, e4), board);
            fail("move is illegal, exception should be thrown");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testEnPassantOnlyLegalFor2SquarePawnMove() throws Exception {
        Board board = new Board();
        board.move(d2, d4);
        board.move(e7, e6);
        board.move(d4, d5);
        board.move(e6, e5);
        
        try {
            rules.assertLegal(new Move(d5, e6), board);
            fail("en passant only legal when enemy pawn moves 2 squares");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testEnPassantIsLegalForOneTurn() throws Exception {
        Board board = new Board();
        board.move(h2, h4);
        board.move(e7, e5);
        board.move(h4, h5);
        board.move(e5, e4);
        board.move(d2, d4);
        
        rules.assertLegal(new Move(e4, d3), board);
        board.move(g7, g5);
        
        rules.assertLegal(new Move(h5, g6), board);
        board.move(b1, c3);
        
        try {
            rules.assertLegal(new Move(e4, d3), board);
            fail("en passant capture only legal for 1 move");
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(a7, a6);
        
        try {
            rules.assertLegal(new Move(h5, g6), board);
            fail("en passant capture only legal for 1 move");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastleKingsideIsLegal() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f1, e2);
        board.move(f8, e7);
        
        rules.assertLegal(new Move(e1, g1), board);
        
        board = new Board(board.getFen().setTurn(Black));
        rules.assertLegal(new Move(e8, g8), board);
    }
    
    public void testKingCannotCapturePieceOfOwnColor() throws Exception {
        Board board = new Board();
        
        try {
            rules.assertLegal(new Move(e1, f1), board);
            fail("king cannot capture piece of own color");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testKingMoves() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(f7, f5);
        board.move(e1, e2);
        board.move(f5, f4);
        
        try {
            rules.assertLegal(new Move(e2, e3), board);
            fail("king cannot move into check from pawn");
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(e2, f3);
        board.move(g8, f6);
        
        // check that king can capture enemy pieces
        rules.assertLegal(new Move(f3, f4), board);
        
        try {
            rules.assertLegal(new Move(f3, g4), board);
            fail("king cannot move into check from knight");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            rules.assertLegal(new Move(f3, h5), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            rules.assertLegal(new Move(f3, d3), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            rules.assertLegal(new Move(f3, e1), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            rules.assertLegal(new Move(f3, f3), board);
            fail("illegal move");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastleKingsideIllegalAfterRookMoves() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f1, e2);
        board.move(f8, e7);
        board.move(h1, g1);
        board.move(h8, g8);
        board.move(g1, h1);
        board.move(g8, h8);
        
        try {
            rules.assertLegal(new Move(e1, g1), board);
            fail("rook has moved, castle kingside illegal");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e8, g8), board);
            fail("rook has moved, castle kingside illegal");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastleKingsideIllegalAfterKingMoves() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f1, e2);
        board.move(f8, e7);
        board.move(e1, f1);
        board.move(e8, f8);
        board.move(f1, e1);
        board.move(f8, e8);
        
        try {
            rules.assertLegal(new Move(e1, g1), board);
            fail("king has moved, castle kingside illegal");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e8, g8), board);
            fail("king has moved, castle kingside illegal");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastleQueensideIsLegal() throws Exception {
        Board board = new Board();
        board.move(d2, d4);
        board.move(d7, d5);
        board.move(b1, c3);
        board.move(b8, c6);
        board.move(c1, e3);
        board.move(c8, e6);
        board.move(d1, d2);
        board.move(d8, d7);
        
        rules.assertLegal(new Move(e1, c1), board);
        
        board = new Board(board.getFen().setTurn(Black));
        rules.assertLegal(new Move(e8, c8), board);
    }
    
    public void testCastleIntoCheckIsIllegal() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, h3);
        board.move(g8, h6);
        board.move(f1, c4);
        board.move(f8, c5);
        board.move(f2, f4);
        board.move(f7, f5);
        
        try {
            rules.assertLegal(new Move(e1, g1), board);
            fail("castling into check is illegal");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e8, g8), board);
            fail("castling into check is illegal");
        }
        catch (IllegalMoveException ime) {
        }
        
    }
    
    public void testCastleThroughCheckIsIllegal() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, h3);
        board.move(g8, h6);
        board.move(f1, e2);
        board.move(f8, e7);
        board.move(f2, f4);
        board.move(f7, f5);
        board.move(f4, e5);
        board.move(f5, e4);
        board.move(e1, g1);
        
        try {
            rules.assertLegal(new Move(e8, g8), board);
            fail("castling through check is illegal");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastlingKingsideIllegalIfThereArePiecesBetweenKingAndRook() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, h3);
        board.move(g8, h6);
        
        try {
            rules.assertLegal(new Move(e1, g1), board);
            fail("castling illegal with bishop in the way");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e8, g8), board);
            fail("castling illegal with bishop in the way");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastlingQueensideIllegalIfThereArePiecesBetweenKingAndRook() throws Exception {
        Board board = new Board();
        board.move(d2, d4);
        board.move(d7, d5);
        board.move(c1, d2);
        board.move(c8, d7);
        
        try {
            rules.assertLegal(new Move(e1, c1), board);
            fail("castling illegal with queen in the way");
        }
        catch (IllegalMoveException ime) {
        }
        
        try {
            board = new Board(board.getFen().setTurn(Black));
            rules.assertLegal(new Move(e8, c8), board);
            fail("castling illegal with queen in the way");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCastlingOutOfCheckIsIllegal() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(e7, BlackQueen);
        position = position.put(h1, WhiteRook);
        
        try {
            rules.assertLegal(new Move(e1, g1), new Board(position, White));
            fail("castling out of check is illegal");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCaptureEnPassantIntoCheckIsIllegal() throws Exception {
        Position position = new Position();
        position = position.put(h5, WhiteKing);
        position = position.put(a5, BlackRook);
        position = position.put(e8, BlackKing);
        position = position.put(f5, WhitePawn);
        position = position.put(e7, BlackPawn);
        
        // black just played e7-e5, by-passing the pinned white f5 pawn
        Board board = new Board(position, Black);
        board.move(e7, e5);
        
        try {
            rules.assertLegal(new Move(f5, e6), board);
            fail("capturing en passent would leave white in check");
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testIsLegalRookMoveNoticesPiecesInBetweenEmptySquares() throws Exception {
        Board board = new Board();
        assertFalse(rules.isLegal(new Move(h1, h3), board));
    }
    
    public void testIsLegalBishopMovesNoticesPiecesInBetweenEmptySquares() throws Exception {
        Board board = new Board();
        assertFalse(rules.isLegal(new Move(c1, a3), board));
    }
    
    public void testIsLegalQueenMoveNoticesPiecesInBetweenEmptySpaces() throws Exception {
        Board board = new Board();
        assertFalse(rules.isLegal(new Move(d1, d5), board));
    }
    
    public void testCanMove() throws Exception {
        Board board = new Board();
        assertTrue(rules.canAnyLegalMoveStartOn(a2, board));
        assertTrue(rules.canAnyLegalMoveStartOn(d2, board));
        assertTrue(rules.canAnyLegalMoveStartOn(h2, board));
        assertTrue(rules.canAnyLegalMoveStartOn(b1, board));
        assertTrue(rules.canAnyLegalMoveStartOn(g1, board));
        
        assertFalse(rules.canAnyLegalMoveStartOn(a1, board));
        assertFalse(rules.canAnyLegalMoveStartOn(c1, board));
        assertFalse(rules.canAnyLegalMoveStartOn(d1, board));
        assertFalse(rules.canAnyLegalMoveStartOn(h1, board));
        assertFalse(rules.canAnyLegalMoveStartOn(e7, board));
        assertFalse(rules.canAnyLegalMoveStartOn(g8, board));
        
        assertFalse(rules.canAnyLegalMoveStartOn(d5, board));
        assertFalse(rules.canAnyLegalMoveStartOn(b4, board));
        assertFalse(rules.canAnyLegalMoveStartOn(h6, board));
        assertFalse(rules.canAnyLegalMoveStartOn(e6, board));
    }
    
    public void testKnightMoves() throws Exception {
        Position position = new Position();
        position = position.put(d3, WhiteKnight);
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        
        List<Square> legalMoves = Arrays.asList(new Square[] { c1, b2, b4, c5, e5, f4, f2 });
        for (Square square : Square.values()) {
            if (legalMoves.contains(square)) {
                rules.assertLegal(new Move(d3, square), new Board(position, White));
            }
            else {
                try {
                    rules.assertLegal(new Move(d3, square), new Board(position, White));
                    fail(square + " is not a legal move for the knight.");
                }
                catch (IllegalMoveException ime) {
                }
            }
        }
    }
    
    public void testBishopMoves() throws Exception {
        Position position = new Position();
        position = position.put(f6, BlackBishop);
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        
        List<Square> legalMoves = Arrays.asList(new Square[] { g7, h8, e5, d4, c3, b2, a1, e7, d8, g5, h4 });
        for (Square square : Square.values()) {
            if (legalMoves.contains(square)) {
                rules.assertLegal(new Move(f6, square), new Board(position, Black));
            }
            else {
                try {
                    rules.assertLegal(new Move(f6, square), new Board(position, Black));
                    fail(square + " is not a legal move for the bishop.");
                }
                catch (IllegalMoveException ime) {
                }
            }
        }
    }
    
    public void testRookMoves() throws Exception {
        Position position = new Position();
        position = position.put(b6, WhiteRook);
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        
        List<Square> legalMoves = Arrays.asList(new Square[] { a6, c6, d6, e6, f6, g6, h6, b7, b8, b5, b4, b3, b2, b1 });
        for (Square square : Square.values()) {
            if (legalMoves.contains(square)) {
                rules.assertLegal(new Move(b6, square), new Board(position, White));
            }
            else {
                try {
                    rules.assertLegal(new Move(b6, square), new Board(position, White));
                    fail(square + " is not a legal move for the rook.");
                }
                catch (IllegalMoveException ime) {
                }
            }
        }
    }
    
    public void testQueenMoves() throws Exception {
        Position position = new Position();
        position = position.put(f4, BlackQueen);
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        
        List<Square> legalMoves = Arrays.asList(new Square[] { a4, b4, c4, d4, e4, g4, h4, f1, f2, f3, f5, f6, f7, f8, b8, c7, d6,
                                                              e5, g3, h2, c1, d2, e3, g5, h6 });
        for (Square square : Square.values()) {
            if (legalMoves.contains(square)) {
                rules.assertLegal(new Move(f4, square), new Board(position, Black));
            }
            else {
                try {
                    rules.assertLegal(new Move(f4, square), new Board(position, Black));
                    fail(square + " is not a legal move for the queen.");
                }
                catch (IllegalMoveException ime) {
                }
            }
        }
    }
    
    public void testCaptureEnPassantRemovesEnemyPawn() throws Exception {
        Position position = new Position();
        position = position.put(e5, WhitePawn);
        position = position.put(f5, BlackPawn);
        Position moved = rules.move(new Move(e5, f6), position);
        assertNull(moved.get(e5));
        assertNull(moved.get(f5));
        assertEquals(WhitePawn, moved.get(f6));
    }
}
