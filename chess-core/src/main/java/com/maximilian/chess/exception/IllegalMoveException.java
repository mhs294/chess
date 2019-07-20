package com.maximilian.chess.exception;

import com.maximilian.chess.objects.Board;
import com.maximilian.chess.objects.Move;

import javax.annotation.Nonnull;

/**
 * Represents a {@link RuntimeException} resulting from an illegal move being made.
 *
 * @author Maximilian Schroeder
 */
public class IllegalMoveException extends RuntimeException {
    /**
     * {@see {@link RuntimeException#RuntimeException(String)}}
     */
    public IllegalMoveException (@Nonnull String message) {
        super(message);
    }

    /**
     * {@see {@link RuntimeException#RuntimeException(String)}}
     */
    public IllegalMoveException (@Nonnull Move move, @Nonnull String message) {
        super(message + "\nMove:\n" + move.toString());
    }

    /**
     * {@see {@link RuntimeException#RuntimeException(String)}}
     */
    public IllegalMoveException (@Nonnull Move move, @Nonnull Board board, @Nonnull String message) {
        super(message + "\nMove:\n" + move + "\nBoard:\n" + board);
    }
}
