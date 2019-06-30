package com.maximilian.chess.enums;

import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
 * Represents the individual ranks on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum Rank {
    ONE(EnumSet.of(A1, B1, C1, D1, E1, F1, G1, H1)),
    TWO(EnumSet.of(A2, B2, C2, D2, E2, F2, G2, H2)),
    THREE(EnumSet.of(A3, B3, C3, D3, E3, F3, G3, H3)),
    FOUR(EnumSet.of(A4, B4, C4, D4, E4, F4, G4, H4)),
    FIVE(EnumSet.of(A5, B5, C5, D5, E5, F5, G5, H5)),
    SIX(EnumSet.of(A6, B6, C6, D6, E6, F6, G6, H6)),
    SEVEN(EnumSet.of(A7, B7, C7, D7, E7, F7, G7, H7)),
    EIGHT(EnumSet.of(A8, B8, C8, D8, E8, F8, G8, H8));

    private static final Map<Square, Rank> SQUARE_TO_RANK_MAP;

    static {
        Square[] squares = Square.values();
        SQUARE_TO_RANK_MAP = new HashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            for (Rank rank : Rank.values()) {
                if ((rank.bitmask & square.bitmask()) != EMPTY_BITMASK) {
                    SQUARE_TO_RANK_MAP.put(square, rank);
                    break;
                }
            }
        }
    }

    /**
     * The 64-bit bitmask that represents the specific {@link Rank} on a bitboard.
     */
    @Getter private final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param squares The {@link Set} of {@link Square}s comprising the {@link Rank}.
     */
    Rank (@Nonnull Set<Square> squares) {
        long bitmask = EMPTY_BITMASK;
        for (Square square : squares) {
            bitmask |= square.bitmask();
        }
        this.bitmask = bitmask;
    }

    /**
     * Gets the {@link Rank} that contains the specified {@link Square}.
     *
     * @param square The {@link Square} to find the {@link Rank} for. Cannot be null.
     * @return The {@link Rank} that contains the specified {@link Square}.
     */
    @Nonnull
    public static Rank getFromSquare (@Nonnull Square square) {
        return SQUARE_TO_RANK_MAP.get(square);
    }
}
