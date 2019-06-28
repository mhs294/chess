package com.maximilian.chess.enums;

/**
 * Represents the different colors of pieces in a chess game.
 *
 * @author Maximilian Schroeder
 */
public enum Color {
    WHITE,
    BLACK;

    /**
     * Gets the {@link Color} that is the opposite of this {@link Color}.
     *
     * @return The {@link Color} that is the opposite of this {@link Color}.
     */
    public Color opposite () {
        return (this == WHITE) ? BLACK : WHITE;
    }

    /**
     * Gets the {@link Color} corresponding to the specified ordinal value.
     *
     * @param ordinal The ordinal corresponding to the desired {@link Color}. Must be in range [0, 1].
     * @return The {@link Color} corresponding to the specified ordinal value.
     */
    public static Color fromOrdinal (int ordinal) {
        return Color.values()[ordinal];
    }
}
