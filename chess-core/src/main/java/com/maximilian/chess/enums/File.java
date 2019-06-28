package com.maximilian.chess.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents the individual files on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
public enum File {
    A(0x0101010101010101L),
    B(0x0202020202020202L),
    C(0x0404040404040404L),
    D(0x0808080808080808L),
    E(0x1010101010101010L),
    F(0x2020202020202020L),
    G(0x4040404040404040L),
    H(0x8080808080808080L);

    /**
     * Gets the 64-bit bitmask that represents the specific {@link File} on a bitboard.
     *
     * @return The 64-bit bitmask that represents the specific {@link File} on a bitboard.
     */
    @Getter private final long bitmask;
}
