package com.chess.board;

import static com.chess.board.PieceType.Bishop;
import static com.chess.board.PieceType.King;
import static com.chess.board.PieceType.Knight;
import static com.chess.board.PieceType.Pawn;
import static com.chess.board.PieceType.Queen;
import static com.chess.board.PieceType.Rook;
import junit.framework.TestCase;

public class PieceTypeTest extends TestCase {
    
    public void testIsMoreValuable() {
        assertTrue(Knight.isMoreValuableThan(Pawn));
        assertFalse(Pawn.isMoreValuableThan(Knight));
        assertTrue(Bishop.isMoreValuableThan(Knight));
        assertFalse(Knight.isMoreValuableThan(Bishop));
        assertTrue(Rook.isMoreValuableThan(Bishop));
        assertFalse(Bishop.isMoreValuableThan(Rook));
        assertTrue(Queen.isMoreValuableThan(Rook));
        assertTrue(King.isMoreValuableThan(Queen));
    }
}
