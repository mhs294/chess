package com.maximilian.chess.movegen.magic;

import com.maximilian.chess.enums.Diagonal;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.movegen.MoveGenerator;
import com.maximilian.chess.objects.Board;
import com.maximilian.chess.objects.GameState;
import com.maximilian.chess.objects.Move;
import com.maximilian.chess.util.BitboardUtils;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BISHOP_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.ROOK_SLIDES;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;

/**
 * A {@link MoveGenerator} implementation that generates moves using Magic Bitboards.
 *
 * @author Maximilian Schroeder
 */
public class MagicBitboardMoveGenerator implements MoveGenerator {
    @Nonnull
    @Override
    public List<Move> generateAllMoves (@Nonnull Board board, @Nonnull GameState gameState) {
        return null;
    }

    @Nonnull
    @Override
    public List<Move> generateNonQuietMoves (@Nonnull Board board, @Nonnull GameState gameState) {
        return null;
    }

    /**
     * Represents the constant bitmask values for all possible moves for a sliding piece (e.g. - bishop, rook) for
     * all squares and any possible combination of blocking pieces.
     */
    static class MagicMovementBitmasks {
        static final Object2ObjectMap<Square, Long2LongMap> BISHOP_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
        static final Object2ObjectMap<Square, Long2LongMap> ROOK_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
        static final Object2LongMap<Square> BISHOP_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);
        static final Object2LongMap<Square> ROOK_BLOCKER_MASKS = new Object2LongOpenHashMap<>(64, 1.0F);

        static {
            // Initialize bishop movements.
            for (Square square : Square.values()) {
                // Initialize bishop magics.
                long bishopBlockerBitmask = getBishopBlockerBitmaskForSquare(square);
                BISHOP_BLOCKER_MASKS.put(square, bishopBlockerBitmask);
                LongSet bishopBlockerPermutations = getBlockerBitmaskPermutationsByBlockerBitmask(bishopBlockerBitmask);
                Long2LongMap bishopMovesByBlockerBitmask = new Long2LongOpenHashMap(bishopBlockerPermutations.size(),
                        1.0F);
                for (long blockerBitmask : bishopBlockerPermutations) {
                    long movementBitmask = getBishopMovementForSquareAndBlockerBitmask(square, blockerBitmask);
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
                    long movementBitmask = getRookMovementForSquareAndBlockerBitmask(square, blockerBitmask);
                    rookMovesByBlockerBitmask.put(blockerBitmask, movementBitmask);
                }
                ROOK_MOVES.put(square, rookMovesByBlockerBitmask);
            }
        }

        /**
         * Gets the blocker bitmask representing the {@link Square}s where a blocking piece can impact the movement of a
         * bishop located on the specified {@link Square}.
         *
         * @param square The {@link Square} where the bishop is located. Cannot be null.
         * @return The blocker bitmask representing the {@link Square}s where a blocking piece can impact the
         * movement of
         * a bishop located on the specified {@link Square}.
         */
        private static long getBishopBlockerBitmaskForSquare (@Nonnull Square square) {
            long bishopBlockerBitmask = BISHOP_SLIDES.getLong(square);

            // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
            bishopBlockerBitmask &= ~A.bitmask();
            bishopBlockerBitmask &= ~H.bitmask();
            bishopBlockerBitmask &= ~ONE.bitmask();
            bishopBlockerBitmask &= ~EIGHT.bitmask();

            return bishopBlockerBitmask;
        }

        /**
         * Gets the blocker bitmask representing the {@link Square}s where a blocking piece can impact the movement of a
         * rook located on the specified {@link Square}.
         *
         * @param square The {@link Square} where the rook is located. Cannot be null.
         * @return The blocker bitmask representing the {@link Square}s where a blocking piece can impact the
         * movement of
         * a rook located on the specified {@link Square}.
         */
        private static long getRookBlockerBitmaskForSquare (@Nonnull Square square) {
            long rookBlockerBitmask = ROOK_SLIDES.getLong(square);

            // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
            rookBlockerBitmask &= (~A.bitmask() | square.file()
                    .bitmask());
            rookBlockerBitmask &= (~H.bitmask() | square.file()
                    .bitmask());
            rookBlockerBitmask &= (~ONE.bitmask() | square.rank()
                    .bitmask());
            rookBlockerBitmask &= (~EIGHT.bitmask() | square.rank()
                    .bitmask());

            return rookBlockerBitmask;
        }

