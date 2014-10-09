package com.chess.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class Path implements Iterable<PositionTree> {
    
    private final PositionTree tree;
    private final List<Move> path = new ArrayList<Move>();
    
    public Path(PositionTree start, Move... moves) {
        this(start, Arrays.asList(moves));
    }
    
    public Path(PositionTree tree, List<Move> path) {
        this.tree = tree;
        this.path.addAll(path);
    }
    
    public Path(PositionTree initialPosition, PositionTree current) {
        this(initialPosition, buildPath(current, initialPosition));
    }
    
    private static List<Move> buildPath(PositionTree current, PositionTree initialPosition) {
        List<Move> moves = new ArrayList<Move>();
        while (current != initialPosition) {
            moves.add(0, current.getLastMove());
            current = current.getParentTree();
        }
        
        return moves;
    }
    
    @Override
    public String toString() {
        return tree.getMoveDescription() + "-" + path.toString();
    }
    
    @Override
    public Iterator<PositionTree> iterator() {
        final Iterator<Move> moveIterator = path.iterator();
        return new Iterator<PositionTree>() {
            
            private PositionTree current = null;
            
            @Override
            public boolean hasNext() {
                return current == null || moveIterator.hasNext();
            }
            
            @Override
            public PositionTree next() {
                if (current == null) {
                    current = tree;
                }
                else {
                    Move move = moveIterator.next();
                    PositionTree next = current.getVariation(move);
                    if (next == null)
                        throw new ConcurrentModificationException("Could not find " + move + " variation under " + current);
                    
                    current = next;
                }
                
                return current;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((tree == null) ? 0 : tree.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Path other = (Path) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        }
        else if (!path.equals(other.path))
            return false;
        if (tree == null) {
            if (other.tree != null)
                return false;
        }
        else if (!tree.equals(other.tree))
            return false;
        return true;
    }
    
    public PositionTree getInitialPosition() {
        return tree;
    }
}
