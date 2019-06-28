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

import java.util.HashMap;
import java.util.Map;

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
    public static final long EMPTY_BITMASK = 0x0000000000000000L;
    public static final long FULL_BITMASK = 0xFFFFFFFFFFFFFFFFL;
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
     * @param builder The {@link Builder} whose state will be used to construct the {@link Board}.
     */
    private Board (Builder builder) {
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
     * @param board The {@link Board} to deep copy.
     */
    private Board (Board board) {
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
    public static Builder builder () {
        return new Builder();
    }

    /**
     * Creates a new {@link Board} configured for the starting position of a chess game. Will never be null.
     *
     * @return A new {@link Board} configured for the starting position of a chess game. Will never be null.
     */
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
     * Creates a bitmask representing all of the occupied squares on the {@link Board}.
     *
     * @return A bitmask representing all of the occupied squares on the {@link Board}.
     */
    public long occupiedBitmask () {
        return whiteOccupiedBitmask() | blackOccupiedBitmask();
    }

    /**
     * Creates a bitmask representing all of the vacant squares on the {@link Board}.
     *
     * @return A bitmask representing all of the vacant squares on the {@link Board}.
     */
    public long vacantBitmask () {
        return ~occupiedBitmask();
    }

    /**
     * Creates a bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     *
     * @return A bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     */
    public long whiteOccupiedBitmask () {
        return whitePawnsBitmask | whiteKnightsBitmask | whiteBishopsBitmask | whiteRooksBitmask | whiteQueensBitmask |
                whiteKingBitmask;
    }

    /**
     * Creates a bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     *
     * @return A bitmask representing all of the squares on the {@link Board} occupied by white pieces.
     */
    public long blackOccupiedBitmask () {
        return blackPawnsBitmask | blackKnightsBitmask | blackBishopsBitmask | blackRooksBitmask | blackQueensBitmask |
                blackKingBitmask;
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
    public void doMove (Move move) {
        // TODO - move validation to separate utility class?
        validateMove(move);

        // Remove the moved piece from its starting position.
        if (!removePieceFromSquare(move.color(), move.piece(), move.start())) {
            throw new IllegalMoveException(move, "The move's piece was not at the specified starting square.");
        }

        // If a capture needs to be performed, remove the piece being captured.
        if (move.capturedPiece() != null) {
            Square captureSquare = (move.enPassantCaptureSquare() != null) ? move.enPassantCaptureSquare() : move.end();
            if (!removePieceFromSquare(move.color()
                    .opposite(), move.capturedPiece(), captureSquare)) {
                throw new IllegalMoveException(move, "The move's captured piece was not at the specified square.");
            }
        }

        // Add the moved piece to its ending position.
        Piece endingPiece = (move.promoteTo() != null) ? move.promoteTo() : move.piece();
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
    public void undoMove (Move move) {
        validateMove(move);

        // Remove the moved piece from its ending position.
        Piece endingPiece = (move.promoteTo() != null) ? move.promoteTo() : move.piece();
        if (!removePieceFromSquare(move.color(), endingPiece, move.end())) {
            throw new IllegalMoveException(move, "The move's ending piece was not at the specified ending square.");
        }

        // If a piece was captured, add it back to the ending position.
        if (move.capturedPiece() != null) {
            Square captureSquare = (move.enPassantCaptureSquare() != null) ? move.enPassantCaptureSquare() : move.end();
            if (!addPieceToSquare(move.color()
                    .opposite(), move.capturedPiece(), captureSquare)) {
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
    private void validateMove (Move move) {
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
     * @param color  The {@link Color} of the piece to add.
     * @param piece  The {@link Piece} to add.
     * @param square The {@link Square} where the piece will be added to.
     * @return True if the {@link Piece} was successfully added to the {@link Square}, false otherwise.
     */
    private boolean addPieceToSquare (Color color, Piece piece, Square square) {
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
     * @param color  The {@link Color} of the piece to remove.
     * @param piece  The {@link Piece} to remove.
     * @param square The {@link Square} where the piece will be removed from.
     * @return True if the {@link Piece} was successfully removed from the {@link Square}, false otherwise.
     */
    private boolean removePieceFromSquare (Color color, Piece piece, Square square) {
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
         * @param squares The {@link Square}s to add white pawns to.
         * @return The updated {@link Builder}.
         */
        public Builder addWhitePawns (Square... squares) {
            for (Square square : squares) {
                this.whitePawns |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white knights to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white knights to.
         * @return The updated {@link Builder}.
         */
        public Builder addWhiteKnights (Square... squares) {
            for (Square square : squares) {
                this.whiteKnights |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white bishops to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white bishops to.
         * @return The updated {@link Builder}.
         */
        public Builder addWhiteBishops (Square... squares) {
            for (Square square : squares) {
                this.whiteBishops |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white rooks to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white rooks to.
         * @return The updated {@link Builder}.
         */
        public Builder addWhiteRooks (Square... squares) {
            for (Square square : squares) {
                this.whiteRooks |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds white queens to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add white queens to.
         * @return The updated {@link Builder}.
         */
        public Builder addWhiteQueens (Square... squares) {
            for (Square square : squares) {
                this.whiteQueens |= square.bitmask();
            }
            return this;
        }

        /**
         * Sets the white king at the specified {@link Square}.
         *
         * @param square The {@link Square} to set the white king to.
         * @return The updated {@link Builder}.
         */
        public Builder setWhiteKing (Square square) {
            this.whiteKing = square.bitmask();
            return this;
        }

        /**
         * Adds black pawns to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black pawns to.
         * @return The updated {@link Builder}.
         */
        public Builder addBlackPawns (Square... squares) {
            for (Square square : squares) {
                this.blackPawns |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black knights to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black knights to.
         * @return The updated {@link Builder}.
         */
        public Builder addBlackKnights (Square... squares) {
            for (Square square : squares) {
                this.blackKnights |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black bishops to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black bishops to.
         * @return The updated {@link Builder}.
         */
        public Builder addBlackBishops (Square... squares) {
            for (Square square : squares) {
                this.blackBishops |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black rooks to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black rooks to.
         * @return The updated {@link Builder}.
         */
        public Builder addBlackRooks (Square... squares) {
            for (Square square : squares) {
                this.blackRooks |= square.bitmask();
            }
            return this;
        }

        /**
         * Adds black queens to the board at the specified {@link Square}s.
         *
         * @param squares The {@link Square}s to add black queens to.
         * @return The updated {@link Builder}.
         */
        public Builder addBlackQueens (Square... squares) {
            for (Square square : squares) {
                this.blackQueens |= square.bitmask();
            }
            return this;
        }

        /**
         * Sets the black king at the specified {@link Square}.
         *
         * @param square The {@link Square} to set the black king to.
         * @return The updated {@link Builder}.
         */
        public Builder setBlackKing (Square square) {
            this.blackKing = square.bitmask();
            return this;
        }

        /**
         * Creates a new {@link Board} constructed from the state of this {@link Builder}.
         *
         * @return A new {@link Board} constructed from the state of this {@link Builder}.
         */
        public Board build () {
            return new Board(this);
        }
    }
}
