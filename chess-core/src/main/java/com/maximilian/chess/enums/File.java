package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;

/**
 * Represents the individual files on a chessboard.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
public enum File {
    A(0),
    B(1),
    C(2),
    D(3),
    E(4),
    F(5),
    G(6),
    H(7);

    private static final Int2ObjectMap<File> INDEX_TO_FILE_MAP;

    static {
        File[] files = File.values();
        INDEX_TO_FILE_MAP = new Int2ObjectOpenHashMap<>(files.length, 1.0F);
        for (File file : files) {
            INDEX_TO_FILE_MAP.put(file.index, file);
        }
    }

    /**
     * The index of this {@link File} from left to right on a chessboard (i.e. - A = 0, ..., H = 7).
     */
    private final int index;
    /**
     * The 64-bit bitmask that represents this {@link File} on a bitboard.
     */
    @Getter private final long bitmask;

    /**
     * (Primary constructor)
     *
     * @param index The index of this {@link File} from left to right on a chessboard (i.e. - A = 0, ..., H = 7).
     */
    File (int index) {
        this.index = index;
        this.bitmask = 0x0101010101010101L << index;
    }

    /**
     * Gets the {@link File} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link File}. Must be in range [0, 7].
     * @return The {@link File} corresponding the specified index value.
     */
    @Nonnull
    static File fromIndex (int index) {
        if (index < 0 || index > 7) {
            throw new IllegalArgumentException("index must be in range [0, 7].");
        }

        return INDEX_TO_FILE_MAP.get(index);
    }
}
