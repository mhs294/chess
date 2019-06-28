package com.maximilian.chess.util;

import com.maximilian.chess.enums.Square;

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

    // This class should never be instantiated.
    private BitboardUtils () {
    }
}
