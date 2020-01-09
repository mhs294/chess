package com.maximilian.chess.objects;

import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Square;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Verify.verify;
import static com.google.common.base.Verify.verifyNotNull;
import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H1;
import static com.maximilian.chess.enums.Square.H8;
import static com.maximilian.chess.objects.Move.Bitmask.CAPTURED_PIECE_TYPE_START;
import static com.maximilian.chess.objects.Move.Bitmask.COLOR_MASK;
import static com.maximilian.chess.objects.Move.Bitmask.COLOR_START;
import static com.maximilian.chess.objects.Move.Bitmask.END_START;
import static com.maximilian.chess.objects.Move.Bitmask.IS_EN_PASSANT_MASK;
import static com.maximilian.chess.objects.Move.Bitmask.IS_EN_PASSANT_SIZE;
import static com.maximilian.chess.objects.Move.Bitmask.IS_EN_PASSANT_START;
import static com.maximilian.chess.objects.Move.Bitmask.PIECE_MASK;
import static com.maximilian.chess.objects.Move.Bitmask.PIECE_TYPE_SIZE;
import static com.maximilian.chess.objects.Move.Bitmask.PIECE_TYPE_START;
import static com.maximilian.chess.objects.Move.Bitmask.PROMOTE_TO_START;
import static com.maximilian.chess.objects.Move.Bitmask.SQUARE_MASK;
import static com.maximilian.chess.objects.Move.Bitmask.SQUARE_SIZE;
import static com.maximilian.chess.objects.Move.Bitmask.START_START;
import static com.maximilian.chess.objects.Piece.Type.KING;
import static com.maximilian.chess.objects.Piece.Type.PAWN;
import static com.maximilian.chess.objects.Piece.Type.ROOK;

/**
 * Represents a single chess move. The move information is stored as a 32-bit bitmask in memory, but convenience
 * APIs are provided in this class to allow for easy serialization/deserialization of information to/from the
 * underlying bitmask. This class doesn't perform any validation with regards to the move information passed in to
 * create a move (such logic should be handled by consuming classes).
 *
 * @author Maximilian Schroeder
 */
@EqualsAndHashCode
public class Move {
    public static final Move WHITE_KINGSIDE_CASTLE = create(WHITE, KING, E1, G1);
    public static final Move WHITE_QUEENSIDE_CASTLE = create(WHITE, KING, E1, C1);
    public static final Move BLACK_KINGSIDE_CASTLE = create(BLACK, KING, E8, G8);
    public static final Move BLACK_QUEENSIDE_CASTLE = create(BLACK, KING, E8, C8);

    // Protected access rook movements as a part of castling.
    static final Move WHITE_KINGSIDE_CASTLE_ROOK = create(WHITE, ROOK, H1, F1);
    static final Move WHITE_QUEENSIDE_CASTLE_ROOK = create(WHITE, ROOK, A1, D1);
    static final Move BLACK_KINGSIDE_CASTLE_ROOK = create(BLACK, ROOK, H8, F8);
    static final Move BLACK_QUEENSIDE_CASTLE_ROOK = create(BLACK, ROOK, A8, D8);

    /*
     * A move is represented using a 32-bit bitmask value in order to conserve heap space, since millions of these
     * objects will be generated during each turn. The structure of the bitmask is as follows:
     *
     * |-0-| |a| |---b--| |---c--| |-d-| |-e-| |-f-| |g| |---h--|
     *  000   0   000000   000000   000   000   000   0   000000
     *
     * 0 = (unused, always 0)
     * a = color making move (0 = white, 1 = black)
     * b = start square (0 = A1, 63 = H8)
     * c = end square (0 = A1, 63 = H8)
     * d = piece being moved (1 = P, 2 = N, 3 = B, 4 = R, 5 = Q, 6 = K)
     * e = piece being captured (1 = P, 2 = N, 3 = B, 4 = R, 5 = Q)
     * f = piece being promoted to (2 = N, 3 = B, 4 = R, 5 = Q)
     * g = is capture en passant? (0 = false, 1 = true)
     * h = en passant square (0 = A1, 63 = H8)
     */
    private final int bitmask;

