package com.chess.board;

import com.chess.util.SerializationTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.chess.board.NAG.*;
import static com.chess.board.Square.*;
import static com.chess.game.ECO.*;

public class MoveHistoryTest extends SerializationTest<MoveHistory> {

    public void testRedoTogglesLineVisibility() throws Exception {
        Board board = new Board();
        board.move(e2, e4).move(e7, e5).move(f2, f4).move(e5, f4);
        board.reset();
        board.redoMove();
        board.getMoveHistory().toggleLineVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isMainLineVisible());

        PositionTree tree = board.getMoveHistory().getCurrentPositionTree();

        board.redoMove();

        // really, we are testing that MoveHistory.setCurrentPositionTree() does the right
        // thing to make the position visible to the user...
        assertTrue(tree.isMainLineVisible());
    }

    public void testClearVariationsWhenVariationsAreAlreadyEmptyDoesNotFireEvent() throws Exception {
        Board board = new Board();
        MoveHistory moveHistory = board.getMoveHistory();

        board.move(e2, e4);
        board.move(e7, e5);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        moveHistory.addMoveHistoryListener(listener);
        moveHistory.clearVariations();
        assertEquals(0, moveHistory.getCurrentPositionTree().getVariations().size());
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testClearVariations() throws Exception {
        Board board = new Board();
        MoveHistory moveHistory = board.getMoveHistory();

        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        board.move(e7, e6);
        board.undoMove();

        SimpleHistoryListener listener = new SimpleHistoryListener();
        moveHistory.addMoveHistoryListener(listener);
        moveHistory.clearVariations();
        assertEquals(0, moveHistory.getCurrentPositionTree().getVariations().size());
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testAppendCommentToEmptyComment() throws Exception {
        Board board = new Board();
        MoveHistory moveHistory = board.getMoveHistory();

        board.move(e2, e4);

        moveHistory.appendComment("test comment");
        assertEquals("test comment", moveHistory.getComment());
    }

    public void testAppendCommentToFullSentence() throws Exception {
        Board board = new Board();
        MoveHistory moveHistory = board.getMoveHistory();

        board.move(e2, e4);

        moveHistory.setComment("test comment goes here.");
        moveHistory.appendComment("another comment");
        assertEquals("test comment goes here. another comment", moveHistory.getComment());
    }

    public void testMakeExistingMoveDisplaysMainLine() throws Exception {
        Board board = new Board();
        MoveHistory moveHistory = board.getMoveHistory();

        board.move(e2, e4);
        board.move(e7, e5);
        PositionTree e5 = moveHistory.getCurrentPositionTree();
        board.move(f2, f4);
        board.undoMove();
        board.move(b1, c3);

        moveHistory.setCurrentPositionTree(e5);
        moveHistory.toggleLineVisibility();
        moveHistory.toggleVariationVisibility();

        assertFalse(e5.isMainLineVisible());
        assertFalse(e5.isVariationVisible());
        board.move(f2, f4);
        assertTrue(e5.isMainLineVisible());
        assertFalse(e5.isVariationVisible());

        board.undoMove();
        assertTrue(e5.isMainLineVisible());
        assertFalse(e5.isVariationVisible());

        board.move(b1, c3);
        assertTrue(e5.isMainLineVisible());
        assertTrue(e5.isVariationVisible());
    }

    public void testToEndOfMainLine() throws Exception {
        Board board = new Board();
        SimpleBoardListener listener = new SimpleBoardListener();
        board.addBoardListener(listener);
        board.toEndOfMainLine();
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());

        board.move(e2, e4);
        board.move(e7, e5);
        PositionTree endOfLine = board.getMoveHistory().getCurrentPositionTree();
        assertEquals(2, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());

        board.toEndOfMainLine();
        assertEquals(2, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());

        board.reset();
        board.move(d2, d4);
        assertEquals(3, listener.getHistoryChangedCount());
        assertEquals(1, listener.getHistorySelectionChangedCount());

        board.toEndOfMainLine();
        assertSame(endOfLine, board.getMoveHistory().getCurrentPositionTree());
        assertEquals(3, listener.getHistoryChangedCount());
        assertEquals(2, listener.getHistorySelectionChangedCount());
    }

    // DatabaseExplorer added the requirement that adding a variation from a different board
    // automatically adjusts the parent of the added variation (ie, the variation is broken
    // in the old board and works in the new board)
    public void testAddVariationFromDifferentBoard() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree parent = board.getMoveHistory().getCurrentPositionTree();

        Board board2 = new Board(parent.getFen());
        board2.move(e7, e5);

        PositionTree variation = board.getMoveHistory().getCurrentPositionTree();
        parent.addVariation(variation);
        assertEquals(parent, variation.getParentTree());
    }

    public void testChangeMoveHistoryCalculatesECO() throws Exception {
        Board board = new Board();
        board.move(e2, e4);

        Board testBoard = new Board(board.getFen());
        testBoard.getMoveHistory().setCurrentPositionTree(board.getMoveHistory().getCurrentPositionTree());
        assertEquals(B00, testBoard.getECO());
    }

    public void testCalculateECOForInitialPosition() throws Exception {
        Board board = new Board();
        board.move(e2, e4);

        Board testBoard = new Board(board.getFen());
        assertEquals(B00, testBoard.getECO());
    }

    public void testECOCalculationTakesLatestNonNullECO() throws Exception {
        Board board = new Board();
        board.move(f2, f4);
        board.move(e7, e5);
        assertEquals(A02, board.getECO());
    }

    public void testECOIsNotCalculateAfterSuspendCalculateECOCall() throws Exception {
        Board board = new Board();
        board.suspendCalculateECO();

        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        assertEquals(A00, board.getECO());

        board.resumeCalculateECO();
        assertEquals(C30, board.getECO());
    }

    public void testAlterECOWhenLineIsPromoted() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        assertEquals(C30, board.getECO());

        board.reset();
        board.move(d2, d4);
        board.move(d7, d5);
        board.move(c2, c4);

        board.reset();
        board.move(d2, d4);

        ECOBoardListener listener = new ECOBoardListener(board);
        board.addBoardListener(listener);

        board.getMoveHistory().promoteVariation();
        assertEquals(D06, board.getECO());
        assertEquals(D06, listener.getECO());

        board.getMoveHistory().demoteVariation();
        assertEquals(C30, board.getECO());
        assertEquals(C30, listener.getECO());
    }

    public void testBoardECOUpdatedBeforeMoveEventFired() throws Exception {
        Board board = new Board();
        ECOBoardListener listener = new ECOBoardListener(board);
        board.addBoardListener(listener);
        board.move(e2, e4);
        assertEquals(B00, listener.getECO());
    }

    public void testCalculateECOForMove() throws Exception {
        Board board = new Board();
        assertEquals(A00, board.getECO());

        board.move(e2, e4);
        assertEquals(B00, board.getECO());

        board.move(e7, e5);
        assertEquals(C20, board.getECO());

        board.move(f2, f4);
        assertEquals(C30, board.getECO());

        board.move(d7, d5);
        assertEquals(C31, board.getECO());
    }

    public void testDoNotAlterECOWhenSidelineAdded() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        assertEquals(B00, board.getECO());

        board.undoMove();
        board.move(d2, d4);
        assertEquals(B00, board.getECO());
    }

    public void testDoNotAlterECOWhenMoveRedoMoveOccurs() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        assertEquals(C20, board.getECO());

        board.reset();
        board.move(e2, e4);
        assertEquals(C20, board.getECO());
    }

    public void testDeleteWordWithNoCommentDoesNothing() {
        Board board = new Board();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);
        board.getMoveHistory().deleteWord();
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testDeleteWordWhenCommentEndsWithSpace() {
        Board board = new Board();
        board.getMoveHistory().setComment("here is a comment ending with space   ");
        board.getMoveHistory().deleteWord();
        assertEquals("here is a comment ending with", board.getMoveHistory().getComment());
    }

    public void testDeleteWordFiresEvent() {
        Board board = new Board();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().setComment("here is a comment.");

        board.getMoveHistory().addMoveHistoryListener(listener);
        board.getMoveHistory().deleteWord();
        assertEquals("here is a", board.getMoveHistory().getComment());
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testDeleteWordForSingleWordComment() {
        Board board = new Board();
        board.getMoveHistory().setComment("hello");
        board.getMoveHistory().deleteWord();
        assertNull(board.getMoveHistory().getComment());
    }

    public void testSetCurrentPositionTreeExpandsNecessaryVariations() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree e4Var = board.getMoveHistory().getCurrentPositionTree();

        board.move(e7, e5);
        board.undoMove();
        board.move(e7, e6);
        board.move(d2, d4);
        board.undoMove();
        board.move(d2, d3);
        PositionTree d3Var = board.getMoveHistory().getCurrentPositionTree();

        board.undoMove();
        board.getMoveHistory().toggleVariationVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());

        board.undoMove();
        board.getMoveHistory().toggleVariationVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());

        board.getMoveHistory().setCurrentPositionTree(d3Var);

        assertTrue(e4Var.isVariationVisible());
        assertTrue(d3Var.getParentTree().isVariationVisible());
    }

    public void testAddANewVariationExpandsVariations() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        board.move(e7, e6);
        board.undoMove();
        board.getMoveHistory().toggleVariationVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());

        board.move(d7, d5);
        assertTrue(board.getMoveHistory().getCurrentPositionTree().getParentTree().isVariationVisible());
    }

    public void testMakeMoveExpandsDoesNotExpandVariationsIfMoveIsMainLine() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        board.move(e7, e6);
        board.undoMove();
        board.getMoveHistory().toggleVariationVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());

        board.move(e7, e5);
        board.undoMove();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());
    }

    public void testMakeMoveExpandsVariationsToShowMove() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        board.move(e7, e6);
        board.undoMove();
        board.getMoveHistory().toggleVariationVisibility();
        assertFalse(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());

        board.move(e7, e6);
        board.undoMove();
        assertTrue(board.getMoveHistory().getCurrentPositionTree().isVariationVisible());
    }

    public void testPrependMatchingComment() throws Exception {
        MoveHistory history = new Board().getMoveHistory();

        history.prependComment("Hello world");
        history.prependComment("Hello");
        assertEquals("Hello world", history.getComment());
    }

    public void testPrependNull() throws Exception {
        MoveHistory history = new Board().getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.prependComment(null);
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testPrependSentence() throws Exception {
        MoveHistory history = new Board().getMoveHistory();

        history.prependComment("Hello");
        history.prependComment("Hello world.");
        assertEquals("Hello world. Hello", history.getComment());
    }

    public void testPrependBeforeWhiteSpace() throws Exception {
        MoveHistory history = new Board().getMoveHistory();

        history.prependComment("   ");
        history.prependComment("This is a test");
        assertEquals("This is a test", history.getComment());
    }

    public void testPrependCommentAddsPeriodBeforeCapitalStartingLetter() throws Exception {
        MoveHistory history = new Board().getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.prependComment("This is a test");
        history.prependComment("Do not be alarmed");
        assertEquals("Do not be alarmed. This is a test", history.getComment());
        assertEquals(2, listener.getHistoryChangedCount());
    }

    public void testPrependCommentBeforeLowerCaseLetter() throws Exception {
        MoveHistory history = new Board().getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.prependComment("  is a very good move   ");
        history.prependComment("Great move");
        assertEquals("Great move and is a very good move", history.getComment());
        assertEquals(2, listener.getHistoryChangedCount());
    }

    public void testAddNAGFiresEvents() throws Exception {
        MoveHistory history = new Board().getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.addNAG(badMove);
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testSerializable() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.getMoveHistory().setComment("hi there!");

        board.getMoveHistory().getCurrentPositionTree().addNAG(badMove);
        board.move(e7, e5);
        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);

        writeObject(board.getMoveHistory());
        MoveHistory compare = readObject();

        assertEquals(board.getMoveHistory(), compare);

        SimpleHistoryListener listener2 = new SimpleHistoryListener();
        compare.addMoveHistoryListener(listener2);

        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
        board.move(new Move(d2, d4));

        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener2.getHistoryChangedCount());
        compare.deleteCurrentMove();
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(1, listener2.getHistoryChangedCount());
    }

    public void testIterateMainLine() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        board.undoMove();
        board.move(d2, d4);
        PositionTree d4Position = board.getMoveHistory().getCurrentPositionTree();

        SimplePositionListener listener = new SimplePositionListener();
        board.getMoveHistory().iterateMainLine(listener);
        List<PositionTree> positions = listener.getPositions();
        assertEquals(4, positions.size());
        assertNull(positions.get(0).getLastMove());
        assertEquals(new Move(e2, e4), positions.get(1).getLastMove());
        assertEquals(new Move(e7, e5), positions.get(2).getLastMove());
        assertEquals(new Move(f2, f4), positions.get(3).getLastMove());

        assertSame(d4Position, board.getMoveHistory().getCurrentPositionTree());
    }

    public void testGetIntitialPosition() throws Exception {
        Board board = new Board();
        PositionTree root = board.getMoveHistory().getCurrentPositionTree();

        board.move(e2, e4);
        board.move(e7, e5);
        board.move(f2, f4);
        board.undoMove();
        board.move(d2, d4);

        assertSame(root, board.getMoveHistory().getInitialPosition());
    }

    public void testGetMainLine() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.reset();
        board.move(d2, d4);

        List<Move> mainLine = board.getMoveHistory().getMainLine();
        assertEquals(2, mainLine.size());
        assertEquals(new Move(e2, e4), mainLine.get(0));
        assertEquals(new Move(e7, e5), mainLine.get(1));
    }

    public void testReset() throws Exception {
        Board board = new Board();
        PositionTree element = board.getMoveHistory().getCurrentPositionTree();

        board.move(e2, e4);
        board.move(e7, e5);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);
        board.getMoveHistory().reset();
        assertSame(element, board.getMoveHistory().getCurrentPositionTree());
        assertEquals(1, listener.getHistorySelectionChangedCount());
    }

    public void testToggleNAG() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        MoveHistory history = board.getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.toggleNAG(badMove);
        assertEquals(badMove, history.getCurrentPositionTree().getNAGs().first());
        assertEquals(1, history.getCurrentPositionTree().getNAGs().size());
        assertEquals(1, listener.getHistoryChangedCount());

        history.toggleNAG(badMove);
        assertEquals(0, history.getCurrentPositionTree().getNAGs().size());
        assertEquals(2, listener.getHistoryChangedCount());
    }

    public void testRequestEventFiredOnlyWhenNAGListActuallyChanges() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        MoveHistory history = board.getMoveHistory();
        history.replaceNag(moveQualityNAGs, goodMove);
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.replaceNag(moveQualityNAGs, goodMove);
        assertEquals(0, listener.getHistoryChangedCount());

        history.replaceNag(positionEvaluationNAGs, null);
        assertEquals(0, listener.getHistoryChangedCount());

        history.replaceNag(moveQualityNAGs, null);
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, history.getCurrentPositionTree().getNAGs().size());
    }

    public void testReplaceNAGRemovesExistingNAGsInListAndFiresEvents() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        MoveHistory history = board.getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.replaceNag(moveQualityNAGs, goodMove);
        assertEquals(goodMove, history.getCurrentPositionTree().getNAGs().first());
        assertEquals(1, history.getCurrentPositionTree().getNAGs().size());

        history.replaceNag(moveQualityNAGs, veryBadMove);
        assertEquals(veryBadMove, history.getCurrentPositionTree().getNAGs().first());
        assertEquals(1, history.getCurrentPositionTree().getNAGs().size());

        history.replaceNag(positionEvaluationNAGs, goodMove);
        assertTrue(history.getCurrentPositionTree().getNAGs().contains(goodMove));
        assertEquals(2, history.getCurrentPositionTree().getNAGs().size());

        history.replaceNag(Arrays.asList(goodMove, veryBadMove), equalPosition);
        assertEquals(equalPosition, history.getCurrentPositionTree().getNAGs().first());
        assertEquals(1, history.getCurrentPositionTree().getNAGs().size());

        assertEquals(4, listener.getHistoryChangedCount());
    }

    public void testNextMoveQualityNAG() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);

        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        board.getMoveHistory().nextMoveQualityNAG(element, true);
        assertEquals(1, element.getNAGs().size());
        assertEquals(veryGoodMove, element.getNAGs().first());
        board.getMoveHistory().nextMoveQualityNAG(element, true);
        assertEquals(1, element.getNAGs().size());
        assertEquals(goodMove, element.getNAGs().first());

        assertEquals(2, listener.getHistoryChangedCount());
    }

    public void testNextMoveEvaluationNAG() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);

        PositionTree element = board.getMoveHistory().getCurrentPositionTree();
        board.getMoveHistory().nextPositionEvaluationNAG(element, true);
        assertEquals(1, element.getNAGs().size());
        assertEquals(decisiveAdvantageWhite, element.getNAGs().first());
        board.getMoveHistory().nextPositionEvaluationNAG(element, true);
        assertEquals(1, element.getNAGs().size());
        assertEquals(moderateAdvantageWhite, element.getNAGs().first());

        assertEquals(2, listener.getHistoryChangedCount());
    }

    public void testSetMoveWithNewMoveElementFiresHistoryChangedEvent() throws Exception {
        Board board = new Board();
        board.move(e2, e4);

        Board board2 = new Board();
        board.move(d7, d5);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        board.getMoveHistory().addMoveHistoryListener(listener);
        board.getMoveHistory().setCurrentPositionTree(board2.getMoveHistory().getCurrentPositionTree());
        assertEquals(1, listener.getHistoryChangedCount());
        assertEquals(0, listener.getHistorySelectionChangedCount());
    }

    public void testSetCommentFiresEvent() throws Exception {
        MoveHistory history = new MoveHistory();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        PositionTree currentMove = history.getCurrentPositionTree();

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.setComment("Hello!");
        assertEquals("Hello!", currentMove.getComment());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testToggleVariationVisibilityVariationStateOfMoveWithMultipleVariations() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        board.move(d7, d5);
        board.undoMove();
        MoveHistory history = board.getMoveHistory();
        PositionTree currentMove = history.getCurrentPositionTree();

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.toggleVariationVisibility();
        assertFalse(currentMove.isVariationVisible());
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(1, listener.getHistorySelectionChangedCount());

        history.toggleVariationVisibility();
        assertTrue(currentMove.isVariationVisible());
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(2, listener.getHistorySelectionChangedCount());
    }

    public void testToggleLineVisibilityOfNonLeafNode() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);
        board.undoMove();
        MoveHistory history = board.getMoveHistory();
        PositionTree currentMove = history.getCurrentPositionTree();

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.toggleLineVisibility();
        assertFalse(currentMove.isMainLineVisible());
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(1, listener.getHistorySelectionChangedCount());

        history.toggleLineVisibility();
        assertTrue(currentMove.isMainLineVisible());
        assertEquals(0, listener.getHistoryChangedCount());
        assertEquals(2, listener.getHistorySelectionChangedCount());
    }

    public void testPromoteDemoteSingleLineDoesNothing() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        board.move(e7, e5);

        MoveHistory history = new MoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.promoteVariation();
        history.demoteVariation();

        assertEquals(0, listener.getHistoryChangedCount());
    }

    public void testPromoteVariationOnEmptyHistoryDoesNothing() throws Exception {
        MoveHistory history = new MoveHistory();
        history.promoteVariation();
        assertEquals(0, history.getCurrentPositionTree().getVariations().size());
    }

    public void testPromoteVariationDeepInVariation() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree parent = board.getMoveHistory().getCurrentPositionTree();
        board.move(d7, d5);
        board.undoMove();
        board.move(e7, e5);
        PositionTree newMainLine = board.getMoveHistory().getCurrentPositionTree();
        board.move(f2, f4);
        board.move(f8, c5);
        PositionTree currentMove = board.getMoveHistory().getCurrentPositionTree();

        MoveHistory history = board.getMoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.promoteVariation();

        assertEquals(newMainLine, parent.getVariations().get(0));
        assertEquals(currentMove, history.getCurrentPositionTree());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testDemoteVariationDeepInVariation() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree parent = board.getMoveHistory().getCurrentPositionTree();
        board.move(d7, d5);
        board.move(e4, d5);
        board.move(d8, d5);
        PositionTree deepInMainLine = board.getMoveHistory().getCurrentPositionTree();
        board.getMoveHistory().setCurrentPositionTree(parent);
        board.move(e7, e5);
        PositionTree newMainLine = board.getMoveHistory().getCurrentPositionTree();
        board.move(f2, f4);
        board.move(f8, c5);

        MoveHistory history = board.getMoveHistory();
        history.setCurrentPositionTree(deepInMainLine);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.demoteVariation();

        assertEquals(newMainLine, parent.getVariations().get(0));
        assertEquals(deepInMainLine, history.getCurrentPositionTree());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testPromoteVariationMovesLineUpOneLevel() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree parent = board.getMoveHistory().getCurrentPositionTree();
        board.move(d7, d5);
        board.undoMove();
        board.move(e7, e5);
        board.undoMove();
        board.move(f7, f5);
        PositionTree currentMove = board.getMoveHistory().getCurrentPositionTree();

        MoveHistory history = board.getMoveHistory();

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        assertEquals(currentMove, parent.getVariations().get(2));
        history.promoteVariation();
        assertEquals(currentMove, parent.getVariations().get(1));
        assertEquals(currentMove, history.getCurrentPositionTree());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testDemoteVaritaionMovesLineDownOneLevel() throws Exception {
        Board board = new Board();
        board.move(e2, e4);
        PositionTree parent = board.getMoveHistory().getCurrentPositionTree();
        board.move(d7, d5);
        PositionTree d5 = board.getMoveHistory().getCurrentPositionTree();
        board.undoMove();
        board.move(e7, e5);
        board.undoMove();
        board.move(f7, f5);

        MoveHistory history = board.getMoveHistory();
        history.setCurrentPositionTree(d5);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        assertEquals(d5, parent.getVariations().get(0));
        history.demoteVariation();
        assertEquals(d5, parent.getVariations().get(1));
        assertEquals(d5, history.getCurrentPositionTree());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testDeleteCurrentMoveInStartPositionClearsVariations() throws Exception {
        MoveHistory history = new MoveHistory();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.setCurrentPositionTree(history.getCurrentPositionTree().getParentTree());
        history.addMove(
                new Move(d2, d4),
                new Fen("rnbqkbnr/pppppppp/8/8/3P4/8/PPPP1PPP/RNBQKBNR b KQkq d3 0 1"),
                "1.d4");
        history.setCurrentPositionTree(history.getCurrentPositionTree().getParentTree());

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.deleteCurrentMove();
        assertEquals(0, history.getCurrentPositionTree().getVariations().size());
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testDeleteCurrentMoveInEmptyHistoryDoesNothing() throws Exception {
        MoveHistory history = new MoveHistory();
        PositionTree move = history.getCurrentPositionTree();
        history.deleteCurrentMove();
        assertEquals(move, history.getCurrentPositionTree());
    }

    public void testDeleteCurrentMove() throws Exception {
        MoveHistory history = new MoveHistory();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        PositionTree result = history.getCurrentPositionTree();
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.deleteCurrentMove();
        assertEquals(result, history.getCurrentPositionTree());
        assertEquals(0, history.getCurrentPositionTree().getVariations().size());
    }

    public void testDeleteCurrentMoveFiresHistoryChangedEvent() throws Exception {
        MoveHistory history = new MoveHistory();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.deleteCurrentMove();
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testThreeMoveRepetitionWithNoMoves() throws Exception {
        MoveHistory history = new MoveHistory();
        assertFalse(history.isThreeMoveRepetition());
    }

    public void testMoveMatchingExistingVariationDoesNotProduceDuplicates() {
        MoveHistory history = new MoveHistory();
        PositionTree start = history.getCurrentPositionTree();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.setCurrentPositionTree(start);
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");

        assertEquals(1, start.getVariations().size());
    }

    public void testMoveMatchingExistingVariationFiresHistoryChangedEvent() {
        MoveHistory history = new MoveHistory();
        PositionTree start = history.getCurrentPositionTree();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.setCurrentPositionTree(start);

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        assertEquals(1, listener.getHistorySelectionChangedCount());
        assertEquals(0, listener.getHistoryChangedCount());
    }

    public void testGetCurrentVariation() {
        MoveHistory history = new MoveHistory();
        PositionTree start = history.getCurrentPositionTree();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        PositionTree one = history.getCurrentPositionTree();

        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");
        history.setCurrentPositionTree(history.getCurrentPositionTree().getParentTree());
        history.addMove(
                new Move(e7, e6),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e6");
        PositionTree two = history.getCurrentPositionTree();

        history.addMove(
                new Move(d2, d3),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "2.d3");
        history.setCurrentPositionTree(two);

        List<PositionTree> variation = history.getCurrentVariation();
        assertSame(start, variation.get(0));
        assertSame(one, variation.get(1));
        assertSame(history.getCurrentPositionTree(), variation.get(2));
        assertEquals(3, variation.size());
    }

    public void testAddMoveAddsVariationToPreviousMoveElement() throws Exception {
        MoveHistory history = new MoveHistory();
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        assertEquals(1, history.getCurrentPositionTree().getParentTree().getVariations().size());
        assertSame(history.getCurrentPositionTree(), history.getCurrentPositionTree().getParentTree().getVariations().get(0));
    }

    public void testChangeCurrentMoveFiresHistoryChangedEvent() throws Exception {
        MoveHistory history = new MoveHistory();

        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);
        history.setCurrentPositionTree(history.getCurrentPositionTree().getParentTree());
        assertEquals(1, listener.getHistorySelectionChangedCount());
        assertEquals(0, listener.getHistoryChangedCount());
    }

    public void testChangeCurrentMoveInMoveHistory() throws Exception {
        MoveHistory history = new MoveHistory();

        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        history.setCurrentPositionTree(history.getCurrentPositionTree().getParentTree());
        assertEquals(new Move(e2, e4), history.getLastMove());
    }

    public void testMoveHistoryListenerCallback() throws Exception {
        MoveHistory history = new MoveHistory();
        SimpleHistoryListener listener = new SimpleHistoryListener();
        history.addMoveHistoryListener(listener);

        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        assertEquals(1, listener.getHistoryChangedCount());

        history.removeMoveHistoryListener(listener);
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");
        assertEquals(1, listener.getHistoryChangedCount());
    }

    public void testGetLastMove() throws Exception {
        MoveHistory history = new MoveHistory();
        assertNull(history.getLastMove());

        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        assertEquals(new Move(e7, e5), history.getLastMove());
    }

    public void testIterateOverMoves() throws Exception {
        MoveHistory history = new MoveHistory();
        Board board = new Board();

        board.move(e2, e4);
        history.addMove(
                new Move(e2, e4),
                new Fen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"),
                "1.e4");

        board.move(e7, e5);
        history.addMove(
                new Move(e7, e5),
                new Fen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e3 0 2"),
                "e5");

        Iterator<PositionTree> moves = history.getCurrentVariation().iterator();
        assertNull(moves.next().getLastMove());
        assertEquals(new Move(e2, e4), moves.next().getLastMove());
        assertEquals(new Move(e7, e5), moves.next().getLastMove());
        assertFalse(moves.hasNext());
    }
}

class SimplePositionListener implements PositionTreeListener {

    private final List<PositionTree> positions = new ArrayList<PositionTree>();

    @Override
    public void acceptPositionTree(PositionTree position) {
        positions.add(position);
    }

    public List<PositionTree> getPositions() {
        return positions;
    }
}
