package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;

/**
 * Represents the individual squares on a chessboard.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum Square {
    A1(0),
    B1(1),
    C1(2),
    D1(3),
    E1(4),
    F1(5),
    G1(6),
    H1(7),
    A2(8),
    B2(9),
    C2(10),
    D2(11),
    E2(12),
    F2(13),
    G2(14),
    H2(15),
    A3(16),
    B3(17),
    C3(18),
    D3(19),
    E3(20),
    F3(21),
    G3(22),
    H3(23),
    A4(24),
    B4(25),
    C4(26),
    D4(27),
    E4(28),
    F4(29),
    G4(30),
    H4(31),
    A5(32),
    B5(33),
    C5(34),
    D5(35),
    E5(36),
    F5(37),
    G5(38),
    H5(39),
    A6(40),
    B6(41),
    C6(42),
    D6(43),
    E6(44),
    F6(45),
    G6(46),
    H6(47),
    A7(48),
    B7(49),
    C7(50),
    D7(51),
    E7(52),
    F7(53),
    G7(54),
    H7(55),
    A8(56),
    B8(57),
    C8(58),
    D8(59),
    E8(60),
    F8(61),
    G8(62),
    H8(63);

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
            BITMASK_TO_SQUARE_MAP.put(square.bitmask(), square);
        }
    }

    /**
     * The index of this {@link Square} in a 64-bitmask.
     */
    private final int index;
    /**
     * The 64-bit bitmask that represents this {@link Square} on a bitboard.
     */
    @Getter private final long bitmask;
    /**
     * The {@link File} containing this {@link Square}.
     */
    @Nonnull
    @Getter
    private final File file;
    /**
     * The {@link Rank} containing this {@link Square}.
     */
    @Nonnull
    @Getter
    private final Rank rank;
    /**
     * The {@link Diagonal} containing this {@link Square}.
     */
    @Nonnull
    @Getter
    private final Diagonal diagonal;
    /**
     * The {@link AntiDiagonal} containing this {@link Square}.
     */
    @Nonnull
    @Getter
    private final AntiDiagonal antiDiagonal;
    /**
     * The {@link Type} of this {@link Square}.
     */
    @Nonnull
    @Getter
    private final Type type;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link Square} in a 64-bit bitmask.
     */
    Square (int index) {
        this.index = index;
        this.bitmask = 1L << index;

        int fileIndex = index % 8;
        int rankIndex = index / 8;
        this.file = File.fromIndex(fileIndex);
        this.rank = Rank.fromIndex(rankIndex);
        this.diagonal = Diagonal.fromIndex((rankIndex - fileIndex) & 15);
        this.antiDiagonal = AntiDiagonal.fromIndex((fileIndex + rankIndex) ^ 7);

        boolean isEvenRankIndex = (index / 8) % 2 == 0;
        boolean isEvenFileIndex = index % 2 == 0;
        this.type = (isEvenRankIndex ^ isEvenFileIndex) ? LIGHT : DARK;
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
