package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Piece.BlackBishop;
import static com.chess.board.Piece.BlackKing;
import static com.chess.board.Piece.BlackKnight;
import static com.chess.board.Piece.BlackPawn;
import static com.chess.board.Piece.BlackQueen;
import static com.chess.board.Piece.BlackRook;
import static com.chess.board.Piece.WhiteBishop;
import static com.chess.board.Piece.WhiteKing;
import static com.chess.board.Piece.WhiteKnight;
import static com.chess.board.Piece.WhitePawn;
import static com.chess.board.Piece.WhiteQueen;
import static com.chess.board.Piece.WhiteRook;
import static com.chess.board.Promotion.Queen;
import static com.chess.board.Side.Kingside;
import static com.chess.board.Side.Queenside;
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
import static com.chess.board.ECO.B00;
import static com.chess.board.ECO.C20;

import java.util.Collection;
import java.util.Collections;

//import com.chess.application.ChessSoundFactory;
import com.chess.board.ECO;
import com.chess.board.SerializationTest;
//import com.chess.util.TextTransfer;

public class BoardTest extends SerializationTest<Board> {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
   // comment out for code sample
   //     ChessSoundFactory.singleton().pause();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    //  comment out for code sample
    //    ChessSoundFactory.singleton().resume();
    }
    
    public void testGetPositionNoticesMoveNumber() throws Exception {
        Board board = new Board();
        Fen fen = board.getFen().setFullMoveCounter(3);
        assertNull(board.getPositionTree(fen));
    }
    
    public void testGetPositionReturnsNullForNonExistantPosition() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5).move(f2, f4);
        Fen d4Fen = new Board().move(d2, d4).getFen();
        assertNull(board.getPositionTree(d4Fen));
    }
    
    public void testGetPositionFindsPositionBeforeCurrentPointer() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5).move(f2, f4);
        board.undoMove();
        
        Fen fen = new Board().move(e2, e4).getFen();
        assertEquals(board.getMoveHistory().getInitialPosition().getVariations().get(0), board.getPositionTree(fen));
    }
    
    public void testCurrentPositionTreeIsNotChangedByGetPositionMethod() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5).move(f2, f4);
        PositionTree current = board.getMoveHistory().getCurrentPositionTree();
        
        Fen fen = new Board().move(e2, e4).getFen();
        board.getPositionTree(fen);
        
        assertEquals(current, board.getMoveHistory().getCurrentPositionTree());
    }
    
    public void testGetPositionFindsPositionAfterCurrentPointer() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5).move(f2, f4);
        board.reset();
        
        Fen fen = new Board().move(e2, e4).getFen();
        assertEquals(board.getMoveHistory().getInitialPosition().getVariations().get(0), board.getPositionTree(fen));
    }
    
    public void testGetPositionFindsPositionEqualToCurrentPointer() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5).move(f2, f4);
        board.undoMove();
        board.undoMove();
        
        Fen fen = new Board().move(e2, e4).getFen();
        assertEquals(board.getMoveHistory().getInitialPosition().getVariations().get(0), board.getPositionTree(fen));
    }
    
    public void testSetFenWipesHistoryAndFiresEvent() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        Fen d4Fen = new Board().move(d2, d4).getFen();
        
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.setFen(d4Fen);
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
        assertEquals(d4Fen, board.getFen());
        assertEquals(null, board.getLastMove());
        assertEquals(null, board.getLastMoveDescription());
        assertEquals(0, board.getMoveHistory().getCurrentPositionTree().getVariations().size());
    }
    
    // a somewhat arbitrary decision here... it would have been
    // reasonable to have Board.getBoard() just return 'this'.
    public void testBoardGeneratorClonesCurrentFen() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        
        Board newBoard = board.getBoard();
        assertEquals(0, newBoard.getMoveHistory().getMainLine().size());
        assertEquals(board.getFen(), newBoard.getFen());
        assertFalse(board == newBoard);
    }
    
    public void testResignFiresEvent() throws Exception {
        Board board = new Board();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.resign();
        assertEquals(1, listener.getResignCount());
    }
    
    public void testDrawOfferedFiresEvent() throws Exception {
        Board board = new Board();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.drawOffered();
        assertEquals(1, listener.getDrawOfferCount());
    }
    
    public void testClearAllFiresEvent() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.clearAll();
        
        assertEquals(1, listener.getHistoryChangedCount());
        
        board.clearAll();
        assertEquals(1, listener.getHistoryChangedCount());
    }
    
    public void testClearAllBoardForNonStandardStartPosition() throws Exception {
        Fen start = new Fen("7k/8/8/8/8/8/8/K7 w - - 0 1");
        Board board = new Board(start);
        board.move(a1, a2);
        board.move(h8, h7);
        board.clearAll();
        
        assertEquals(start, board.getFen());
        assertEquals(0, board.getMoveHistory().getCurrentPositionTree().getVariations().size());
    }

    /* comment out for code sample
    
    public void testPasteFENThrowsExceptionForWhitespace() throws Exception {
        Board board = new Board();
        new TextTransfer().setClipboardContents(" ");
        try {
            board.pasteFEN();
            fail("should throw exception");
        }
        catch (FENFormatException ffe) {
        }
    }
    */

    public void testIllegalCastleBug() throws Exception {
        Board board = new Board(new Fen("r2q1rk1/3b1pbp/3p1np1/1BpPn3/4P3/2N1BPNP/1P4P1/R2QK2R b KQ - 0 15"));
        board.move(a8, a1);
        assertEquals("3q1rk1/3b1pbp/3p1np1/1BpPn3/4P3/2N1BPNP/1P4P1/r2QK2R w K - 0 16", board.getFen().toString());
    }
    
    public void testGetNextMainLineMoveDescription() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        board.reset();
        board.move(new Move(d2, d4));
        board.undoMove();
        
        assertEquals("1.e4", board.getNextMainLineMoveDescription());
        board.redoMove();
        assertEquals("1...e5", board.getNextMainLineMoveDescription());
        board.redoMove();
        assertNull(board.getNextMainLineMoveDescription());
    }
    
    public void testGetNextMainLineMove() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.undoMove();
        board.move(new Move(d2, d4));
        board.undoMove();
        
        assertEquals(new Move(e2, e4), board.getNextMainLineMove());
        
        board.move(new Move(d2, d4));
        board.getMoveHistory().promoteVariation();
        board.reset();
        
        assertEquals(new Move(d2, d4), board.getNextMainLineMove());
        board.redoMove();
        
        assertNull(board.getNextMainLineMove());
    }
    
    public void testFenChangesAfterMove() throws Exception {
        Board board = new Board();
        Fen fen = board.getFen();
        board.move(e2, e4);
        assertFalse(fen.equals(board.getFen()));
    }

    public void testBoardRespectsEnPassantSquareInFen() throws Exception {
        Board board = new Board(new Fen("r3k2r/pppp1ppp/8/8/3Pp3/8/PPP1PPPP/R3K2R b Qk d3 0 15"));
        board.move(e4, d3);
        board = new Board(new Fen("r3k2r/pppp1ppp/8/8/3Pp3/8/PPP1PPPP/R3K2R b Qk - 0 15"));
        try {
            board.move(e4, d3);
            fail();
        }
        catch (IllegalMoveException ime) {
        }
    }
    
    public void testCanCastleTo() throws Exception {
        Board board = new Board(new Fen("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w Qk - 0 15"));
        assertTrue(board.canCastleTo(Queenside));
        assertFalse(board.canCastleTo(Kingside));
        board.move(e1, f1);
        assertFalse(board.canCastleTo(Queenside));
        assertTrue(board.canCastleTo(Kingside));
        board.move(e8, f8);
        assertFalse(board.canCastleTo(Queenside));
        assertFalse(board.canCastleTo(Kingside));
    }
    
    public void testCastlingRespectsFen() throws Exception {
        Board board = new Board(new Fen("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w Qk - 0 15"));
        try {
            board.move(e1, g1);
            fail();
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(e1, c1);
        
        try {
            board.move(e8, c8);
            fail();
        }
        catch (IllegalMoveException ime) {
        }
        
        board.move(e8, g8);
    }
    
    public void testPawnMoveAlgebraicWithCheck() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(h2, WhitePawn);
        position = position.put(g5, BlackKing);
        Board board = new Board(position, White);
        board.moveAlgebraic("h4+");
        assertTrue(board.isCheck());
    }
    /*
    comment out for code sample

    public void testCopyFEN() throws Exception {
        TextTransfer transfer = new TextTransfer();
        transfer.setClipboardContents("test");
        Board board = new Board();
        board.move(a2, a3);
        board.copyFEN();
        
        assertEquals(board.getFen().toString(), transfer.getClipboardContents());
    }
    
    public void testPasteGarbageFEN() throws Exception {
        Board board = new Board();
        Fen expected = board.getFen();
        new TextTransfer().setClipboardContents("definitely not a FEN string!");
        try {
            board.pasteFEN();
            fail("exception should have been thrown");
        }
        catch (FENFormatException ffe) {
            assertEquals(expected, board.getFen());
        }
    }
    
    public void testPasteFEN() throws Exception {
        TextTransfer transfer = new TextTransfer();
        
        Fen expected = null;
        {
            Board board = new Board();
            board.move(e2, e4);
            board.move(e7, e5);
            transfer.setClipboardContents(board.getFen().toString());
            expected = board.getFen();
        }
        
        Board board = new Board();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.pasteFEN();
        
        assertEquals(expected, board.getFen());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    */
    
    public void testCopyPosition() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        
        Board copy = board.copyPosition();
        assertEquals(Black, copy.getTurn());
        assertEquals(board.getPosition(), copy.getPosition());
        assertEquals(0, copy.getMoveHistory().getCurrentPositionTree().getVariations().size());
        assertNull(copy.getMoveHistory().getCurrentPositionTree().getParentTree());
        
        assertEquals(copy.getFen(), board.getFen());
        
        copy.move(d7, d5);
        board.move(d7, d5);
        
        assertEquals(copy.getFen(), board.getFen());
        
        copy.move(a2, a3);
        assertFalse(copy.equals(board));
    }
    
    public void testAlgebraicDescriptionIncludesMateSign() throws Exception {
        Position position = new Position();
        position = position.put(h8, BlackKing);
        position = position.put(g6, WhiteKing);
        position = position.put(h6, WhiteKnight);
        position = position.put(d8, WhiteBishop);
        Board board = new Board(position, White);
        board.move(d8, f6);
        assertEquals("1.Bf6#", board.getMoveHistory().getCurrentPositionTree().getMoveDescription());
    }
    
    public void testResetGoesToInitialPosition() throws Exception {
        Fen fen = new Fen("r1bq1rk1/1p1nppbp/p2p1np1/3P4/Q1B1P3/2N1BN2/PP3PPP/R3K2R w KQ - 3 10");
        Board board = new Board(fen);
        board.moveAlgebraic("Bb5");
        PositionTree child = board.getMoveHistory().getCurrentPositionTree();
        board.reset();
        assertEquals(fen, board.getFen());
        assertEquals(child, board.getMoveHistory().getCurrentPositionTree().getVariations().get(0));
    }
    
    public void testFenEncodingConsistency() throws Exception {
        Board board = new Board(new Fen("r1bq1rk1/1p1nppbp/p2p1np1/3P4/Q1B1P3/2N1BN2/PP3PPP/R3K2R w KQ - 3 10"));
        board.moveAlgebraic("Bb5");
        board.moveAlgebraic("Rb8");
        assertEquals(board.getFen(), board.getMoveHistory().getCurrentPositionTree().getFen());
    }
    
    public void testAlgebraicMoveWithFileDifferentiatorAndCapture() throws Exception {
        Board board = new Board(new Fen("1rbq1rk1/4ppbp/p2p1np1/1pnP4/1P2P3/2NBBN2/P1Q2PPP/R3K2R b KQ b3 0 13"));
        board.moveAlgebraic("Ncxe4");
        assertEquals(new Move(c5, e4), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("Nfxe4");
        assertEquals(new Move(f6, e4), board.getLastMove());
    }
    
    public void testAlgebraicMoveWithRankDifferentiatorAndCapture() throws Exception {
        Board board = new Board(new Fen("1rbq1rk1/4ppbp/p2p1np1/1pnP4/1P2P3/2NBBN2/P1Q2PPP/R3K2R b KQ b3 0 13"));
        board.moveAlgebraic("N5xe4");
        assertEquals(new Move(c5, e4), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("N6xe4");
        assertEquals(new Move(f6, e4), board.getLastMove());
    }
    
    public void testAlgebraicCastleQueenside() throws Exception {
        Board board = new Board();
        board.move(d2, d4);
        board.move(d7, d5);
        board.move(d1, d3);
        board.move(d8, d6);
        board.move(c1, g5);
        board.move(c8, g4);
        board.move(b1, c3);
        board.move(b8, c6);
        board.moveAlgebraic("O-O-O");
        assertEquals(new Move(e1, c1), board.getLastMove());
        board.moveAlgebraic("O-O-O");
        assertEquals(new Move(e8, c8), board.getLastMove());
    }
    
    public void testAlgebraicCastleKingside() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f1, c4);
        board.move(f8, c5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.moveAlgebraic("O-O");
        assertEquals(new Move(e1, g1), board.getLastMove());
        
        board.moveAlgebraic("O-O");
        assertEquals(new Move(e8, g8), board.getLastMove());
    }
    
    public void testAlgebraicMoveWithFileDifferentiator() throws Exception {
        Board board = new Board();
        board.moveAlgebraic("d4");
        board.moveAlgebraic("e5");
        board.moveAlgebraic("Nf3");
        board.moveAlgebraic("Nc6");
        board.moveAlgebraic("Nbd2");
        assertEquals(new Move(b1, d2), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("Nfd2");
        assertEquals(new Move(f3, d2), board.getLastMove());
        board.moveAlgebraic("Nge7");
        assertEquals(new Move(g8, e7), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("Nce7");
        assertEquals(new Move(c6, e7), board.getLastMove());
    }
    
    public void testAlgebraicMoveWithPawnPromotion() throws Exception {
        Position position = new Position();
        position = position.put(c7, WhitePawn);
        position = position.put(e8, BlackKing);
        position = position.put(e1, WhiteKing);
        Board board = new Board(position, White);
        board.moveAlgebraic("c8=N");
        assertEquals(new Move(c7, c8).setPromotion(Promotion.Knight), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("c8=R+");
        assertEquals(new Move(c7, c8).setPromotion(Promotion.Rook), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("c8=B");
        assertEquals(new Move(c7, c8).setPromotion(Promotion.Bishop), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("c8=Q+");
        assertEquals(new Move(c7, c8, Queen), board.getLastMove());
    }
    
    public void testAlgebraicMoveWithPawnCaptureAndPromotion() throws Exception {
        Position position = new Position();
        position = position.put(c7, WhitePawn);
        position = position.put(b8, BlackKnight);
        position = position.put(e8, BlackKing);
        position = position.put(e1, WhiteKing);
        Board board = new Board(position, White);
        board.moveAlgebraic("cxb8=N");
        assertEquals(new Move(c7, b8).setPromotion(Promotion.Knight), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("cxb8=R+");
        assertEquals(new Move(c7, b8).setPromotion(Promotion.Rook), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("cxb8=B");
        assertEquals(new Move(c7, b8).setPromotion(Promotion.Bishop), board.getLastMove());
        
        board.undoMove();
        board.moveAlgebraic("cxb8=Q+");
        assertEquals(new Move(c7, b8, Queen), board.getLastMove());
    }
    
    public void testAlgebraicMoveWithRankDifferentiator() throws Exception {
        Position position = new Position();
        position = position.put(a8, WhiteRook);
        position = position.put(a1, WhiteRook);
        position = position.put(b7, BlackQueen);
        position = position.put(b2, BlackQueen);
        position = position.put(h3, WhiteKing);
        position = position.put(h5, BlackKing);
        Board board = new Board(position, White);
        board.moveAlgebraic("R1a7");
        assertEquals(new Move(a1, a7), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("R8a7");
        assertEquals(new Move(a8, a7), board.getLastMove());
        
        board.moveAlgebraic("Q7b6");
        assertEquals(new Move(b7, b6), board.getLastMove());
        board.undoMove();
        board.moveAlgebraic("Q2b6");
        assertEquals(new Move(b2, b6), board.getLastMove());
    }
    
    public void testMoveAlgebraic() throws Exception {
        Board board = new Board();
        board.moveAlgebraic("e4");
        assertEquals(new Move(e2, e4), board.getLastMove());
        
        board.moveAlgebraic("g5");
        board.moveAlgebraic("e5");
        board.moveAlgebraic("f5");
        board.moveAlgebraic("exf6 e.p.");
        board.moveAlgebraic("a5");
        board.moveAlgebraic("Qh5#");
        
        assertEquals(new Move(d1, h5), board.getLastMove());
    }
    
    public void testUndoToNonStandardStartPosition() throws Exception {
        Position position = new Position();
        position = position.put(a8, WhiteRook);
        position = position.put(a1, WhiteRook);
        position = position.put(b7, BlackQueen);
        position = position.put(b2, BlackQueen);
        position = position.put(h3, WhiteKing);
        position = position.put(h5, BlackKing);
        
        Board board = new Board(position, White);
        board.moveAlgebraic("Rc8");
        board.undoMove();
        assertEquals(position, board.getPosition());
    }
    
    public void testFenEncoding() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFen().toString());
    }
    
    public void testChangeMoveHistoryChangesBoard() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        
        PositionTree currentMoveElement = board.getMoveHistory().getCurrentPositionTree();
        board.getMoveHistory().setCurrentPositionTree(currentMoveElement.getParentTree());
        
        assertNull(board.getPieceAt(e5));
    }
    
    public void testChangeMoveHistoryFiresBoardChangeEvent() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        
        PositionTree currentMoveElement = board.getMoveHistory().getCurrentPositionTree();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.getMoveHistory().setCurrentPositionTree(currentMoveElement.getParentTree());
        
        assertEquals(1, listener.getHistorySelectionChangedCount());
    }
    
    public void testUndoMoveSetsPositionInMoveHistory() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        board.move(new Move(e7, e5));
        board.undoMove();
        assertSame(element, board.getMoveHistory().getCurrentPositionTree());
    }
    
    public void testRedoMoveSetsPositionInMoveHistory() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        board.undoMove();
        board.redoMove();
        assertSame(element, board.getMoveHistory().getCurrentPositionTree());
    }
    
    public void testRedoMoveInStartPositionDoesNothing() throws Exception {
        Board board = new Board();
        board.redoMove();
        assertEquals(new Board().getFen(), board.getFen());
    }
    
    public void testRedoMoveAtEndOfMoveListDoesNothing() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        Fen encoding = board.getFen();
        
        board.redoMove();
        assertEquals(encoding, board.getFen());
    }
    
    public void testRedoMove() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        Fen encoding = board.getFen();
        
        board.undoMove();
        board.redoMove();
        assertEquals(encoding, board.getFen());
    }
    
    public void testRedoMoveFiresBoardChangedEvent() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.undoMove();
        
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.redoMove();
        assertEquals(1, listener.getHistorySelectionChangedCount());
    }
    
    public void testUndoMoveInStartPositionDoesNothing() throws Exception {
        Board board = new Board();
        board.undoMove();
        assertNull(board.getLastMove());
        assertEquals(new Board().getFen(), board.getFen());
    }
    
    public void testUndoMoveChangesPosition() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.undoMove();
        assertEquals(new Board().getFen(), board.getFen());
    }
    
    public void testUndoMoveFiresBoardEvent() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.undoMove();
        
        assertEquals(1, listener.getHistorySelectionChangedCount());
    }
    
    public void testOnlyPawnsCanPromote() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(d8, BlackQueen);
        Board board = new Board(position, Black);
        board.move(d8, d1);
        assertEquals("Qd1+", board.getMoveHistory().getCurrentVariation().get(1).getMoveDescription());
    }
    
    public void testNoDisambiguationForPawnCaptures() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(d7, d5);
        board.move(c2, c4);
        board.move(g8, f6);
        board.move(c4, d5);
        
        assertEquals("3.cxd5", board.getMoveHistory().getCurrentVariation().get(5).getMoveDescription());
    }
    
    public void testAmbiguousFileMoveDescription() throws Exception {
        Board board = new Board();
        board.move(d2, d3);
        board.move(d7, d6);
        board.move(b1, d2);
        board.move(b8, d7);
        board.move(g1, f3);
        board.move(g8, f6);
        
        assertEquals("3.Ngf3", board.getMoveHistory().getCurrentVariation().get(5).getMoveDescription());
        assertEquals("Ngf6", board.getMoveHistory().getCurrentVariation().get(6).getMoveDescription());
    }
    
    public void testAmbigousRankMoveDescription() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(c2, WhiteRook);
        position = position.put(c7, WhiteRook);
        Board board = new Board(position, White);
        board.move(c7, c5);
        assertEquals("1.R7c5", board.getMoveHistory().getCurrentVariation().get(1).getMoveDescription());
    }
    
    public void testCheckMateMoveDescription() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(f7, f6);
        board.move(d2, d4);
        board.move(g7, g5);
        board.move(d1, h5);
        
        assertEquals("3.Qh5#", board.getMoveHistory().getCurrentVariation().get(5).getMoveDescription());
    }
    
    public void testCheckMoveDescription() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        board.move(d8, h4);
        
        assertEquals("Qh4+", board.getMoveHistory().getCurrentVariation().get(4).getMoveDescription());
    }
    
    public void testCaptureEnPassantDescription() throws Exception {
        Board board = new Board();
        board.move(new Move(d2, d4));
        board.move(new Move(b8, c6));
        board.move(new Move(d4, d5));
        board.move(new Move(e7, e5));
        board.move(new Move(d5, e6));
        
        assertEquals("3.dxe6 ep.", board.getMoveHistory().getCurrentVariation().get(5).getMoveDescription());
    }
    
    public void testCastleQueensideDescription() throws Exception {
        Board board = new Board();
        board.move(new Move(d2, d4));
        board.move(new Move(d7, d5));
        board.move(new Move(b1, c3));
        board.move(new Move(b8, c6));
        board.move(new Move(c1, f4));
        board.move(new Move(c8, f5));
        board.move(new Move(d1, d2));
        board.move(new Move(d8, d7));
        board.move(new Move(e1, c1));
        board.move(new Move(e8, c8));
        
        assertEquals("5.O-O-O", board.getMoveHistory().getCurrentVariation().get(9).getMoveDescription());
        assertEquals("O-O-O", board.getMoveHistory().getCurrentVariation().get(10).getMoveDescription());
    }
    
    public void testCastleKingsideDescription() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        board.move(new Move(g1, f3));
        board.move(new Move(g8, f6));
        board.move(new Move(f1, c4));
        board.move(new Move(f8, c5));
        board.move(new Move(e1, g1));
        board.move(new Move(e8, g8));
        
        assertEquals("4.O-O", board.getMoveHistory().getCurrentVariation().get(7).getMoveDescription());
        assertEquals("O-O", board.getMoveHistory().getCurrentVariation().get(8).getMoveDescription());
    }
    
    public void testPawnMoveDescriptionJustNamesEndSquare() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        assertEquals("1.e4", board.getMoveHistory().getCurrentVariation().get(1).getMoveDescription());
    }
    
    public void testPawnCaptureDescription() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(f7, f5));
        board.move(new Move(e4, f5));
        assertEquals("2.exf5", board.getMoveHistory().getCurrentVariation().get(3).getMoveDescription());
    }
    
    public void testPromotionMoveDescription() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(h2, BlackPawn);
        Board board = new Board(position, Black);
        board.move(new Move(h2, h1).setPromotion(Promotion.Knight));
        assertEquals("h1=N", board.getMoveHistory().getCurrentVariation().get(1).getMoveDescription());
    }
    
    public void testPromotionAndCapture() throws Exception {
        Position position = new Position();
        position = position.put(e1, WhiteKing);
        position = position.put(e8, BlackKing);
        position = position.put(h2, BlackPawn);
        position = position.put(g1, WhiteKnight);
        Board board = new Board(position, Black);
        board.move(new Move(h2, g1).setPromotion(Promotion.Bishop));
        assertEquals("hxg1=B", board.getMoveHistory().getCurrentVariation().get(1).getMoveDescription());
    }
    
    public void testCheckmateIsNotStalemate() throws Exception {
        Position position = new Position();
        position = position.put(c1, WhiteKing);
        position = position.put(a2, BlackRook);
        position = position.put(h8, BlackQueen);
        position = position.put(e8, BlackKing);
        Board board = new Board(position, Black);
        
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        
        board.move(h8, h1);
        assertNull(listener.getDrawType());
        assertEquals(White, listener.getCheckMatedPlayer());
    }
    
    public void testCheckmateQueriesRules() throws Exception {
        Position position = new Position();
        position = position.put(c1, WhiteKing);
        position = position.put(a2, BlackRook);
        position = position.put(h8, BlackQueen);
        position = position.put(e8, BlackKing);
        Board board = new Board(position, Black);
        
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.setRules(new AllDrawRules());
        
        board.move(h8, h1);
        assertNull(listener.getCheckMatedPlayer());
    }
    
    public void testFenEncodingOfStartPosition() throws Exception {
        Board board = new Board();
        assertEquals(new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"), board.getFen());
    }
    
    public void testFenEncodingNoticesEnPassant() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        assertEquals(new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"), board.getFen());
    }
    
    public void testFenEncodingCountsNonPawnMovesToward50MoveDraw() throws Exception {
        Board board = new Board();
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, d4);
        
        assertEquals(new Fen("rnbqkb1r/pppppppp/5n2/8/3N4/8/PPPPPPPP/RNBQKB1R b KQkq - 3 2"), board.getFen());
    }
    
    public void testFenEncodingNoticesQRMove() throws Exception {
        Board board = new Board();
        board.move(b1, c3);
        board.move(b8, c6);
        board.move(a1, b1);
        
        assertEquals(new Fen("r1bqkbnr/pppppppp/2n5/8/8/2N5/PPPPPPPP/1RBQKBNR b Kkq - 3 2"), board.getFen());
        
        board.move(a8, b8);
        
        assertEquals(new Fen("1rbqkbnr/pppppppp/2n5/8/8/2N5/PPPPPPPP/1RBQKBNR w Kk - 4 3"), board.getFen());
    }
    
    public void testFenEncodingNoticesKRMove() throws Exception {
        Board board = new Board();
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(h1, g1);
        
        assertEquals(new Fen("rnbqkb1r/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKBR1 b Qkq - 3 2"), board.getFen());
        
        board.move(h8, g8);
        
        assertEquals(new Fen("rnbqkbr1/pppppppp/5n2/8/8/5N2/PPPPPPPP/RNBQKBR1 w Qq - 4 3"), board.getFen());
    }
    
    public void testFenEncodingNoticesKingMove() throws Exception {
        Board board = new Board();
        board.move(e2, e3);
        board.move(e7, e6);
        board.move(f1, e2);
        board.move(f8, e7);
        board.move(e1, f1);
        
        assertEquals(new Fen("rnbqk1nr/ppppbppp/4p3/8/8/4P3/PPPPBPPP/RNBQ1KNR b kq - 3 3"), board.getFen());
        
        board.move(e8, f8);
        assertEquals(new Fen("rnbq1knr/ppppbppp/4p3/8/8/4P3/PPPPBPPP/RNBQ1KNR w - - 4 4"), board.getFen());
    }
    
    public void testFenEncodingResets50MoveCountOnEnPassantCapture() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(b8, a6);
        board.move(e4, e5);
        board.move(f7, f5);
        board.move(e5, f6);
        
        assertEquals(new Fen("r1bqkbnr/ppppp1pp/n4P2/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3"), board.getFen());
    }
    
    public void testFenEncodingResets50MoveCountOnAnyCapture() throws Exception {
        Board board = new Board();
        board.move(g1, f3);
        board.move(b8, c6);
        board.move(f3, e5);
        board.move(c6, e5);
        
        assertEquals(new Fen("r1bqkbnr/pppppppp/8/4n3/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 3"), board.getFen());
    }
    
    public void testGetLegalMovesEndingOnSquarePassesThroughToRules() {
        Board board = new Board();
        Collection<Move> moves = board.getLegalMovesEndingOn(e4);
        assertEquals(1, moves.size());
        assertEquals(new Move(e2, e4), moves.iterator().next());
        
        board.setRules(new AllIllegalRules());
        assertEquals(0, board.getLegalMovesEndingOn(e4).size());
    }
    
    public void testBoardListener() throws Exception {
        Board board = new Board();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        
        board.move(new Move(e2, e4));
        assertEquals(1, listener.getHistoryChangedCount());
        try {
            board.move(g1, e5);
            fail("illegal move!");
        }
        catch (IllegalMoveException ime) {
            assertEquals(1, listener.getHistoryChangedCount());
            assertEquals(0, listener.getHistorySelectionChangedCount());
        }
        
        board.removeBoardListener(listener);
        
        board.move(new Move(e7, e5));
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }
    
    public void testCanMoveConsultsRules() throws Exception {
        Board board = new Board();
        assertTrue(board.canAnyLegalMoveBeginOn(e2));
        board.setRules(new AllIllegalRules());
        assertFalse(board.canAnyLegalMoveBeginOn(e2));
    }
    
    public void testStartPosition() throws Exception {
        Board board = new Board();
        assertEquals(WhitePawn, board.getPieceAt(a2));
        assertEquals(WhitePawn, board.getPieceAt(b2));
        assertEquals(WhitePawn, board.getPieceAt(c2));
        assertEquals(WhitePawn, board.getPieceAt(d2));
        assertEquals(WhitePawn, board.getPieceAt(e2));
        assertEquals(WhitePawn, board.getPieceAt(f2));
        assertEquals(WhitePawn, board.getPieceAt(g2));
        assertEquals(WhitePawn, board.getPieceAt(h2));
        
        assertEquals(BlackPawn, board.getPieceAt(a7));
        assertEquals(BlackPawn, board.getPieceAt(b7));
        assertEquals(BlackPawn, board.getPieceAt(c7));
        assertEquals(BlackPawn, board.getPieceAt(d7));
        assertEquals(BlackPawn, board.getPieceAt(e7));
        assertEquals(BlackPawn, board.getPieceAt(f7));
        assertEquals(BlackPawn, board.getPieceAt(g7));
        assertEquals(BlackPawn, board.getPieceAt(h7));
        
        assertEquals(WhiteRook, board.getPieceAt(a1));
        assertEquals(WhiteKnight, board.getPieceAt(b1));
        assertEquals(WhiteBishop, board.getPieceAt(c1));
        assertEquals(WhiteQueen, board.getPieceAt(d1));
        assertEquals(WhiteKing, board.getPieceAt(e1));
        assertEquals(WhiteBishop, board.getPieceAt(f1));
        assertEquals(WhiteKnight, board.getPieceAt(g1));
        assertEquals(WhiteRook, board.getPieceAt(h1));
        
        assertEquals(BlackRook, board.getPieceAt(a8));
        assertEquals(BlackKnight, board.getPieceAt(b8));
        assertEquals(BlackBishop, board.getPieceAt(c8));
        assertEquals(BlackQueen, board.getPieceAt(d8));
        assertEquals(BlackKing, board.getPieceAt(e8));
        assertEquals(BlackBishop, board.getPieceAt(f8));
        assertEquals(BlackKnight, board.getPieceAt(g8));
        assertEquals(BlackRook, board.getPieceAt(h8));
        
        assertNull(board.getPieceAt(a3));
        assertNull(board.getPieceAt(a4));
        assertNull(board.getPieceAt(a5));
        assertNull(board.getPieceAt(a6));
        assertNull(board.getPieceAt(b3));
        assertNull(board.getPieceAt(b4));
        assertNull(board.getPieceAt(b5));
        assertNull(board.getPieceAt(b6));
        assertNull(board.getPieceAt(c3));
        assertNull(board.getPieceAt(c4));
        assertNull(board.getPieceAt(c5));
        assertNull(board.getPieceAt(c6));
        assertNull(board.getPieceAt(d3));
        assertNull(board.getPieceAt(d4));
        assertNull(board.getPieceAt(d5));
        assertNull(board.getPieceAt(d6));
        assertNull(board.getPieceAt(e3));
        assertNull(board.getPieceAt(e4));
        assertNull(board.getPieceAt(e5));
        assertNull(board.getPieceAt(e6));
        assertNull(board.getPieceAt(f3));
        assertNull(board.getPieceAt(f4));
        assertNull(board.getPieceAt(f5));
        assertNull(board.getPieceAt(f6));
        assertNull(board.getPieceAt(g3));
        assertNull(board.getPieceAt(g4));
        assertNull(board.getPieceAt(g5));
        assertNull(board.getPieceAt(g6));
        assertNull(board.getPieceAt(h3));
        assertNull(board.getPieceAt(h4));
        assertNull(board.getPieceAt(h5));
        assertNull(board.getPieceAt(h6));
    }
    
    public void testCastleKingsideMovesRook() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f1, c4);
        board.move(f8, c5);
        board.move(e1, g1);
        
        assertEquals(WhiteRook, board.getPieceAt(f1));
        assertNull(board.getPieceAt(h1));
        assertNull(board.getPieceAt(e1));
        
        board.move(e8, g8);
        assertEquals(BlackRook, board.getPieceAt(f8));
        assertNull(board.getPieceAt(h8));
        assertNull(board.getPieceAt(e8));
    }
    
    public void testPromotionToQueen() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(f7, f5));
        board.move(new Move(e4, f5));
        board.move(new Move(g7, g5));
        board.move(new Move(f5, g6));
        board.move(new Move(h7, h6));
        board.move(new Move(g6, g7));
        board.move(new Move(h6, h5));
        board.move(new Move(g7, f8, Queen));
        
        assertEquals(WhiteQueen, board.getPieceAt(f8));
        assertTrue(board.isCheck());
        assertEquals(Color.Black, board.getTurn());
    }
    
    public void testPromotionToKnight() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(f7, f5);
        board.move(e4, f5);
        board.move(g7, g5);
        board.move(f5, g6);
        board.move(h7, h6);
        board.move(g6, g7);
        board.move(g8, f6);
        board.move(new Move(g7, g8).setPromotion(Promotion.Knight));
        
        assertEquals(WhiteKnight, board.getPieceAt(g8));
        assertEquals(Color.Black, board.getTurn());
        assertFalse(board.isCheck());
    }
    
    public void testCheckFromBishop() throws Exception {
        Board board = new Board();
        board.move(f2, f4);
        board.move(e7, e6);
        board.move(e1, f2);
        board.move(f8, c5);
        assertTrue(board.isCheck());
    }
    
    public void testAnyLegalQueriesRules() throws Exception {
        Board board = new Board();
        assertTrue(board.canAnyLegalMoveEndOn(f3));
        board.setRules(new AllIllegalRules());
        assertFalse(board.canAnyLegalMoveEndOn(f3));
    }
    
    public void testCaptureAndPromoteToRookWithCheck() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(g8, f6);
        board.move(e4, e5);
        board.move(g7, g6);
        board.move(e5, f6);
        board.move(f8, g7);
        board.move(f6, g7);
        board.move(b8, c6);
        board.move(new Move(g7, h8).setPromotion(Promotion.Rook));
        
        assertEquals(WhiteRook, board.getPieceAt(h8));
        assertEquals(Color.Black, board.getTurn());
        assertTrue(board.isCheck());
    }
    
    public void testRulesAreConsultedBeforeMakingMove() throws Exception {
        Board board = new Board();
        board.setRules(new AllIllegalRules());
        
        try {
            board.move(e2, e4);
            fail("illegal move should have been thrown!");
        }
        catch (IllegalMoveException ime) {
            // check that the pawn has not been moved!
            assertNull(board.getPieceAt(e4));
        }
    }
    
    public void testDrawByRepetitionQueriesRules() throws Exception {
        Board board = new Board();
        board.setRules(new NoDrawRules());
        
        GameStateListener listener = new GameStateListener();
        board.addBoardListener(listener);
        board.move(g1, f3);
        board.move(g8, f6);
        board.move(f3, g1);
        board.move(f6, g8);
        
        assertNull(listener.getDrawType());
        
        board.move(g1, f3);
        assertNull(listener.getDrawType());
        
        board.setRules(new AllDrawRules());
        board.move(h2, h3);
        assertEquals(DrawType.FiftyMove, listener.getDrawType());
    }
    
    public void testMovePawn2SquaresWhite() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        assertEquals(null, board.getPieceAt(e2));
        assertEquals(WhitePawn, board.getPieceAt(e4));
    }
}

