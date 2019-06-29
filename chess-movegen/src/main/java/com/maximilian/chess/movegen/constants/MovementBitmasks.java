package com.maximilian.chess.movegen.constants;

import com.maximilian.chess.enums.File;
import com.maximilian.chess.enums.Rank;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.util.BitboardUtils;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.B;
import static com.maximilian.chess.enums.File.G;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.enums.Rank.SEVEN;
import static com.maximilian.chess.enums.Rank.TWO;
import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;
import static com.maximilian.chess.objects.Board.DARK_SQUARES_BITMASK;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;
import static com.maximilian.chess.objects.Board.LIGHT_SQUARES_BITMASK;

/**
 * Represents the constant bitmask values for how specific piece types can move when located at any particular square.
 *
 * @author Maximilian Schroeder
 */
public class MovementBitmasks {
    /*
     * There is no data structure for queen moves, since those moves are calculated as a bitwise-or union of bishop
     * moves and rook moves.
     */
    public static final Object2LongMap<Square> WHITE_PAWN_ADVANCES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> WHITE_PAWN_CAPTURES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> BLACK_PAWN_ADVANCES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> BLACK_PAWN_CAPTURES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> KNIGHT_MOVES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> KING_MOVES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2ObjectMap<Square, Long2LongMap> BISHOP_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
    public static final Object2ObjectMap<Square, Long2LongMap> ROOK_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
    // Additional structures are required for efficient lookup of sliding piece moves.
    public static final Object2LongMap<Square> BISHOP_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> ROOK_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);

    static {
        // Initialize simple movement/capture bitmasks.
        for (Square square : Square.values()) {
            WHITE_PAWN_ADVANCES.put(square, getWhitePawnMovementBitmaskForSquare(square));
            WHITE_PAWN_CAPTURES.put(square, getWhitePawnCaptureBitmaskForSquare(square));
            BLACK_PAWN_ADVANCES.put(square, getBlackPawnMovementBitmaskForSquare(square));
            BLACK_PAWN_CAPTURES.put(square, getBlackPawnCaptureBitmaskForSquare(square));
            KNIGHT_MOVES.put(square, getKnightMovementBitmaskForSquare(square));
            KING_MOVES.put(square, getKingMovementBitmaskForSquare(square));
        }

        // Initialize bishop movements.
        for (Square square : Square.values()) {
            // Initialize bishop magics.
            long bishopBlockerBitmask = getBishopBlockerBitmaskForSquare(square);
            BISHOP_BLOCKER_MASKS.put(square, bishopBlockerBitmask);
            LongSet bishopBlockerPermutations = getBlockerBitmaskPermutationsByBlockerBitmask(bishopBlockerBitmask);
            Long2LongMap bishopMovesByBlockerBitmask = new Long2LongOpenHashMap(bishopBlockerPermutations.size(), 1.0F);
            for (long blockerBitmask : bishopBlockerPermutations) {
                long movementBitmask = getBishopMovementForBlockerBitmask(blockerBitmask);
                bishopMovesByBlockerBitmask.put(blockerBitmask, movementBitmask);
            }
            BISHOP_MOVES.put(square, bishopMovesByBlockerBitmask);
        }

        // Initialize rook movements.
        for (Square square : Square.values()) {
            long rookBlockerBitmask = getRookBlockerBitmaskForSquare(square);
            ROOK_BLOCKER_MASKS.put(square, rookBlockerBitmask);
            LongSet rookBlockerPermutations = getBlockerBitmaskPermutationsByBlockerBitmask(rookBlockerBitmask);
            Long2LongMap rookMovesByBlockerBitmask = new Long2LongOpenHashMap(rookBlockerPermutations.size(), 1.0F);
            for (long blockerBitmask : rookBlockerPermutations) {
                long movementBitmask = getRookMovementForBlockerBitmask(blockerBitmask);
                rookMovesByBlockerBitmask.put(blockerBitmask, movementBitmask);
            }
            ROOK_MOVES.put(square, rookMovesByBlockerBitmask);
        }
    }

    private static long getWhitePawnMovementBitmaskForSquare (Square square) {
        if (square.rank() == ONE || square.rank() == EIGHT) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long pawnMoveBitmask = squareBitmask << 8;
        if (square.rank() == TWO) {
            pawnMoveBitmask |= (squareBitmask << 16);
        }

        return pawnMoveBitmask;
    }

    private static long getWhitePawnCaptureBitmaskForSquare (Square square) {
        if (square.rank() == ONE || square.rank() == EIGHT) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long nextRankBitmask = square.rank()
                .bitmask() << 8;
        return ((squareBitmask << 7) | (squareBitmask << 9)) & nextRankBitmask;
    }

    private static long getBlackPawnMovementBitmaskForSquare (Square square) {
        if (square.rank() == ONE || square.rank() == EIGHT) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long pawnMoveBitmask = squareBitmask >>> 8;
        if (square.rank() == SEVEN) {
            pawnMoveBitmask |= (squareBitmask >>> 16);
        }

        return pawnMoveBitmask;
    }

    private static long getBlackPawnCaptureBitmaskForSquare (Square square) {
        if (square.rank() == ONE || square.rank() == EIGHT) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long nextRankBitmask = square.rank()
                .bitmask() >>> 8;

        return ((squareBitmask >>> 7) | (squareBitmask >>> 9)) & nextRankBitmask;
    }

    private static long getKnightMovementBitmaskForSquare (Square square) {
        long squareBitmask = square.bitmask();
        long knightBitmask =
                (squareBitmask << 6) | (squareBitmask << 10) | (squareBitmask << 15) | (squareBitmask << 17) |
                        (squareBitmask >>> 6) | (squareBitmask >>> 10) | (squareBitmask >>> 15) |
                        (squareBitmask >>> 17);

        // Exclude any invalid squares included as a result of bit-shifting that wrapped around the board.
        Rank rank = square.rank();
        File file = square.file();
        long rankBitmask = rank.bitmask();
        long fileBitmask = file.bitmask();
        long ranksBitmask =
                (rankBitmask << 16) | (rankBitmask << 8) | rankBitmask | (rankBitmask >>> 8) | (rankBitmask >>> 16);
        long filesBitmask = fileBitmask;
        if (file != A) {
            filesBitmask |= (fileBitmask >>> 1);
            if (file != B) {
                filesBitmask |= (fileBitmask >>> 2);
            }
        }
        if (file != H) {
            filesBitmask |= (fileBitmask << 1);
            if (file != G) {
                filesBitmask |= (fileBitmask << 2);
            }
        }
        long validBitmask = ranksBitmask & filesBitmask;
        knightBitmask &= validBitmask;

        return knightBitmask;
    }

    private static long getKingMovementBitmaskForSquare (Square square) {
        long rankBitmask = square.rank()
                .bitmask();
        long fileBitmask = square.file()
                .bitmask();
        long ranksBitmask = (rankBitmask << 8) | rankBitmask | (rankBitmask >>> 8);
        long filesBitmask = (fileBitmask << 1) | fileBitmask | (fileBitmask >>> 1);
        long kingBitmask = (ranksBitmask & filesBitmask) ^ square.bitmask();

        // Exclude any invalid squares included as a result of bit-shifting that wrapped around the board.
        if (square.file() == A) {
            kingBitmask &= ~H.bitmask();
        } else if (square.file() == H) {
            kingBitmask &= ~A.bitmask();
        }

        return kingBitmask;
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

    private static LongSet getBlockerBitmaskPermutationsByBlockerBitmask (long blockerBitmask) {
        // Total number of possible blocker bitmasks to generate = 2^(number of high bits) in blocker bitmask.
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

        // Iterate over all possible combinations of blockers for the blocker bitmask.
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

    private static long getBishopMovementForBlockerBitmask (long blockerBitmask) {
        // TODO - calculate bishop moves by blocker bitmask
        return 0L;
    }

    private static long getRookMovementForBlockerBitmask (long blockerBitmask) {
        // TODO - calculate bishop moves by blocker bitmask
        return 0L;
    }

    // This class should never be instantiated.
    private MovementBitmasks () {
    }
}
