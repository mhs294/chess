package com.maximilian.chess.objects;

import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Piece;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.exception.IllegalMoveException;
import com.maximilian.chess.util.BitboardUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.Piece.BISHOP;
import static com.maximilian.chess.enums.Piece.KING;
import static com.maximilian.chess.enums.Piece.KNIGHT;
import static com.maximilian.chess.enums.Piece.PAWN;
import static com.maximilian.chess.enums.Piece.QUEEN;
import static com.maximilian.chess.enums.Piece.ROOK;
import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A2;
import static com.maximilian.chess.enums.Square.A7;
import static com.maximilian.chess.enums.Square.A8;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B2;
import static com.maximilian.chess.enums.Square.B7;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C2;
import static com.maximilian.chess.enums.Square.C7;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D2;
import static com.maximilian.chess.enums.Square.D7;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E2;
import static com.maximilian.chess.enums.Square.E7;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F2;
import static com.maximilian.chess.enums.Square.F7;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G2;
import static com.maximilian.chess.enums.Square.G7;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H1;
import static com.maximilian.chess.enums.Square.H2;
import static com.maximilian.chess.enums.Square.H7;
import static com.maximilian.chess.enums.Square.H8;
import static com.maximilian.chess.enums.Square.Type.DARK;
import static com.maximilian.chess.enums.Square.Type.LIGHT;
import static com.maximilian.chess.objects.Move.BLACK_KINGSIDE_CASTLE;
import static com.maximilian.chess.objects.Move.BLACK_KINGSIDE_CASTLE_ROOK;
import static com.maximilian.chess.objects.Move.BLACK_QUEENSIDE_CASTLE_ROOK;
import static com.maximilian.chess.objects.Move.WHITE_KINGSIDE_CASTLE;
import static com.maximilian.chess.objects.Move.WHITE_KINGSIDE_CASTLE_ROOK;
import static com.maximilian.chess.objects.Move.WHITE_QUEENSIDE_CASTLE;
import static com.maximilian.chess.objects.Move.WHITE_QUEENSIDE_CASTLE_ROOK;

