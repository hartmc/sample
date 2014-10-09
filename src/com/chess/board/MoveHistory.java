package com.chess.board;

//import com.chess.application.ChessSoundFactory;
import com.chess.board.ECO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.chess.board.ECO.A00;

@SuppressWarnings("SynchronizeOnNonFinalField")
public class MoveHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum VariationType {
        Overwrite, MainVariation, SubVariation, Insert
    }

    private transient List<MoveHistoryListener> listeners = new ArrayList<MoveHistoryListener>();
    private transient ECO eco = A00;
    private transient boolean calculateECO = true;
    private PositionTree currentMove = null;

    public MoveHistory() {
        this(getDefaultFen());
    }

    public MoveHistory(Fen fenEncoding) {
        this(new PositionTree(null, null, fenEncoding, null));
    }

    public MoveHistory(PositionTree startElement) {
        this.currentMove = startElement;
        calculateECO();
    }

    public void addMoveHistoryListener(MoveHistoryListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeMoveHistoryListener(MoveHistoryListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public static Fen getDefaultFen() {
        return new Fen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public void addMove(Move move, Fen fen, String description) {
        PositionTree newMoveElement = new PositionTree(currentMove, move, fen, description);
        PositionTree existing = currentMove.getVariation(move);
        if (existing != null) {
            if (currentMove.getVariations().get(0) != existing) {
                currentMove.setShowVariations(true);
            }
            else {
                currentMove.setShowLine(true);
            }

            currentMove = existing;
            fireSelectionChanged();
        }
        else {
            currentMove.setShowVariations(true);
            currentMove.addVariation(newMoveElement);
            currentMove = newMoveElement;

            if (currentMove.isMainLine() && currentMove.getParentTree().getVariations().size() == 1)
                calculateECO();

            fireHistoryChanged();
        }
    }

    public void fireHistoryChanged() {
        List<MoveHistoryListener> copy = new ArrayList<MoveHistoryListener>();
        synchronized (listeners) {
            copy.addAll(listeners);
        }

        for (MoveHistoryListener listener : copy) {
            listener.moveHistoryChanged(this);
        }
    }

    public void fireSelectionChanged() {
        List<MoveHistoryListener> copy = new ArrayList<MoveHistoryListener>();
        synchronized (listeners) {
            copy.addAll(listeners);
        }

        for (MoveHistoryListener listener : copy) {
            listener.moveHistorySelectionChanged(this);
        }
    }

    public Move getLastMove() {
        if (currentMove == null) {
            return null;
        }
        return currentMove.getLastMove();
    }

    public boolean isThreeMoveRepetition() {
        if (currentMove == null)
            return false;

        int count = 0;
        Fen currentFen = currentMove.getFen();
        for (PositionTree move : getCurrentVariation()) {
            if (move.getFen().equalsIgnoreMoveCount(currentFen)) {
                count++;
            }
        }

        return count > 2;
    }

    public List<PositionTree> getCurrentVariation() {
        List<PositionTree> variation = new ArrayList<PositionTree>();
        PositionTree next = currentMove;
        while (next != null) {
            variation.add(0, next);
            next = next.getParentTree();
        }

        return variation;
    }

    public void setCurrentPositionTree(PositionTree moveElement) {
        PositionTree current = currentMove;
        currentMove = moveElement;

        PositionTree nextTree = currentMove;
        while (nextTree.getParentTree() != null) {
            if (nextTree.getParentTree().getVariations().get(0) != nextTree) {
                nextTree.getParentTree().setShowVariations(true);
            }
            else if (!nextTree.getParentTree().isMainLineVisible()) {
                nextTree.getParentTree().setShowLine(true);
            }
            nextTree = nextTree.getParentTree();
        }

        if (current.containsInTree(moveElement)) {
            fireSelectionChanged();
        }
        else {
            calculateECO();
            fireHistoryChanged();
        }
    }

    public PositionTree getCurrentPositionTree() {
        return currentMove;
    }

    public void deleteCurrentMove() {
        if (currentMove.getParentTree() == null) {
            currentMove.clearVariations();
        }
        else {
            currentMove.getParentTree().deleteVariation(currentMove);
            currentMove = currentMove.getParentTree();
        }

        if (currentMove.isMainLine())
            calculateECO();

        fireHistoryChanged();
    }

    public void promoteVariation() {
        PositionTree child = currentMove;
        while (child.getParentTree() != null && child.getParentTree().getVariations().size() < 2) {
            child = child.getParentTree();
        }

        if (child.getParentTree() == null)
            return;

        child.getParentTree().promoteVariation(child);
        calculateECO();
        fireHistoryChanged();
    }

    public void demoteVariation() {
        PositionTree child = currentMove;
        while (child.getParentTree() != null && child.getParentTree().getVariations().size() < 2) {
            child = child.getParentTree();
        }

        if (child.getParentTree() == null)
            return;

        child.getParentTree().demoteVariation(child);
        calculateECO();
        fireHistoryChanged();
    }

    public void toggleVariationVisibility() {
        PositionTree tree = getCurrentPositionTree();
        if (tree.getVariations().size() > 1) {
            tree.setShowVariations(!tree.isVariationVisible());
            fireSelectionChanged();
        }
    }

    public void toggleLineVisibility() {
        PositionTree tree = getCurrentPositionTree();
        tree.setShowLine(!tree.isMainLineVisible());
        fireSelectionChanged();
    }

    public void setComment(String comment) {
        getCurrentPositionTree().setComment(comment);
        fireHistoryChanged();
    }

    public void nextMoveQualityNAG(PositionTree element, boolean forward) {
        NAG currentNag = NAG.chooseMoveQualityNag(element.getNAGs());
        element.removeNAG(currentNag);
        element.addNAG(NAG.nextMoveQualityNAG(currentNag, forward));
        fireHistoryChanged();
    }

    public void nextPositionEvaluationNAG(PositionTree element, boolean forward) {
        NAG currentNag = NAG.choosePositionEvaluationNag(element.getNAGs());
        element.removeNAG(currentNag);
        element.addNAG(NAG.nextPositionEvaluationNAG(currentNag, forward));
        fireHistoryChanged();
    }

    public void replaceNag(List<NAG> nagListToRemove, NAG nagToAdd) {
        boolean changed = false;
        PositionTree element = getCurrentPositionTree();

        for (NAG nag : nagListToRemove) {
            if (nag != nagToAdd && element.getNAGs().contains(nag)) {
                element.removeNAG(nag);
                changed = true;
            }
        }

        if (nagToAdd != null && !element.getNAGs().contains(nagToAdd)) {
            element.addNAG(nagToAdd);
            changed = true;
        }

        if (changed) {
            fireHistoryChanged();
        }
    }

    public void toggleNAG(NAG nag) {
        PositionTree element = getCurrentPositionTree();
        if (element.getNAGs().contains(nag)) {
            element.removeNAG(nag);
        }
        else {
            element.addNAG(nag);
        }

        fireHistoryChanged();
    }

    public void addNAG(NAG nag) {
        if (getCurrentPositionTree().addNAG(nag))
            fireHistoryChanged();
    }

    public void reset() {
        PositionTree element = getCurrentPositionTree();
        while (element.getParentTree() != null)
            element = element.getParentTree();
        setCurrentPositionTree(element);
    }

    public List<Move> getMainLine() {
        List<Move> result = new ArrayList<Move>();
        PositionTree parent = getCurrentPositionTree().getInitialPosition();

        while (parent.getVariations().size() > 0) {
            PositionTree mainLine = parent.getVariations().get(0);
            result.add(mainLine.getLastMove());
            parent = mainLine;
        }

        return result;
    }

    public PositionTree getInitialPosition() {
        return getCurrentPositionTree().getInitialPosition();
    }

    public void iterateMainLine(PositionTreeListener positionListener) {
        PositionTree position = getInitialPosition();
        while (position.getVariations().size() > 0) {
            positionListener.acceptPositionTree(position);
            position = position.getVariations().get(0);
        }
        positionListener.acceptPositionTree(position);
    }

    @Override
    public String toString() {
        List<Move> moves = getMainLine();
        moves = moves.subList(0, Math.min(moves.size(), 4));
        StringBuilder buffer = new StringBuilder("moves:");
        for (Move move : moves)
            buffer.append(" ").append(move);
        return buffer.toString();
    }

    // "magic" method gets called when object is deserialized
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        listeners = new ArrayList<MoveHistoryListener>();
        calculateECO();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentMove == null) ? 0 : currentMove.hashCode());
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
        MoveHistory other = (MoveHistory) obj;
        if (currentMove == null) {
            if (other.currentMove != null)
                return false;
        }
        else if (!currentMove.equals(other.currentMove))
            return false;
        return true;
    }

    public void redoMove() {
        PositionTree currentMoveElement = getCurrentPositionTree();
        if (currentMoveElement == null) {
            return;
        }

        if (currentMoveElement.getVariations().size() > 0) {
            PositionTree nextMoveElement = currentMoveElement.getVariations().get(0);
            setCurrentPositionTree(nextMoveElement);
           // ChessSoundFactory.singleton().move(nextMoveElement.getMoveDescription());
        }
    }

    public void undoMove() {
        PositionTree currentMoveElement = getCurrentPositionTree();
        PositionTree previousMoveElement = currentMoveElement.getParentTree();
        if (previousMoveElement == null) {
            return;
        }

        setCurrentPositionTree(previousMoveElement);
    }

    public String getComment() {
        return getCurrentPositionTree().getComment();
    }

    public void prependComment(String newComment) {
        if (newComment == null)
            return;

        if (getComment() == null || getComment().trim().length() == 0) {
            setComment(newComment.trim());
            return;
        }

        newComment = newComment.trim();

        String currentComment = getComment().trim();
        if (currentComment.contains(newComment))
            return;

        if (currentComment.length() == 0) {
            setComment(newComment);
        }
        else if (Character.isUpperCase(currentComment.charAt(0)) || Character.isDigit(currentComment.charAt(0))) {
            if (!newComment.endsWith("."))
                newComment += ". ";
            else
                newComment += " ";

            setComment(newComment + currentComment);
        }
        else {
            boolean endsWithPeriod = newComment.endsWith(".");
            if (endsWithPeriod)
                newComment = newComment.substring(0, newComment.length() - 1);

            setComment(newComment.trim() + " and " + currentComment + (endsWithPeriod ? "." : ""));
        }
    }

    public void appendComment(String comment) {
        getCurrentPositionTree().appendComment(comment);
        fireHistoryChanged();
    }

    public void deleteWord() {
        if (getCurrentPositionTree().deleteWord()) {
            fireHistoryChanged();
        }
    }

    public ECO getECO() {
        return eco;
    }

    private void calculateECO() {
        if (calculateECO) {
            ECOPositionListener listener = new ECOPositionListener();
            iterateMainLine(listener);
            this.eco = listener.getECO();
        }
    }

    public void suspendCalculateECO() {
        this.calculateECO = false;
    }

    public void resumeCalculateECO() {
        this.calculateECO = true;
        calculateECO();
    }

    public void toEndOfMainLine() {
        PositionTree root = getEndOfMainLine();

        if (root != getCurrentPositionTree()) { // == is ok here
            setCurrentPositionTree(root);
        }
    }

    public PositionTree getEndOfMainLine() {
        PositionTree root = getInitialPosition();
        while (root.getVariations().size() > 0) {
            root = root.getVariations().get(0);
        }
        return root;
    }

    public void clearVariations() {
        PositionTree position = getCurrentPositionTree();
        if (position.getVariations().size() > 0) {
            position.clearVariations();
            fireHistoryChanged();
        }
    }
}
