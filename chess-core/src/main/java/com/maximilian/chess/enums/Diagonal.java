package com.maximilian.chess.enums;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static com.maximilian.chess.enums.Diagonal.Direction.ASCENDING;
import static com.maximilian.chess.enums.Diagonal.Direction.DESCENDING;
import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A2;
import static com.maximilian.chess.enums.Square.A3;
import static com.maximilian.chess.enums.Square.A4;
import static com.maximilian.chess.enums.Square.A5;
import static com.maximilian.chess.enums.Square.A6;
import static com.maximilian.chess.enums.Square.A7;
import static com.maximilian.chess.enums.Square.A8;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B2;
import static com.maximilian.chess.enums.Square.B3;
import static com.maximilian.chess.enums.Square.B4;
import static com.maximilian.chess.enums.Square.B5;
import static com.maximilian.chess.enums.Square.B6;
import static com.maximilian.chess.enums.Square.B7;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C2;
import static com.maximilian.chess.enums.Square.C3;
import static com.maximilian.chess.enums.Square.C4;
import static com.maximilian.chess.enums.Square.C5;
import static com.maximilian.chess.enums.Square.C6;
import static com.maximilian.chess.enums.Square.C7;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D2;
import static com.maximilian.chess.enums.Square.D3;
import static com.maximilian.chess.enums.Square.D4;
import static com.maximilian.chess.enums.Square.D5;
import static com.maximilian.chess.enums.Square.D6;
import static com.maximilian.chess.enums.Square.D7;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E2;
import static com.maximilian.chess.enums.Square.E3;
import static com.maximilian.chess.enums.Square.E4;
import static com.maximilian.chess.enums.Square.E5;
import static com.maximilian.chess.enums.Square.E6;
import static com.maximilian.chess.enums.Square.E7;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F2;
import static com.maximilian.chess.enums.Square.F3;
import static com.maximilian.chess.enums.Square.F4;
import static com.maximilian.chess.enums.Square.F5;
import static com.maximilian.chess.enums.Square.F6;
import static com.maximilian.chess.enums.Square.F7;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G2;
import static com.maximilian.chess.enums.Square.G3;
import static com.maximilian.chess.enums.Square.G4;
import static com.maximilian.chess.enums.Square.G5;
import static com.maximilian.chess.enums.Square.G6;
import static com.maximilian.chess.enums.Square.G7;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H1;
import static com.maximilian.chess.enums.Square.H2;
import static com.maximilian.chess.enums.Square.H3;
import static com.maximilian.chess.enums.Square.H4;
import static com.maximilian.chess.enums.Square.H5;
import static com.maximilian.chess.enums.Square.H6;
import static com.maximilian.chess.enums.Square.H7;
import static com.maximilian.chess.enums.Square.H8;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;

