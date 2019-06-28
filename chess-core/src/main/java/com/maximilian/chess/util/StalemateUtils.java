package com.maximilian.chess.util;

import com.maximilian.chess.objects.Board;
import com.maximilian.chess.objects.GameState;

import static com.maximilian.chess.objects.Board.DARK_SQUARES_BITMASK;
import static com.maximilian.chess.objects.Board.LIGHT_SQUARES_BITMASK;

/**
 * A utility class containing convenience operations for stalemate conditions.
 *
 * @author Maximilian Schroeder
 */
public class StalemateUtils {
    /**
     * Determines if the current {@link GameState} represents a stalemate due to insufficient mating material (e.g. -
     * king and bishop vs. king).
     *
     * @return True if the current {@link GameState} represents a stalemate due to insufficient mating material,
     * false otherwise.
     */
    public static boolean isStalemateByInsufficientMaterial (Board board) {
        // King vs. King + Knight/Bishop draw
        if (board.whitePiecesCount() == 1 && board.blackPiecesCount() == 2) {
            if (board.blackKnightsCount() == 1) {
                return true;
            } else if (board.blackBishopsCount() == 1) {
                return true;
            }
        } else if (board.blackPiecesCount() == 1 && board.whitePiecesCount() == 2) {
            if (board.whiteKnightsCount() == 1) {
                return true;
            } else if (board.whiteBishopsCount() == 1) {
                return true;
            }
        }

        // King + Bishop(s) vs. King + Bishop(s) draw (bishops must all be on same color)
        if (board.whiteBishopsCount() == (board.whitePiecesCount() - 1) &&
                board.blackBishopsCount() == (board.blackPiecesCount() - 1)) {
            int totalBishopsCount = board.whiteBishopsCount() + board.blackBishopsCount();
            int lightSquareBishopsCount =
                    BitboardUtils.countHighBitsInBitmask(board.whiteBishopsBitmask() & LIGHT_SQUARES_BITMASK) +
                            BitboardUtils.countHighBitsInBitmask(board.blackBishopsBitmask() & LIGHT_SQUARES_BITMASK);
            int darkSquareBishopsCount =
                    BitboardUtils.countHighBitsInBitmask(board.whiteBishopsBitmask() & DARK_SQUARES_BITMASK) +
                            BitboardUtils.countHighBitsInBitmask(board.blackBishopsBitmask() & DARK_SQUARES_BITMASK);

            return (lightSquareBishopsCount == 0 && darkSquareBishopsCount == totalBishopsCount) ||
                    (lightSquareBishopsCount == totalBishopsCount && darkSquareBishopsCount == 0);
        }

        // King vs. King draw (otherwise mate is still possible)
        return board.whitePiecesCount() == 1 && board.blackPiecesCount() == 1;
    }

    // This class should never be instantiated.
    private StalemateUtils () {
    }
}
