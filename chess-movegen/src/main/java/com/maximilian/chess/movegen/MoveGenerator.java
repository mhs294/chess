package com.maximilian.chess.movegen;

import com.maximilian.chess.objects.Board;
import com.maximilian.chess.objects.GameState;
import com.maximilian.chess.objects.Move;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents a class that generates available legal moves for a given board state. This class provides a set of APIs
 * for generating specific sets of moves depending on the needs of the consuming class (e.g. - all moves, quiet moves
 * only, etc).
 *
 * @author Maximilian Schroeder
 */
@SuppressWarnings("unused") // This interface is intended to be consumed outside of this module
public interface MoveGenerator {
    /**
     * Generates all legal moves available to the player to move, given the current {@link Board} and {@link GameState}.
     *
     * @param board     The current {@link Board} representation to generate moves for. Cannot be null.
     * @param gameState The current {@link GameState} provided contextual information necessary for move generation.
     *                  Cannot be null.
     * @return A {@link List} containing all of the legal {@link Move}s available to the player to move. Will never
     * be null, may be empty (i.e. - no legal moves are available).
     */
    @Nonnull
    List<Move> generateAllMoves (@Nonnull Board board, @Nonnull GameState gameState);

    /**
     * Generates all legal non-quiet (i.e. - checks and captures only) moves available to the player to move, given
     * the current {@link Board} and {@link GameState}. This API is designed to be used specifically during
     * Quiescence Search to help mitigate the Horizon Effect of iterative-depth searching.
     *
     * @param board     The current {@link Board} representation to generate moves for. Cannot be null.
     * @param gameState The current {@link GameState} provided contextual information necessary for move
     *                  generation. Cannot be null.
     * @return A {@link List} containing all of the legal non-quiet {@link Move}s available to the player to move. Will
     * never be null, may be empty (i.e. - no legal moves are available).
     */
    @Nonnull
    List<Move> generateNonQuietMoves (@Nonnull Board board, @Nonnull GameState gameState);
}
