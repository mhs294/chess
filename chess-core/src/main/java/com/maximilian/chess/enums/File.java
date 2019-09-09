package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.util.EnumSet;
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
 * Represents the individual files on a chessboard using bitmasks.
 *
 * @author Maximilian Schroeder
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
public enum File {
    A(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    B(EnumSet.of(B1, B2, B3, B4, B5, B6, B7, B8)),
    C(EnumSet.of(C1, C2, C3, C4, C5, C6, C7, C8)),
    D(EnumSet.of(D1, D2, D3, D4, D5, D6, D7, D8)),
    E(EnumSet.of(E1, E2, E3, E4, E5, E6, E7, E8)),
    F(EnumSet.of(F1, F2, F3, F4, F5, F6, F7, F8)),
    G(EnumSet.of(G1, G2, G3, G4, G5, G6, G7, G8)),
    H(EnumSet.of(H1, H2, H3, H4, H5, H6, H7, H8));

    private static final Object2ObjectMap<Square, File> SQUARE_TO_FILE_MAP;

    static {
        Square[] squares = Square.values();
        SQUARE_TO_FILE_MAP = new Object2ObjectOpenHashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            for (File file : File.values()) {
                if ((file.bitmask & square.bitmask) != EMPTY_BITMASK) {
                    SQUARE_TO_FILE_MAP.put(square, file);
                    break;
                }
            }
        }
    }

    /**
     * The 64-bit bitmask that represents the specific {@link File} on a bitboard.
     */
    public final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param squares The {@link Set} of {@link Square}s comprising the {@link File}.
     */
    File (@Nonnull Set<Square> squares) {
        long bitmask = EMPTY_BITMASK;
        for (Square square : squares) {
            bitmask |= square.bitmask;
        }
        this.bitmask = bitmask;
    }

    /**
     * Gets the {@link File} that contains the specified {@link Square}.
     *
     * @param square The {@link Square} to find the {@link File} for. Cannot be null.
     * @return The {@link File} that contains the specified {@link Square}.
     */
    @Nonnull
    public static File getFromSquare (@Nonnull Square square) {
        return SQUARE_TO_FILE_MAP.get(square);
    }
}
