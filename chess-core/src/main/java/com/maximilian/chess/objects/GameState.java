package com.maximilian.chess.objects;

import com.maximilian.chess.enums.CastlingRight;
import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.exception.IllegalMoveException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.Type.KING;
import static com.maximilian.chess.enums.Type.PAWN;
import static com.maximilian.chess.enums.Type.ROOK;
import static com.maximilian.chess.enums.Rank.FIVE;
import static com.maximilian.chess.enums.Rank.FOUR;
import static com.maximilian.chess.enums.Rank.SEVEN;
import static com.maximilian.chess.enums.Rank.SIX;
import static com.maximilian.chess.enums.Rank.THREE;
import static com.maximilian.chess.enums.Rank.TWO;

/**
 * Represents a snapshot of a chess game state using a several objects/flags/counters/etc to track various game
 * components and rules (e.g. - player to move, castling rights, etc). The state of this class is designed to be
 * mutable to allow simple representation of state change during recursive searching.
 *
 * @author Maximilian Schroeder
 */
@Accessors(fluent = true)
@EqualsAndHashCode(exclude = {"halfMoveClock", "fullMoveNumber", "previousGameState"})
@ToString
public class GameState {
    /**
     * The hash value for the board for the game state.
     */
    @Getter private int boardHash;
    /**
     * The {@link Color} to move for the game state. Will never be null.
     */
    @Getter private Color colorToMove;
    /**
     * The {@link CastlingRight}s to the set of available castling rights for the game state. Will
     * never be null, may be empty.
     */
    @Getter private Set<CastlingRight> castlingRights;
    /**
     * The {@link Square} that indicates where a legal en passant capture may be performed for the current
     * game state. May be null (if no eligible en passant square exists).
     */
    @Getter private Square enPassantSquare;
    /**
     * The half-move clock value for the game state. This value represents the number of half-moves (single
     * move per color) made since the last capturing move or last pawn advancement for fifty-move draw rule.
     */
    @Getter private int halfMoveClock;
    /**
     * The full-move number for the game state. This number represents the number of the current move in a
     * notated chess game (e.g. - 1. e4, 1...e5, 2. Nf3, etc). One move by each color constitutes one full move.
     */
    @Getter private int fullMoveNumber;
    /**
     * The {@link GameState} representing the state of the game immediately prior to this current game state.
     */
    @Getter private GameState previousGameState;

    /**
     * Primary constructor (declared private to prevent direct instantiation).
     *
     * @param builder The {@link Builder} whose state will be used to construct the {@link GameState}. Cannot be null.
     */
    private GameState (@Nonnull Builder builder) {
        this.boardHash = builder.boardHash;
        this.colorToMove = builder.colorToMove;
        this.castlingRights = builder.castlingRights;
        this.enPassantSquare = builder.enPassantSquare;
        this.halfMoveClock = builder.halfMoveClock;
        this.fullMoveNumber = builder.fullMoveNumber;
        this.previousGameState = builder.previousGameState;
    }

    /**
     * Constructs a new {@link GameState} as a deep copy of an existing game state (declared private to prevent
     * direct instantiation).
     *
     * @param gameState The {@link GameState} to deep copy. Cannot be null.
     */
    private GameState (@Nonnull GameState gameState) {
        this.boardHash = gameState.boardHash;
        this.colorToMove = gameState.colorToMove;
        this.castlingRights = EnumSet.copyOf(gameState.castlingRights);
        this.enPassantSquare = gameState.enPassantSquare;
        this.halfMoveClock = gameState.halfMoveClock;
        this.fullMoveNumber = gameState.fullMoveNumber;
        if (gameState.previousGameState == null) {
            this.previousGameState = null;
        } else {
            this.previousGameState = gameState.previousGameState;
        }
    }

    /**
     * Creates a new {@link Builder} that can be used to construct a {@link GameState}. Will never be null.
     *
     * @return A new {@link Builder} that can be used to construct a {@link GameState}. Will never be null.
     */
    @Nonnull
    public static Builder builder () {
        return new Builder();
    }

    /**
     * Creates a new {@link GameState} configured for the starting state of a chess game. Will never be null.
     *
     * @return A new {@link GameState} configured for the starting state of a chess game. Will never be null.
     */
    @Nonnull
    public static GameState createStartingGameState () {
        return GameState.builder()
                .withBoard(Board.createStartingBoard())
                .withColorToMove(WHITE)
                .withCastlingRights(EnumSet.allOf(CastlingRight.class))
                .withHalfMoveClock(0)
                .withFullMoveNumber(1)
                .build();
    }

