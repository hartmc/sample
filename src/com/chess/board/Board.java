package com.chess.board;

import com.chess.board.ECO;
//import com.chess.game.ECOHelper;
//import com.chess.util.TextTransfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.chess.board.Color.Black;
import static com.chess.board.Color.White;
import static com.chess.board.File.*;
import static com.chess.board.PieceType.*;
import static com.chess.board.Rank.eighth;
import static com.chess.board.Rank.first;
import static com.chess.board.Side.Kingside;
import static com.chess.board.Square.*;
import static com.chess.board.ECO.A00;

@SuppressWarnings("SynchronizeOnNonFinalField")
public class Board implements MoveHistoryListener, Serializable, BoardGenerator {

    private static final long serialVersionUID = 1L;

    private final MoveHistory moveHistory;
    private Rules rules = new ChessRules();

    private transient List<BoardListener> boardListeners = new ArrayList<BoardListener>();

    public Board() {
        this(MoveHistory.getDefaultFen());
    }

    public ECO getECO() {
        return moveHistory.getECO();
    }

    public Board(Fen fen) {
        moveHistory = new MoveHistory(fen);
        moveHistory.addMoveHistoryListener(this);
    }

    public Board(Position position, Color color) {
        Fen fen = Fen.buildPartialFenFromPosition(position).setTurn(color);
        this.moveHistory = new MoveHistory(fen);
        moveHistory.addMoveHistoryListener(this);
    }

    @Override
    public String toString() {
        return getFen().toString() + (getLastMove() != null ? " " + getLastMove() : "");
    }

    public void undoMove() {
        moveHistory.undoMove();
    }

    public PositionTree getPositionTree(Fen position) {
        PositionTree start = getMoveHistory().getInitialPosition();
        int diff = start.getFen().getHalfMoveDifferenceTo(position);
        if (diff < 0)
            return null;

        Collection<PositionTree> positions = new ArrayList<PositionTree>();
        searchRecursive(diff, start, positions);

        for (PositionTree tree : positions) {
            if (tree.getFen().equals(position))
                return tree;
        }

        return null;
    }

    private void searchRecursive(int counter, PositionTree position, Collection<PositionTree> positions) {
        if (counter == 0) {
            positions.add(position);
            return;
        }

        List<PositionTree> variations = position.getVariations();
        if (variations.size() == 0)
            return;

        for (PositionTree variation : variations) {
            searchRecursive(counter - 1, variation, positions);
        }
    }

    public void redoMove() {
        moveHistory.redoMove();
    }

    public MoveHistory getMoveHistory() {
        return moveHistory;
    }

    public boolean isLegal(Move move) {
        return rules.isLegal(move, this);
    }

    public void moveAlgebraic(String moveString) throws IllegalMoveException {
        final String initialString = moveString;
        try {
            StrippedMoveString strippedString = stripMoveString(moveString);
            moveString = strippedString.getMoveString();

            Move move;
            if (moveString.startsWith("O") || moveString.startsWith("0")) {
                if (moveString.length() == 5) {
                    Rank firstRank = first.fromPerspective(getTurn());
                    move = new Move(Square.getSquare(e, firstRank), Square.getSquare(c, firstRank));
                }
                else {
                    Rank firstRank = first.fromPerspective(getTurn());
                    move = new Move(Square.getSquare(e, firstRank), Square.getSquare(g, firstRank));
                }
            }
            else {
                Piece piece = parsePiece(moveString.charAt(0));
                Square endSquare = parseEndSquare(moveString);

                if (piece.getPieceType() == Pawn) {
                    File pawnStartFile = File.valueOf(String.valueOf(moveString.charAt(0)));
                    Square startSquare = Square.getSquare(
                            pawnStartFile,
                            endSquare.getRank().getRelativeRank(-1 * getTurn().multiplier()));
                    if (getPieceAt(startSquare) == null)
                        startSquare = startSquare.getRelativeSquare(0, -1 * getTurn().multiplier());
                    move = new Move(startSquare, endSquare);
                    if (endSquare.getRank().fromPerspective(getTurn()) == eighth) {
                        Piece promotionPiece = parsePiece(moveString.charAt(moveString.length() - 1));
                        move.setPromotion(Promotion.valueOf(promotionPiece.getPieceType().toString()));
                    }
                }
                else {
                    move = chooseMove(getCandidateMoves(piece, endSquare), moveString.charAt(1), moveString);
                }
            }

            move(move);
            PositionTree currentMove = getMoveHistory().getCurrentPositionTree();
            for (NAG nag : strippedString) {
                currentMove.addNAG(nag);
            }
        }
        catch (RuntimeException re) {
            throw new RuntimeException("failed token: \"" + initialString + "\"", re);
        }
    }

