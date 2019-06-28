package com.maximilian.chess.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the different types of chess pieces and their respective inherent properties.
 *
 * @author Maximilian Schroeder
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
public enum Piece {
    PAWN(1),
    KNIGHT(2),
    BISHOP(3),
    ROOK(4),
    QUEEN(5),
    KING(6);

    private static final Map<Integer, Piece> ID_TO_PIECE_MAP;

    static {
        ID_TO_PIECE_MAP = new HashMap<>(Piece.values().length);
        for (Piece piece : Piece.values()) {
            ID_TO_PIECE_MAP.put(piece.id(), piece);
        }
    }

    /**
     * Gets the numeric ID of the specific {@link Piece}.
     *
     * @return The numeric ID of the specific {@link Piece}.
     */
    @Getter private final int id;

    /**
     * Gets the {@link Piece} corresponding to the specified ID.
     *
     * @param id The ID corresponding to the desired {@link Piece}. Must be in range [1, 6].
     * @return The {@link Piece} corresponding to the specified ID (or null if no corresponding {@link Piece} exists).
     */
    public static Piece fromId (int id) {
        return ID_TO_PIECE_MAP.get(id);
    }
}
