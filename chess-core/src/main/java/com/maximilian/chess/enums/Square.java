package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

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
    B1(1, LIGHT),
    C1(2, DARK),
    D1(3, LIGHT),
    E1(4, DARK),
    F1(5, LIGHT),
    G1(6, DARK),
    H1(7, LIGHT),
    A2(8, LIGHT),
    B2(9, DARK),
    C2(10, LIGHT),
    D2(11, DARK),
    E2(12, LIGHT),
    F2(13, DARK),
    G2(14, LIGHT),
    H2(15, DARK),
    A3(16, DARK),
    B3(17, LIGHT),
    C3(18, DARK),
    D3(19, LIGHT),
    E3(20, DARK),
    F3(21, LIGHT),
    G3(22, DARK),
    H3(23, LIGHT),
    A4(24, LIGHT),
    B4(25, DARK),
    C4(26, LIGHT),
    D4(27, DARK),
    E4(28, LIGHT),
    F4(29, DARK),
    G4(30, LIGHT),
    H4(31, DARK),
    A5(32, DARK),
    B5(33, LIGHT),
    C5(34, DARK),
    D5(35, LIGHT),
    E5(36, DARK),
    F5(37, LIGHT),
    G5(38, DARK),
    H5(39, LIGHT),
    A6(40, LIGHT),
    B6(41, DARK),
    C6(42, LIGHT),
    D6(43, DARK),
    E6(44, LIGHT),
    F6(45, DARK),
    G6(46, LIGHT),
    H6(47, DARK),
    A7(48, DARK),
    B7(49, LIGHT),
    C7(50, DARK),
    D7(51, LIGHT),
    E7(52, DARK),
    F7(53, LIGHT),
    G7(54, DARK),
    H7(55, LIGHT),
    A8(56, LIGHT),
    B8(57, DARK),
    C8(58, LIGHT),
    D8(59, DARK),
    E8(60, LIGHT),
    F8(61, DARK),
    G8(62, LIGHT),
    H8(63, DARK);

    private static final Int2ObjectMap<Square> INDEX_TO_SQUARE_MAP;
    private static final Long2ObjectMap<Square> BITMASK_TO_SQUARE_MAP;

    static {
        Square[] squares = Square.values();
        INDEX_TO_SQUARE_MAP = new Int2ObjectOpenHashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            INDEX_TO_SQUARE_MAP.put(square.index, square);
        }

        BITMASK_TO_SQUARE_MAP = new Long2ObjectOpenHashMap<>(squares.length, 1.0F);
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
     * Gets the {@link Set} of {@link Diagonal}s for this {@link Square}.
     *
     * @return The {@link Set} of {@link Diagonal}s for this {@link Square}. Will never be null.
     */
    @Nonnull
    public Set<Diagonal> diagonals () {
        return Diagonal.fromSquare(this);
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