    /**
     * Creates a new {@link Board} that is a deep copy of this {@link GameState}. Will never be null.
     *
     * @return A new {@link Board} that is a deep copy of this {@link GameState}. Will never be null.
     */
    @Nonnull
    public GameState deepCopy () {
        return new GameState(this);
    }

    /**
     * Gets the previous half-move clock value from the current {@link GameState}.
     *
     * @return the previous half-move clock value from the current {@link GameState}. Will be 0 if no previous
     * {@link GameState}s exist.
     */
    public int previousHalfMoveClock () {
        if (previousGameState == null) {
            return 0;
        }

        return previousGameState.halfMoveClock();
    }

    /**
     * Determines if the current player to move can declare a draw and end the game.
     *
     * @return True if the current player to move can declare a draw, false otherwise.
     */
    public boolean canDeclareDraw () {
        return canDeclareThreefoldRepetitionDraw() || canDeclareFiftyMoveRuleDraw();
    }

    /**
     * Determines if the current player to move can declare a draw by invoking the three-move-repetition rule and end
     * the game.
     *
     * @return True if the current player to move can declare a draw by invoking the three-move-repetition rule,
     * false otherwise.
     */
    private boolean canDeclareThreefoldRepetitionDraw () {
        boolean hasGameStateBeenRepeatedThreeTimes = false;
        List<GameState> previousGameStates = allPreviousGameStates();
        for (final GameState previousGameState : previousGameStates) {
            long gameStateSeenCount = previousGameStates.stream().filter(p -> p.equals(previousGameState)).count();

            // The current game state represents the third instance of the repeated game state.
            if (gameStateSeenCount >= 2L) {
                hasGameStateBeenRepeatedThreeTimes = true;
                break;
            }
        }

        return hasGameStateBeenRepeatedThreeTimes;
    }

    /**
     * Determines if the current player to move can declare a draw by invoking the fifty-move rule and end the game.
     *
     * @return True if the current player to move can declare a draw by invoking the fifty-move rule, false otherwise.
     */
    private boolean canDeclareFiftyMoveRuleDraw () {
        return halfMoveClock >= 50;
    }

    /**
     * Gets a {@link List} of all of the {@link GameState}s prior to the current {@link GameState}.
     *
     * @return A {@link List} of all of the {@link GameState}s prior to the current {@link GameState}. Will never be
     * null, may be empty.
     */
    @Nonnull
    private List<GameState> allPreviousGameStates () {
        List<GameState> previousGameStates = new LinkedList<>();
        GameState previousGameState = this.previousGameState;
        while (previousGameState != null) {
            previousGameStates.add(previousGameState);
            previousGameState = previousGameState.previousGameState();
        }

        return previousGameStates;
    }

    /**
     * Gets the {@link GameState} after performing the specified move.
     *
     * @param board The {@link Board} on which the specified {@link Move} will be performed. Cannot be null.
     * @param move  The {@link Move} to perform on this {@link GameState}. Cannot be null.
     * @return The new {@link GameState} resulting from performing the specified {@link Move}. Will never be null.
     * @throws IllegalMoveException If the specified {@link Move} cannot be performed on this {@link GameState}.
     */
    @Nonnull
    public GameState doMove (@Nonnull Board board, @Nonnull Move move) {
        // Validate the board.
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null.");
        }

        // Validate the move.
        if (move == null) {
            throw new IllegalArgumentException("move cannot be null.");
        } else if (move.color() != colorToMove) {
            throw new IllegalMoveException("It is not " + move.color() + "'s turn to move.");
        } else if (move.enPassantCaptureSquare() != null && move.end() != enPassantSquare) {
            throw new IllegalMoveException(move,
                    "Illegal en passant square specified for move (legal en passant = " + enPassantSquare + ").");
        } else if (move.isCastling()) {
            if (move.equals(Move.WHITE_KINGSIDE_CASTLE) && !castlingRights.contains(CastlingRight.WHITE_KINGSIDE)) {
                throw new IllegalMoveException(move,
                        "White does not have kingside castling rights (castling rights = " + castlingRights + ").");
            } else if (move.equals(Move.WHITE_QUEENSIDE_CASTLE) &&
                    !castlingRights.contains(CastlingRight.WHITE_QUEENSIDE)) {
                throw new IllegalMoveException(move,
                        "White does not have queenside castling rights (castling rights = " + castlingRights + ").");
            } else if (move.equals(Move.BLACK_KINGSIDE_CASTLE) &&
                    !castlingRights.contains(CastlingRight.BLACK_KINGSIDE)) {
                throw new IllegalMoveException(move,
                        "Black does not have kingside castling rights (castling rights = " + castlingRights + ").");
            } else if (move.equals(Move.BLACK_QUEENSIDE_CASTLE) &&
                    !castlingRights.contains(CastlingRight.BLACK_QUEENSIDE)) {
                throw new IllegalMoveException(move,
                        "Black does not have queenside castling rights (castling rights = " + castlingRights + ").");
            }
        }