    /**
     * Internal class containing constants used for serializing/deserializing information to/from the bitmask.
     */
    static final class Bitmask {
        static final int COLOR_MASK = 0x01;
        static final int SQUARE_MASK = 0x3F;
        static final int PIECE_MASK = 0x07;
        static final int IS_EN_PASSANT_MASK = 0x01;
        static final int SQUARE_SIZE = 6;
        static final int PIECE_TYPE_SIZE = 3;
        static final int IS_EN_PASSANT_SIZE = 1;
        static final int COLOR_START = 28;
        static final int START_START = 22;
        static final int END_START = 16;
        static final int PIECE_TYPE_START = 13;
        static final int CAPTURED_PIECE_TYPE_START = 10;
        static final int PROMOTE_TO_START = 7;
        static final int IS_EN_PASSANT_START = 6;
    }

    /**
     * Constructs a new {@link Move} (declared private to prevent direct instantiation).
     *
     * @param color                  The {@link Color} of the piece being moved. Cannot be null.
     * @param pieceType              The {@link Piece.Type} of the piece that is being moved. Cannot be null.
     * @param capturedType           The {@link Piece.Type} of the piece being captured as a result of the move
     *                               (optional). May be null.
     * @param start                  The starting {@link Square} of the move. Cannot be null.
     * @param end                    The ending {@link Square} of the move. Cannot be null.
     * @param promoteTo              The {@link Piece.Type} being promoted to as a result of the move (optional). May be
     *                               null.
     * @param enPassantCaptureSquare The {@link Square} from which an opposing pawn was captured via en passant
     *                               (optional). May be null.
     */
    private Move (@Nonnull Color color, @Nonnull Piece.Type pieceType, @Nullable Piece.Type capturedType,
            @Nonnull Square start, @Nonnull Square end, @Nullable Piece.Type promoteTo,
            @Nullable Square enPassantCaptureSquare) {
        int bitmask = 0;

        bitmask += (color == BLACK) ? 1 : 0;
        bitmask <<= SQUARE_SIZE;
        bitmask += start.ordinal();
        bitmask <<= SQUARE_SIZE;
        bitmask += end.ordinal();
        bitmask <<= PIECE_TYPE_SIZE;
        bitmask += pieceType.id();
        bitmask <<= PIECE_TYPE_SIZE;
        if (capturedType != null) {
            bitmask += capturedType.id();
        }
        bitmask <<= PIECE_TYPE_SIZE;
        if (promoteTo != null) {
            bitmask += promoteTo.id();
        }
        bitmask <<= IS_EN_PASSANT_SIZE;
        if (enPassantCaptureSquare != null) {
            bitmask += 1;
        }
        bitmask <<= SQUARE_SIZE;
        if (enPassantCaptureSquare != null) {
            bitmask += enPassantCaptureSquare.ordinal();
        }

        this.bitmask = bitmask;
    }

