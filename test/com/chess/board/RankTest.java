package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.Rank.eighth;
import static com.chess.board.Rank.fifth;
import static com.chess.board.Rank.first;
import static com.chess.board.Rank.fourth;
import static com.chess.board.Rank.second;
import static com.chess.board.Rank.seventh;
import static com.chess.board.Rank.sixth;
import static com.chess.board.Rank.third;
import junit.framework.TestCase;

public class RankTest extends TestCase {
    
    public void testDistanceFromCenter() throws Exception {
        assertEquals(3, first.distanceFromCenter());
        assertEquals(2, second.distanceFromCenter());
        assertEquals(1, third.distanceFromCenter());
        assertEquals(0, fourth.distanceFromCenter());
        assertEquals(0, fifth.distanceFromCenter());
        assertEquals(1, sixth.distanceFromCenter());
        assertEquals(2, seventh.distanceFromCenter());
        assertEquals(3, eighth.distanceFromCenter());
    }
    
    public void testRankPerspective() throws Exception {
        assertEquals(eighth, first.fromPerspective(Black));
        assertEquals(seventh, second.fromPerspective(Black));
        assertEquals(sixth, third.fromPerspective(Black));
        assertEquals(fifth, fourth.fromPerspective(Black));
        assertEquals(fourth, fifth.fromPerspective(Black));
        assertEquals(third, sixth.fromPerspective(Black));
        assertEquals(second, seventh.fromPerspective(Black));
        assertEquals(first, eighth.fromPerspective(Black));
        
        for (Rank rank : Rank.values()) {
            assertSame(rank, rank.fromPerspective(White));
        }
    }
    
    public void testGetRelativeRank() throws Exception {
        assertEquals(first, second.getRelativeRank(-1));
        assertNull(second.getRelativeRank(-2));
        assertEquals(eighth, first.getRelativeRank(7));
        assertEquals(fourth, eighth.getRelativeRank(-4));
        assertNull(eighth.getRelativeRank(1));
    }
    
    public void testNumber() {
        assertEquals(1, first.number());
        assertEquals(2, second.number());
        assertEquals(3, third.number());
        assertEquals(4, fourth.number());
        assertEquals(5, fifth.number());
        assertEquals(6, sixth.number());
        assertEquals(7, seventh.number());
        assertEquals(8, eighth.number());
    }
}