    private Move chooseMove(List<Move> candidateMoves, char differentiator, String moveString) throws IllegalMoveException {
        if (candidateMoves.size() == 0)
            throw new IllegalMoveException(moveString + " is not a legal move. " + getFen());

        if (candidateMoves.size() == 1)
            return candidateMoves.get(0);

        Move firstMove = candidateMoves.get(0);
        Move secondMove = candidateMoves.get(1);
        if (Character.isLetter(differentiator)) {
            if (firstMove.getStartSquare().getFile().toString().charAt(0) == differentiator)
                return firstMove;
            else
                return secondMove;
        }
        else if (firstMove.getStartSquare().getRank().number() == differentiator - '0') {
            return firstMove;
        }
        else {
            return secondMove;
        }
    }

    private List<Move> getCandidateMoves(Piece piece, Square endSquare) {
        List<Move> result = new ArrayList<Move>();
        for (Move move : getLegalMovesEndingOn(endSquare)) {
            if (getPieceAt(move.getStartSquare()) == piece) {
                result.add(move);
            }
        }

        return result;
    }

    private Piece parsePiece(char pieceChar) {
        switch (pieceChar) {
            case 'K':
                return Piece.getPiece(getTurn(), King);
            case 'R':
                return Piece.getPiece(getTurn(), Rook);
            case 'N':
                return Piece.getPiece(getTurn(), Knight);
            case 'B':
                return Piece.getPiece(getTurn(), Bishop);
            case 'Q':
                return Piece.getPiece(getTurn(), Queen);
            default:
                return Piece.getPiece(getTurn(), Pawn);
        }
    }

    private Square parseEndSquare(String moveString) {
        if (moveString.length() < 4 && Character.isDigit(moveString.charAt(1))) {
            return Square.valueOf(moveString.substring(0, 2));
        }

        int capture = moveString.indexOf('x');
        if (capture > 0) {
            return Square.valueOf(moveString.substring(capture + 1, capture + 3));
        }
        else {
            try {
                if (moveString.length() >= 5) {
                    try {
                        // see if this is long algebraic notation
                        return Square.valueOf(moveString.substring(3, 5));
                    }
                    catch (IllegalArgumentException iae) {
                        // do nothing, drop through to standard case
                    }
                }

                // > 90% of the cases fit here
                return Square.valueOf(moveString.substring(1, 3));
            }
            catch (IllegalArgumentException iae) {
                // this happens when there are two pieces that can move to the same square and they must
                // be differentiated.
                return Square.valueOf(moveString.substring(2, 4));
            }
        }
    }

    private StrippedMoveString stripMoveString(String moveString) {
        List<NAG> nags = new ArrayList<NAG>();
        for (NAG nag : NAG.values()) {
            if (nag.getDescription().length() > 0 && moveString.contains(nag.getDescription())) {
                moveString = moveString.replace(nag.getDescription(), "");
                nags.add(nag);
            }
        }

        moveString = moveString.replace("+", "");
        moveString = moveString.replace("#", "");
        return new StrippedMoveString(moveString, nags);
    }

    public Board move(Move move) throws IllegalMoveException {
        rules.assertLegal(move, this);

        Fen newFen = createFen(move);
        String moveDescription = getPartialMoveDescription(move);
        Color nextTurn = getTurn().getOppositeColor();
        boolean isMate = rules.isCheckMate(new Board(newFen));

        if (isMate)
            moveDescription += "#";
        else if (newFen.getPosition().isInCheck(nextTurn))
            moveDescription += "+";

        moveHistory.addMove(move, newFen, moveDescription);

        if (isMate) {
            fireMateEvent();
        }
        else {
            DrawType draw = rules.getDrawType(this);
            if (draw != null) {
                fireDrawEvent(draw);
            }
        }

        return this;
    }