class NoDrawRules extends ChessRules {
    
    private static final long serialVersionUID = 1L;
}

class AllDrawRules implements Rules {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void assertLegal(Move move, Board board) throws IllegalMoveException {
    }
    
    @Override
    public boolean canAnyLegalMoveEndOn(Square square, Board board) {
        return true;
    }
    
    @Override
    public boolean canAnyLegalMoveStartOn(Square square, Board board) {
        return true;
    }
    
    @Override
    public DrawType getDrawType(Board board) {
        return DrawType.FiftyMove;
    }
    
    @Override
    public Collection<Move> getLegalMovesEndingOn(Square square, Board board) {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isLegal(Move move, Board board) {
        return true;
    }
    
    @Override
    public Position move(Move move, Position position) {
        return position;
    }
    
    @Override
    public boolean isCheckMate(Board board) {
        return false;
    }
    
    @Override
    public Collection<Move> getLegalMovesStartingOn(Square square, Board board) {
        return Collections.emptyList();
    }
    
    @Override
    public Move getAnyLegalMoveStartingOn(Square square, Board board) {
        return null;
    }
    
    @Override
    public Move getAnyLegalMoveEndingOn(Square square, Board board) {
        return null;
    }
    
    @Override
    public Move getAnyLegalMove(Board board) {
        return null;
    }
}

class SimpleBoardListener implements BoardListener {
    
