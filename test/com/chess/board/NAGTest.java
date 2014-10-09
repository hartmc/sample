package com.chess.board;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.NAG.badMove;
import static com.chess.board.NAG.decisiveAdvantageBlack;
import static com.chess.board.NAG.decisiveAdvantageWhite;
import static com.chess.board.NAG.dubiousMove;
import static com.chess.board.NAG.equalPosition;
import static com.chess.board.NAG.goodMove;
import static com.chess.board.NAG.moderateAdvantageBlack;
import static com.chess.board.NAG.moderateAdvantageWhite;
import static com.chess.board.NAG.slightAdvantageBlack;
import static com.chess.board.NAG.slightAdvantageWhite;
import static com.chess.board.NAG.veryBadMove;
import static com.chess.board.NAG.veryGoodMove;
import static com.chess.board.Square.d1;
import static com.chess.board.Square.e2;
import static com.chess.board.Square.e4;
import static com.chess.board.Square.g5;
import static com.chess.board.Square.g7;
import static com.chess.board.Square.h5;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import com.chess.ai.PartialCalculation;

public class NAGTest extends TestCase {
    
    public void testGetNAGForCalculation() {
        assertEquals(equalPosition, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(15)));
        assertEquals(slightAdvantageWhite, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(35)));
        assertEquals(slightAdvantageWhite, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-35)));
        assertEquals(slightAdvantageBlack, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-35)));
        assertEquals(slightAdvantageBlack, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(35)));
        assertEquals(moderateAdvantageWhite, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(90)));
        assertEquals(moderateAdvantageWhite, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-90)));
        assertEquals(moderateAdvantageBlack, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-90)));
        assertEquals(moderateAdvantageBlack, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(90)));
        assertEquals(decisiveAdvantageWhite, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(300)));
        assertEquals(decisiveAdvantageWhite, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-300)));
        assertEquals(decisiveAdvantageBlack, NAG.getNAGForCalculation(White, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(-300)));
        assertEquals(decisiveAdvantageBlack, NAG.getNAGForCalculation(Black, new PartialCalculation().setScoreInCentiPawnsFromCurrentPlayerViewpoint(300)));
        assertEquals(
                decisiveAdvantageWhite,
                NAG.getNAGForCalculation(White, new PartialCalculation().setMate(1).setMainLine(new Move(e2, e4))));
        assertEquals(decisiveAdvantageWhite, NAG.getNAGForCalculation(
                Black,
                new PartialCalculation().setMate(-1).setMainLine(new Move(g7, g5), new Move(d1, h5))));
        assertEquals(decisiveAdvantageBlack, NAG.getNAGForCalculation(
                White,
                new PartialCalculation().setMate(-1).setMainLine(new Move(g7, g5), new Move(d1, h5))));
        assertEquals(
                decisiveAdvantageBlack,
                NAG.getNAGForCalculation(Black, new PartialCalculation().setMate(1).setMainLine(new Move(e2, e4))));
    }
    
    public void testGetNAGForEvalInCentipawns() {
        assertEquals(equalPosition, NAG.getNagForEvalInCentiPawns(0));
        assertEquals(equalPosition, NAG.getNagForEvalInCentiPawns(24));
        assertEquals(equalPosition, NAG.getNagForEvalInCentiPawns(-24));
        assertEquals(slightAdvantageWhite, NAG.getNagForEvalInCentiPawns(25));
        assertEquals(slightAdvantageWhite, NAG.getNagForEvalInCentiPawns(69));
        assertEquals(moderateAdvantageWhite, NAG.getNagForEvalInCentiPawns(70));
        assertEquals(moderateAdvantageWhite, NAG.getNagForEvalInCentiPawns(139));
        assertEquals(decisiveAdvantageWhite, NAG.getNagForEvalInCentiPawns(140));
        assertEquals(slightAdvantageBlack, NAG.getNagForEvalInCentiPawns(-25));
        assertEquals(slightAdvantageBlack, NAG.getNagForEvalInCentiPawns(-69));
        assertEquals(moderateAdvantageBlack, NAG.getNagForEvalInCentiPawns(-70));
        assertEquals(moderateAdvantageBlack, NAG.getNagForEvalInCentiPawns(-139));
        assertEquals(decisiveAdvantageBlack, NAG.getNagForEvalInCentiPawns(-140));
    }
    
    public void testGetNAGByEncoding() throws Exception {
        assertEquals(slightAdvantageWhite, NAG.getNAGForEncoding(14));
    }
    
    public void testChooseMoveQualityNag() {
        assertNull(NAG.chooseMoveQualityNag(new ArrayList<NAG>()));
        assertEquals(
                goodMove,
                NAG.chooseMoveQualityNag(Arrays.asList(new NAG[] { equalPosition, decisiveAdvantageBlack, goodMove, badMove,
                                                                  slightAdvantageWhite })));
        assertNull(NAG.chooseMoveQualityNag(Arrays.asList(new NAG[] { equalPosition, slightAdvantageBlack })));
    }
    
    public void testNextMoveQualityNAG() {
        assertNull(NAG.nextMoveQualityNAG(equalPosition, true));
        assertNull(NAG.nextMoveQualityNAG(equalPosition, false));
        
        NAG nag = goodMove;
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(nag, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(dubiousMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(badMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(veryBadMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertNull(null, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(veryGoodMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, true);
        assertEquals(goodMove, nag);
        
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(veryGoodMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(null, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(veryBadMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(badMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(dubiousMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(nag, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(goodMove, nag);
        nag = NAG.nextMoveQualityNAG(nag, false);
        assertEquals(veryGoodMove, nag);
    }
    
    public void testChoosePositionEvaluationNag() {
        assertNull(NAG.choosePositionEvaluationNag(new ArrayList<NAG>()));
        assertEquals(
                equalPosition,
                NAG.choosePositionEvaluationNag(Arrays.asList(new NAG[] { veryGoodMove, equalPosition, goodMove, badMove,
                                                                         slightAdvantageWhite })));
        assertNull(NAG.choosePositionEvaluationNag(Arrays.asList(new NAG[] { veryGoodMove, badMove })));
    }
    
    public void testNextPositionEvaluationNAG() {
        assertNull(NAG.nextPositionEvaluationNAG(goodMove, true));
        assertNull(NAG.nextPositionEvaluationNAG(goodMove, false));
        
        assertEquals(decisiveAdvantageWhite, NAG.nextPositionEvaluationNAG(null, true));
        assertEquals(decisiveAdvantageBlack, NAG.nextPositionEvaluationNAG(null, false));
        
        NAG nag = decisiveAdvantageWhite;
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(moderateAdvantageWhite, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(slightAdvantageWhite, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(equalPosition, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(slightAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(moderateAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(decisiveAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertNull(nag);
        nag = NAG.nextPositionEvaluationNAG(nag, true);
        assertEquals(decisiveAdvantageWhite, nag);
        
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertNull(nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(decisiveAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(moderateAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(slightAdvantageBlack, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(equalPosition, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(slightAdvantageWhite, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(moderateAdvantageWhite, nag);
        nag = NAG.nextPositionEvaluationNAG(nag, false);
        assertEquals(decisiveAdvantageWhite, nag);
    }
}
