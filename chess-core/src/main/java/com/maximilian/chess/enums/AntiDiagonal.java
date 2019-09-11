package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;

/**
 * Represents the individual anti-diagonals (i.e. - descending diagonals) on a chessboard.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum AntiDiagonal {
    A8_H1(0),
    A7_G1(1),
    A6_F1(2),
    A5_E1(3),
    A4_D1(4),
    A3_C1(5),
    A2_B1(6),
    A1_A1(7),
    H8_H8(9),
    G8_H7(10),
    F8_H6(11),
    E8_H5(12),
    D8_H4(13),
    C8_H3(14),
    B8_H2(15);

    private static final Int2ObjectMap<AntiDiagonal> INDEX_TO_ANTIDIAGONAL_MAP;

    static {
        AntiDiagonal[] antiDiagonals = AntiDiagonal.values();
        INDEX_TO_ANTIDIAGONAL_MAP = new Int2ObjectOpenHashMap<>(antiDiagonals.length, 1.0F);
        for (AntiDiagonal antiDiagonal : antiDiagonals) {
            INDEX_TO_ANTIDIAGONAL_MAP.put(antiDiagonal.index, antiDiagonal);
        }
    }

    /**
     * The index of this {@link AntiDiagonal} starting from the center and going southwest, wrapping around from the
     * northeast.
     */
    private final int index;
    /**
     * The 64-bit bitmask that represents this {@link AntiDiagonal} on a bitboard.
     */
    @Getter private final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link AntiDiagonal} starting from the center and going southwest, wrapping
     *              around from the northeast.
     */
    AntiDiagonal (int index) {
        this.index = index;

        long initialBitmask = 0x80L;
        int numberOfShifts;
        if (index < 8) {
            initialBitmask >>>= index;
            numberOfShifts = 7 - index;
        } else {
            initialBitmask <<= ((16 - index) * 8);
            numberOfShifts = index - 9;
        }

        long bitmask = initialBitmask;
        for (int i = 1; i <= numberOfShifts; i++) {
            bitmask |= initialBitmask << (7 * i);
        }
        this.bitmask = bitmask;
    }

    /**
     * Gets the {@link AntiDiagonal} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link AntiDiagonal}. Must be in range [0, 7] or [9, 15].
     * @return The {@link AntiDiagonal} corresponding the specified index value.
     */
    @Nonnull
    static AntiDiagonal fromIndex (int index) {
        if (index < 0 || index == 8 || index > 15) {
            throw new IllegalArgumentException("index must be in range [0, 7] or [9, 15].");
        }

        return INDEX_TO_ANTIDIAGONAL_MAP.get(index);
    }
}
