package com.maximilian.chess.objects;

import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Square;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.maximilian.chess.objects.Piece.Type.KING;
import static com.maximilian.chess.objects.Piece.Type.PAWN;

/**
 * Represents a piece on the chess board.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Piece {
    /**
     * The {@link Color} of the piece. Will never be null.
     */
    @Nonnull
    @Getter
    private final Color color;
    /**
     * The {@link Type} of the piece. Will never be null. May change (if this piece is a {@link Type#PAWN} and
     * promotes).
     */
    @Nonnull
    @Getter
    private Type type;
    /**
     * The {@link Square} where this piece is located. May be null (if this piece is not on the board).
     */
    @Nullable
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Square square;

    /**
     * Gets the {@link Color} and {@link Type} of this piece as an ordered {@link Pair}.
     *
     * @return The {@link Color} and {@link Type} of this piece as an ordered {@link Pair}. Will never be null.
     */
    @Nonnull
    public Pair<Color, Type> getColorTypePair () {
        return Pair.of(color, type);
    }

    /**
     * Promotes this piece to the specified {@link Type}. May only be called if this piece is a {@link Type#PAWN}.
     *
     * @param newType The new {@link Type} this piece will promote to. Must be one of the following values:
     *                {@link Type#QUEEN}, {@link Type#ROOK}, {@link Type#BISHOP}, {@link Type#KNIGHT}.
     */
    void promoteTo (@Nonnull Type newType) {
        if (type != PAWN) {
            throw new IllegalStateException("This piece (" + type + ") is not one that can promote.");
        }

        if (newType == PAWN || newType == KING) {
            throw new IllegalArgumentException("A pawn cannot be promoted to a " + newType);
        }

        this.type = newType;
    }

    /**
     * Undoes promotion on this piece (i.e. - sets its {@link Type} to {@link Type#PAWN}. May only be called if this
     * piece is one of the following {@link Type}s: {@link Type#QUEEN}, {@link Type#ROOK}, {@link Type#BISHOP},
     * {@link Type#KNIGHT}.
     */
    void undoPromote () {
        if (type == PAWN || type == KING) {
            throw new IllegalStateException("This piece (" + type + ") is not capable of undoing promotion.");
        }

        this.type = PAWN;
    }

    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @Accessors(fluent = true)
    public enum Type {
        PAWN(1),
        KNIGHT(2),
        BISHOP(3),
        ROOK(4),
        QUEEN(5),
        KING(6);

        private static final Int2ObjectMap<Type> ID_TO_PIECE_MAP;

        static {
            Type[] types = values();
            ID_TO_PIECE_MAP = new Int2ObjectOpenHashMap<>(types.length);
            for (Type type : types) {
                ID_TO_PIECE_MAP.put(type.id, type);
            }
        }

        /**
         * The numeric ID of the specific {@link Type}.
         */
        @Getter private final int id;

        /**
         * Gets the {@link Type} corresponding to the specified ID.
         *
         * @param id The ID corresponding to the desired {@link Type}. Must be in range [1, 6].
         * @return The {@link Type} corresponding to the specified ID. May be null (if the ID value is invalid).
         */
        @Nullable
        public static Type fromId (int id) {
            return ID_TO_PIECE_MAP.get(id);
        }
    }
}