        // Perform the move on the board.
        board.doMove(move);

        // Store a copy of the current game state to use for the new game state's previous game state.
        GameState newPreviousGameState = this.deepCopy();

        // Change the color to move.
        Color newColorToMove = colorToMove.opposite();

        // Update the half-move clock.
        int newHalfMoveClock;
        if (move.capturedPiece() != null || move.piece() == PAWN) {
            newHalfMoveClock = 0;
        } else {
            newHalfMoveClock = halfMoveClock + 1;
        }

        // Update the full-move number.
        int newFullMoveNumber = fullMoveNumber;
        if (move.color() == BLACK) {
            newFullMoveNumber++;
        }

        // Update castling rights.
        Set<CastlingRight> newCastlingRights = EnumSet.copyOf(castlingRights);
        if (!newCastlingRights.isEmpty()) {
            if (move.isCastling() || move.piece() == KING) {
                if (move.color() == WHITE) {
                    newCastlingRights.remove(CastlingRight.WHITE_KINGSIDE);
                    newCastlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
                } else if (move.color() == BLACK) {
                    newCastlingRights.remove(CastlingRight.BLACK_KINGSIDE);
                    newCastlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
                }
            } else if (move.piece() == ROOK) {
                if (move.color() == WHITE) {
                    if (move.start() == Square.H1) {
                        newCastlingRights.remove(CastlingRight.WHITE_KINGSIDE);
                    } else if (move.start() == Square.A1) {
                        newCastlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
                    }
                } else if (move.color() == BLACK) {
                    if (move.start() == Square.H8) {
                        newCastlingRights.remove(CastlingRight.BLACK_KINGSIDE);
                    } else if (move.start() == Square.A8) {
                        newCastlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
                    }
                }
            }

            // If a rook is captured, remove castling rights for the side corresponding to that rook.
            if (move.capturedPiece() == ROOK) {
                if (move.end() == Square.H1) {
                    newCastlingRights.remove(CastlingRight.WHITE_KINGSIDE);
                } else if (move.end() == Square.A1) {
                    newCastlingRights.remove(CastlingRight.WHITE_QUEENSIDE);
                } else if (move.end() == Square.H8) {
                    newCastlingRights.remove(CastlingRight.BLACK_KINGSIDE);
                } else if (move.end() == Square.A8) {
                    newCastlingRights.remove(CastlingRight.BLACK_QUEENSIDE);
                }
            }
        }

        // Update the en passant square.
        Square newEnPassantSquare = null;
        if (move.piece() == PAWN) {
            if (move.color() == WHITE && move.start().rank() == TWO && move.end().rank() == FOUR) {
                newEnPassantSquare = Square.fromFileAndRank(move.start().file(), THREE);
            } else if (move.color() == BLACK && move.start().rank() == SEVEN && move.end().rank() == FIVE) {
                newEnPassantSquare = Square.fromFileAndRank(move.start().file(), SIX);
            }
        }

