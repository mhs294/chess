package com.maximilian.chess.movegen.constants;

import com.maximilian.chess.enums.Diagonal;
import com.maximilian.chess.enums.File;
import com.maximilian.chess.enums.Rank;
import com.maximilian.chess.enums.Square;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maximilian.chess.enums.Diagonal.Direction.ASCENDING;
import static com.maximilian.chess.enums.Diagonal.Direction.DESCENDING;
import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.B;
import static com.maximilian.chess.enums.File.G;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.enums.Rank.SEVEN;
import static com.maximilian.chess.enums.Rank.TWO;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;

/**
 * Represents the constant bitmask values for how specific piece types can move when located at any particular square.
 *
 * @author Maximilian Schroeder
 */
public final class MovementBitmasks {
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
    public static final Object2LongMap<Square> BISHOP_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> ROOK_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    /*
     * The cardinal direction slides are the portions of the bishop/rook slides corresponding to their respective
     * direction on the board.
     */
    public static final Object2LongMap<Square> NORTH_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> EAST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> SOUTH_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> WEST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> NORTHWEST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> NORTHEAST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> SOUTHWEST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);
    public static final Object2LongMap<Square> SOUTHEAST_SLIDES = new Object2LongOpenHashMap<>(64, 1.0F);

    public enum CardinalDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        NORTHWEST,
        NORTHEAST,
        SOUTHWEST,
        SOUTHEAST
    }

    static {
        for (Square square : Square.values()) {
            WHITE_PAWN_ADVANCES.put(square, getWhitePawnMovementBitmaskForSquare(square));
            WHITE_PAWN_CAPTURES.put(square, getWhitePawnCaptureBitmaskForSquare(square));
            BLACK_PAWN_ADVANCES.put(square, getBlackPawnMovementBitmaskForSquare(square));
            BLACK_PAWN_CAPTURES.put(square, getBlackPawnCaptureBitmaskForSquare(square));
            KNIGHT_MOVES.put(square, getKnightMovementBitmaskForSquare(square));
            KING_MOVES.put(square, getKingMovementBitmaskForSquare(square));
            BISHOP_SLIDES.put(square, getBishopSlidingBitmaskForSquare(square));
            ROOK_SLIDES.put(square, getRookSlidingBitmaskForSquare(square));

            NORTH_SLIDES.put(square, getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.NORTH));
            EAST_SLIDES.put(square, getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.EAST));
            SOUTH_SLIDES.put(square, getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.SOUTH));
            WEST_SLIDES.put(square, getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.WEST));
            NORTHWEST_SLIDES.put(square,
                    getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.NORTHWEST));
            NORTHEAST_SLIDES.put(square,
                    getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.NORTHEAST));
            SOUTHWEST_SLIDES.put(square,
                    getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.SOUTHWEST));
            SOUTHEAST_SLIDES.put(square,
                    getCardinalDirectionSlidingBitmaskForSquare(square, CardinalDirection.SOUTHEAST));
        }
    }

    /**
     * Gets the bitmask representing the advancing moves a white pawn can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the white pawn is located. Cannot be null.
     * @return The bitmask representing the advancing moves a white pawn can make from the specified {@link Square}.
     */
    private static long getWhitePawnMovementBitmaskForSquare (@Nonnull Square square) {
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

    /**
     * Gets the bitmask representing the capturing moves a white pawn can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the white pawn is located. Cannot be null.
     * @return The bitmask representing the capturing moves a white pawn can make from the specified {@link Square}.
     */
    private static long getWhitePawnCaptureBitmaskForSquare (@Nonnull Square square) {
        if (square.rank() == EIGHT) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long nextRankBitmask = square.rank().bitmask() << 8;
        return ((squareBitmask << 7) | (squareBitmask << 9)) & nextRankBitmask;
    }

    /**
     * Gets the bitmask representing the advancing moves a black pawn can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the black pawn is located. Cannot be null.
     * @return The bitmask representing the advancing moves a black pawn can make from the specified {@link Square}.
     */
    private static long getBlackPawnMovementBitmaskForSquare (@Nonnull Square square) {
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

    /**
     * Gets the bitmask representing the capturing moves a black pawn can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the black pawn is located. Cannot be null.
     * @return The bitmask representing the capturing moves a black pawn can make from the specified {@link Square}.
     */
    private static long getBlackPawnCaptureBitmaskForSquare (@Nonnull Square square) {
        if (square.rank() == ONE) {
            return EMPTY_BITMASK;
        }

        long squareBitmask = square.bitmask();
        long nextRankBitmask = square.rank().bitmask() >>> 8;

        return ((squareBitmask >>> 7) | (squareBitmask >>> 9)) & nextRankBitmask;
    }

    /**
     * Gets the bitmask representing the moves a knight can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the knight is located. Cannot be null.
     * @return The bitmask representing the moves a knight can make from the specified {@link Square}.
     */
    private static long getKnightMovementBitmaskForSquare (@Nonnull Square square) {
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

    /**
     * Gets the bitmask representing the moves a king can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the king is located. Cannot be null.
     * @return The bitmask representing the moves a king can make from the specified {@link Square}.
     */
    private static long getKingMovementBitmaskForSquare (@Nonnull Square square) {
        long rankBitmask = square.rank().bitmask();
        long fileBitmask = square.file().bitmask();
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

    /**
     * Gets the bitmask representing the potential slides a bishop can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the bishop is located. Cannot be null.
     * @return The bitmask representing the potential slides a bishop can make from the specified {@link Square}.
     */
    private static long getBishopSlidingBitmaskForSquare (@Nonnull Square square) {
        Set<Diagonal> diagonals = Diagonal.fromSquare(square);
        long bishopBitmask = EMPTY_BITMASK;
        for (Diagonal diagonal : diagonals) {
            bishopBitmask |= diagonal.bitmask();
        }

        return bishopBitmask ^ square.bitmask();
    }

    /**
     * Gets the bitmask representing the potential slides a rook can make from the specified {@link Square}.
     *
     * @param square The {@link Square} where the rook is located. Cannot be null.
     * @return The bitmask representing the potential slides a rook can make from the specified {@link Square}.
     */
    private static long getRookSlidingBitmaskForSquare (@Nonnull Square square) {
        return (square.rank().bitmask() | square.file().bitmask()) ^ square.bitmask();
    }

    /**
     * Gets the bitmask representing the potential slides available starting from the specified {@link Square} and
     * proceeding in the specified {@link CardinalDirection} until a boundary of the board is reached.
     *
     * @param square    The {@link Square} from which the sliding bitmask will start. Cannot be null.
     * @param direction The {@link CardinalDirection} along which the sliding bitmask will be generated. Cannot be null.
     * @return The bitmask representing the potential slides available starting from the specified {@link Square} in
     * the specified {@link CardinalDirection}
     */
    private static long getCardinalDirectionSlidingBitmaskForSquare (@Nonnull Square square,
            CardinalDirection direction) {
        long squareBitmask = square.bitmask();
        long slideBitmask = squareBitmask;
        for (int i = 1; i <= 7; i++) {
            switch (direction) {
                case NORTH:
                    slideBitmask |= (slideBitmask << (i * 8));
                    break;
                case EAST:
                    slideBitmask |= (slideBitmask << (i));
                    break;
                case SOUTH:
                    slideBitmask |= (slideBitmask >>> (i * 8));
                    break;
                case WEST:
                    slideBitmask |= (slideBitmask >>> (i));
                    break;
                case NORTHWEST:
                    slideBitmask |= (slideBitmask << (i * 7));
                    break;
                case NORTHEAST:
                    slideBitmask |= (slideBitmask << (i * 9));
                    break;
                case SOUTHWEST:
                    slideBitmask |= (slideBitmask >>> (i * 9));
                    break;
                case SOUTHEAST:
                    slideBitmask |= (slideBitmask >>> (i * 7));
                    break;
            }
        }
        switch (direction) {
            case NORTH:
            case SOUTH:
                slideBitmask &= square.file().bitmask();
                break;
            case EAST:
            case WEST:
                slideBitmask &= square.rank().bitmask();
                break;
            case NORTHWEST:
            case SOUTHEAST:
                slideBitmask &= Diagonal.combinedBitmask(square.diagonals()
                        .stream()
                        .filter(diagonal -> diagonal.direction() == DESCENDING)
                        .collect(Collectors.toSet()));
                break;
            case NORTHEAST:
            case SOUTHWEST:
                slideBitmask &= Diagonal.combinedBitmask(square.diagonals()
                        .stream()
                        .filter(diagonal -> diagonal.direction() == ASCENDING)
                        .collect(Collectors.toSet()));
                break;
        }

        return slideBitmask & ~squareBitmask;
    }

    // This class should never be instantiated.
    private MovementBitmasks () {
    }
}