    private int historyChanged;
    private int historySelectionChanged;
    private int resignCount;
    private int drawCount;
    
    @Override
    public void moveHistoryChanged(MoveHistory moveHistory) {
        historyChanged++;
    }
    
    public int getDrawOfferCount() {
        return drawCount;
    }
    
    @Override
    public void moveHistorySelectionChanged(MoveHistory moveHistory) {
        historySelectionChanged++;
    }
    
    public int getHistoryChangedCount() {
        return historyChanged;
    }
    
    public int getHistorySelectionChangedCount() {
        return historySelectionChanged;
    }
    
    @Override
    public void gameDrawn(Board board, DrawType draw) {
    }
    
    @Override
    public void checkMate(Board board) {
    }
    
    @Override
    public void offerDraw(Board board) {
        drawCount++;
    }
    
    @Override
    public void resign(Board board) {
        resignCount++;
    }
    
    public int getResignCount() {
        return resignCount;
    }
}

class GameStateListener implements BoardListener {
    
    private DrawType draw = null;
    private Color checkMate = null;
    
    @Override
    public void moveHistoryChanged(MoveHistory moveHistory) {
    }
    
    @Override
    public void moveHistorySelectionChanged(MoveHistory moveHistory) {
    }
    
    @Override
    public void gameDrawn(Board board, DrawType draw) {
        this.draw = draw;
    }
    
