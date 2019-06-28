package com.maximilian.chess.movegen.magic;

import com.maximilian.chess.enums.Square;
import com.maximilian.chess.util.BitboardUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;
import static com.maximilian.chess.objects.Board.DARK_SQUARES_BITMASK;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;
import static com.maximilian.chess.objects.Board.LIGHT_SQUARES_BITMASK;

/**
 * Represents a utility class which calculates magic numbers for sliding attack bitboards (i.e. - rook and bishop).
 * These magic numbers are used in conjunction with standard, predefined movement bitboards to rapidly calculate
 * pseudo-legal moves for a sliding piece on a given square. These magic numbers only need to be calculated once at
 * startup and then stored in a lookup table to be referenced by the move generator as needed.
 *
 * @author Maximilian Schroeder
 */
public class BitboardMagic {
    public static final Object2LongMap<Square> BISHOP_MAGICS = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> ROOK_MAGICS = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> BISHOP_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> ROOK_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);

    static {
        for (Square square : Square.values()) {
            // Initialize bishop magics.
            long bishopBlockerMask = getBishopBlockerBitmaskForSquare(square);
            BISHOP_BLOCKER_MASKS.put(square, bishopBlockerMask);
            LongSet bishopOccupancyPermutations = getBlockerOccupancyBitmasksByBlockerBitmask(bishopBlockerMask);
            BISHOP_MAGICS.put(square, getMagicForOccupancyBitmasks(bishopOccupancyPermutations));

            // Initialize rook magics.
            long rookBlockerMask = getRookBlockerBitmaskForSquare(square);
            ROOK_BLOCKER_MASKS.put(square, rookBlockerMask);
            LongSet rookOccupancyPermutations = getBlockerOccupancyBitmasksByBlockerBitmask(rookBlockerMask);
            ROOK_MAGICS.put(square, getMagicForOccupancyBitmasks(rookOccupancyPermutations));
        }
    }

    private static long getBishopBlockerBitmaskForSquare (Square square) {
        long squareBitmask = square.bitmask();
        long bishopBitmask = EMPTY_BITMASK;
        for (int i = 1; i <= 6; i++) {
            bishopBitmask |= (squareBitmask << (7 * i) | squareBitmask >>> (7 * i));
            bishopBitmask |= (squareBitmask << (9 * i) | squareBitmask >>> (9 * i));
        }

        // Exclude any invalid squares included as a result of bit-shifting that wrapped around the board.
        if (square.type() == LIGHT) {
            bishopBitmask &= LIGHT_SQUARES_BITMASK;
        } else if (square.type() == DARK) {
            bishopBitmask &= DARK_SQUARES_BITMASK;
        }

        // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
        bishopBitmask &= ~A.bitmask();
        bishopBitmask &= ~H.bitmask();
        bishopBitmask &= ~ONE.bitmask();
        bishopBitmask &= ~EIGHT.bitmask();

        return bishopBitmask;
    }

    private static long getRookBlockerBitmaskForSquare (Square square) {
        long rookBitmask = (square.rank()
                .bitmask() | square.file()
                .bitmask()) ^ square.bitmask();

        // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
        rookBitmask &= (~A.bitmask() | square.file()
                .bitmask());
        rookBitmask &= (~H.bitmask() | square.file()
                .bitmask());
        rookBitmask &= (~ONE.bitmask() | square.rank()
                .bitmask());
        rookBitmask &= (~EIGHT.bitmask() | square.rank()
                .bitmask());

        return rookBitmask;
    }

    private static LongSet getBlockerOccupancyBitmasksByBlockerBitmask (long blockerBitmask) {
        // Total number of blocker occupancy bitmasks to generate = 2^(number of high bits) in blocker bitmask.
        int highBitsInBlockerBitmask = BitboardUtils.countHighBitsInBitmask(blockerBitmask);
        int totalBlockerOccupancyBitmasks = 1 << highBitsInBlockerBitmask;

        // Determine individual values of high bits in blocker bitmask.
        List<Long> bitValues = new ArrayList<>(highBitsInBlockerBitmask);
        for (int i = 0; blockerBitmask >>> i != 0L; i++) {
            long currentBitValue = blockerBitmask >>> i;
            if ((currentBitValue & 1L) == 1L) {
                bitValues.add(currentBitValue);
            }
        }

        // Iterate over all possible combinations of blocker occupancies for the blocker bitmask.
        LongSet blockerOccupancyBitmasks = new LongOpenHashSet(totalBlockerOccupancyBitmasks);
        for (int i = 0; i < totalBlockerOccupancyBitmasks; i++) {
            long occupancyBitmask = blockerBitmask;
            for (int k = 0; i >>> k != 0; k++) {
                if (((i >>> k) & 1) == 1) {
                    occupancyBitmask -= bitValues.get(k);
                }
            }
            blockerOccupancyBitmasks.add(occupancyBitmask);
        }

        return blockerOccupancyBitmasks;
    }

    private static long getMagicForOccupancyBitmasks (LongSet occupancyBitmasks) {
        Random random = new Random();
        LongSet magicKeys = new LongOpenHashSet(occupancyBitmasks.size());

        // Continue trying random numbers until one with no collisions is found.
        while (true) {
            boolean magicFound = true;
            long magic = random.nextLong();
            for (long occupancyBitmask : occupancyBitmasks) {
                // The magic key is an index created by hashing the random number with the current occupancy bitmask.
                long magicKey = magic * occupancyBitmask;
                if (magicKeys.contains(magicKey)) {
                    // If a hash collision is detected, start from the beginning with a new random number.
                    magicFound = false;
                    break;
                }
                magicKeys.add(magicKey);
            }
            if (magicFound) {
                // The current random number has no hash collisions - it is a magic for the set of occupancy bitmasks.
                return magic;
            }
            magicKeys.clear();
        }
    }

    // This class should never be instantiated.
    private BitboardMagic () {
    }
}
