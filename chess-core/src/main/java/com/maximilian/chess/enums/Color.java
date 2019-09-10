package com.maximilian.chess.enums;

import com.maximilian.chess.objects.Move;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the different colors of pieces in a chess game.
 *
 * @author Maximilian Schroeder
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Color {
    WHITE(0),
    BLACK(1);

    private static final Int2ObjectMap<Color> INDEX_TO_COLOR_MAP;

    static {
        Color[] colors = Color.values();
        INDEX_TO_COLOR_MAP = new Int2ObjectOpenHashMap<>(colors.length, 1.0F);
        for (Color color : colors) {
            INDEX_TO_COLOR_MAP.put(color.index, color);
        }
    }

    /**
     * The index of the {@link Color} when used in {@link Move} bitmasks.
     */
    private final int index;

    /**
     * Gets the {@link Color} that is the opposite of this {@link Color}.
     *
     * @return The {@link Color} that is the opposite of this {@link Color}.
     */
    @Nonnull
    public Color opposite () {
        return (this == WHITE) ? BLACK : WHITE;
    }

    /**
     * Gets the {@link Color} corresponding to the specified index value.
     *
     * @param index The index corresponding to the desired {@link Color}. Must be in range [0, 1].
     * @return The {@link Color} corresponding to the specified index value. May be null (if the index value is
     * invalid).
     */
    @Nullable
    public static Color fromIndex (int index) {
        return INDEX_TO_COLOR_MAP.get(index);
    }
}