    public DrawType getDrawType() {
        return draw;
    }
    
    @Override
    public void checkMate(Board board) {
        checkMate = board.getTurn();
    }
    
    public Color getCheckMatedPlayer() {
        return checkMate;
    }
    
    @Override
    public void offerDraw(Board board) {
    }
    
    @Override
    public void resign(Board board) {
    }
}

class AllIllegalRules implements Rules {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void assertLegal(Move move, Board board) throws IllegalMoveException {
        throw new IllegalMoveException("Everything is illegal!");
    }
    
    @Override
    public Position move(Move move, Position position) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isLegal(Move move, Board board) {
        return false;
    }
    
    @Override
    public boolean canAnyLegalMoveStartOn(Square square, Board board) {
        return false;
    }
    
    @Override
    public boolean canAnyLegalMoveEndOn(Square square, Board board) {
        return false;
    }
    
    @Override
    public Collection<Move> getLegalMovesEndingOn(Square square, Board board) {
        return Collections.emptyList();
    }
    
    @Override
    public DrawType getDrawType(Board board) {
        return null;
    }
    
    @Override
    public boolean isCheckMate(Board board) {
        return false;
    }
    
    @Override
    public Collection<Move> getLegalMovesStartingOn(Square square, Board board) {
        return Collections.emptyList();
    }
    
    @Override
    public Move getAnyLegalMoveStartingOn(Square square, Board board) {
        return null;
    }
    
    @Override
    public Move getAnyLegalMoveEndingOn(Square square, Board board) {
        return null;
    }
    
    @Override
    public Move getAnyLegalMove(Board board) {
        return null;
    }
}

class ECOBoardListener implements BoardListener {
    
    private final Board board;
    private ECO eco;
    
    public ECOBoardListener(Board board) {
        this.board = board;
    }
    
    public ECO getECO() {
        return eco;
    }
    
    @Override
    public void moveHistoryChanged(MoveHistory moveHistory) {
        this.eco = board.getECO();
    }
    
    @Override
    public void moveHistorySelectionChanged(MoveHistory moveHistory) {
    }
    
    @Override
    public void gameDrawn(Board board, DrawType draw) {
    }
    
    @Override
    public void checkMate(Board board) {
    }
    
    @Override
    public void offerDraw(Board board) {
    }
    
    @Override
    public void resign(Board board) {
    }
}
