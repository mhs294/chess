package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;

/**
 * Represents the individual diagonals (i.e. - ascending diagonals) on a chessboard.
 *
 * @author Maximilian Schroeder
 */
public enum Diagonal {
    A1_H8(0),
    A2_G8(1),
    A3_F8(2),
    A4_E8(3),
    A5_D8(4),
    A6_C8(5),
    A7_B8(6),
    A8_A8(7),
    H1_H1(9),
    G1_H2(10),
    F1_H3(11),
    E1_H4(12),
    D1_H5(13),
    C1_H6(14),
    B1_H7(15);

    private static final Int2ObjectMap<Diagonal> INDEX_TO_DIAGONAL_MAP;

    static {
        Diagonal[] diagonals = Diagonal.values();
        INDEX_TO_DIAGONAL_MAP = new Int2ObjectOpenHashMap<>(diagonals.length, 1.0F);
        for (Diagonal diagonal : diagonals) {
            INDEX_TO_DIAGONAL_MAP.put(diagonal.index, diagonal);
        }
    }

    /**
     * The index of this {@link Diagonal} starting from the center and going northwest, wrapping around from the
     * southeast.
     */
    private final int index;
    /**
     * The 64-bit bitmask that represents this {@link Diagonal} on a bitboard.
     */
    public final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link Diagonal} starting from the center and going northwest, wrapping around
     *              from the southeast.
     */
    Diagonal (int index) {
        this.index = index;

        long initialBitmask = 1L;
        int numberOfShifts;
        if (index < 8) {
            initialBitmask <<= (8 * index);
            numberOfShifts = 7 - index;
        } else {
            initialBitmask <<= (16 - index);
            numberOfShifts = index - 9;
        }

        long bitmask = initialBitmask;
        for (int i = 1; i <= numberOfShifts; i++) {
            bitmask |= initialBitmask << (9 * i);
        }
        this.bitmask = bitmask;
    }

    /**
     * Gets the {@link Diagonal} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link Diagonal}. Must be in range [0, 7] or [9, 15].
     * @return The {@link Diagonal} corresponding the specified index value.
     */
    @Nonnull
    static Diagonal fromIndex (int index) {
        if (index < 0 || index == 8 || index > 15) {
            throw new IllegalArgumentException("index must be in range [0, 7] or [9, 15].");
        }

        return INDEX_TO_DIAGONAL_MAP.get(index);
    }
}
