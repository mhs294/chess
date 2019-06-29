package com.maximilian.chess.enums;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.B;
import static com.maximilian.chess.enums.File.C;
import static com.maximilian.chess.enums.File.D;
import static com.maximilian.chess.enums.File.E;
import static com.maximilian.chess.enums.File.F;
import static com.maximilian.chess.enums.File.G;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.FIVE;
import static com.maximilian.chess.enums.Rank.FOUR;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.enums.Rank.SEVEN;
import static com.maximilian.chess.enums.Rank.SIX;
import static com.maximilian.chess.enums.Rank.THREE;
import static com.maximilian.chess.enums.Rank.TWO;
import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;

/**
 * Represents the individual squares on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum Square {
    A1(A, ONE, DARK),
    A2(A, TWO, LIGHT),
    A3(A, THREE, DARK),
    A4(A, FOUR, LIGHT),
    A5(A, FIVE, DARK),
    A6(A, SIX, LIGHT),
    A7(A, SEVEN, DARK),
    A8(A, EIGHT, LIGHT),
    B1(B, ONE, LIGHT),
    B2(B, TWO, DARK),
    B3(B, THREE, LIGHT),
    B4(B, FOUR, DARK),
    B5(B, FIVE, LIGHT),
    B6(B, SIX, DARK),
    B7(B, SEVEN, LIGHT),
    B8(B, EIGHT, DARK),
    C1(C, ONE, DARK),
    C2(C, TWO, LIGHT),
    C3(C, THREE, DARK),
    C4(C, FOUR, LIGHT),
    C5(C, FIVE, DARK),
    C6(C, SIX, LIGHT),
    C7(C, SEVEN, DARK),
    C8(C, EIGHT, LIGHT),
    D1(D, ONE, LIGHT),
    D2(D, TWO, DARK),
    D3(D, THREE, LIGHT),
    D4(D, FOUR, DARK),
    D5(D, FIVE, LIGHT),
    D6(D, SIX, DARK),
    D7(D, SEVEN, LIGHT),
    D8(D, EIGHT, DARK),
    E1(E, ONE, DARK),
    E2(E, TWO, LIGHT),
    E3(E, THREE, DARK),
    E4(E, FOUR, LIGHT),
    E5(E, FIVE, DARK),
    E6(E, SIX, LIGHT),
    E7(E, SEVEN, DARK),
    E8(E, EIGHT, LIGHT),
    F1(F, ONE, LIGHT),
    F2(F, TWO, DARK),
    F3(F, THREE, LIGHT),
    F4(F, FOUR, DARK),
    F5(F, FIVE, LIGHT),
    F6(F, SIX, DARK),
    F7(F, SEVEN, LIGHT),
    F8(F, EIGHT, DARK),
    G1(G, ONE, DARK),
    G2(G, TWO, LIGHT),
    G3(G, THREE, DARK),
    G4(G, FOUR, LIGHT),
    G5(G, FIVE, DARK),
    G6(G, SIX, LIGHT),
    G7(G, SEVEN, DARK),
    G8(G, EIGHT, LIGHT),
    H1(H, ONE, LIGHT),
    H2(H, TWO, DARK),
    H3(H, THREE, LIGHT),
    H4(H, FOUR, DARK),
    H5(H, FIVE, LIGHT),
    H6(H, SIX, DARK),
    H7(H, SEVEN, LIGHT),
    H8(H, EIGHT, DARK);

    private static final Map<Pair<File, Rank>, Square> FILE_AND_RANK_TO_SQUARE_MAP;
    private static final Map<Long, Square> BITMASK_TO_SQUARE_MAP;

    static {
        FILE_AND_RANK_TO_SQUARE_MAP = new HashMap<>(Square.values().length);
        for (Square square : Square.values()) {
            FILE_AND_RANK_TO_SQUARE_MAP.put(Pair.of(square.file, square.rank), square);
        }
        BITMASK_TO_SQUARE_MAP = new HashMap<>(Square.values().length);
        for (Square square : Square.values()) {
            BITMASK_TO_SQUARE_MAP.put(square.bitmask(), square);
        }
    }

    /**
     * The {@link File} of this {@link Square}.
     */
    @Getter private final File file;
    /**
     * The {@link Rank} of this {@link Square}.
     */
    @Getter private final Rank rank;
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
     * @param file The {@link File} of this {@link Square}.
     * @param rank The {@link Rank} of this {@link Square}.
     * @param type The {@link Type} of this {@link Square}.
     */
    Square (File file, Rank rank, Type type) {
        this.file = file;
        this.rank = rank;
        this.type = type;
        this.bitmask = rank.bitmask() & file.bitmask();
    }

    /**
     * Gets the {@link Square} corresponding to the specified {@link File} and {@link Rank}.
     *
     * @param file The {@link File} of the {@link Square}.
     * @param rank The {@link File} of the {@link Square}.
     * @return The {@link Square} corresponding to the specified {@link File} and {@link Rank}. Will never be null.
     */
    public static Square fromFileAndRank (File file, Rank rank) {
        return FILE_AND_RANK_TO_SQUARE_MAP.get(Pair.of(file, rank));
    }

    /**
     * Gets the {@link Square} corresponding to the specified bitmask value.
     *
     * @param bitmask The bitmask value corresponding to the desired {@link Square}.
     * @return The {@link Square} corresponding to the specified bitmask value (or {@code null} if no corresponding
     * {@link Square} exists).
     */
    public static Square fromBitmask (long bitmask) {
        return BITMASK_TO_SQUARE_MAP.get(bitmask);
    }

    /**
     * Gets the {@link Square} corresponding to the specified ordinal value.
     *
     * @param ordinal The ordinal corresponding to the desired {@link Square}. Must be in range [0, 63].
     * @return The {@link Square} corresponding to the specified ordinal value.
     */
    public static Square fromOrdinal (int ordinal) {
        return Square.values()[ordinal];
    }

    /**
     * Represents the different types of colored squares (light/dark).
     */
    public enum Type {
        LIGHT,
        DARK
    }
}
