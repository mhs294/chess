package com.maximilian.chess.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;

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

    private static final Int2ObjectMap<Piece> ID_TO_PIECE_MAP;

    static {
        Piece[] pieces = Piece.values();
        ID_TO_PIECE_MAP = new Int2ObjectOpenHashMap<>(pieces.length);
        for (Piece piece : pieces) {
            ID_TO_PIECE_MAP.put(piece.id, piece);
        }
    }

    /**
     * The numeric ID of the specific {@link Piece}.
     */
    @Getter private final int id;

    /**
     * Gets the {@link Piece} corresponding to the specified ID.
     *
     * @param id The ID corresponding to the desired {@link Piece}. Must be in range [1, 6].
     * @return The {@link Piece} corresponding to the specified ID. May be null (if the ID value is invalid).
     */
    @Nullable
    public static Piece fromId (int id) {
        return ID_TO_PIECE_MAP.get(id);
    }
}
