package com.chess.board;

import java.io.Serializable;
import java.util.Collection;

public interface Rules extends Serializable {
    
    public void assertLegal(Move move, Board board) throws IllegalMoveException;
    
    public Position move(Move move, Position position);
    
    public boolean isLegal(Move move, Board board);
    
    public boolean canAnyLegalMoveStartOn(Square square, Board board);
    
    public Move getAnyLegalMoveStartingOn(Square square, Board board);
    
    public boolean canAnyLegalMoveEndOn(Square square, Board board);
    
    public Move getAnyLegalMoveEndingOn(Square square, Board board);
    
    public Collection<Move> getLegalMovesEndingOn(Square square, Board board);
    
    public Collection<Move> getLegalMovesStartingOn(Square square, Board board);
    
    public DrawType getDrawType(Board board);
    
    public boolean isCheckMate(Board board);
    
    public Move getAnyLegalMove(Board board);
}