/**
 * Represents the individual diagonals on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum Diagonal {
    A1_H8(EnumSet.of(A1, B2, C3, D4, E5, F6, G7, H8), ASCENDING),
    A2_G8(EnumSet.of(A2, B3, C4, D5, E6, F7, G8), ASCENDING),
    A3_F8(EnumSet.of(A3, B4, C5, D6, E7, F8), ASCENDING),
    A4_E8(EnumSet.of(A4, B5, C6, D7, E8), ASCENDING),
    A5_D8(EnumSet.of(A5, B6, C7, D8), ASCENDING),
    A6_C8(EnumSet.of(A6, B7, C8), ASCENDING),
    A7_B8(EnumSet.of(A7, B8), ASCENDING),
    B1_H7(EnumSet.of(B1, C2, D3, E4, F5, G6, H7), ASCENDING),
    C1_H6(EnumSet.of(C1, D2, E3, F4, G5, H6), ASCENDING),
    D1_H5(EnumSet.of(D1, E2, F3, G4, H5), ASCENDING),
    E1_H4(EnumSet.of(E1, F2, G3, H4), ASCENDING),
    F1_H3(EnumSet.of(F1, G2, H3), ASCENDING),
    G1_H2(EnumSet.of(G1, H2), ASCENDING),
    A8_H1(EnumSet.of(A8, B7, C6, D5, E4, F3, G2, H1), DESCENDING),
    A7_G1(EnumSet.of(A7, B6, C5, D4, E3, F2, G1), DESCENDING),
    A6_F1(EnumSet.of(A6, B5, C4, D3, E2, F1), DESCENDING),
    A5_E1(EnumSet.of(A5, B4, C3, D2, E1), DESCENDING),
    A4_D1(EnumSet.of(A4, B3, C2, D1), DESCENDING),
    A3_C1(EnumSet.of(A3, B2, C1), DESCENDING),
    A2_B1(EnumSet.of(A2, B1), DESCENDING),
    B8_H2(EnumSet.of(B8, C7, D6, E5, F4, G3, H2), DESCENDING),
    C8_H3(EnumSet.of(C8, D7, E6, F5, G4, H3), DESCENDING),
    D8_H4(EnumSet.of(D8, E7, F6, G5, H4), DESCENDING),
    E8_H5(EnumSet.of(E8, F7, G6, H5), DESCENDING),
    F8_H6(EnumSet.of(F8, G7, H6), DESCENDING),
    G8_H7(EnumSet.of(G8, H7), DESCENDING);

    private static final SetMultimap<Square, Diagonal> SQUARE_TO_DIAGONALS_MAP;

    static {
        SQUARE_TO_DIAGONALS_MAP = HashMultimap.create(Square.values().length, 2);
        for (Square square : Square.values()) {
            Set<Diagonal> diagonals = new HashSet<>(2);
            for (Diagonal diagonal : Diagonal.values()) {
                if ((diagonal.bitmask & square.bitmask) != EMPTY_BITMASK) {
                    diagonals.add(diagonal);
                    if (diagonals.size() == 2) {
                        break;
                    }
                }
            }
            SQUARE_TO_DIAGONALS_MAP.putAll(square, diagonals);
        }
    }

    /**
     * The 64-bit bitmask that represents the specific {@link Diagonal} on a bitboard.
     */
    public final long bitmask;
    /**
     * The {@link Direction} of this {@link Diagonal} (as viewed from A file to H file).
     */
    @Getter private final Direction direction;

    /**
     * (Primary constructor)
     *
     * @param squares The {@link Set} of {@link Square}s comprising the {@link Diagonal}.
     * @param direction The {@link Direction} of this {@link Diagonal} (as viewed from A file to H file).
     */
    Diagonal (@Nonnull Set<Square> squares, @Nonnull Direction direction) {
        long bitmask = EMPTY_BITMASK;
        for (Square square : squares) {
            bitmask |= square.bitmask;
        }
        this.bitmask = bitmask;
        this.direction = direction;
    }

    /**
     * Gets the {@link Set} of {@link Diagonal}s corresponding to the specified {@link Square}.
     *
     * @param square The {@link Square} to get the {@link Set} of {@link Diagonal}s for. Cannot be null.
     * @return The {@link Set} of {@link Diagonal}s corresponding to the specified {@link Square}. Will always have a
     * size of 1 or 2.
     */
    @Nonnull
    public static Set<Diagonal> fromSquare (@Nonnull Square square) {
        return SQUARE_TO_DIAGONALS_MAP.get(square);
    }

    /**
     * Gets the bitwise-or combined bitmask from the specified {@link Set} of {@link Diagonal}s.
     *
     * @param diagonals The {@link Set} of {@link Diagonal}s to get the combined bitmask for. Cannot be null.
     * @return The bitwise-or combined bitmask from the specified {@link Set} of {@link Diagonal}s.
     */
    public static long combinedBitmask (@Nonnull Set<Diagonal> diagonals) {
        long combinedBitmask = EMPTY_BITMASK;
        for (Diagonal diagonal : diagonals) {
            combinedBitmask |= diagonal.bitmask;
        }

        return combinedBitmask;
    }

    /**
     * Represents the different directions of diagonals (as viewed from A file to H file).
     */
    public enum Direction {
        ASCENDING, DESCENDING
    }
}
