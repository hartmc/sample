package com.chess.board;

import static com.chess.board.NAG.decisiveAdvantageBlack;
import static com.chess.board.NAG.goodMove;
import static com.chess.board.Square.c2;
import static com.chess.board.Square.c4;
import static com.chess.board.Square.c5;
import static com.chess.board.Square.c7;
import static com.chess.board.Square.d2;
import static com.chess.board.Square.d4;
import static com.chess.board.Square.d5;
import static com.chess.board.Square.d7;
import static com.chess.board.Square.e2;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.e5;
import static com.chess.board.Square.e7;
import static com.chess.board.Square.f2;
import static com.chess.board.Square.f3;
import static com.chess.board.Square.f4;
import static com.chess.board.Square.f5;
import static com.chess.board.Square.f6;
import static com.chess.board.Square.f7;
import static com.chess.board.Square.g1;

import java.util.Set;

import com.chess.util.SerializationTest;

public class PositionTreeTest extends SerializationTest<PositionTree> {
    
    public void testGetMoves() throws Exception {
        Board board = new Board().move(e2, e4).move(e7, e5);
        board.reset();
        board.move(d2, d4).move(d7, d5);
        board.reset();
        board.move(c2, c4);
        board.reset();
        
        Set<Move> moves = board.getMoveHistory().getCurrentPositionTree().getNextMoves();
        assertEquals(3, moves.size());
        assertTrue(moves.contains(new Move(e2, e4)));
        assertTrue(moves.contains(new Move(d2, d4)));
        assertTrue(moves.contains(new Move(c2, c4)));
    }
    
    public void testStartPositionCannotHideLines() throws Exception {
        PositionTree start = new Board().getMoveHistory().getCurrentPositionTree();
        start.setShowLine(false);
        assertTrue(start.isMainLineVisible());
    }
    
    private boolean called = false;
    
    public void testCollapseTree() throws Exception {
        called = false;
        PositionTree tree = new Board().getMoveHistory().getCurrentPositionTree();
        tree.addVariation(new PositionTree(tree, new Move(e2, e4), Fen.getDefaultFen(), "1.e4") {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void collapseSubTree() {
                called = true;
            }
        });
        
        tree.collapseSubTree();
        assertTrue(called);
    }
    
    public void testCollapseTreeChangesAllNodesWithMultipleVariations() throws Exception {
        Board board = new Board();
        PositionTree start = board.getMoveHistory().getCurrentPositionTree();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        board.undoMove();
        PositionTree e5Var = board.getMoveHistory().getCurrentPositionTree();
        board.move(g1, f3);
        board.reset();
        board.move(d2, d4);
        
        board.reset();
        board.getMoveHistory().getCurrentPositionTree().collapseSubTree();
        
        assertFalse(start.isVariationVisible());
        assertFalse(e5Var.isVariationVisible());
    }
    
    public void testSerializable() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        PositionTree tree = board.getMoveHistory().getCurrentPositionTree();
        tree.setComment("testing");
        tree.addNAG(decisiveAdvantageBlack);
        tree.setShowVariations(false);
        
        assertSerialize(tree);
    }
    
    public void testNAGOrdering() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        
        element.addNAG(goodMove);
        element.addNAG(decisiveAdvantageBlack);
        
        assertEquals(goodMove, element.getNAGs().first());
        assertEquals(decisiveAdvantageBlack, element.getNAGs().last());
        
        element.removeNAG(goodMove);
        element.removeNAG(NAG.decisiveAdvantageBlack);
        assertEquals(0, element.getNAGs().size());
        
        element.addNAG(decisiveAdvantageBlack);
        element.addNAG(goodMove);
        assertEquals(goodMove, element.getNAGs().first());
        assertEquals(decisiveAdvantageBlack, element.getNAGs().last());
    }
    
    public void testAddNullNagDoesNothing() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        element.addNAG(null);
        assertEquals(0, element.getNAGs().size());
    }
    
    public void testContains() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(d7, d5);
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        
        board.move(g1, f3);
        PositionTree element2 = board.getMoveHistory().getCurrentPositionTree();
        
        assertTrue(element2.containsInTree(element));
        assertTrue(element.containsInTree(element2));
        assertTrue(element.containsInTree(element));
        
        Board board2 = new Board();
        board2.move(e2, e4);
        assertFalse(element.containsInTree(board2.getMoveHistory().getCurrentPositionTree()));
    }
    
    public void testIsMainLine() throws Exception {
        Board board = new Board();
        assertTrue(board.getMoveHistory().getCurrentPositionTree().isMainLine());
        board.move(e2, e4);
        assertTrue(board.getMoveHistory().getCurrentPositionTree().isMainLine());
        board.move(d7, d5);
        assertTrue(board.getMoveHistory().getCurrentPositionTree().isMainLine());
        board.undoMove();
        board.move(c7, c5);
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isMainLine());
    }
    
    public void testGetInitialPosition() throws Exception {
        PositionTree element = new Board().getMoveHistory().getCurrentPositionTree();
        assertSame(element, element.getInitialPosition());
        
        Board board = new Board();
        element = board.getMoveHistory().getCurrentPositionTree();
        board.move(e2, e4);
        assertEquals(element, board.getMoveHistory().getCurrentPositionTree().getInitialPosition());
    }
    
    public void testDoNotStoreEmptyComment() throws Exception {
        PositionTree element = new PositionTree(null, new Move(e2, e4), new Board().getFen(), "1.e4");
        element.setComment("");
        assertNull(element.getComment());
    }
    
    public void testGetVariation() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        board.move(new Move(e7, e5));
        PositionTree e5 = board.getMoveHistory().getCurrentPositionTree();
        
        board.undoMove();
        board.move(new Move(d7, d5));
        PositionTree d5 = board.getMoveHistory().getCurrentPositionTree();
        
        board.undoMove();
        board.move(new Move(f7, f5));
        PositionTree f5 = board.getMoveHistory().getCurrentPositionTree();
        
        board.undoMove();
        PositionTree currentMove = board.getMoveHistory().getCurrentPositionTree();
        assertEquals(d5, currentMove.getVariation(new Move(d7, Square.d5)));
        assertEquals(f5, currentMove.getVariation(new Move(f7, Square.f5)));
        assertEquals(e5, currentMove.getVariation(new Move(e7, Square.e5)));
        assertNull(currentMove.getVariation(new Move(f7, f6)));
    }
    
    public void testEquality() throws Exception {
        Board board = new Board();
        board.move(new Move(e2, e4));
        Fen fen = board.getFen();
        
        PositionTree element = new PositionTree(null, new Move(e2, e4), fen, "1.e4");
        PositionTree element2 = new PositionTree(null, new Move(e2, e4), board.getFen(), "1.e4");
        
        assertEquals(element, element2);
        
        // testing the parent and the variations leads to an infinite loop
        // MoveElement withParent = new MoveElement(element, new Move(e2, e4), fen, "1.e4");
        // assertFalse(element.equals(withParent));
        
        PositionTree differentMove = new PositionTree(null, new Move(d2, d4), fen, "1.e4");
        assertFalse(element.equals(differentMove));
        
        PositionTree differentFen = new PositionTree(null, new Move(e2, e4), new Board().getFen(), "1.e4");
        assertFalse(element.equals(differentFen));
        
        PositionTree differentMoveDescription = new PositionTree(null, new Move(e2, e4), fen, "1.e4+");
        assertFalse(element.equals(differentMoveDescription));
        
        PositionTree differentVariations = new PositionTree(null, new Move(e2, e4), fen, "1.e4");
        differentVariations.addVariation(element);
        assertFalse(element.equals(differentVariations));
    }
}