    /**
     * Constructs a new {@link Move} that doesn't involve either any capture or a promotion.
     *
     * @param color The {@link Color} of the player making the move. Cannot be null.
     * @param type  The {@link Piece.Type} of the piece that is being moved. Cannot be null.
     * @param start The starting {@link Square} of the move. Cannot be null.
     * @param end   The ending {@link Square} of the move. Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    private static Move create (@Nonnull Color color, @Nonnull Piece.Type type, @Nonnull Square start,
            @Nonnull Square end) {
        verifyNotNull(color, "color cannot be null.");
        verifyNotNull(type, "piece cannot be null.");
        verifyNotNull(start, "start cannot be null.");
        verifyNotNull(end, "end cannot be null.");

        return new Move(color, type, null, start, end, null, null);
    }

    /**
     * Constructs a new {@link Move} that doesn't involve either any capture or a promotion.
     *
     * @param piece The {@link Piece} that is being moved. Cannot be null.
     * @param end   The ending {@link Square} of the move. Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    public static Move create (@Nonnull Piece piece, @Nonnull Square end) {
        Square start = piece.square();
        verifyNotNull(piece, "piece cannot be null.");
        verifyNotNull(start, "piece must be on the board.");
        verifyNotNull(end, "end cannot be null.");

        return new Move(piece.color(), piece.type(), null, start, end, null, null);
    }

    /**
     * Constructs a new {@link Move} that involves an en passant capture.
     *
     * @param piece                  The {@link Piece} that is being moved. Cannot be null.
     * @param end                    The ending {@link Square} of the move. Cannot be null.
     * @param enPassantCaptureSquare The {@link Square} from which an opposing pawn was captured via en passant.
     *                               Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    public static Move createEnPassant (@Nonnull Piece piece, @Nonnull Square end,
            @Nonnull Square enPassantCaptureSquare) {
        Square start = piece.square();
        verifyNotNull(piece, "piece cannot be null.");
        verifyNotNull(piece.square(), "piece must be on the board.");
        verify(piece.type() == PAWN, "piece must be a PAWN for en passant capture.");
        verifyNotNull(end, "end cannot be null.");
        verifyNotNull(enPassantCaptureSquare, "enPassantCaptureSquare cannot be null.");

        return new Move(piece.color(), PAWN, PAWN, start, end, null, enPassantCaptureSquare);
    }

    /**
     * Constructs a new {@link Move} that involves a standard capture, but not a promotion.
     *
     * @param piece        The {@link Piece} that is being moved. Cannot be null.
     * @param capturedType The {@link Piece.Type} of the piece being captured as a result of the move. Cannot be null.
     * @param end          The ending {@link Square} of the move. Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    public static Move createCapture (@Nonnull Piece piece, @Nonnull Piece.Type capturedType, @Nonnull Square end) {
        Square start = piece.square();
        verifyNotNull(piece, "piece cannot be null.");
        verifyNotNull(start, "piece must be on the board.");
        verifyNotNull(capturedType, "capturedPiece cannot be null.");
        verifyNotNull(end, "end cannot be null.");

        return new Move(piece.color(), piece.type(), capturedType, start, end, null, null);
    }

    /**
     * Constructs a new {@link Move} that involves a promotion, but not a standard capture.
     *
     * @param piece     The {@link Piece} that is being moved. Cannot be null.
     * @param end       The ending {@link Square} of the move. Cannot be null.
     * @param promoteTo The {@link Piece.Type} being promoted to as a result of the move. Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    public static Move createPromotion (@Nonnull Piece piece, @Nonnull Square end, @Nonnull Piece.Type promoteTo) {
        Square start = piece.square();
        verifyNotNull(piece, "piece cannot be null.");
        verifyNotNull(start, "piece must be on the board.");
        verify(piece.type() == PAWN, "piece must be a PAWN for promotion.");
        verifyNotNull(end, "end cannot be null.");
        verifyNotNull(promoteTo, "promoteTo cannot be null.");

        return new Move(piece.color(), PAWN, null, start, end, promoteTo, null);
    }

    /**
     * Constructs a new {@link Move} that involves a promotion, but not a standard capture.
     *
     * @param piece        The {@link Piece} that is being moved. Cannot be null.
     * @param capturedType The {@link Piece.Type} of the piece being captured as a result of the move. Cannot be null.
     * @param end          The ending {@link Square} of the move. Cannot be null.
     * @param promoteTo    The {@link Piece.Type} being promoted to as a result of the move. Cannot be null.
     * @return A new {@link Move} containing the specified information. Will never be null.
     */
    @Nonnull
    public static Move createCapturePromotion (@Nonnull Piece piece, @Nonnull Piece.Type capturedType,
            @Nonnull Square end, @Nonnull Piece.Type promoteTo) {
        Square start = piece.square();
        verifyNotNull(piece, "piece cannot be null.");
        verifyNotNull(start, "piece must be on the board.");
        verifyNotNull(piece.type() == PAWN, "piece must be a PAWN for capture promotion.");
        verifyNotNull(capturedType, "capturedPiece cannot be null.");
        verifyNotNull(end, "end cannot be null.");
        verifyNotNull(promoteTo, "promoteTo cannot be null.");

        return new Move(piece.color(), PAWN, capturedType, start, end, promoteTo, null);
    }

    /**
     * Gets the {@link Color} of the player making the move. Will never be null.
     *
     * @return The {@link Color} of the player making the move. Will never be null.
     */
    @Nonnull
    public Color color () {
        Color color = Color.fromIndex((bitmask >>> COLOR_START) & COLOR_MASK);
        if (color == null) {
            throw new IllegalStateException("move contained an invalid color bitmask: " + Long.toHexString(bitmask));
        }
        return color;
    }

