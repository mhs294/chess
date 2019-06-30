package com.maximilian.chess.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
    B(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    C(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    D(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    E(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    F(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    G(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8)),
    H(EnumSet.of(A1, A2, A3, A4, A5, A6, A7, A8));

    private static final Map<Square, File> SQUARE_TO_FILE_MAP;

    static {
        Square[] squares = Square.values();
        SQUARE_TO_FILE_MAP = new HashMap<>(squares.length, 1.0F);
        for (Square square : squares) {
            for (File file : File.values()) {
                if ((file.bitmask & square.bitmask()) != EMPTY_BITMASK) {
                    SQUARE_TO_FILE_MAP.put(square, file);
                    break;
                }
            }
        }
    }

    /**
     * The 64-bit bitmask that represents the specific {@link File} on a bitboard.
     */
    @Getter private final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param squares The {@link Set} of {@link Square}s comprising the {@link File}.
     */
    File (@Nonnull Set<Square> squares) {
        long bitmask = EMPTY_BITMASK;
        for (Square square : squares) {
            bitmask |= square.bitmask();
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
