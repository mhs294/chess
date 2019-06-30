package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;

/**
 * Represents the individual squares on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum Square {
    A1(0, DARK),
    A2(1, LIGHT),
    A3(2, DARK),
    A4(3, LIGHT),
    A5(4, DARK),
    A6(5, LIGHT),
    A7(6, DARK),
    A8(7, LIGHT),
    B1(8, LIGHT),
    B2(9, DARK),
    B3(10, LIGHT),
    B4(11, DARK),
    B5(12, LIGHT),
    B6(13, DARK),
    B7(14, LIGHT),
    B8(15, DARK),
    C1(16, DARK),
    C2(17, LIGHT),
    C3(18, DARK),
    C4(19, LIGHT),
    C5(20, DARK),
    C6(21, LIGHT),
    C7(22, DARK),
    C8(23, LIGHT),
    D1(24, LIGHT),
    D2(25, DARK),
    D3(26, LIGHT),
    D4(27, DARK),
    D5(28, LIGHT),
    D6(29, DARK),
    D7(30, LIGHT),
    D8(31, DARK),
    E1(32, DARK),
    E2(33, LIGHT),
    E3(34, DARK),
    E4(35, LIGHT),
    E5(36, DARK),
    E6(37, LIGHT),
    E7(38, DARK),
    E8(39, LIGHT),
    F1(40, LIGHT),
    F2(41, DARK),
    F3(42, LIGHT),
    F4(43, DARK),
    F5(44, LIGHT),
    F6(45, DARK),
    F7(46, LIGHT),
    F8(47, DARK),
    G1(48, DARK),
    G2(49, LIGHT),
    G3(50, DARK),
    G4(51, LIGHT),
    G5(52, DARK),
    G6(53, LIGHT),
    G7(54, DARK),
    G8(55, LIGHT),
    H1(56, LIGHT),
    H2(57, DARK),
    H3(58, LIGHT),
    H4(59, DARK),
    H5(60, LIGHT),
    H6(61, DARK),
    H7(62, LIGHT),
    H8(63, DARK);

    private static final Int2ObjectMap<Square> INDEX_TO_SQUARE_MAP;
    private static final Map<Long, Square> BITMASK_TO_SQUARE_MAP;

    static {
        Square[] squares = Square.values();
        INDEX_TO_SQUARE_MAP = new Int2ObjectOpenHashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            INDEX_TO_SQUARE_MAP.put(square.index, square);
        }

        BITMASK_TO_SQUARE_MAP = new HashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            BITMASK_TO_SQUARE_MAP.put(square.bitmask, square);
        }
    }

    /**
     * The index of this {@link Square} in a 64-bitmask.
     */
    @Getter private final int index;
    /**
     * The {@link Type} of this {@link Square}.
     */
    @Getter private final Type type;
    /**
     * The bitmask of this {@link Square}.
     */
    @Getter private final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link Square} in a 64-bit bitmask.
     * @param type  The {@link Type} of this {@link Square}.
     */
    Square (int index, @Nonnull Type type) {
        this.index = index;
        this.type = type;
        this.bitmask = 1L << index;
    }

    /**
     * Gets the {@link File} for this {@link Square}.
     *
     * @return The {@link File} for this {@link Square}. Will never be null.
     */
    @Nonnull
    public File file () {
        return File.getFromSquare(this);
    }

    /**
     * Gets the {@link Rank} for this {@link Square}.
     *
     * @return The {@link Rank} for this {@link Square}. Will never be null.
     */
    @Nonnull
    public Rank rank () {
        return Rank.getFromSquare(this);
    }

    /**
     * Gets the {@link Square} corresponding to the specified {@link File} and {@link Rank}.
     *
     * @param file The {@link File} of the {@link Square}. Cannot be null.
     * @param rank The {@link File} of the {@link Square}. Cannot be null.
     * @return The {@link Square} corresponding to the specified {@link File} and {@link Rank}.
     */
    @Nonnull
    public static Square fromFileAndRank (@Nonnull File file, @Nonnull Rank rank) {
        return BITMASK_TO_SQUARE_MAP.get(file.bitmask() & rank.bitmask());
    }

    /**
     * Gets the {@link Square} corresponding to the specified bitmask value.
     *
     * @param bitmask The bitmask value corresponding to the desired {@link Square}.
     * @return The {@link Square} corresponding to the specified bitmask value (or {@code null} if no corresponding
     * {@link Square} exists).
     */
    @Nullable
    public static Square fromBitmask (long bitmask) {
        return BITMASK_TO_SQUARE_MAP.get(bitmask);
    }

    /**
     * Gets the {@link Square} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link Square}. Must be in range [0, 63].
     * @return The {@link Square} corresponding to the specified index value. May be null (if the index value is
     * invalid).
     */
    @Nullable
    public static Square fromIndex (int index) {
        return INDEX_TO_SQUARE_MAP.get(index);
    }

    /**
     * Represents the different types of colored squares (light/dark).
     */
    public enum Type {
        LIGHT,
        DARK
    }
}
