package com.chess.board;

import static com.chess.board.Square.e2;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.e5;
import static com.chess.board.Square.e7;
import static com.chess.board.Square.f2;
import static com.chess.board.Square.f4;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class PathTest extends TestCase {
    
    public void testIterationForSingleElement() throws Exception {
        PositionTree tree = new Board().getMoveHistory().getCurrentPositionTree();
        Path path = new Path(tree);
        assertTrue(path.iterator().hasNext());
        assertEquals(tree, path.iterator().next());
    }
    
    public void testIteration() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree start = board.getMoveHistory().getCurrentPositionTree();
        
        board.move(e7, e5);
        PositionTree e5Var = board.getMoveHistory().getCurrentPositionTree();
        board.move(f2, f4);
        PositionTree f4Var = board.getMoveHistory().getCurrentPositionTree();
        board.move(e5, f4);
        PositionTree exf4Var = board.getMoveHistory().getCurrentPositionTree();
        
        Path path = new Path(start, new Move(e7, e5), new Move(f2, f4), new Move(e5, f4));
        
        Iterator<PositionTree> iterator = path.iterator();
        
        assertTrue(iterator.hasNext());
        assertSame(start, iterator.next());
        
        assertTrue(iterator.hasNext());
        assertSame(e5Var, iterator.next());
        
        assertTrue(iterator.hasNext());
        assertSame(f4Var, iterator.next());
        
        assertTrue(iterator.hasNext());
        assertSame(exf4Var, iterator.next());
        
        assertFalse(iterator.hasNext());
        
        try {
            iterator.next();
            fail("should throw exception");
        }
        catch (NoSuchElementException nsee) {
        }
    }
    
    public void testConcurrentModificationException() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree start = board.getMoveHistory().getCurrentPositionTree();
        
        board.move(e7, e5);
        board.move(f2, f4);
        board.move(e5, f4);
        
        Path path = new Path(start, new Move(e7, e5), new Move(f2, f4), new Move(e5, f4));
        
        Iterator<PositionTree> iterator = path.iterator();
        
        assertTrue(iterator.hasNext());
        assertSame(start, iterator.next());
        
        start.clearVariations();
        
        try {
            iterator.next();
            fail("should throw exception");
        }
        catch (ConcurrentModificationException cme) {
        }
    }
}