        /**
         * Gets the set of all blocker bitmask permutations for the specified blocker bitmask.
         *
         * @param blockerBitmask The blocker bitmask to get all blocker permutations for.
         * @return A {@link LongSet} representing the set of all blocker bitmask permutations for the specified blocker
         * bitmask. Will never be null.
         */
        @Nonnull
        private static LongSet getBlockerBitmaskPermutationsByBlockerBitmask (long blockerBitmask) {
            // Total number of possible blocker bitmasks to generate = 2^(number of high bits) in blocker bitmask.
            int highBitsInBlockerBitmask = BitboardUtils.countHighBitsInBitmask(blockerBitmask);
            int totalBlockerOccupancyBitmasks = 1 << highBitsInBlockerBitmask;

            // Determine individual values of high bits in blocker bitmask.
            LongList bitValues = new LongArrayList(highBitsInBlockerBitmask);
            for (int i = 0; blockerBitmask >>> i != 0L; i++) {
                long currentBitValue = blockerBitmask >>> i;
                if ((currentBitValue & 1L) == 1L) {
                    bitValues.add(blockerBitmask & (1L << i));
                }
            }

            // Iterate over all possible combinations of blockers for the blocker bitmask.
            LongSet blockerOccupancyBitmasks = new LongOpenHashSet(totalBlockerOccupancyBitmasks);
            for (int i = 0; i < totalBlockerOccupancyBitmasks; i++) {
                long occupancyBitmask = blockerBitmask;
                for (int k = 0; i >>> k != 0; k++) {
                    if (((i >>> k) & 1) == 1) {
                        occupancyBitmask -= bitValues.getLong(k);
                    }
                }
                blockerOccupancyBitmasks.add(occupancyBitmask);
            }

            return blockerOccupancyBitmasks;
        }

        /**
         * Gets the bitmask representing the moves a bishop on the specified {@link Square} can make, given a specified
         * blocker bitmask representing the presence of blocking pieces that could impact the bishop's movement.
         *
         * @param square         The {@link Square} where the bishop is located. Cannot be null.
         * @param blockerBitmask The blocker bitmask representing squares on the board where piece's that could hinder
         *                       the bishop's movement exist.
         * @return The bitmask representing the moves a bishop on the specified {@link Square} can make, taking into
         * consideration potential blocking pieces.
         */
        private static long getBishopMovementForSquareAndBlockerBitmask (@Nonnull Square square, long blockerBitmask) {
            Set<Diagonal> diagonals = Diagonal.fromSquare(square);
            long diagonalsBitmask = EMPTY_BITMASK;
            for (Diagonal diagonal : diagonals) {
                diagonalsBitmask |= diagonal.bitmask();
            }
            long bishopMovesBitmask = diagonalsBitmask ^= square.bitmask();

            // Modify northwest bishop movements to account for blockers.
            boolean blockerFound = false;
            for (long bitmask = square.bitmask() << 7; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask <<= 7) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify northeast bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() << 9; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask <<= 9) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify southwest bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() >>> 7; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask >>>= 7) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify southeast bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() >>> 9; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask >>>= 9) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            return bishopMovesBitmask;
        }

        /**
         * Gets the bitmask representing the moves a rook on the specified {@link Square} can make, given a specified
         * blocker bitmask representing the presence of blocking pieces that could impact the rook's movement.
         *
         * @param square         The {@link Square} where the rook is located. Cannot be null.
         * @param blockerBitmask The blocker bitmask representing squares on the board where piece's that could hinder
         *                       the rook's movement exist.
         * @return The bitmask representing the moves a rook on the specified {@link Square} can make, taking into
         * consideration potential blocking pieces.
         */
        private static long getRookMovementForSquareAndBlockerBitmask (@Nonnull Square square, long blockerBitmask) {
            long rankBitmask = square.rank()
                    .bitmask();
            long fileBitmask = square.file()
                    .bitmask();
            long rookMovesBitmask = rankBitmask ^ fileBitmask;

            // Modify west rook movements to account for blockers.
            boolean blockerFound = false;
            for (long bitmask = square.bitmask() << 1; (bitmask & rankBitmask) != EMPTY_BITMASK; bitmask <<= 1) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify east rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() >>> 1; (bitmask & rankBitmask) != EMPTY_BITMASK; bitmask >>>= 1) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify north rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() << 8; (bitmask & fileBitmask) != EMPTY_BITMASK; bitmask <<= 8) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify south rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask() >>> 8; (bitmask & fileBitmask) != EMPTY_BITMASK; bitmask >>>= 8) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            return rookMovesBitmask;
        }
    }
}
