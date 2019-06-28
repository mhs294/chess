package com.maximilian.chess.constants;

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

    // This class should never be instantiated.
    private ANConstants () {
    }
}
