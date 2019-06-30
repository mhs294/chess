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
public class BitboardUtils {
    /**
     * Counts the number of high bits in the specified bitmask.
     *
     * @param bitmask The bitmask to count the high bits from.
     * @return The number of high bits in the specified bitmask.
     */
    public static int countHighBitsInBitmask (long bitmask) {
        int count = 0;
        for (long mask = bitmask; mask != 0L; mask >>>= 1) {
            if ((mask & 0x00000000000000001L) == 0x00000000000000001L) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the {@link Set} of {@link Square}s represented by the high bits in the specified bitmask.
     *
     * @param movementBitmask The bitmask to get the {@link Square}s from.
     * @return The {@link Set} of {@link Square}s represented by the high bits in the specified bitmask. Will never
     * be null, may be empty.
     */
    @Nonnull
    public static Set<Square> getSquaresFromBitmask (long movementBitmask) {
        Set<Square> targetSquares = new HashSet<>(countHighBitsInBitmask(movementBitmask));
        for (Square square : Square.values()) {
            if ((square.bitmask() & movementBitmask) == EMPTY_BITMASK) {
                continue;
            }

            targetSquares.add(square);
        }

        return targetSquares;
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
        String fileMarkers = "  A B C D E F G H ";
        StringBuilder bitboardStringBuilder = new StringBuilder(fileMarkers + "\n");
        for (int i = 7; i >= 0; i--) {
            bitboardStringBuilder.append(i + 1)
                    .append(" ");
            long bitboardRow = bitboard >>> 64 - (8 * i);
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
            bitboardStringBuilder.append(rowStringBuilder.toString())
                    .append(" ")
                    .append(i + 1)
                    .append("\n");
        }
        return bitboardStringBuilder.append(fileMarkers)
                .toString();
    }

    // This class should never be instantiated.
    private BitboardUtils () {
    }
}
