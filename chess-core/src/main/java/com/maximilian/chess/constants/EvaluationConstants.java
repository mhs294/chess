package com.maximilian.chess.constants;

/**
 * Represents the constant values used when calculating an evaluation score for a particular position.
 *
 * @author Maximilian Schroeder
 */
public final class EvaluationConstants {
    public static final int CHECKMATE = 1000000;

    // TODO - add evaluation bonus/penalty constants

    // This class should never be instantiated.
    private EvaluationConstants () {
    }
}
