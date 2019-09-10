package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;

/**
 * Represents the individual ranks on a chessboard.
 *
 * @author Maximilian Schroeder
 */
public enum Rank {
    ONE(0),
    TWO(1),
    THREE(2),
    FOUR(3),
    FIVE(4),
    SIX(5),
    SEVEN(6),
    EIGHT(7);

    private static final Int2ObjectMap<Rank> INDEX_TO_RANK_MAP;

    static {
        Rank[] ranks = Rank.values();
        INDEX_TO_RANK_MAP = new Int2ObjectOpenHashMap<>(ranks.length, 1.0F);
        for (Rank rank : ranks) {
            INDEX_TO_RANK_MAP.put(rank.index, rank);
        }
    }

    /**
     * The index of this {@link Rank} from bottom to top on a chessboard (i.e. - ONE = 0, ..., EIGHT = 7).
     */
    private final int index;
    /**
     * The 64-bit bitmask that represents this {@link Rank} on a bitboard.
     */
    public final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link Rank} from bottom to top on a chessboard (i.e. - ONE = 0, ..., EIGHT = 7).
     */
    Rank (int index) {
        this.index = index;
        this.bitmask = 0xFFL << (8 * index);
    }

    /**
     * Gets the {@link Rank} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link Rank}. Must be in range [0, 7].
     * @return The {@link Rank} corresponding the specified index value.
     */
    @Nonnull
    static Rank fromIndex (int index) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("index must be in range [0, 7].");
        }

        return INDEX_TO_RANK_MAP.get(index);
    }
}