    /**
     * Gets the starting {@link Square} of the move. Will never be null.
     *
     * @return The starting {@link Square} of the move. Will never be null.
     */
    @Nonnull
    public Square start () {
        Square square = Square.fromIndex((bitmask >>> START_START) & SQUARE_MASK);
        if (square == null) {
            throw new IllegalStateException("move contained an invalid start bitmask: " + Long.toHexString(bitmask));
        }
        return square;
    }

    /**
     * Gets the ending {@link Square} of the move. Will never be null.
     *
     * @return The ending {@link Square} of the move. Will never be null.
     */
    @Nonnull
    public Square end () {
        Square square = Square.fromIndex((bitmask >>> END_START) & SQUARE_MASK);
        if (square == null) {
            throw new IllegalStateException("move contained an invalid end bitmask: " + Long.toHexString(bitmask));
        }
        return square;
    }

    /**
     * Gets the {@link Piece.Type} being moved. Will never be null.
     *
     * @return The {@link Piece.Type} being moved. Will never be null.
     */
    @Nonnull
    public Piece.Type piece () {
        Piece.Type type = Piece.Type.fromId((bitmask >>> PIECE_TYPE_START) & PIECE_MASK);
        if (type == null) {
            throw new IllegalStateException("move contained an invalid piece bitmask: " + Long.toHexString(bitmask));
        }
        return type;
    }

    /**
     * Gets the {@link Piece.Type} being captured. May be null (if no piece is being captured).
     *
     * @return The {@link Piece.Type} being captured. May be null (if no piece is being captured).
     */
    @Nullable
    public Piece.Type capturedPiece () {
        return Piece.Type.fromId((bitmask >>> CAPTURED_PIECE_TYPE_START) & PIECE_MASK);
    }

    /**
     * Gets the {@link Piece.Type} being promoted to. May be null (if no promotion is occurring).
     *
     * @return The {@link Piece.Type} being promoted to. May be null (if no promotion is occurring).
     */
    @Nullable
    public Piece.Type promoteTo () {
        return Piece.Type.fromId((bitmask >>> PROMOTE_TO_START) & PIECE_MASK);
    }

    /**
     * Gets the {@link Square} from which the opposing pawn was captured via en passant. May be null (if no en
     * passant is occurring).
     *
     * @return The {@link Square} from which the opposing pawn was captured via en passant. May be null (if no en
     * passant is occurring).
     */
    @Nullable
    public Square enPassantCaptureSquare () {
        boolean isEnPassant = ((bitmask >>> IS_EN_PASSANT_START) & IS_EN_PASSANT_MASK) != 0;
        return (isEnPassant) ? Square.fromIndex(bitmask & SQUARE_MASK) : null;
    }

    /**
     * Determines whether or not this {@link Move} is castling.
     *
     * @return True if this {@link Move} is castling, false otherwise.
     */
    public boolean isCastling () {
        return this.equals(WHITE_KINGSIDE_CASTLE) ||
                this.equals(WHITE_QUEENSIDE_CASTLE) ||
                this.equals(BLACK_KINGSIDE_CASTLE) ||
                this.equals(BLACK_QUEENSIDE_CASTLE);
    }

    @Override
    public String toString () {
        if (this == WHITE_KINGSIDE_CASTLE) {
            return "WHITE KINGSIDE CASTLE";
        } else if (this == WHITE_QUEENSIDE_CASTLE) {
            return "WHITE QUEENSIDE CASTLE";
        } else if (this == BLACK_KINGSIDE_CASTLE) {
            return "BLACK KINGSIDE CASTLE";
        } else if (this == BLACK_QUEENSIDE_CASTLE) {
            return "BLACK QUEENSIDE CASTLE";
        }

        String moveString = color().toString() +
                " " +
                piece().toString() +
                " " +
                start().toString() +
                "-" +
                end().toString();
        Piece.Type capturedType = capturedPiece();
        if (capturedType != null) {
            moveString += ", Capture: " + color().opposite().toString() + " " + capturedType.toString();
            Square enPassantCaptureSquare = enPassantCaptureSquare();
            if (enPassantCaptureSquare != null) {
                moveString += " (en-passant on " + enPassantCaptureSquare.toString() + ")";
            }
        }
        Piece.Type promoteTo = promoteTo();
        if (promoteTo != null) {
            moveString += ", Promotion to " + promoteTo.toString();
        }

        return moveString;
    }
}