    private Fen createFen(Move move) {
        int totalFullMoves = getFen().getFullMoveCount();
        if (getTurn() == Black)
            totalFullMoves++;
        int totalHalfMovesToDraw = getFen().getHalfMovesSinceLastPawnMoveOrCapture();
        Square enPassantSquare = null;
        final Piece piece = getPosition().get(move.getStartSquare());
        if (piece.getPieceType() == Pawn) {
            int rankDiff = Math.abs(move.getStartSquare().getRank().ordinal() - move.getEndSquare().getRank().ordinal());
            if (rankDiff == 2) {
                enPassantSquare = move.getEndSquare().getRelativeSquare(0, -getTurn().multiplier());
            }

            totalHalfMovesToDraw = 0;
        }
        else {
            totalHalfMovesToDraw++;
        }

        if (getPosition().get(move.getEndSquare()) != null) {
            totalHalfMovesToDraw = 0;
        }

        Fen result = Fen.buildPartialFenFromPosition(rules.move(move, getPosition()));
        result = result.setCanWhiteCastleKingside(getFen().canWhiteCastleKingside() && move.getStartSquare() != h1
                                                  && move.getStartSquare() != e1);
        result = result.setCanWhiteCastleQueenside(getFen().canWhiteCastleQueenside() && move.getStartSquare() != a1
                                                   && move.getStartSquare() != e1);
        result = result.setCanBlackCastleKingside(getFen().canBlackCastleKingside() && move.getStartSquare() != h8
                                                  && move.getStartSquare() != e8);
        result = result.setCanBlackCastleQueenside(getFen().canBlackCastleQueenside() && move.getStartSquare() != a8
                                                   && move.getStartSquare() != e8);
        result = result.setTurn(getTurn().getOppositeColor());
        result = result.setFullMoveCounter(totalFullMoves);
        result = result.setDrawHalfMoveCounter(totalHalfMovesToDraw);
        result = result.setEnPassantSquare(enPassantSquare);

        return result;
    }

    private String getPartialMoveDescription(Move move) {
        StringBuilder result = new StringBuilder();
        if (getTurn() == White) {
            result.append(getFen().getFullMoveCount()).append(".");
        }

        Piece startPiece = getPosition().get(move.getStartSquare());

        int fileDiff = Math.abs(move.getEndSquare().getFile().ordinal() - move.getStartSquare().getFile().ordinal());
        if (startPiece.getPieceType() == King && fileDiff > 1) {
            if (move.getEndSquare().getFile() == g)
                result.append("O-O");
            else
                result.append("O-O-O");
        }
        else {
            // first, we notate the piece that is moving (except for pawns)
            result.append(startPiece.getPieceType().getAlgebraicDescription());

            // if the piece start square is ambiguous, we add a partial description
            // of the start square
            Square ambiguousSquare = findAmbigousSquare(move, startPiece);
            if (ambiguousSquare != null) {
                result.append(removeAmbiguity(move.getStartSquare(), ambiguousSquare));
            }

            // now we add an "x" if there was a capture
            Piece capturePiece = getPosition().get(move.getEndSquare());
            boolean isEnPassant = capturePiece == null && startPiece.getPieceType() == Pawn && fileDiff > 0;
            if (capturePiece != null || isEnPassant) {
                if (startPiece.getPieceType() == Pawn) {
                    result.append(move.getStartSquare().getFile());
                }
                result.append("x");
            }

            // next, we write the end square
            result.append(move.getEndSquare());

            // finally, we add any needed +,#,=Q, or ep. to denote special moves
            if (startPiece.getPieceType() == Pawn && move.getEndSquare().getRank().fromPerspective(getTurn()) == eighth) {
                result.append("=").append(move.getPromotionPiece().getPieceType().getAlgebraicDescription());
            }
            else if (isEnPassant) {
                result.append(" ep.");
            }
        }

        return result.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((moveHistory == null) ? 0 : moveHistory.hashCode());
        result = prime * result + ((rules == null) ? 0 : rules.hashCode());
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
        Board other = (Board) obj;
        if (moveHistory == null) {
            if (other.moveHistory != null)
                return false;
        }
        else if (!moveHistory.equals(other.moveHistory))
            return false;
        if (rules == null) {
            if (other.rules != null)
                return false;
        }
        else if (!rules.equals(other.rules))
            return false;
        return true;
    }

