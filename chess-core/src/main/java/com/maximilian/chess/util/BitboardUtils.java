package com.maximilian.chess.util;

import com.maximilian.chess.enums.Square;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;

/**
 * A utility class containing convenience operations for bitboards.
 *
 * @author Maximilian Schroeder
 */
public final class BitboardUtils {
    /**
     * Counts the number of high bits in the specified bitmask.
     *
     * @param bitmask The bitmask to count the high bits from.
     * @return The number of high bits in the specified bitmask.
     */
    public static int countHighBitsInBitmask (long bitmask) {
        int count = 0;
        for (long mask = bitmask; mask != EMPTY_BITMASK; mask >>>= 1) {
            if ((mask & 1L) == 1L) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the {@link Set} of {@link Square}s represented by the high bits in the specified bitmask.
     *
     * @param bitmask The bitmask to get the {@link Square}s from.
     * @return The {@link Set} of {@link Square}s represented by the high bits in the specified bitmask. Will never
     * be null, may be empty.
     */
    @Nonnull
    public static Set<Square> getSquaresFromBitmask (long bitmask) {
        Set<Square> targetSquares = new HashSet<>(countHighBitsInBitmask(bitmask));
        for (Square square : Square.values()) {
            if ((square.bitmask & bitmask) == EMPTY_BITMASK) {
                continue;
            }

            targetSquares.add(square);
        }

        return targetSquares;
    }

    /**
     * Gets a bitmask with high bits corresponding to the specified {@link Set} of {@link Square}s.
     *
     * @param squares The {@link Square}s that will be represented by high bits in the bitmask. Cannot be null, may
     *                be empty.
     * @return A bitmask with high bits corresponding to the specified {@link Set} of {@link Square}s.
     */
    public static long getBitmaskFromSquares (@Nonnull Set<Square> squares) {
        long bitmask = EMPTY_BITMASK;
        for (Square square : squares) {
            bitmask |= square.bitmask;
        }

        return bitmask;
    }

    /**
     * Creates a human-readable string representation of the specified bitboard using rank and file markers. High bits
     * are denoted with an {@code x} while low bits are denoted with a {@code .}.
     *
     * @param bitboard The bitboard to convert to a human-readable {@link String}.
     * @return A human-readable string representation of the specified bitboard. Will never be null, empty, or blank.
     */
    @Nonnull
    public static String bitboardToBoardString (long bitboard) {
        String fileMarkers = "  a b c d e f g h ";
        StringBuilder bitboardStringBuilder = new StringBuilder(fileMarkers + "\n");
        for (int i = 1; i <= 8; i++) {
            int rowLabel = (8 - i) + 1;
            bitboardStringBuilder.append(rowLabel).append(" ");
            long bitboardRow = bitboard >>> (64 - (8 * i));
            StringBuilder rowStringBuilder = new StringBuilder();
            for (int k = 0; k < 8; k++) {
                long currentBit = (bitboardRow >>> k) & 1L;
                if (currentBit == 1L) {
                    rowStringBuilder.append("x");
                } else {
                    rowStringBuilder.append(".");
                }
                if ((k + 1) < 8) {
                    rowStringBuilder.append(" ");
                }
            }
            bitboardStringBuilder.append(rowStringBuilder.toString()).append(" ").append(rowLabel).append("\n");
        }
        return bitboardStringBuilder.append(fileMarkers).toString();
    }

    // This class should never be instantiated.
    private BitboardUtils () {
    }
}
