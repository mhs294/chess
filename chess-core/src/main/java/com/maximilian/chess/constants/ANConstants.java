package com.maximilian.chess.constants;

import com.maximilian.chess.enums.Color;
import com.maximilian.chess.objects.Piece;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.objects.Piece.Type.BISHOP;
import static com.maximilian.chess.objects.Piece.Type.KING;
import static com.maximilian.chess.objects.Piece.Type.KNIGHT;
import static com.maximilian.chess.objects.Piece.Type.PAWN;
import static com.maximilian.chess.objects.Piece.Type.QUEEN;
import static com.maximilian.chess.objects.Piece.Type.ROOK;

/**
 * Represents constants used for creating algebraic notation game records.
 *
 * @author Maximilian Schroeder
 */
public final class ANConstants {
    public static final String KINGSIDE_CASTLE = "O-O";
    public static final String QUEENSIDE_CASTLE = "O-O-O";

    public static final String CAPTURE = "x";
    public static final String PROMOTION = "=";

    public static final String CHECK = "+";
    public static final String DOUBLE_CHECK = "++";
    public static final String CHECKMATE = "#";

    public static final String WHITE_WIN = "1-0";
    public static final String BLACK_WIN = "0-1";
    public static final String DRAW = "\u00BD-\u00BD"; // "1/2-1/2"

    public static final Map<Pair<Color, Piece.Type>, String> LETTERS_BY_PIECES;
    public static final Map<Pair<Color, Piece.Type>, String> SYMBOLS_BY_PIECES;

    static {
        LETTERS_BY_PIECES = new HashMap<>(12, 1.0F);
        LETTERS_BY_PIECES.put(Pair.of(WHITE, PAWN), "P");
        LETTERS_BY_PIECES.put(Pair.of(WHITE, KNIGHT), "N");
        LETTERS_BY_PIECES.put(Pair.of(WHITE, BISHOP), "B");
        LETTERS_BY_PIECES.put(Pair.of(WHITE, ROOK), "R");
        LETTERS_BY_PIECES.put(Pair.of(WHITE, QUEEN), "Q");
        LETTERS_BY_PIECES.put(Pair.of(WHITE, KING), "K");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, PAWN), "p");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, KNIGHT), "n");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, BISHOP), "b");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, ROOK), "r");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, QUEEN), "q");
        LETTERS_BY_PIECES.put(Pair.of(BLACK, KING), "k");

        SYMBOLS_BY_PIECES = new HashMap<>(12, 1.0F);
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, PAWN), "\u2659");
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, KNIGHT), "\u2658");
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, BISHOP), "\u2657");
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, ROOK), "\u2656");
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, QUEEN), "\u2655");
        SYMBOLS_BY_PIECES.put(Pair.of(WHITE, KING), "\u2654");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, PAWN), "\u265F");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, KNIGHT), "\u265E");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, BISHOP), "\u265D");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, ROOK), "\u265C");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, QUEEN), "\u265B");
        SYMBOLS_BY_PIECES.put(Pair.of(BLACK, KING), "\u265A");
    }

    // This class should never be instantiated.
    private ANConstants () {
    }
}