    private String removeAmbiguity(Square startSquare, Square ambiguousSquare) {
        if (startSquare.getFile() != ambiguousSquare.getFile())
            return startSquare.getFile().toString();
        else
            return String.valueOf(startSquare.getRank().number());
    }

    private Square findAmbigousSquare(Move move, Piece startPiece) {
        // pawn captures are never ambiguous
        if (startPiece.getPieceType() == Pawn)
            return null;

        Collection<Move> legalMoves = getLegalMovesEndingOn(move.getEndSquare());
        for (Move possibleMove : legalMoves) {
            if (!move.equals(possibleMove) && getPosition().get(possibleMove.getStartSquare()) == startPiece) {
                return possibleMove.getStartSquare();
            }
        }

        return null;
    }

    public Fen getFen() {
        return moveHistory.getCurrentPositionTree().getFen();
    }

    private void fireDrawEvent(DrawType draw) {
        List<BoardListener> listeners = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            listeners.addAll(boardListeners);
        }

        for (BoardListener listener : listeners) {
            listener.gameDrawn(this, draw);
        }
    }

    private void fireMateEvent() {
        List<BoardListener> listeners = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            listeners.addAll(boardListeners);
        }

        for (BoardListener listener : listeners) {
            listener.checkMate(this);
        }
    }

    public Move getLastMove() {
        return moveHistory.getLastMove();
    }

    public String getLastMoveDescription() {
        return moveHistory.getCurrentPositionTree().getMoveDescription();
    }

    public Color getTurn() {
        return getFen().getTurn();
    }

    public Piece getPieceAt(Square square) {
        return getPosition().get(square);
    }

    public boolean isCheck() {
        return getPosition().isInCheck(getTurn());
    }

    public Board move(Square start, Square end) throws IllegalMoveException {
        return move(new Move(start, end));
    }

    public Board move(Square start, Square end, Promotion piece) throws IllegalMoveException {
        return move(new Move(start, end, piece));
    }

    public Board setRules(Rules rules) {
        this.rules = rules;
        return this;
    }

    public Position getPosition() {
        return getFen().getPosition();
    }

    public void addBoardListener(BoardListener listener) {
        synchronized (boardListeners) {
            boardListeners.add(listener);
        }
    }

    public void removeBoardListener(BoardListener listener) {
        synchronized (boardListeners) {
            boardListeners.remove(listener);
        }
    }

    public boolean canAnyLegalMoveBeginOn(Square square) {
        return rules.canAnyLegalMoveStartOn(square, this);
    }

    public boolean canAnyLegalMoveEndOn(Square square) {
        return rules.canAnyLegalMoveEndOn(square, this);
    }

    public Collection<Move> getLegalMovesEndingOn(Square square) {
        return rules.getLegalMovesEndingOn(square, this);
    }

    public Collection<Move> getLegalMovesStartingOn(Square square) {
        return rules.getLegalMovesStartingOn(square, this);
    }

    public int getHalfMovesSinceLastPawnMoveOrCapture() {
        return getFen().getHalfMovesSinceLastPawnMoveOrCapture();
    }

    @Override
    public void moveHistoryChanged(MoveHistory moveHistory) {
        fireMoveHistoryChanged();
    }

    public void suspendCalculateECO() {
        moveHistory.suspendCalculateECO();
    }

    public void resumeCalculateECO() {
        moveHistory.resumeCalculateECO();
    }

    @Override
    public void moveHistorySelectionChanged(MoveHistory moveHistory) {
        fireMoveHistorySelectionChanged();
    }

    public void fireMoveHistoryChanged() {
        List<BoardListener> listeners = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            listeners.addAll(boardListeners);
        }

        for (BoardListener listener : listeners) {
            listener.moveHistoryChanged(getMoveHistory());
        }
    }

    public void fireMoveHistorySelectionChanged() {
        List<BoardListener> listeners = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            listeners.addAll(boardListeners);
        }

        for (BoardListener listener : listeners) {
            listener.moveHistorySelectionChanged(getMoveHistory());
        }
    }

    public void reset() {
        moveHistory.setCurrentPositionTree(moveHistory.getInitialPosition());
    }

    // "magic" method is called when object is deserialized
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boardListeners = new ArrayList<BoardListener>();
        getMoveHistory().addMoveHistoryListener(this);
        resumeCalculateECO();
    }

    public Board copyPosition() {
        return new Board(getFen());
    }
