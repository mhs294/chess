package com.maximilian.chess.exception;

import com.maximilian.chess.objects.Move;

/**
 * Represents a {@link RuntimeException} resulting from an illegal move being made.
 *
 * @author Maximilian Schroeder
 */
public class IllegalMoveException extends RuntimeException {
    /**
     * {@see {@link RuntimeException#RuntimeException(String)}}
     */
    public IllegalMoveException (String message) {
        super(message);
    }

    /**
     * {@see {@link RuntimeException#RuntimeException(String)}}
     */
    public IllegalMoveException (Move move, String message) {
        super(message + "\nMove:\n" + move.toString());
    }
}
