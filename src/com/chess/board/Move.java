package com.chess.board;

import static com.chess.board.PieceType.Pawn;
import static com.chess.board.Promotion.Queen;

import java.io.Serializable;

//import com.chess.application.ApplicationProperties;

public class Move implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Square startSquare;
    private final Square endSquare;
    private Promotion promotion = null;
    
    public Move(Square start, Square end) {
        this(start, end, null);
    }
    
    public Move(Square start, Square end, Promotion piece) {
        this.startSquare = start;
        this.endSquare = end;
        this.promotion = piece;
    }
    
    public static Move buildMove(Square start, Square end, Board board) {
        if (end.isBackRank() && board.getPieceAt(start) != null && board.getPieceAt(start).getPieceType() == Pawn) {
     // comment for code sample so don't need to import ApplicationProperties
     // return new Move(start, end, ApplicationProperties.getPromotionPiece());
            return new Move(start, end, Queen);
        }
        else {
            return new Move(start, end);
        }
    }
    
    public Square getStartSquare() {
        return startSquare;
    }
    
    public Square getEndSquare() {
        return endSquare;
    }
    
    public Move setPromotion(Promotion piece) {
        if (piece == null)
            this.promotion = Queen;
        else
            this.promotion = piece;
        
        return this;
    }
    
    public Promotion getPromotionPiece() {
        return promotion;
    }
    
    public String getLongAlgebraicNotation() {
        return startSquare.toString() + endSquare.toString()
               + (promotion != null ? promotion.getPieceType().getAlgebraicDescription() : "");
    }
    
    public static Move parseLongAlgebraicNotation(String moveString) {
        if (moveString == null || moveString.length() < 4)
            return null;
        
        try {
            Move move = new Move(Square.valueOf(moveString.substring(0, 2)), Square.valueOf(moveString.substring(2, 4)));
            if (moveString.length() > 4) {
                Promotion promotion = Promotion.fromChar(moveString.charAt(4));
                if (promotion != null)
                    move.setPromotion(promotion);
            }
            
            return move;
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return startSquare + "-" + endSquare + (promotion != null ? "=" + promotion.getPieceType().getAlgebraicDescription() : "");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endSquare == null) ? 0 : endSquare.hashCode());
        result = prime * result + ((promotion == null) ? 0 : promotion.hashCode());
        result = prime * result + ((startSquare == null) ? 0 : startSquare.hashCode());
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
        Move other = (Move) obj;
        if (endSquare == null) {
            if (other.endSquare != null)
                return false;
        }
        else if (!endSquare.equals(other.endSquare))
            return false;
        if (promotion == null) {
            if (other.promotion != null)
                return false;
        }
        else if (!promotion.equals(other.promotion))
            return false;
        if (startSquare == null) {
            if (other.startSquare != null)
                return false;
        }
        else if (!startSquare.equals(other.startSquare))
            return false;
        return true;
    }
}