/**
 * Represents a single state of a chessboard using bitboards. The state of this class is designed to be mutable to
 * allow for simple representation of state change during recursive searching. This class also provides convenience
 * APIs for easy serialization/deserialization of information to/from the underlying bitboards.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
@EqualsAndHashCode
public class Board {
    /*
     * Convenient bitmask constants to use with bitboard operations.
     */
    public static final long EMPTY_BITMASK = 0L;
    public static final long FULL_BITMASK = Long.MIN_VALUE | Long.MAX_VALUE;
    public static final long LIGHT_SQUARES_BITMASK;
    public static final long DARK_SQUARES_BITMASK;

    static {
        long lightSquaresBitmask = EMPTY_BITMASK;
        long darkSquaresBitmask = EMPTY_BITMASK;
        for (Square square : Square.values()) {
            if (square.type() == LIGHT) {
                lightSquaresBitmask |= square.bitmask();
            } else if (square.type() == DARK) {
                darkSquaresBitmask |= square.bitmask();
            }
        }
        LIGHT_SQUARES_BITMASK = lightSquaresBitmask;
        DARK_SQUARES_BITMASK = darkSquaresBitmask;
    }

    /*
     * A bitboard is represented by a simple 64-bit bitmask value to keep the process of traversing move search trees
     * as efficient as possible. Each bit represents a single square on the chessboard, with the most significant bit
     * representing H8 and the least significant bit representing A1. Each color/piece combination has its own bitmask.
     * A high bit represents a piece of that type occupying the square corresponding to the high bit. Conversely, low
     * bits represent squares that do not contain a piece of that color/type. This means that combining all of these
     * bitmask values with the logical OR operator will result in a bitmask value representing all occupied squares on
     * the chessboard. Similar bitwise operations can be executed on these bitmask values to derive other information
     * about the state of the chessboard (e.g. - empty squares, squares occupied by white pieces, etc.).
     */
    /**
     * The 64-bit bitmask representing the squares occupied by white pawns.
     */
    @Getter private long whitePawnsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by white knights.
     */
    @Getter private long whiteKnightsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by white bishops.
     */
    @Getter private long whiteBishopsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by white rooks.
     */
    @Getter private long whiteRooksBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by white queens.
     */
    @Getter private long whiteQueensBitmask;
    /**
     * The 64-bit bitmask representing the square occupied by the white king.
     */
    @Getter private long whiteKingBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by black pawns.
     */
    @Getter private long blackPawnsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by black knights.
     */
    @Getter private long blackKnightsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by black bishops.
     */
    @Getter private long blackBishopsBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by black rooks.
     */
    @Getter private long blackRooksBitmask;
    /**
     * The 64-bit bitmask representing the squares occupied by black queens.
     */
    @Getter private long blackQueensBitmask;
    /**
     * The 64-bit bitmask representing the square occupied by the black king.
     */
    @Getter private long blackKingBitmask;

    /**
     * Primary constructor (declared private to prevent direct instantiation).
     *
     * @param builder The {@link Builder} whose state will be used to construct the {@link Board}. Cannot be null.
     */
    private Board (@Nonnull Builder builder) {
        this.whitePawnsBitmask = builder.whitePawns;
        this.whiteKnightsBitmask = builder.whiteKnights;
        this.whiteBishopsBitmask = builder.whiteBishops;
        this.whiteRooksBitmask = builder.whiteRooks;
        this.whiteQueensBitmask = builder.whiteQueens;
        this.whiteKingBitmask = builder.whiteKing;
        this.blackPawnsBitmask = builder.blackPawns;
        this.blackKnightsBitmask = builder.blackKnights;
        this.blackBishopsBitmask = builder.blackBishops;
        this.blackRooksBitmask = builder.blackRooks;
        this.blackQueensBitmask = builder.blackQueens;
        this.blackKingBitmask = builder.blackKing;
    }

    /**
     * Constructs a new {@link Board} as a deep copy of an existing board (declared private to prevent direct
     * instantiation).
     *
     * @param board The {@link Board} to deep copy. Cannot be null.
     */
    private Board (@Nonnull Board board) {
        this.whitePawnsBitmask = board.whitePawnsBitmask;
        this.whiteKnightsBitmask = board.whiteKnightsBitmask;
        this.whiteBishopsBitmask = board.whiteBishopsBitmask;
        this.whiteRooksBitmask = board.whiteRooksBitmask;
        this.whiteQueensBitmask = board.whiteQueensBitmask;
        this.whiteKingBitmask = board.whiteKingBitmask;
        this.blackPawnsBitmask = board.blackPawnsBitmask;
        this.blackKnightsBitmask = board.blackKnightsBitmask;
        this.blackBishopsBitmask = board.blackBishopsBitmask;
        this.blackRooksBitmask = board.blackRooksBitmask;
        this.blackQueensBitmask = board.blackQueensBitmask;
        this.blackKingBitmask = board.blackKingBitmask;
    }

    /**
     * Creates a new {@link Builder} that can be used to construct a {@link Board}. Will never be null.
     *
     * @return A new {@link Builder} that can be used to construct a {@link Board}. Will never be null.
     */
    @Nonnull
    public static Builder builder () {
        return new Builder();
    }

    /**
     * Creates a new {@link Board} configured for the starting position of a chess game. Will never be null.
     *
     * @return A new {@link Board} configured for the starting position of a chess game. Will never be null.
     */
    @Nonnull
    public static Board createStartingBoard () {
        return Board.builder()
                .addWhitePawns(A2, B2, C2, D2, E2, F2, G2, H2)
                .addWhiteKnights(B1, G1)
                .addWhiteBishops(C1, F1)
                .addWhiteRooks(A1, H1)
                .addWhiteQueens(D1)
                .setWhiteKing(E1)
                .addBlackPawns(A7, B7, C7, D7, E7, F7, G7, H7)
                .addBlackKnights(B8, G8)
                .addBlackBishops(C8, F8)
                .addBlackRooks(A8, H8)
                .addBlackQueens(D8)
                .setBlackKing(E8)
                .build();
    }

    /**
     * Creates a new {@link Board} that is a deep copy of this {@link Board}. Will never be null.
     *
     * @return A new {@link Board} that is a deep copy of this {@link Board}. Will never be null.
     */
    @Nonnull
    public Board deepCopy () {
        return new Board(this);
    }

    /**
     * Creates a {@link Map} representation of the state of the {@link Board}.
     *
     * @return A {@link Map} representation of the state of the {@link Board}, using {@link Square} keys and
     * {@link Pair} values (which consist of {@link Color} and {@link Piece}). Only occupied squares will be present
     * in the map's key set, thus, if {@code null} is returned from {@link Map#get(Object)}, it is implied that the
     * specified {@link Square} is vacant.
     */
    @Nonnull
    public Map<Square, Pair<Color, Piece>> toMap () {
        Map<Square, Pair<Color, Piece>> squareToPieceMap = new HashMap<>(32);
        for (Square square : Square.values()) {
            if ((square.bitmask() & occupiedBitmask()) == 0L) {
                // The current square is vacant.
                continue;
            }

            if ((square.bitmask() & whiteOccupiedBitmask()) != 0L) {
                // The piece in the current square is white.
                if ((square.bitmask() & whitePawnsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, PAWN));
                } else if ((square.bitmask() & whiteKnightsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, KNIGHT));
                } else if ((square.bitmask() & whiteBishopsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, BISHOP));
                } else if ((square.bitmask() & whiteRooksBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, ROOK));
                } else if ((square.bitmask() & whiteQueensBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, QUEEN));
                } else if ((square.bitmask() & whiteKingBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(WHITE, KING));
                }
            } else if ((square.bitmask() & blackOccupiedBitmask()) != 0L) {
                // The piece in the current square is black.
                if ((square.bitmask() & blackPawnsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, PAWN));
                } else if ((square.bitmask() & blackKnightsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, KNIGHT));
                } else if ((square.bitmask() & blackBishopsBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, BISHOP));
                } else if ((square.bitmask() & blackRooksBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, ROOK));
                } else if ((square.bitmask() & blackQueensBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, QUEEN));
                } else if ((square.bitmask() & blackKingBitmask) != 0L) {
                    squareToPieceMap.put(square, Pair.of(BLACK, KING));
                }
            }
        }

        return squareToPieceMap;
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing white pawns.
     *
     * @return The {@link Set} of {@link Square}s containing white pawns. Will never be null, may be empty (if the
     * {@link Board} contains no white pawns).
     */
    @Nonnull
    public Set<Square> whitePawnSquares () {
        return BitboardUtils.getSquaresFromBitmask(whitePawnsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing white knights.
     *
     * @return The {@link Set} of {@link Square}s containing white knights. Will never be null, may be empty (if the
     * {@link Board} contains no white knights).
     */
    @Nonnull
    public Set<Square> whiteKnightSquares () {
        return BitboardUtils.getSquaresFromBitmask(whiteKnightsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing white bishops.
     *
     * @return The {@link Set} of {@link Square}s containing white bishops. Will never be null, may be empty (if the
     * {@link Board} contains no white bishops).
     */
    @Nonnull
    public Set<Square> whiteBishopSquares () {
        return BitboardUtils.getSquaresFromBitmask(whiteBishopsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing white rooks.
     *
     * @return The {@link Set} of {@link Square}s containing white rooks. Will never be null, may be empty (if the
     * {@link Board} contains no white rooks).
     */
    @Nonnull
    public Set<Square> whiteRookSquares () {
        return BitboardUtils.getSquaresFromBitmask(whiteRooksBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing white queens.
     *
     * @return The {@link Set} of {@link Square}s containing white queens. Will never be null, may be empty (if the
     * {@link Board} contains no white queens).
     */
    @Nonnull
    public Set<Square> whiteQueenSquares () {
        return BitboardUtils.getSquaresFromBitmask(whiteQueensBitmask);
    }

    /**
     * Gets the {@link Square} containing the white king.
     *
     * @return The {@link Square} containing the white king. Will never be null.
     */
    @Nonnull
    public Square whiteKingSquare () {
        Square square = Square.fromBitmask(whiteKingBitmask);
        if (square == null) {
            throw new IllegalStateException("board does not contain a white king.");
        }

        return square;
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing black pawns.
     *
     * @return The {@link Set} of {@link Square}s containing black pawns. Will never be null, may be empty (if the
     * {@link Board} contains no black pawns).
     */
    @Nonnull
    public Set<Square> blackPawnSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackPawnsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing black knights.
     *
     * @return The {@link Set} of {@link Square}s containing black knights. Will never be null, may be empty (if the
     * {@link Board} contains no black knights).
     */
    @Nonnull
    public Set<Square> blackKnightSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackKnightsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing black bishops.
     *
     * @return The {@link Set} of {@link Square}s containing black bishops. Will never be null, may be empty (if the
     * {@link Board} contains no black bishops).
     */
    @Nonnull
    public Set<Square> blackBishopSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackBishopsBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing black rooks.
     *
     * @return The {@link Set} of {@link Square}s containing black rooks. Will never be null, may be empty (if the
     * {@link Board} contains no black rooks).
     */
    @Nonnull
    public Set<Square> blackRookSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackRooksBitmask);
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing black queens.
     *
     * @return The {@link Set} of {@link Square}s containing black queens. Will never be null, may be empty (if the
     * {@link Board} contains no black queens).
     */
    @Nonnull
    public Set<Square> blackQueenSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackQueensBitmask);
    }

    /**
     * Gets the {@link Square} containing the black king.
     *
     * @return The {@link Square} containing the black king. Will never be null.
     */
    @Nonnull
    public Square blackKingSquare () {
        Square square = Square.fromBitmask(blackKingBitmask);
        if (square == null) {
            throw new IllegalStateException("board does not contain a black king.");
        }

        return square;
    }

    /**
     * Gets a bitmask representing all of the occupied squares on the {@link Board}.
     *
     * @return A bitmask representing all of the occupied squares on the {@link Board}.
     */
    public long occupiedBitmask () {
        return whiteOccupiedBitmask() | blackOccupiedBitmask();
    }

    /**
     * Gets the {@link Set} of occupied {@link Square}s on the {@link Board}.
     *
     * @return The {@link Set} of occupied {@link Square}s on the {@link Board}. Will never be null, may be empty (if
     * the {@link Board} is empty).
     */
    public Set<Square> occupiedSquares () {
        return BitboardUtils.getSquaresFromBitmask(occupiedBitmask());
    }

    /**
     * Gets a bitmask representing all of the vacant squares on the {@link Board}.
     *
     * @return A bitmask representing all of the vacant squares on the {@link Board}.
     */
    public long vacantBitmask () {
        return ~occupiedBitmask();
    }

    /**
     * Gets the {@link Set} of vacant {@link Square}s on the {@link Board}.
     *
     * @return The {@link Set} of vacant {@link Square}s on the {@link Board}. Will never be null, may be empty (if
     * the {@link Board} is full).
     */
    public Set<Square> vacantSquares () {
        return BitboardUtils.getSquaresFromBitmask(vacantBitmask());
    }

    /**
     * Gets a bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     *
     * @return A bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     */
    public long whiteOccupiedBitmask () {
        return whitePawnsBitmask | whiteKnightsBitmask | whiteBishopsBitmask | whiteRooksBitmask | whiteQueensBitmask |
                whiteKingBitmask;
    }

    /**
     * Gets the {@link Set} of {@link Square}s occupied by white pieces on the {@link Board}.
     *
     * @return The {@link Set} of {@link Square}s occupied by white pieces on the {@link Board}. Will never be null,
     * may be empty (if the {@link Board} has no white pieces).
     */
    public Set<Square> whiteOccupiedSquares () {
        return BitboardUtils.getSquaresFromBitmask(whiteOccupiedBitmask());
    }

    /**
     * Gets a bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     *
     * @return A bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     */
    public long blackOccupiedBitmask () {
        return blackPawnsBitmask | blackKnightsBitmask | blackBishopsBitmask | blackRooksBitmask | blackQueensBitmask |
                blackKingBitmask;
    }

    /**
     * Gets the {@link Set} of {@link Square}s occupied by black pieces on the {@link Board}.
     *
     * @return The {@link Set} of {@link Square}s occupied by black pieces on the {@link Board}. Will never be null,
     * may be empty (if the {@link Board} has no black pieces).
     */
    public Set<Square> blackOccupiedSquares () {
        return BitboardUtils.getSquaresFromBitmask(blackOccupiedBitmask());
    }

    /**
     * Gets the number of white pawns on the {@link Board}.
     *
     * @return The number of white pawns on the {@link Board}.
     */
    public int whitePawnsCount () {
        return BitboardUtils.countHighBitsInBitmask(whitePawnsBitmask);
    }

    /**
     * Gets the number of white knights on the {@link Board}.
     *
     * @return The number of white knights on the {@link Board}.
     */
    public int whiteKnightsCount () {
        return BitboardUtils.countHighBitsInBitmask(whiteKnightsBitmask);
    }

    /**
     * Gets the number of white bishops on the {@link Board}.
     *
     * @return The number of white bishops on the {@link Board}.
     */
    public int whiteBishopsCount () {
        return BitboardUtils.countHighBitsInBitmask(whiteBishopsBitmask);
    }

    /**
     * Gets the number of white rooks on the {@link Board}.
     *
     * @return The number of white rooks on the {@link Board}.
     */
    public int whiteRooksCount () {
        return BitboardUtils.countHighBitsInBitmask(whiteRooksBitmask);
    }

    /**
     * Gets the number of white queens on the {@link Board}.
     *
     * @return The number of white queens on the {@link Board}.
     */
    public int whiteQueensCount () {
        return BitboardUtils.countHighBitsInBitmask(whiteQueensBitmask);
    }

    /**
     * Gets the total number of white pieces on the {@link Board}.
     *
     * @return The total number of white pieces on the {@link Board}.
     */
    public int whitePiecesCount () {
        return BitboardUtils.countHighBitsInBitmask(whiteOccupiedBitmask());
    }

    /**
     * Gets the number of black pawns on the {@link Board}.
     *
     * @return The number of black pawns on the {@link Board}.
     */
    public int blackPawnsCount () {
        return BitboardUtils.countHighBitsInBitmask(blackPawnsBitmask);
    }

    /**
     * Gets the number of black knights on the {@link Board}.
     *
     * @return The number of black knights on the {@link Board}.
     */
    public int blackKnightsCount () {
        return BitboardUtils.countHighBitsInBitmask(blackKnightsBitmask);
    }

    /**
     * Gets the number of black bishops on the {@link Board}.
     *
     * @return The number of black bishops on the {@link Board}.
     */
    public int blackBishopsCount () {
        return BitboardUtils.countHighBitsInBitmask(blackBishopsBitmask);
    }

    /**
     * Gets the number of black rooks on the {@link Board}.
     *
     * @return The number of black rooks on the {@link Board}.
     */
    public int blackRooksCount () {
        return BitboardUtils.countHighBitsInBitmask(blackRooksBitmask);
    }

    /**
     * Gets the number of black queens on the {@link Board}.
     *
     * @return The number of black queens on the {@link Board}.
     */
    public int blackQueensCount () {
        return BitboardUtils.countHighBitsInBitmask(blackQueensBitmask);
    }

    /**
     * Gets the total number of black pieces on the {@link Board}.
     *
     * @return The total number of black pieces on the {@link Board}.
     */
    public int blackPiecesCount () {
        return BitboardUtils.countHighBitsInBitmask(blackOccupiedBitmask());
    }

    /**
     * Mutates the {@link Board} by performing the specified move.
     *
     * @param move The {@link Move} to perform on the state of the {@link Board}. Cannot be null.
     * @throws IllegalMoveException If the specified {@link Move} cannot be performed on the current state of the
     *                              {@link Board}.
     */
    public void doMove (@Nonnull Move move) {
        // TODO - move validation to separate utility class?
        validateMove(move);

        // Remove the moved piece from its starting position.
        if (!removePieceFromSquare(move.color(), move.piece(), move.start())) {
            throw new IllegalMoveException(move, "The move's piece was not at the specified starting square.");
        }

        // If a capture needs to be performed, remove the piece being captured.
        Piece capturedPiece = move.capturedPiece();
        if (capturedPiece != null) {
            Square enPassantCaptureSquare = move.enPassantCaptureSquare();
            Square captureSquare = (enPassantCaptureSquare != null) ? enPassantCaptureSquare : move.end();
            if (!removePieceFromSquare(move.color()
                    .opposite(), capturedPiece, captureSquare)) {
                throw new IllegalMoveException(move, "The move's captured piece was not at the specified square.");
            }
        }

        // Add the moved piece to its ending position.
        Piece promoteTo = move.promoteTo();
        Piece endingPiece = (promoteTo != null) ? promoteTo : move.piece();
        if (!addPieceToSquare(move.color(), endingPiece, move.end())) {
            throw new IllegalMoveException(move, "Could not add the move's piece to the specified ending square.");
        }

        // If the move was a castling move, perform the rook movement in addition to the king movement.
        if (move.isCastling()) {
            Move rookMove;
            if (move.equals(WHITE_KINGSIDE_CASTLE)) {
                rookMove = WHITE_KINGSIDE_CASTLE_ROOK;
            } else if (move.equals(WHITE_QUEENSIDE_CASTLE)) {
                rookMove = WHITE_QUEENSIDE_CASTLE_ROOK;
            } else if (move.equals(BLACK_KINGSIDE_CASTLE)) {
                rookMove = BLACK_KINGSIDE_CASTLE_ROOK;
            } else {
                rookMove = BLACK_QUEENSIDE_CASTLE_ROOK;
            }

            if (!removePieceFromSquare(rookMove.color(), rookMove.piece(), rookMove.start())) {
                throw new IllegalMoveException(rookMove,
                        "The rook could not be removed from its starting square for castling.");
            }
            if (!addPieceToSquare(rookMove.color(), rookMove.piece(), rookMove.end())) {
                throw new IllegalMoveException(rookMove,
                        "The rook could not be added to its ending square for castling.");
            }
        }
    }

    /**
     * Mutates the {@link Board} by undoing the specified move.
     *
     * @param move The {@link Move} to undo on the state of the {@link Board}. Cannot be null.
     * @throws IllegalMoveException If the specified {@link Move} cannot be undone on the current state of the
     *                              {@link Board}.
     */
    public void undoMove (@Nonnull Move move) {
        // TODO - move validation to separate utility class?
        validateMove(move);

        // Remove the moved piece from its ending position.
        Piece promoteTo = move.promoteTo();
        Piece endingPiece = (promoteTo != null) ? promoteTo : move.piece();
        if (!removePieceFromSquare(move.color(), endingPiece, move.end())) {
            throw new IllegalMoveException(move, "The move's ending piece was not at the specified ending square.");
        }

        // If a piece was captured, add it back to the ending position.
        Piece capturedPiece = move.capturedPiece();
        if (capturedPiece != null) {
            Square captureSquare = (move.enPassantCaptureSquare() != null) ? move.enPassantCaptureSquare() : move.end();
            if (captureSquare == null || !addPieceToSquare(move.color()
                    .opposite(), capturedPiece, captureSquare)) {
                throw new IllegalMoveException(move,
                        "The move's captured piece could not be added to its original square.");
            }
        }

        // Add the moved piece to its starting position.
        if (!addPieceToSquare(move.color(), move.piece(), move.start())) {
            throw new IllegalMoveException(move, "Could not add the move's piece to the specified starting square.");
        }

        // If the move was a castling move, undo the rook movement in addition to the king movement.
        if (move.isCastling()) {
            Move rookMove;
            if (move.equals(WHITE_KINGSIDE_CASTLE)) {
                rookMove = WHITE_KINGSIDE_CASTLE_ROOK;
            } else if (move.equals(WHITE_QUEENSIDE_CASTLE)) {
                rookMove = WHITE_QUEENSIDE_CASTLE_ROOK;
            } else if (move.equals(BLACK_KINGSIDE_CASTLE)) {
                rookMove = BLACK_KINGSIDE_CASTLE_ROOK;
            } else {
                rookMove = BLACK_QUEENSIDE_CASTLE_ROOK;
            }

            if (!removePieceFromSquare(rookMove.color(), rookMove.piece(), rookMove.end())) {
                throw new IllegalMoveException(rookMove,
                        "The rook could not be removed from its ending square for castling.");
            }
            if (!addPieceToSquare(rookMove.color(), rookMove.piece(), rookMove.start())) {
                throw new IllegalMoveException(rookMove,
                        "The rook could not be added to its starting square for castling.");
            }
        }
    }

    /**
     * Performs basic validation on the specified {@link Move} to ensure that it can be executed on the {@link Board}.
     *
     * @param move The {@link Move} to validate.
     * @throws IllegalMoveException If the specified {@link Move} is null or cannot be executed on the {@link Board}.
     */
    private void validateMove (@Nullable Move move) {
        if (move == null) {
            throw new IllegalArgumentException("move cannot be null.");
        } else if (move.start() == move.end()) {
            throw new IllegalMoveException(move, "move start and end cannot be identical.");
        } else if (move.capturedPiece() == KING) {
            throw new IllegalMoveException(move, "capturedPiece cannot be KING.");
        } else if (move.promoteTo() == PAWN || move.promoteTo() == KING) {
            throw new IllegalMoveException(move, "promoteTo cannot be PAWN or KING.");
        }
    }

    /**
     * Adds the specified {@link Piece} of the specified {@link Color} to the specified {@link Square}.
     *
     * @param color  The {@link Color} of the piece to add. Cannot be null.
     * @param piece  The {@link Piece} to add. Cannot be null.
     * @param square The {@link Square} where the piece will be added to. Cannot be null.
     * @return True if the {@link Piece} was successfully added to the {@link Square}, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // This method is intended to be consumed with the "!" operator.
    private boolean addPieceToSquare (@Nonnull Color color, @Nonnull Piece piece, @Nonnull Square square) {
        if ((square.bitmask() & vacantBitmask()) == 0L) {
            // The square we are trying to add the piece to is occupied.
            return false;
        }

        long addBitmask = square.bitmask();
        switch (color) {
            case WHITE:
                switch (piece) {
                    case PAWN:
                        whitePawnsBitmask |= addBitmask;
                        break;
                    case KNIGHT:
                        whiteKnightsBitmask |= addBitmask;
                        break;
                    case BISHOP:
                        whiteBishopsBitmask |= addBitmask;
                        break;
                    case ROOK:
                        whiteRooksBitmask |= addBitmask;
                        break;
                    case QUEEN:
                        whiteQueensBitmask |= addBitmask;
                        break;
                    case KING:
                        whiteKingBitmask |= addBitmask;
                        break;
                }
                break;
            case BLACK:
                switch (piece) {
                    case PAWN:
                        blackPawnsBitmask |= addBitmask;
                        break;
                    case KNIGHT:
                        blackKnightsBitmask |= addBitmask;
                        break;
                    case BISHOP:
                        blackBishopsBitmask |= addBitmask;
                        break;
                    case ROOK:
                        blackRooksBitmask |= addBitmask;
                        break;
                    case QUEEN:
                        blackQueensBitmask |= addBitmask;
                        break;
                    case KING:
                        blackKingBitmask |= addBitmask;
                        break;
                }
                break;
        }

        return true;
    }

    /**
     * Removes the specified {@link Piece} of the specified {@link Color} from the specified {@link Square}.
     *
     * @param color  The {@link Color} of the piece to remove. Cannot be null.
     * @param piece  The {@link Piece} to remove. Cannot be null.
     * @param square The {@link Square} where the piece will be removed from. Cannot be null.
     * @return True if the {@link Piece} was successfully removed from the {@link Square}, false otherwise.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // This method is intended to be consumed with the "!" operator.
    private boolean removePieceFromSquare (@Nonnull Color color, @Nonnull Piece piece, @Nonnull Square square) {
        long removeBitmask = ~square.bitmask();
        switch (color) {
            case WHITE:
                switch (piece) {
                    case PAWN:
                        if ((square.bitmask() & whitePawnsBitmask) == 0L) {
                            // There is no white pawn at the square we are trying to remove it from.
                            return false;
                        }
                        whitePawnsBitmask &= removeBitmask;
                        break;
                    case KNIGHT:
                        if ((square.bitmask() & whiteKnightsBitmask) == 0L) {
                            // There is no white knight at the square we are trying to remove it from.
                            return false;
                        }
                        whiteKnightsBitmask &= removeBitmask;
                        break;
                    case BISHOP:
                        if ((square.bitmask() & whiteBishopsBitmask) == 0L) {
                            // There is no white bishop at the square we are trying to remove it from.
                            return false;
                        }
                        whiteBishopsBitmask &= removeBitmask;
                        break;
                    case ROOK:
                        if ((square.bitmask() & whiteRooksBitmask) == 0L) {
                            // There is no white rook at the square we are trying to remove it from.
                            return false;
                        }
                        whiteRooksBitmask &= removeBitmask;
                        break;
                    case QUEEN:
                        if ((square.bitmask() & whiteQueensBitmask) == 0L) {
                            // There is no white queen at the square we are trying to remove it from.
                            return false;
                        }
                        whiteQueensBitmask &= removeBitmask;
                        break;
                    case KING:
                        if ((square.bitmask() & whiteKingBitmask) == 0L) {
                            // There is no white king at the square we are trying to remove it from.
                            return false;
                        }
                        whiteKingBitmask &= removeBitmask;
                        break;
                }
                break;
            case BLACK:
                switch (piece) {
                    case PAWN:
                        if ((square.bitmask() & blackPawnsBitmask) == 0L) {
                            // There is no black pawn at the square we are trying to remove it from.
                            return false;
                        }
                        blackPawnsBitmask &= removeBitmask;
                        break;
                    case KNIGHT:
                        if ((square.bitmask() & blackKnightsBitmask) == 0L) {
                            // There is no black knight at the square we are trying to remove it from.
                            return false;
                        }
                        blackKnightsBitmask &= removeBitmask;
                        break;
                    case BISHOP:
                        if ((square.bitmask() & blackBishopsBitmask) == 0L) {
                            // There is no black bishop at the square we are trying to remove it from.
                            return false;
                        }
                        blackBishopsBitmask &= removeBitmask;
                        break;
                    case ROOK:
                        if ((square.bitmask() & blackRooksBitmask) == 0L) {
                            // There is no black rook at the square we are trying to remove it from.
                            return false;
                        }
                        blackRooksBitmask &= removeBitmask;
                        break;
                    case QUEEN:
                        if ((square.bitmask() & blackQueensBitmask) == 0L) {
                            // There is no black queen at the square we are trying to remove it from.
                            return false;
                        }
                        blackQueensBitmask &= removeBitmask;
                        break;
                    case KING:
                        if ((square.bitmask() & blackKingBitmask) == 0L) {
                            // There is no black king at the square we are trying to remove it from.
                            return false;
                        }
                        blackKingBitmask &= removeBitmask;
                        break;
                }
                break;
        }

        return true;
    }

    /**
     * Represents a builder class for constructing a {@link Board}.
     */
    public static final class Builder {
        private long whitePawns = 0x0000000000000000L;
        private long whiteKnights = 0x0000000000000000L;
        private long whiteBishops = 0x0000000000000000L;
        private long whiteRooks = 0x0000000000000000L;
        private long whiteQueens = 0x0000000000000000L;
        private long whiteKing = 0x0000000000000000L;
        private long blackPawns = 0x0000000000000000L;
        private long blackKnights = 0x0000000000000000L;
        private long blackBishops = 0x0000000000000000L;
        private long blackRooks = 0x0000000000000000L;
        private long blackQueens = 0x0000000000000000L;
        private long blackKing = 0x0000000000000000L;

        /**
         * Adds white pawns to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white pawns to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addWhitePawns (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.whitePawns |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white knights to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white knights to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addWhiteKnights (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.whiteKnights |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white bishops to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white bishops to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addWhiteBishops (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.whiteBishops |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white rooks to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white rooks to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addWhiteRooks (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.whiteRooks |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white queens to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white queens to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addWhiteQueens (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.whiteQueens |= square.bitmask();
            }
            return this;
        }

        /**
         * Sets the white king at the specified {@link Square}.
         *
         * @param square The {@link Square} to set the white king to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder setWhiteKing (@Nonnull Square square) {
            this.whiteKing = square.bitmask();
            return this;
        }

        /**
         * Adds black pawns to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black pawns to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addBlackPawns (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.blackPawns |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black knights to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black knights to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addBlackKnights (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.blackKnights |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black bishops to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black bishops to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addBlackBishops (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.blackBishops |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black rooks to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black rooks to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addBlackRooks (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.blackRooks |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black queens to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black queens to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder addBlackQueens (@Nonnull Square... squares) {
            for (Square square : squares) {
                this.blackQueens |= square.bitmask();
            }
            return this;
        }

        /**
         * Sets the black king at the specified {@link Square}.
         *
         * @param square The {@link Square} to set the black king to. Cannot be null.
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder setBlackKing (@Nonnull Square square) {
            this.blackKing = square.bitmask();
            return this;
        }

        /**
         * Creates a new {@link Board} constructed from the state of this {@link Builder}.
         *
         * @return A new {@link Board} constructed from the state of this {@link Builder}. Will never be null.
         */
        @Nonnull
        public Board build () {
            return new Board(this);
        }
    }
}
