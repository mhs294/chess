package com.maximilian.chess.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents the individual ranks on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
public enum Rank {
    ONE(0x00000000000000FFL),
    TWO(0x000000000000FF00L),
    THREE(0x0000000000FF0000L),
    FOUR(0x00000000FF000000L),
    FIVE(0x000000FF00000000L),
    SIX(0x0000FF0000000000L),
    SEVEN(0x00FF000000000000L),
    EIGHT(0xFF00000000000000L);

    /**
     * Gets the 64-bit bitmask that represents the specific {@link Rank} on a bitboard.
     *
     * @return The 64-bit bitmask that represents the specific {@link Rank} on a bitboard.
     */
    @Getter private final long bitmask;
}