        // Create a new game state to represent the state of the game after the move.
        return GameState.builder()
                .withBoard(board)
                .withColorToMove(newColorToMove)
                .withCastlingRights(newCastlingRights)
                .withEnPassantSquare(newEnPassantSquare)
                .withHalfMoveClock(newHalfMoveClock)
                .withFullMoveNumber(newFullMoveNumber)
                .withPreviousGameState(newPreviousGameState)
                .build();
    }

    /**
     * Gets the {@link GameState} after undoing the most recently made move.
     *
     * @param board The {@link Board} on which the specified {@link Move} will be performed. Cannot be null.
     * @param move  The {@link Move} to perform on this {@link GameState}. Cannot be null.
     * @return The new {@link GameState} resulting from undoing the specified {@link Move}. Will never be null.
     * @throws IllegalMoveException If the specified {@link Move} cannot be undone on this {@link GameState}.
     */
    @Nonnull
    public GameState undoMove (@Nonnull Board board, @Nonnull Move move) {
        // Validate the board.
        if (board == null) {
            throw new IllegalArgumentException("board cannot be null.");
        }

        // Validate the move.
        if (move == null) {
            throw new IllegalArgumentException("move cannot be null.");
        } else if (previousGameState == null) {
            throw new IllegalStateException("There are no previous game states - move cannot be undone.");
        }

        // Undo the move on the board.
        board.undoMove(move);

        return previousGameState;
    }

    /**
     * Represents a builder class for constructing a {@link GameState}.
     */
    public static final class Builder {
        private int boardHash = 0;
        private Color colorToMove = WHITE;
        private Set<CastlingRight> castlingRights = EnumSet.noneOf(CastlingRight.class);
        private Square enPassantSquare = null;
        private int halfMoveClock = 0;
        private int fullMoveNumber = 1;
        private GameState previousGameState = null;

        /**
         * Sets the board hash value for the game state using the specified {@link Board}.
         *
         * @param board The {@link Board} whose hash value will be used for the game state. Cannot be null (if {@code
         *              null} is used, the state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withBoard (@Nullable Board board) {
            if (board != null) {
                this.boardHash = board.hashCode();
            }
            return this;
        }

        /**
         * Sets the {@link Color} to move for the game state.
         *
         * @param colorToMove The {@link Color} to move for the game state. Cannot be null (if {@code null} is used, the
         *                    state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withColorToMove (@Nullable Color colorToMove) {
            if (colorToMove != null) {
                this.colorToMove = colorToMove;
            }
            return this;
        }

        /**
         * Sets the set of available castling rights for the game state to the specified {@link EnumSet} of
         * {@link CastlingRight}s.
         *
         * @param castlingRights The {@link EnumSet} of {@link CastlingRight}s to use for the game state. Cannot be
         *                       null (if {@code null} is used, the state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withCastlingRights (@Nullable Set<CastlingRight> castlingRights) {
            if (castlingRights != null) {
                this.castlingRights = castlingRights;
            }
            return this;
        }

        /**
         * Sets the {@link Square} that indicates where a legal en passant capture may be performed for the current
         * game state.
         *
         * @param enPassantSquare The {@link Square} that indicates where a legal en passant capture may be performed
         *                        for the current game state. May be null (to indicate that no legal en passant capture
         *                        is available).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withEnPassantSquare (@Nullable Square enPassantSquare) {
            this.enPassantSquare = enPassantSquare;
            return this;
        }

        /**
         * Sets the half-move clock value for the game state. This value represents the number of half-moves (single
         * move per color) made since the last capturing move or last pawn advancement for fifty-move draw rule.
         *
         * @param halfMoveClock The half-move clock value for the game state. Must be greater than or equal to 0 (if
         *                      a negative number is used, the state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withHalfMoveClock (int halfMoveClock) {
            if (halfMoveClock >= 0) {
                this.halfMoveClock = halfMoveClock;
            }
            return this;
        }

        /**
         * Sets the full-move number for the game state. This number represents the number of the current move in a
         * notated chess game (e.g. - 1. e4, 1...e5, 2. Nf3, etc). One move by each color constitutes one full move.
         *
         * @param fullMoveNumber The full-move number for the game state.. Must be greater than 0 (if a non-positive
         *                       number is used, the state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withFullMoveNumber (int fullMoveNumber) {
            if (fullMoveNumber > 0) {
                this.fullMoveNumber = fullMoveNumber;
            }
            return this;
        }

        /**
         * Sets the {@link GameState} representing the state of the game immediately prior to this current game state.
         *
         * @param previousGameState The {@link GameState} representing the state of the game immediately prior to
         *                          this current game state. Cannot be null (if {@code null}
         *                          is used, the state of the {@link Builder} will remain unchanged).
         * @return The updated {@link Builder}. Will never be null.
         */
        @Nonnull
        public Builder withPreviousGameState (@Nullable GameState previousGameState) {
            if (previousGameState != null) {
                this.previousGameState = previousGameState;
            }
            return this;
        }

        /**
         * Creates a new {@link GameState} constructed from the state of this {@link Builder}.
         *
         * @return A new {@link GameState} constructed from the state of this {@link Builder}. Will never be null.
         */
        @Nonnull
        public GameState build () {
            return new GameState(this);
        }
    }
}