/*
    comment out for code sample so don't have to import TextTransfer

    public void pasteFEN() throws FENFormatException {
        PositionTree element = new PositionTree(null, null, new Fen(new TextTransfer().getClipboardContents()), "start position");
        getMoveHistory().setCurrentPositionTree(element);
    }

    public void copyFEN() {
        new TextTransfer().setClipboardContents(getFen().toString());
    }
*/
    public boolean canCastleTo(Side side) {
        if (getTurn() == White) {
            return side == Kingside ? getFen().canWhiteCastleKingside() : getFen().canWhiteCastleQueenside();
        }
        else {
            return side == Kingside ? getFen().canBlackCastleKingside() : getFen().canBlackCastleQueenside();
        }
    }

    public Square getEnPassantSquare() {
        return getFen().getEnPassantSquare();
    }

    public Move getNextMainLineMove() {
        if (getMoveHistory().getCurrentPositionTree().getVariations().size() > 0)
            return getMoveHistory().getCurrentPositionTree().getVariations().get(0).getLastMove();
        return null;
    }

    public String getNextMainLineMoveDescription() {
        if (getMoveHistory().getCurrentPositionTree().getVariations().size() > 0) {
            String description = getMoveHistory().getCurrentPositionTree().getVariations().get(0).getMoveDescription();
            if (getTurn() == Black)
                description = getFen().getFullMoveCount() + "..." + description;

            return description;
        }

        return null;
    }

    public void clearAll() {
        if (moveHistory.getInitialPosition().getVariations().size() > 0) {
            Fen start = moveHistory.getInitialPosition().getFen();
            moveHistory.setCurrentPositionTree(new Board(start).getMoveHistory().getCurrentPositionTree());
        }
    }

    public void drawOffered() {
        List<BoardListener> copy = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            copy.addAll(boardListeners);
        }

        for (BoardListener listener : copy) {
            listener.offerDraw(this);
        }
    }

    public void resign() {
        List<BoardListener> copy = new ArrayList<BoardListener>();
        synchronized (boardListeners) {
            copy.addAll(boardListeners);
        }

        for (BoardListener listener : copy) {
            listener.resign(this);
        }
    }

    public void toEndOfMainLine() {
        moveHistory.toEndOfMainLine();
    }

    @Override
    public Board getBoard() {
        return new Board(getFen());
    }

    public void setFen(Fen fen) {
        Board board = new Board(fen);
        moveHistory.setCurrentPositionTree(board.getMoveHistory().getCurrentPositionTree());
    }
}

class StrippedMoveString implements Iterable<NAG> {

    private final String moveString;
    private final Collection<NAG> nags;

    public StrippedMoveString(String moveString, Collection<NAG> nags) {
        this.moveString = moveString;
        this.nags = nags;
    }

    public String getMoveString() {
        return moveString;
    }

    @Override
    public Iterator<NAG> iterator() {
        return nags.iterator();
    }
}

class ECOPositionListener implements PositionTreeListener {

    private ECO eco = A00;


    @Override
    public void acceptPositionTree(PositionTree position) {
        /* comment out so don't have to import ECOHelper for code sample
        ECO eco = ECOHelper.getECOFromFen(position.getFen());
        if (eco != null)
            this.eco = eco;
        */
    }

    public ECO getECO() {
        return eco;
    }
}
