package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Piece.BlackPawn;
import static com.chess.board.Piece.WhiteKnight;
import static com.chess.board.PieceType.Knight;
import static com.chess.board.PieceType.Pawn;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

public class PieceTest extends TestCase {
    
    public void testIsWhite() throws Exception {
        for (Piece piece : Piece.values()) {
            if (piece.getColor() == White) {
                assertTrue(piece.isWhite());
                assertFalse(piece.isBlack());
            }
            else {
                assertTrue(piece.isBlack());
                assertFalse(piece.isWhite());
            }
        }
    }
    
    public void testGetPiece() throws Exception {
        assertEquals(WhiteKnight, Piece.getPiece(White, Knight));
        assertEquals(BlackPawn, Piece.getPiece(Black, Pawn));
        
        SortedSet<Piece> pieces = new TreeSet<Piece>();
        for (Color color : Color.values()) {
            for (PieceType piece : PieceType.values()) {
                pieces.add(Piece.getPiece(color, piece));
            }
        }
        
        assertEquals(12, pieces.size());
    }
    
    public void testGetPieceFromFenEncoding() {
        for (Piece piece : Piece.values()) {
            assertEquals(piece, Piece.pieceFromFenEncoding(piece.getFenEncoding().charAt(0)));
        }
        
        assertNull(Piece.pieceFromFenEncoding('1'));
    }
}
