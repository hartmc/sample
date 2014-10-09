package com.chess.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * MoveElement is a Move placed in the context of a game, so it knows things like whether or not each side can castle, how many
 * moves toward the 50-move draw have been played, whether or not capturing e.p. is possible, etc.
 */
public class PositionTree implements Serializable {

    private static final long serialVersionUID = 1L;

    // the FEN encoding of the board after the given move was played
    private final Fen fen;
    private final Move lastMove;
    private final String description;
    private final List<PositionTree> variations = new ArrayList<PositionTree>();
    private PositionTree parent;
    private String comment;
    private final SortedSet<NAG> nagList = new TreeSet<NAG>();
    private boolean showVariations = true;
    private boolean showLine = true;

    public PositionTree(PositionTree parent, Move move, Fen fen, String description) {
        this.parent = parent;
        this.lastMove = move;
        this.fen = fen;
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((fen == null) ? 0 : fen.hashCode());
        result = prime * result + ((lastMove == null) ? 0 : lastMove.hashCode());
        result = prime * result + nagList.hashCode();
        result = prime * result + (showLine ? 1231 : 1237);
        result = prime * result + (showVariations ? 1231 : 1237);
        result = prime * result + variations.hashCode();
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
        PositionTree other = (PositionTree) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        }
        else if (!comment.equals(other.comment))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        }
        else if (!description.equals(other.description))
            return false;
        if (fen == null) {
            if (other.fen != null)
                return false;
        }
        else if (!fen.equals(other.fen))
            return false;
        if (lastMove == null) {
            if (other.lastMove != null)
                return false;
        }
        else if (!lastMove.equals(other.lastMove))
            return false;
        if (!nagList.equals(other.nagList))
            return false;
        if (showLine != other.showLine)
            return false;
        if (showVariations != other.showVariations)
            return false;
        if (!variations.equals(other.variations))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return description == null ? "start position" : getMoveDescription();
    }

    public PositionTree getParentTree() {
        return parent;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Fen getFen() {
        return fen;
    }

    public String getMoveDescription() {
        return description;
    }

    public void addVariation(PositionTree element) {
        element.parent = this;
        variations.add(element);
    }

    public List<PositionTree> getVariations() {
        return Collections.unmodifiableList(variations);
    }

    public PositionTree getVariation(Move move) {
        synchronized (variations) {
            for (PositionTree element : variations) {
                if (element.getLastMove().equals(move)) {
                    return element;
                }
            }
        }

        return null;
    }

    public void deleteVariation(PositionTree move) {
        variations.remove(move);
    }

    public void clearVariations() {
        variations.clear();
    }

    public void demoteVariation(PositionTree child) {
        int index = variations.indexOf(child);
        if (index < variations.size() - 1) {
            variations.remove(child);
            variations.add(index + 1, child);
        }
    }

    public void promoteVariation(PositionTree child) {
        int index = variations.indexOf(child);
        if (index > 0) {
            variations.remove(child);
            variations.add(index - 1, child);
        }
    }

    public void setComment(String comment) {
        if ("".equals(comment))
            comment = null;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public SortedSet<NAG> getNAGs() {
        return nagList;
    }

    public boolean addNAG(NAG nag) {
        if (nag != null) {
            nagList.add(nag);
            return true;
        }

        return false;
    }

    public void removeNAG(NAG nag) {
        if (nag != null)
            nagList.remove(nag);
    }

    public PositionTree getInitialPosition() {
        PositionTree result = this;
        while (result.getParentTree() != null)
            result = result.getParentTree();
        return result;
    }

    public boolean isMainLine() {
        PositionTree element = this;
        while (element.getParentTree() != null) {
            if (element.getParentTree().getVariations().get(0) != element)
                return false;

            element = element.getParentTree();
        }

        return true;
    }

    public boolean containsInTree(PositionTree moveElement) {
        return containsInTree(getInitialPosition(), moveElement);
    }

    private boolean containsInTree(PositionTree toSearch, PositionTree moveElement) {
        if (toSearch == moveElement)
            return true;

        for (PositionTree variation : toSearch.getVariations()) {
            if (containsInTree(variation, moveElement))
                return true;
        }

        return false;
    }

    public void collapseSubTree() {
        setShowVariations(false);
        for (PositionTree tree : getVariations()) {
            tree.collapseSubTree();
        }
    }

    public void setShowLine(boolean showLine) {
        if (getParentTree() != null)
            this.showLine = showLine;
    }

    public boolean isMainLineVisible() {
        return showLine;
    }

    public boolean isVariationVisible() {
        return showVariations;
    }

    public void setShowVariations(boolean showVariations) {
        this.showVariations = showVariations;
    }

    public boolean deleteWord() {
        String copy = comment;
        if (copy == null)
            return false;

        StringTokenizer st = new StringTokenizer(copy.trim());
        StringBuilder newComment = new StringBuilder();
        String lastToken = st.nextToken();
        while (st.hasMoreTokens()) {
            newComment.append(lastToken).append(" ");
            lastToken = st.nextToken();
        }

        if (newComment.length() > 0) {
            setComment(newComment.toString().trim());
        }
        else {
            setComment(null);
        }

        return true;
    }

    public void appendComment(String kibitz) {
        setComment((getComment() != null ? getComment() + " " : "") + kibitz);
    }

    public Set<Move> getNextMoves() {
        Set<Move> moves = new HashSet<Move>();
        synchronized (variations) {
            for (PositionTree variation : variations) {
                moves.add(variation.getLastMove());
            }
        }

        return moves;
    }
}
