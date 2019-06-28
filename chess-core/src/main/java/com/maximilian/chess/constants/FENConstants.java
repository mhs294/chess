package com.maximilian.chess.constants;

/**
 * Represents constants used for creating Forsyth-Edwards Notation strings for game states.
 *
 * @author Maximilian Schroeder
 */
public final class FENConstants {
    public static final String LINE_DELIMITER = "/";
    public static final String NO_EN_PASSANT = "-";

    // This class should never be instantiated.
    private FENConstants () {
    }
}
