package com.chess.board;

//import com.chess.ai.PartialCalculation;

import java.util.*;

import static com.chess.board.Color.Black;

public enum NAG {
    goodMove("!", 1),
    badMove("?", 2),
    veryGoodMove("!!", 3),
    veryBadMove("??", 4),
    interestingMove("!?", 5),
    dubiousMove("?!", 6),
    forcedMove("", 7),
    equalPosition("=", 10),
    equalQuietPosition("=", 11),
    equalActivePosition("=", 12),
    unclearPosition("\u221E", 13),
    slightAdvantageWhite("+/=", 14),
    slightAdvantageBlack("=/+", 15),
    moderateAdvantageWhite("+/-", 16),
    moderateAdvantageBlack("-/+", 17),
    decisiveAdvantageWhite("+-", 18),
    decisiveAdvantageBlack("-+", 19),
    zugzwangWhite(" zugzwang", 22),
    zugzwangBlack(" zugzwang", 23),
    idea("ie:", 140),
    novelty(" novelty", 146);

    public static List<NAG> moveQualityNAGs = Collections.unmodifiableList(
            Arrays.asList(veryGoodMove, goodMove,interestingMove,
                    dubiousMove,badMove, veryBadMove));
    public static List<NAG> positionEvaluationNAGs = Collections.unmodifiableList(
            Arrays.asList(decisiveAdvantageWhite,moderateAdvantageWhite,
             slightAdvantageWhite,equalPosition,slightAdvantageBlack,
             moderateAdvantageBlack,decisiveAdvantageBlack));
    private String description;
    private int encoding;
    private static final Map<Integer, NAG> encodingToNag = new HashMap<Integer, NAG>();

    static {
        for (NAG nag : NAG.values()) {
            encodingToNag.put(nag.encoding, nag);
        }
    }

    private NAG(String description, int encoding) {
        this.description = description;
        this.encoding = encoding;
    }

    public String getDescription() {
        return description;
    }

    public static NAG getNAGForEncoding(int encoding) {
        return encodingToNag.get(encoding);
    }

    public int nagNumber() {
        return encoding;
    }

    public static NAG getNagForEvalInCentiPawns(int evalInCentiPawns) {
        if (evalInCentiPawns > 139)
            return decisiveAdvantageWhite;
        if (evalInCentiPawns > 69)
            return moderateAdvantageWhite;
        else if (evalInCentiPawns > 24)
            return slightAdvantageWhite;
        else if (evalInCentiPawns > -25)
            return equalPosition;
        else if (evalInCentiPawns > -70)
            return slightAdvantageBlack;
        else if (evalInCentiPawns > -140)
            return moderateAdvantageBlack;
        else
            return decisiveAdvantageBlack;
    }

    public static NAG chooseMoveQualityNag(Collection<NAG> nags) {
        return chooseNAG(nags, moveQualityNAGs);
    }

    public static NAG nextMoveQualityNAG(NAG nag, boolean forward) {
        return nextNAG(nag, forward, moveQualityNAGs);
    }

    public static NAG choosePositionEvaluationNag(Collection<NAG> nags) {
        return chooseNAG(nags, positionEvaluationNAGs);
    }

    public static NAG nextPositionEvaluationNAG(NAG nag, boolean forward) {
        return nextNAG(nag, forward, positionEvaluationNAGs);
    }

    private static NAG chooseNAG(Collection<NAG> nags, List<NAG> nagList) {
        for (NAG nag : nags) {
            if (nagList.contains(nag))
                return nag;
        }

        return null;
    }

    private static NAG nextNAG(NAG nag, boolean forward, List<NAG> nagList) {
        if (nag == null)
            return forward ? nagList.get(0) : nagList.get(nagList.size() - 1);

        int index = nagList.indexOf(nag);
        if (index < 0)
            return null;

        if (forward && index == nagList.size() - 1)
            return null;

        if (!forward && index == 0)
            return null;

        return nagList.get(forward ? index + 1 : index - 1);
    }

    /**
     * Commenting out for sample so class compiles without importing PartialCalculation etc...

    public static NAG getNAGForCalculation(Color turn, PartialCalculation bestMoveAvailable) {
        int score;
        if (bestMoveAvailable.isMate())
            if (bestMoveAvailable.isDeliveringMate())
                score = 1000;
            else
                score = -1000;
        else
            score = bestMoveAvailable.getScoreInCentiPawnsFromCurrentPlayerViewpoint();

        if (turn == Black)
            score *= -1;

        return getNagForEvalInCentiPawns(score);
    }
     */
}
