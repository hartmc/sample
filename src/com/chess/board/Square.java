package com.chess.board;

import static com.chess.board.File.a;
import static com.chess.board.File.b;
import static com.chess.board.File.c;
import static com.chess.board.File.d;
import static com.chess.board.File.e;
import static com.chess.board.File.f;
import static com.chess.board.File.g;
import static com.chess.board.File.h;
import static com.chess.board.Rank.eighth;
import static com.chess.board.Rank.fifth;
import static com.chess.board.Rank.first;
import static com.chess.board.Rank.fourth;
import static com.chess.board.Rank.second;
import static com.chess.board.Rank.seventh;
import static com.chess.board.Rank.sixth;
import static com.chess.board.Rank.third;

public enum Square {
    a1(a, first), a2(a, second), a3(a, third), a4(a, fourth), a5(a, fifth), a6(a, sixth), a7(a, seventh), a8(a, eighth), b1(
            b,
            first), b2(b, second), b3(b, third), b4(b, fourth), b5(b, fifth), b6(b, sixth), b7(b, seventh), b8(b, eighth), c1(
            c,
            first), c2(c, second), c3(c, third), c4(c, fourth), c5(c, fifth), c6(c, sixth), c7(c, seventh), c8(c, eighth), d1(
            d,
            first), d2(d, second), d3(d, third), d4(d, fourth), d5(d, fifth), d6(d, sixth), d7(d, seventh), d8(d, eighth), e1(
            e,
            first), e2(e, second), e3(e, third), e4(e, fourth), e5(e, fifth), e6(e, sixth), e7(e, seventh), e8(e, eighth), f1(
            f,
            first), f2(f, second), f3(f, third), f4(f, fourth), f5(f, fifth), f6(f, sixth), f7(f, seventh), f8(f, eighth), g1(
            g,
            first), g2(g, second), g3(g, third), g4(g, fourth), g5(g, fifth), g6(g, sixth), g7(g, seventh), g8(g, eighth), h1(
            h,
            first), h2(h, second), h3(h, third), h4(h, fourth), h5(h, fifth), h6(h, sixth), h7(h, seventh), h8(h, eighth);
    
    private File file;
    private Rank rank;
    
    private Square(File file, Rank rank) {
        this.file = file;
        this.rank = rank;
    }
    
    public boolean isBackRank() {
        return this.toString().endsWith("1") || this.toString().endsWith("8");
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public File getFile() {
        return file;
    }
    
    public static Square getSquare(File file, Rank rank) {
        if (file == null || rank == null)
            return null;
        return values()[8 * file.ordinal() + rank.ordinal()];
    }
    
    public boolean isDarkSquare() {
        return (file.ordinal() + rank.ordinal()) % 2 == 0;
    }
    
    public boolean isLightSquare() {
        return !isDarkSquare();
    }
    
    public Square getRelativeSquare(int fileOffset, int rankOffset) {
        File newFile = getFile().getRelativeFile(fileOffset);
        Rank newRank = getRank().getRelativeRank(rankOffset);
        if (newFile == null || newRank == null) {
            return null;
        }
        return getSquare(newFile, newRank);
    }
    
    public Square[] getSquaresBetween(Square endSquare) {
        int rankDiff = endSquare.getRank().ordinal() - getRank().ordinal();
        int fileDiff = endSquare.getFile().ordinal() - getFile().ordinal();
        
        // return squares along this rank between this square and end square
        if (rankDiff == 0) {
            int multiplier = fileDiff > 0 ? 1 : -1;
            Square[] result = new Square[Math.abs(fileDiff) - 1];
            for (int i = 0; i < result.length; i++) {
                result[i] = Square.getSquare(file.getRelativeFile((i + 1) * multiplier), getRank());
            }
            
            return result;
        }
        // return squares along this file between this square and end square
        else if (fileDiff == 0) {
            int multiplier = rankDiff > 0 ? 1 : -1;
            Square[] result = new Square[Math.abs(rankDiff) - 1];
            for (int i = 0; i < result.length; i++) {
                result[i] = Square.getSquare(getFile(), rank.getRelativeRank((i + 1) * multiplier));
            }
            
            return result;
        }
        // return squares along the diagonal between this square and end square
        else if (Math.abs(fileDiff) == Math.abs(rankDiff)) {
            int rankMultiplier = rankDiff > 0 ? 1 : -1;
            int fileMultiplier = fileDiff > 0 ? 1 : -1;
            Square[] result = new Square[Math.abs(rankDiff) - 1];
            for (int i = 0; i < result.length; i++) {
                result[i] = Square.getSquare(
                        file.getRelativeFile((i + 1) * fileMultiplier),
                        rank.getRelativeRank((i + 1) * rankMultiplier));
            }
            
            return result;
        }
        
        return new Square[] {};
    }
}
