package com.maximilian.chess.movegen.magic;

import com.maximilian.chess.enums.CastlingRight;
import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Piece;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.movegen.MoveGenerator;
import com.maximilian.chess.movegen.constants.MovementBitmasks;
import com.maximilian.chess.objects.Board;
import com.maximilian.chess.objects.GameState;
import com.maximilian.chess.objects.Move;
import com.maximilian.chess.util.BitboardUtils;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

import static com.maximilian.chess.enums.CastlingRight.BLACK_KINGSIDE;
import static com.maximilian.chess.enums.CastlingRight.BLACK_QUEENSIDE;
import static com.maximilian.chess.enums.CastlingRight.WHITE_KINGSIDE;
import static com.maximilian.chess.enums.CastlingRight.WHITE_QUEENSIDE;
import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.File.A;
import static com.maximilian.chess.enums.File.H;
import static com.maximilian.chess.enums.Piece.BISHOP;
import static com.maximilian.chess.enums.Piece.KING;
import static com.maximilian.chess.enums.Piece.KNIGHT;
import static com.maximilian.chess.enums.Piece.PAWN;
import static com.maximilian.chess.enums.Piece.QUEEN;
import static com.maximilian.chess.enums.Piece.ROOK;
import static com.maximilian.chess.enums.Rank.EIGHT;
import static com.maximilian.chess.enums.Rank.FIVE;
import static com.maximilian.chess.enums.Rank.FOUR;
import static com.maximilian.chess.enums.Rank.ONE;
import static com.maximilian.chess.enums.Rank.SEVEN;
import static com.maximilian.chess.enums.Rank.SIX;
import static com.maximilian.chess.enums.Rank.THREE;
import static com.maximilian.chess.enums.Rank.TWO;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BISHOP_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BLACK_PAWN_ADVANCES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BLACK_PAWN_CAPTURES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.EAST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.KING_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.KNIGHT_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.NORTHEAST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.NORTHWEST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.NORTH_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.ROOK_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.SOUTHEAST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.SOUTHWEST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.SOUTH_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.WEST_SLIDES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.WHITE_PAWN_ADVANCES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.WHITE_PAWN_CAPTURES;
import static com.maximilian.chess.movegen.magic.MagicBitboardMoveGenerator.MagicMovementBitmasks.BISHOP_BLOCKERS;
import static com.maximilian.chess.movegen.magic.MagicBitboardMoveGenerator.MagicMovementBitmasks.BISHOP_MOVES;
import static com.maximilian.chess.movegen.magic.MagicBitboardMoveGenerator.MagicMovementBitmasks.ROOK_BLOCKERS;
import static com.maximilian.chess.movegen.magic.MagicBitboardMoveGenerator.MagicMovementBitmasks.ROOK_MOVES;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;

/**
 * A {@link MoveGenerator} implementation that generates moves using Magic Bitboards.
 *
 * @author Maximilian Schroeder
 */
public class MagicBitboardMoveGenerator implements MoveGenerator {
    @Nonnull
    @Override
    public List<Move> generateAllMoves (@Nonnull Board board, @Nonnull GameState gameState) {
        Color colorToMove = gameState.colorToMove();
        Color enemyColor = colorToMove.opposite();
        Object2ObjectMap<Square, Pair<Color, Piece>> piecesBySquares = board.toMap();

        /*
         * These bitmasks represent the squares where the pieces to move are allowed to move to/capture on (can be
         * restricted if the king is in check).
         */
        long occupiedBitmask = board.occupiedBitmask();
        long allowedMovesBitmask = ~occupiedBitmask;
        long allowedCapturesBitmask = (colorToMove == WHITE) ?
                board.blackOccupiedBitmask() :
                board.whiteOccupiedBitmask();

        /*
         * Generate king moves first, since the king may be in check. If the king is in check, we may be able to
         * complete move generation early.
         */
        Square kingSquare = (colorToMove == WHITE) ? board.whiteKingSquare() : board.blackKingSquare();
        long kingMovementBitmask = generateKingMovesBitmask(board, enemyColor, kingSquare,
                allowedMovesBitmask | allowedCapturesBitmask, occupiedBitmask);

        /*
         * If the king is attacked by exactly one piece, the king is in check (i.e. - capturing and blocking the
         * attacking piece may be possible). If the king is attacked by multiple pieces, the king is in double check
         * (i.e. - the only legal moves are king moves).
         */
        boolean isInCheck = false;
        Set<Square> kingAttackerSquares = getKingAttackerSquares(board, colorToMove, kingSquare, occupiedBitmask);
        if (!kingAttackerSquares.isEmpty()) {
            if (kingAttackerSquares.size() > 1) {
                // King is double check - only legal moves are for the king to move out of check.
                return new ObjectArrayList<>(getMovesFromMovementBitmask(colorToMove, KING, kingSquare, kingMovementBitmask,
                        piecesBySquares));
            } else {
                // King is in check, restrict legal moves to those which remove the king from check.
                isInCheck = true;
                Square attackerSquare = kingAttackerSquares.iterator().next();
                allowedCapturesBitmask = attackerSquare.bitmask;
                Pair<Color, Piece> attackerColorPiecePair = piecesBySquares.get(attackerSquare);
                if (attackerColorPiecePair == null) {
                    throw new IllegalStateException(
                            "Piece attacking king did not exist at expected square. Square: " + attackerSquare +
                                    "\nBoard:\n" + board);
                }

                Piece attackerPiece = attackerColorPiecePair.getValue();
                if (attackerPiece == PAWN || attackerPiece == KNIGHT) {
                    // Since the attacking piece isn't a sliding piece, check can't be escaped by blocking.
                    allowedMovesBitmask = EMPTY_BITMASK;
                } else {
                    // The attacking piece is a sliding piece, so allowed squares only include squares that block check.
                    allowedMovesBitmask = getAllowedCheckBlocksBitmask(attackerPiece, attackerSquare, kingSquare,
                            occupiedBitmask);
                }
            }
        }
        List<Move> moves = new ObjectArrayList<>(
                getMovesFromMovementBitmask(colorToMove, KING, kingSquare, kingMovementBitmask, piecesBySquares));
        long allowedBitmask = allowedMovesBitmask | allowedCapturesBitmask;

        // Generate moves for pinned pieces separately, since their movement will be restricted.
        Set<Square> pinnedPieceSquares = getPinnedPieceSquares(board, colorToMove, kingSquare, occupiedBitmask);
        for (Square pinnedPieceSquare : pinnedPieceSquares) {
            Pair<Color, Piece> pinnedColorPiecePair = piecesBySquares.get(pinnedPieceSquare);
            if (pinnedColorPiecePair == null) {
                throw new IllegalStateException(
                        "Pinned piece did not exist at expected square. Square: " + pinnedPieceSquare + "\nBoard:\n" +
                                board);
            }

            Piece pinnedPiece = pinnedColorPiecePair.getValue();
            if (pinnedPiece == KNIGHT) {
                // Pinned knights cannot make any legal moves.
                continue;
            }

            long movementBitmask;
            long rankBitmask = pinnedPieceSquare.rank.bitmask;
            long fileBitmask = pinnedPieceSquare.file.bitmask;
            if ((rankBitmask & kingSquare.bitmask) != EMPTY_BITMASK) {
                // The piece is pinned horizontally, so only consider horizontal moves.
                if (pinnedPiece == BISHOP || pinnedPiece == PAWN) {
                    // Bishops and Pawns pinned horizontally cannot make any legal moves.
                    continue;
                }

                movementBitmask =
                        generateRookMovesBitmask(pinnedPieceSquare, allowedBitmask & rankBitmask, occupiedBitmask) &
                                rankBitmask;
            } else if ((fileBitmask & kingSquare.bitmask) != EMPTY_BITMASK) {
                // The piece is pinned vertically, so only consider vertical moves.
                if (pinnedPiece == BISHOP) {
                    // Bishops pinned vertically cannot make any legal moves.
                    continue;
                } else if (pinnedPiece == PAWN) {
                    // Pawns pinned vertically can only make advancing moves.
                    movementBitmask = generatePawnMovesBitmask(colorToMove, pinnedPieceSquare, allowedMovesBitmask,
                            EMPTY_BITMASK, occupiedBitmask);
                } else {
                    movementBitmask =
                            generateRookMovesBitmask(pinnedPieceSquare, allowedBitmask & fileBitmask, occupiedBitmask) &
                                    fileBitmask;
                }
            } else {
                // The piece is pinned diagonally, so only consider diagonal moves.
                if (pinnedPiece == ROOK) {
                    // Rooks pinned diagonally cannot make any legal moves.
                    continue;
                } else {
                    long diagonalBitmask;
                    if ((pinnedPieceSquare.diagonal.bitmask & kingSquare.bitmask) != EMPTY_BITMASK) {
                        diagonalBitmask = pinnedPieceSquare.diagonal.bitmask;
                    } else {
                        diagonalBitmask = pinnedPieceSquare.antiDiagonal.bitmask;
                    }

                    if (pinnedPiece == PAWN) {
                        // Pawns pinned diagonally can only make capturing moves.
                        movementBitmask = ((colorToMove == WHITE) ?
                                WHITE_PAWN_CAPTURES.getLong(pinnedPieceSquare) :
                                BLACK_PAWN_CAPTURES.getLong(pinnedPieceSquare)) & allowedCapturesBitmask &
                                diagonalBitmask;
                    } else {
                        movementBitmask =
                                generateBishopMovesBitmask(pinnedPieceSquare, allowedBitmask & diagonalBitmask,
                                        occupiedBitmask) & diagonalBitmask;
                    }
                }
            }

            moves.addAll(getMovesFromMovementBitmask(colorToMove, pinnedPiece, pinnedPieceSquare, movementBitmask,
                    piecesBySquares));
        }

        // Pawns
        Set<Square> pawnSquares = (colorToMove == WHITE) ? board.whitePawnSquares() : board.blackPawnSquares();
        pawnSquares.removeAll(pinnedPieceSquares);
        for (Square pawnSquare : pawnSquares) {
            long pawnMovementBitmask = generatePawnMovesBitmask(colorToMove, pawnSquare, allowedMovesBitmask,
                    allowedCapturesBitmask, occupiedBitmask);
            moves.addAll(
                    getMovesFromMovementBitmask(colorToMove, PAWN, pawnSquare, pawnMovementBitmask, piecesBySquares));
        }

        // Knights
        Set<Square> knightSquares = (colorToMove == WHITE) ? board.whiteKnightSquares() : board.blackKnightSquares();
        knightSquares.removeAll(pinnedPieceSquares);
        for (Square knightSquare : knightSquares) {
            long knightMovementBitmask = generateKnightMovesBitmask(knightSquare, allowedBitmask);
            moves.addAll(getMovesFromMovementBitmask(colorToMove, KNIGHT, knightSquare, knightMovementBitmask,
                    piecesBySquares));
        }

        // Bishops
        Set<Square> bishopSquares = (colorToMove == WHITE) ? board.whiteBishopSquares() : board.blackBishopSquares();
        bishopSquares.removeAll(pinnedPieceSquares);
        for (Square bishopSquare : bishopSquares) {
            long bishopMovementBitmask = generateBishopMovesBitmask(bishopSquare, allowedBitmask, occupiedBitmask);
            moves.addAll(getMovesFromMovementBitmask(colorToMove, BISHOP, bishopSquare, bishopMovementBitmask,
                    piecesBySquares));
        }

        // Rooks
        Set<Square> rookSquares = (colorToMove == WHITE) ? board.whiteRookSquares() : board.blackRookSquares();
        rookSquares.removeAll(pinnedPieceSquares);
        for (Square rookSquare : rookSquares) {
            long rookMovementBitmask = generateRookMovesBitmask(rookSquare, allowedBitmask, occupiedBitmask);
            moves.addAll(
                    getMovesFromMovementBitmask(colorToMove, ROOK, rookSquare, rookMovementBitmask, piecesBySquares));
        }

        // Queens
        Set<Square> queenSquares = (colorToMove == WHITE) ? board.whiteQueenSquares() : board.blackQueenSquares();
        queenSquares.removeAll(pinnedPieceSquares);
        for (Square queenSquare : queenSquares) {
            long queenMovementBitmask = generateBishopMovesBitmask(queenSquare, allowedBitmask, occupiedBitmask) |
                    generateRookMovesBitmask(queenSquare, allowedBitmask, occupiedBitmask);
            moves.addAll(getMovesFromMovementBitmask(colorToMove, QUEEN, queenSquare, queenMovementBitmask,
                    piecesBySquares));
        }

        // Castling
        Set<CastlingRight> castlingRights = gameState.castlingRights();
        if (!isInCheck && !castlingRights.isEmpty()) {
            for (CastlingRight castlingRight : castlingRights) {
                if (colorToMove == WHITE && (castlingRight == BLACK_KINGSIDE || castlingRight == BLACK_QUEENSIDE)) {
                    continue;
                } else if (colorToMove == BLACK &&
                        (castlingRight == WHITE_KINGSIDE || castlingRight == WHITE_QUEENSIDE)) {
                    continue;
                }

                long requiredSafeSquaresBitmask;
                long requiredVacantSquaresBitmask;
                Move castlingMove;
                switch (castlingRight) {
                    case WHITE_KINGSIDE:
                        requiredSafeSquaresBitmask = F1.bitmask | G1.bitmask;
                        requiredVacantSquaresBitmask = requiredSafeSquaresBitmask;
                        castlingMove = Move.WHITE_KINGSIDE_CASTLE;
                        break;
                    case WHITE_QUEENSIDE:
                        requiredSafeSquaresBitmask = C1.bitmask | D1.bitmask;
                        requiredVacantSquaresBitmask = requiredSafeSquaresBitmask | B1.bitmask;
                        castlingMove = Move.WHITE_QUEENSIDE_CASTLE;
                        break;
                    case BLACK_KINGSIDE:
                        requiredSafeSquaresBitmask = F8.bitmask | G8.bitmask;
                        requiredVacantSquaresBitmask = requiredSafeSquaresBitmask;
                        castlingMove = Move.BLACK_KINGSIDE_CASTLE;
                        break;
                    case BLACK_QUEENSIDE:
                        requiredSafeSquaresBitmask = C8.bitmask | D8.bitmask;
                        requiredVacantSquaresBitmask = requiredSafeSquaresBitmask | B8.bitmask;
                        castlingMove = Move.BLACK_QUEENSIDE_CASTLE;
                        break;
                    default:
                        throw new IllegalStateException(
                                "Board.castlingRights() contained an invalid value: " + castlingRight);
                }
                long availableSquaresBitmask =
                        allowedMovesBitmask & ~getAttackedByBitmaskForColor(board, enemyColor, occupiedBitmask);
                if ((requiredSafeSquaresBitmask & availableSquaresBitmask) == requiredSafeSquaresBitmask &&
                        (requiredVacantSquaresBitmask & allowedMovesBitmask) == requiredVacantSquaresBitmask) {
                    moves.add(castlingMove);
                }
            }
        }

        // En Passant
        Square enPassantSquare = gameState.enPassantSquare();
        if (enPassantSquare != null) {
            long enPassantStartBitmask = (colorToMove == WHITE) ?
                    BLACK_PAWN_CAPTURES.getLong(enPassantSquare) & board.whitePawnsBitmask() :
                    WHITE_PAWN_CAPTURES.getLong(enPassantSquare) & board.blackPawnsBitmask();

            // Ignore en passant for pinned pieces.
            long pinnedPiecesBitmask = BitboardUtils.getBitmaskFromSquares(pinnedPieceSquares);
            enPassantStartBitmask &= ~pinnedPiecesBitmask;
            if (enPassantStartBitmask != EMPTY_BITMASK) {
                long captureBitmask = (colorToMove == WHITE) ?
                        BLACK_PAWN_ADVANCES.getLong(enPassantSquare) :
                        WHITE_PAWN_ADVANCES.getLong(enPassantSquare);
                Square captureSquare = Square.fromBitmask(captureBitmask & allowedCapturesBitmask);
                if (captureSquare != null) {
                    /*
                     * Check for an illegal en passant capture (i.e. - one that would result in putting the king of the
                     * player to move in check)
                     */
                    boolean isEnPassantLegal = true;
                    long rankBitmask = captureSquare.rank.bitmask;
                    long whitePawnsInRankBitmask = board.whitePawnsBitmask() & rankBitmask;
                    long blackPawnsInRankBitmask = board.blackPawnsBitmask() & rankBitmask;
                    if ((rankBitmask & kingSquare.bitmask) != EMPTY_BITMASK &&
                            BitboardUtils.countHighBitsInBitmask(whitePawnsInRankBitmask) == 1 &&
                            BitboardUtils.countHighBitsInBitmask(blackPawnsInRankBitmask) == 1) {
                        /*
                         * If there's exactly one pawn of each color on the rank adjacent to the en passant square and
                         * the king of the player to move exists on that rank, see if the king would be in check if both
                         * pawns are removed from the rank.
                         */
                        long postEnPassantOccupiedBitmask =
                                occupiedBitmask ^ whitePawnsInRankBitmask ^ blackPawnsInRankBitmask;
                        long slidingPieceFromKingBitmask =
                                ROOK_MOVES.get(kingSquare).get(postEnPassantOccupiedBitmask) & allowedCapturesBitmask;
                        if (slidingPieceFromKingBitmask != EMPTY_BITMASK) {
                            Square slidingPieceSquare = Square.fromBitmask(slidingPieceFromKingBitmask);
                            if (slidingPieceSquare == null) {
                                throw new IllegalStateException(
                                        "Invalid sliding piece bitmask when checking for invalid en passant capture. " +
                                                "Bitmask:" + " " + slidingPieceFromKingBitmask + "\nBoard:\n" + board);
                            }

                            Pair<Color, Piece> slidingColorPiecePair = piecesBySquares.get(slidingPieceSquare);
                            if (slidingColorPiecePair == null) {
                                throw new IllegalStateException(
                                        "Sliding piece from king did not exist at expected square. Square: " +
                                                slidingPieceSquare + "\nBoard:\n" + board);
                            }

                            Piece slidingPieceFromKing = slidingColorPiecePair.getValue();
                            if (slidingPieceFromKing == ROOK || slidingPieceFromKing == QUEEN) {
                                // Capturing via en passant would put the king of the player to move in check.
                                isEnPassantLegal = false;
                            }
                        }
                    }

                    if (isEnPassantLegal) {
                        Set<Square> startSquares = BitboardUtils.getSquaresFromBitmask(enPassantStartBitmask);
                        for (Square startSquare : startSquares) {
                            moves.add(Move.createEnPassant(colorToMove, startSquare, enPassantSquare, captureSquare));
                        }
                    }
                }
            }
        }

        return moves;
    }

    @Nonnull
    @Override
    public List<Move> generateNonQuietMoves (@Nonnull Board board, @Nonnull GameState gameState) {
        // TODO - implement this method
        return new ObjectArrayList<>();
    }

    /**
     * Generates a bitmask representing the legal king moves for the color to move.
     *
     * @param board           The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param enemyColor      The {@link Color} of the enemy pieces of the king. Cannot be null.
     * @param kingSquare      The {@link Square} where the king is located. Cannot be null.
     * @param allowedBitmask  The bitmask representing all the allowed destination squares for the color to move.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing the legal king moves for the color to move.
     */
    private long generateKingMovesBitmask (@Nonnull Board board, @Nonnull Color enemyColor, @Nonnull Square kingSquare,
            long allowedBitmask, long occupiedBitmask) {
        /*
         * Determine all squares that are attacked by sliding enemy pieces assuming the king to move is not on the
         * board (this helps to prevent allowing the king to move further away from a sliding piece that is still
         * attacking it).
         */
        long attackedSquaresBitmask = getAttackedByBitmaskForColor(board, enemyColor,
                (occupiedBitmask & ~kingSquare.bitmask));
        return KING_MOVES.getLong(kingSquare) & allowedBitmask & ~attackedSquaresBitmask;
    }

    /**
     * Generates a bitmask representing the legal pawn moves for the specified {@link Color}.
     *
     * @param colorToMove            The {@link Color} of the pawn for which legal moves will be generated. Cannot be
     *                               null.
     * @param pawnSquare             The {@link Square} where the pawn is located. Cannot be null.
     * @param allowedMovesBitmask    The bitmask representing all the allowed non-capture destination squares for the
     *                               color to move.
     * @param allowedCapturesBitmask The bitmask representing all the allowed capture destination squares for the
     *                               color to move.
     * @param occupiedBitmask        The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing the legal pawn moves for the specified {@link Color}.
     */
    private long generatePawnMovesBitmask (@Nonnull Color colorToMove, @Nonnull Square pawnSquare,
            long allowedMovesBitmask, long allowedCapturesBitmask, long occupiedBitmask) {
        long pawnMovesBitmask = allowedMovesBitmask & ((colorToMove == WHITE) ?
                WHITE_PAWN_ADVANCES.getLong(pawnSquare) :
                BLACK_PAWN_ADVANCES.getLong(pawnSquare));
        // Modify advancement from the pawn's initial rank to account for blocking pieces.
        switch (colorToMove) {
            case WHITE:
                if (pawnSquare.rank == TWO) {
                    long pawnFileBitmask = pawnSquare.file.bitmask;
                    long rankThreeBitmask = THREE.bitmask;
                    long rankFourBitmask = FOUR.bitmask;
                    if ((rankThreeBitmask & pawnFileBitmask & occupiedBitmask) != EMPTY_BITMASK) {
                        // There is a blocker on the third rank - this pawn cannot advance.
                        pawnMovesBitmask &= ~(rankThreeBitmask & pawnFileBitmask);
                        pawnMovesBitmask &= ~(rankFourBitmask & pawnFileBitmask);
                    } else if ((rankFourBitmask & pawnFileBitmask & occupiedBitmask) != EMPTY_BITMASK) {
                        // There is a blocker on the fourth rank - this pawn can only advance one square.
                        pawnMovesBitmask &= ~(rankFourBitmask & pawnFileBitmask);
                    }
                }
                break;
            case BLACK:
                if (pawnSquare.rank == SEVEN) {
                    long pawnFileBitmask = pawnSquare.file.bitmask;
                    long rankSixBitmask = SIX.bitmask;
                    long rankFiveBitmask = FIVE.bitmask;
                    if ((rankSixBitmask & pawnFileBitmask & occupiedBitmask) != EMPTY_BITMASK) {
                        // There is a blocker on the sixth rank - this pawn cannot advance.
                        pawnMovesBitmask &= ~(rankSixBitmask & pawnFileBitmask);
                        pawnMovesBitmask &= ~(rankFiveBitmask & pawnFileBitmask);
                    } else if ((rankFiveBitmask & pawnFileBitmask & occupiedBitmask) != EMPTY_BITMASK) {
                        // There is a blocker on the fifth rank - this pawn can only advance one square.
                        pawnMovesBitmask &= ~(rankFiveBitmask & pawnFileBitmask);
                    }
                }
                break;
        }
        pawnMovesBitmask |= allowedCapturesBitmask & ((colorToMove == WHITE) ?
                WHITE_PAWN_CAPTURES.getLong(pawnSquare) :
                BLACK_PAWN_CAPTURES.getLong(pawnSquare));

        return pawnMovesBitmask;
    }

    /**
     * Generates a bitmask representing the legal knight moves for the specified {@link Color}.
     *
     * @param knightSquare   The {@link Square} where the knight is located. Cannot be null.
     * @param allowedBitmask The bitmask representing all the allowed destination squares for the color to move.
     * @return The bitmask representing the legal knight moves for the specified {@link Color}.
     */
    private long generateKnightMovesBitmask (@Nonnull Square knightSquare, long allowedBitmask) {
        return KNIGHT_MOVES.getLong(knightSquare) & allowedBitmask;
    }

    /**
     * Generates a bitmask representing the legal bishop moves for the specified {@link Color}.
     *
     * @param bishopSquare    The {@link Square} where the bishop is located. Cannot be null.
     * @param allowedBitmask  The bitmask representing all the allowed destination squares for the color to move.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing the legal bishop moves for the specified {@link Color}.
     */
    private long generateBishopMovesBitmask (@Nonnull Square bishopSquare, long allowedBitmask, long occupiedBitmask) {
        return BISHOP_MOVES.get(bishopSquare).get(BISHOP_BLOCKERS.getLong(bishopSquare) & occupiedBitmask) &
                allowedBitmask;
    }

    /**
     * Generates a bitmask representing the legal rook moves for the specified {@link Color}.
     *
     * @param rookSquare      The {@link Square} where the rook is located. Cannot be null.
     * @param allowedBitmask  The bitmask representing all the allowed destination squares for the color to move.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing the legal rook moves for the specified {@link Color}.
     */
    private long generateRookMovesBitmask (@Nonnull Square rookSquare, long allowedBitmask, long occupiedBitmask) {
        return ROOK_MOVES.get(rookSquare).get(ROOK_BLOCKERS.getLong(rookSquare) & occupiedBitmask) & allowedBitmask;
    }

    /**
     * Gets the bitmask representing all squares attacked by pieces of the specified {@link Color}, disregarding how
     * the attacked squares are occupied (i.e. - even squares containing the same color piece are considered attacked).
     *
     * @param board           The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param attackingColor  The {@link Color} of the pieces for which attacked squares will be determined. Cannot be
     *                        null.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing all squares attacked by pieces of the specified {@link Color}.
     */
    private long getAttackedByBitmaskForColor (@Nonnull Board board, @Nonnull Color attackingColor,
            long occupiedBitmask) {
        return getFixedMovementAttackedByBitmaskForColor(board, attackingColor) |
                getSlidingMovementAttackedByBitmaskForColor(board, attackingColor, occupiedBitmask);
    }

    /**
     * Gets the bitmask representing all squares attacked by fixed-movement pieces of the specified {@link Color},
     * disregarding how the attacked squares are occupied (i.e. - even squares containing the same color piece are
     * considered attacked).
     *
     * @param board          The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param attackingColor The {@link Color} of the pieces for which attacked squares will be determined. Cannot be
     *                       null.
     * @return The bitmask representing all squares attacked by fixed-movement pieces of the specified {@link Color}.
     */
    private long getFixedMovementAttackedByBitmaskForColor (@Nonnull Board board, @Nonnull Color attackingColor) {
        long attackedBitmask = EMPTY_BITMASK;

        // King
        Square kingSquare = (attackingColor == WHITE) ? board.whiteKingSquare() : board.blackKingSquare();
        attackedBitmask |= KING_MOVES.getLong(kingSquare);

        // Pawns
        Set<Square> pawnSquares = (attackingColor == WHITE) ? board.whitePawnSquares() : board.blackPawnSquares();
        for (Square square : pawnSquares) {
            attackedBitmask |= (attackingColor == WHITE) ?
                    WHITE_PAWN_CAPTURES.getLong(square) :
                    BLACK_PAWN_CAPTURES.getLong(square);
        }

        // Knights
        Set<Square> knightSquares = (attackingColor == WHITE) ? board.whiteKnightSquares() : board.blackKnightSquares();
        for (Square square : knightSquares) {
            attackedBitmask |= KNIGHT_MOVES.getLong(square);
        }

        return attackedBitmask;
    }

    /**
     * Gets the bitmask representing all squares attacked by sliding-movement pieces of the specified {@link Color},
     * disregarding how the attacked squares are occupied (i.e. - even squares containing the same color piece are
     * considered attacked).
     *
     * @param board           The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param attackingColor  The {@link Color} of the pieces for which attacked squares will be determined. Cannot be
     *                        null.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing all squares attacked by sliding-movement pieces of the specified {@link Color}.
     */
    private long getSlidingMovementAttackedByBitmaskForColor (@Nonnull Board board, @Nonnull Color attackingColor,
            long occupiedBitmask) {
        long attackedBitmask = EMPTY_BITMASK;

        // Bishops
        Set<Square> bishopSquares = (attackingColor == WHITE) ? board.whiteBishopSquares() : board.blackBishopSquares();
        for (Square square : bishopSquares) {
            attackedBitmask |= BISHOP_MOVES.get(square).get(BISHOP_BLOCKERS.getLong(square) & occupiedBitmask);
        }

        // Rooks
        Set<Square> rookSquares = (attackingColor == WHITE) ? board.whiteRookSquares() : board.blackRookSquares();
        for (Square square : rookSquares) {
            attackedBitmask |= ROOK_MOVES.get(square).get(ROOK_BLOCKERS.getLong(square) & occupiedBitmask);
        }

        // Queens
        Set<Square> queenSquares = (attackingColor == WHITE) ? board.whiteQueenSquares() : board.blackQueenSquares();
        for (Square square : queenSquares) {
            attackedBitmask |= BISHOP_MOVES.get(square).get(BISHOP_BLOCKERS.getLong(square) & occupiedBitmask);
            attackedBitmask |= ROOK_MOVES.get(square).get(ROOK_BLOCKERS.getLong(square) & occupiedBitmask);
        }

        return attackedBitmask;
    }

    /**
     * Gets the {@link Set} of {@link Square}s from which the king of the specified {@link Color} is attacked.
     *
     * @param board           The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param kingColor       The {@link Color} of the king being attacked. Cannot be null.
     * @param kingSquare      The {@link Square} where the king is located. Cannot be null.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The {@link Set} of {@link Square}s from which the king of the specified {@link Color} is attacked.
     * Will never be null, may be empty.
     */
    @Nonnull
    private Set<Square> getKingAttackerSquares (@Nonnull Board board, @Nonnull Color kingColor,
            @Nonnull Square kingSquare, long occupiedBitmask) {
        // Generate moves for all piece types from king's square to build a bitmask of squares containing attackers.
        long attackersBitmask = EMPTY_BITMASK;
        attackersBitmask |= (kingColor == WHITE) ?
                (WHITE_PAWN_CAPTURES.getLong(kingSquare) & board.blackPawnsBitmask()) :
                (BLACK_PAWN_CAPTURES.getLong(kingSquare) & board.whitePawnsBitmask());
        attackersBitmask |= (KNIGHT_MOVES.getLong(kingSquare) &
                ((kingColor == WHITE) ? board.blackKnightsBitmask() : board.whiteKnightsBitmask()));
        long queensBitmask = (kingColor == WHITE) ? board.blackQueensBitmask() : board.whiteQueensBitmask();
        long bishopsBitmask = (kingColor == WHITE) ? board.blackBishopsBitmask() : board.whiteBishopsBitmask();
        long bishopAttacksBitmask = BISHOP_MOVES.get(kingSquare)
                .get(BISHOP_BLOCKERS.getLong(kingSquare) & occupiedBitmask);
        attackersBitmask |= (bishopAttacksBitmask & (bishopsBitmask | queensBitmask));
        long rooksBitmask = (kingColor == WHITE) ? board.blackRooksBitmask() : board.whiteRooksBitmask();
        long rookAttacksBitmask = ROOK_MOVES.get(kingSquare).get(ROOK_BLOCKERS.getLong(kingSquare) & occupiedBitmask);
        attackersBitmask |= (rookAttacksBitmask & (rooksBitmask | queensBitmask));

        return BitboardUtils.getSquaresFromBitmask(attackersBitmask);
    }

    /**
     * Gets the bitmask representing the squares that can be occupied by pieces of the color to move in order to
     * block the king from check.
     *
     * @param attackerPiece   The {@link Piece} attacking the king of the player to move. Cannot be null.
     * @param attackerSquare  The {@link Square} containing the piece attacking the king. Cannot be null.
     * @param kingSquare      The {@link Square} where the king of the player to move is located. Cannot be null.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The bitmask representing the squares that can be occupied by pieces of the color to move in order to
     * block the king from check.
     */
    private long getAllowedCheckBlocksBitmask (@Nonnull Piece attackerPiece, @Nonnull Square attackerSquare,
            @Nonnull Square kingSquare, long occupiedBitmask) {
        long kingSquareBitmask = kingSquare.bitmask;
        long allowedBitmask = ~occupiedBitmask | kingSquareBitmask;
        long attackerSlidesBitmask = EMPTY_BITMASK;
        if (attackerPiece == ROOK || attackerPiece == QUEEN) {
            attackerSlidesBitmask |= generateRookMovesBitmask(attackerSquare, allowedBitmask, occupiedBitmask);
        }
        if (attackerPiece == BISHOP || attackerPiece == QUEEN) {
            attackerSlidesBitmask |= generateBishopMovesBitmask(attackerSquare, allowedBitmask, occupiedBitmask);
        }
        for (MovementBitmasks.CardinalDirection cardinalDirection : MovementBitmasks.CardinalDirection.values()) {
            switch (cardinalDirection) {
                case NORTH:
                    if (attackerPiece == BISHOP) {
                        // Bishops can't check the king orthogonally.
                        continue;
                    }
                    long northAttackSlidesBitmask = attackerSlidesBitmask & NORTH_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & northAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return northAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case EAST:
                    if (attackerPiece == BISHOP) {
                        // Bishops can't check the king orthogonally.
                        continue;
                    }
                    long eastAttackSlidesBitmask = attackerSlidesBitmask & EAST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & eastAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return eastAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case SOUTH:
                    if (attackerPiece == BISHOP) {
                        // Bishops can't check the king orthogonally.
                        continue;
                    }
                    long southAttackSlidesBitmask = attackerSlidesBitmask & SOUTH_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & southAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return southAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case WEST:
                    if (attackerPiece == BISHOP) {
                        // Bishops can't check the king orthogonally.
                        continue;
                    }
                    long westAttackSlidesBitmask = attackerSlidesBitmask & WEST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & westAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return westAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case NORTHWEST:
                    if (attackerPiece == ROOK) {
                        // Rooks can't check the king diagonally.
                        continue;
                    }
                    long northwestAttackSlidesBitmask =
                            attackerSlidesBitmask & NORTHWEST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & northwestAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return northwestAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case NORTHEAST:
                    if (attackerPiece == ROOK) {
                        // Rooks can't check the king diagonally.
                        continue;
                    }
                    long northeastAttackSlidesBitmask =
                            attackerSlidesBitmask & NORTHEAST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & northeastAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return northeastAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case SOUTHWEST:
                    if (attackerPiece == ROOK) {
                        // Rooks can't check the king diagonally.
                        continue;
                    }
                    long southwestAttackSlidesBitmask =
                            attackerSlidesBitmask & SOUTHWEST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & southwestAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return southwestAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
                case SOUTHEAST:
                    if (attackerPiece == ROOK) {
                        // Rooks can't check the king diagonally.
                        continue;
                    }
                    long southeastAttackSlidesBitmask =
                            attackerSlidesBitmask & SOUTHEAST_SLIDES.getLong(attackerSquare);
                    if ((kingSquareBitmask & southeastAttackSlidesBitmask) != EMPTY_BITMASK) {
                        return southeastAttackSlidesBitmask & ~kingSquareBitmask;
                    }
                    break;
            }
        }

        // If we made it this far, something went wrong.
        return EMPTY_BITMASK;
    }

    /**
     * Gets the {@link Set} of {@link Square}s containing pieces of the specified {@link Color} that are pinned (i.e.
     * - their movement is restricted because they are currently blocking the king from check).
     *
     * @param board           The {@link Board} representing the current state of the pieces. Cannot be null.
     * @param colorToMove     The {@link Color} of the player to move. Cannot be null.
     * @param kingSquare      The {@link Square} where the king of the player to move is located. Cannot be null.
     * @param occupiedBitmask The bitmask representing all the occupied squares on the board.
     * @return The {@link Set} of {@link Square}s containing pieces of the specified {@link Color} that are pinned.
     * Will never be null, may be empty (if no pieces are currently pinned).
     */
    @Nonnull
    private Set<Square> getPinnedPieceSquares (@Nonnull Board board, @Nonnull Color colorToMove,
            @Nonnull Square kingSquare, long occupiedBitmask) {
        // TODO - clean up code and redundant method calls
        Set<Square> pinnedPieceSquares = new ObjectOpenHashSet<>();
        long colorToMovePiecesBitmask = (colorToMove == WHITE) ?
                board.whiteOccupiedBitmask() :
                board.blackOccupiedBitmask();
        long orthogonalPinPiecesBitmask = (colorToMove == WHITE) ?
                board.blackQueensBitmask() | board.blackRooksBitmask() :
                board.whiteQueensBitmask() | board.whiteRooksBitmask();
        long diagonalPinPiecesBitmask = (colorToMove == WHITE) ?
                board.blackQueensBitmask() | board.blackBishopsBitmask() :
                board.whiteQueensBitmask() | board.whiteBishopsBitmask();

        // North pins
        long northSlidesBitmask = NORTH_SLIDES.getLong(kingSquare);
        long northPinPiecesBitmask = northSlidesBitmask & orthogonalPinPiecesBitmask;
        if (northPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    ROOK_MOVES.get(kingSquare).get(occupiedBitmask & ROOK_BLOCKERS.getLong(kingSquare)) &
                            northSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(northPinPiecesBitmask)) {
                long slideToKingBitmask = ROOK_MOVES.get(square).get(occupiedBitmask & ROOK_BLOCKERS.getLong(square)) &
                        SOUTH_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // South pins
        long southSlidesBitmask = SOUTH_SLIDES.getLong(kingSquare);
        long southPinPiecesBitmask = southSlidesBitmask & orthogonalPinPiecesBitmask;
        if (southPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    ROOK_MOVES.get(kingSquare).get(occupiedBitmask & ROOK_BLOCKERS.getLong(kingSquare)) &
                            southSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(southPinPiecesBitmask)) {
                long slideToKingBitmask = ROOK_MOVES.get(square).get(occupiedBitmask & ROOK_BLOCKERS.getLong(square)) &
                        NORTH_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // East pins
        long eastSlidesBitmask = EAST_SLIDES.getLong(kingSquare);
        long eastPinPiecesBitmask = eastSlidesBitmask & orthogonalPinPiecesBitmask;
        if (eastPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    ROOK_MOVES.get(kingSquare).get(occupiedBitmask & ROOK_BLOCKERS.getLong(kingSquare)) &
                            eastSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(eastPinPiecesBitmask)) {
                long slideToKingBitmask = ROOK_MOVES.get(square).get(occupiedBitmask & ROOK_BLOCKERS.getLong(square)) &
                        WEST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // West pins
        long westSlidesBitmask = WEST_SLIDES.getLong(kingSquare);
        long westPinPiecesBitmask = westSlidesBitmask & orthogonalPinPiecesBitmask;
        if (westPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    ROOK_MOVES.get(kingSquare).get(occupiedBitmask & ROOK_BLOCKERS.getLong(kingSquare)) &
                            westSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(westPinPiecesBitmask)) {
                long slideToKingBitmask = ROOK_MOVES.get(square).get(occupiedBitmask & ROOK_BLOCKERS.getLong(square)) &
                        EAST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // Northeast pins
        long northeastSlidesBitmask = NORTHEAST_SLIDES.getLong(kingSquare);
        long northeastPinPiecesBitmask = northeastSlidesBitmask & diagonalPinPiecesBitmask;
        if (northeastPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    BISHOP_MOVES.get(kingSquare).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(kingSquare)) &
                            northeastSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(northeastPinPiecesBitmask)) {
                long slideToKingBitmask =
                        BISHOP_MOVES.get(square).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(square)) &
                                SOUTHWEST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // Northwest pins
        long northwestSlidesBitmask = NORTHWEST_SLIDES.getLong(kingSquare);
        long northwestPinPiecesBitmask = northwestSlidesBitmask & diagonalPinPiecesBitmask;
        if (northwestPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    BISHOP_MOVES.get(kingSquare).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(kingSquare)) &
                            northwestSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(northwestPinPiecesBitmask)) {
                long slideToKingBitmask =
                        BISHOP_MOVES.get(square).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(square)) &
                                SOUTHEAST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // Southeast pins
        long southeastSlidesBitmask = SOUTHEAST_SLIDES.getLong(kingSquare);
        long southeastPinPiecesBitmask = southeastSlidesBitmask & diagonalPinPiecesBitmask;
        if (southeastPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    BISHOP_MOVES.get(kingSquare).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(kingSquare)) &
                            southeastSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(southeastPinPiecesBitmask)) {
                long slideToKingBitmask =
                        BISHOP_MOVES.get(square).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(square)) &
                                NORTHWEST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        // Southwest pins
        long southwestSlidesBitmask = SOUTHWEST_SLIDES.getLong(kingSquare);
        long southwestPinPiecesBitmask = southwestSlidesBitmask & diagonalPinPiecesBitmask;
        if (southwestPinPiecesBitmask != EMPTY_BITMASK) {
            long slideFromKingBitmask =
                    BISHOP_MOVES.get(kingSquare).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(kingSquare)) &
                            southwestSlidesBitmask;
            for (Square square : BitboardUtils.getSquaresFromBitmask(southwestPinPiecesBitmask)) {
                long slideToKingBitmask =
                        BISHOP_MOVES.get(square).get(occupiedBitmask & BISHOP_BLOCKERS.getLong(square)) &
                                NORTHEAST_SLIDES.getLong(square);
                long pinnedPieceBitmask = slideFromKingBitmask & slideToKingBitmask & colorToMovePiecesBitmask;
                if (pinnedPieceBitmask != EMPTY_BITMASK) {
                    pinnedPieceSquares.add(Square.fromBitmask(pinnedPieceBitmask));
                    break;
                }
            }
        }

        return pinnedPieceSquares;
    }

    /**
     * Gets a {@link List} of {@link Move}s for a specified piece and movement bitmask.
     *
     * @param colorToMove     The {@link Color} of the piece being moved. Cannot be null.
     * @param piece           The {@link Piece} being moved. Cannot be null.
     * @param startSquare     The {@link Square} containing the piece being moved. Cannot be null.
     * @param movementBitmask The movement bitmask representing the squares the piece can move to.
     * @param piecesBySquares The {@link Object2ObjectMap} of {@link Square} keys and {@link Pair} of {@link Color}
     *                        and {@link Piece} values, representing the contents of the board. Cannot be null.
     * @return The {@link List} of {@link Move}s for the specified piece and movement bitmask. Will never be null,
     * may be empty.
     */
    @Nonnull
    private List<Move> getMovesFromMovementBitmask (@Nonnull Color colorToMove, @Nonnull Piece piece,
            @Nonnull Square startSquare, long movementBitmask,
            @Nonnull Object2ObjectMap<Square, Pair<Color, Piece>> piecesBySquares) {
        List<Move> moves = new ObjectArrayList<>();
        if (movementBitmask == EMPTY_BITMASK) {
            return moves;
        }

        Set<Square> endSquares = BitboardUtils.getSquaresFromBitmask(movementBitmask);
        for (Square endSquare : endSquares) {
            Pair<Color, Piece> capturedColorPiecePair = piecesBySquares.get(endSquare);
            Piece capturedPiece = (capturedColorPiecePair != null) ? capturedColorPiecePair.getValue() : null;

            // If the piece being moved is a promoting pawn, generate all possible promotion moves.
            if (piece == PAWN) {
                if ((colorToMove == WHITE && endSquare.rank == EIGHT) ||
                        (colorToMove == BLACK && endSquare.rank == ONE)) {
                    moves.add((capturedPiece != null) ?
                            Move.createCapturePromotion(colorToMove, capturedPiece, startSquare, endSquare, QUEEN) :
                            Move.createPromotion(colorToMove, startSquare, endSquare, QUEEN));
                    moves.add((capturedPiece != null) ?
                            Move.createCapturePromotion(colorToMove, capturedPiece, startSquare, endSquare, KNIGHT) :
                            Move.createPromotion(colorToMove, startSquare, endSquare, KNIGHT));
                    moves.add((capturedPiece != null) ?
                            Move.createCapturePromotion(colorToMove, capturedPiece, startSquare, endSquare, ROOK) :
                            Move.createPromotion(colorToMove, startSquare, endSquare, ROOK));
                    moves.add((capturedPiece != null) ?
                            Move.createCapturePromotion(colorToMove, capturedPiece, startSquare, endSquare, BISHOP) :
                            Move.createPromotion(colorToMove, startSquare, endSquare, BISHOP));
                    continue;
                }
            }

            moves.add((capturedPiece != null) ?
                    Move.createCapture(colorToMove, piece, capturedPiece, startSquare, endSquare) :
                    Move.create(colorToMove, piece, startSquare, endSquare));
        }

        return moves;
    }

    /**
     * Represents the constant bitmask values for all possible moves for a sliding piece (e.g. - bishop, rook) for
     * all squares and any possible combination of blocking pieces.
     */
    static class MagicMovementBitmasks {
        static final Object2ObjectMap<Square, Long2LongMap> BISHOP_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
        static final Object2ObjectMap<Square, Long2LongMap> ROOK_MOVES = new Object2ObjectOpenHashMap<>(64, 1.0F);
        static final Object2LongMap<Square> BISHOP_BLOCKERS = new Object2LongOpenHashMap<>(64, 1.0F);
        static final Object2LongMap<Square> ROOK_BLOCKERS = new Object2LongOpenHashMap<>(64, 1.0F);

        static {
            // Initialize bishop movements.
            for (Square square : Square.values()) {
                // Initialize bishop magics.
                long bishopBlockerBitmask = getBishopBlockerBitmaskForSquare(square);
                BISHOP_BLOCKERS.put(square, bishopBlockerBitmask);
                LongSet bishopBlockerPermutations = getBlockerBitmaskPermutationsByBlockerBitmask(bishopBlockerBitmask);
                Long2LongMap bishopMovesByBlockerBitmask = new Long2LongOpenHashMap(bishopBlockerPermutations.size(),
                        1.0F);
                for (long blockerBitmask : bishopBlockerPermutations) {
                    long movementBitmask = getBishopMovementForSquareAndBlockerBitmask(square, blockerBitmask);
                    bishopMovesByBlockerBitmask.put(blockerBitmask, movementBitmask);
                }
                BISHOP_MOVES.put(square, bishopMovesByBlockerBitmask);
            }

            // Initialize rook movements.
            for (Square square : Square.values()) {
                long rookBlockerBitmask = getRookBlockerBitmaskForSquare(square);
                ROOK_BLOCKERS.put(square, rookBlockerBitmask);
                LongSet rookBlockerPermutations = getBlockerBitmaskPermutationsByBlockerBitmask(rookBlockerBitmask);
                Long2LongMap rookMovesByBlockerBitmask = new Long2LongOpenHashMap(rookBlockerPermutations.size(), 1.0F);
                for (long blockerBitmask : rookBlockerPermutations) {
                    long movementBitmask = getRookMovementForSquareAndBlockerBitmask(square, blockerBitmask);
                    rookMovesByBlockerBitmask.put(blockerBitmask, movementBitmask);
                }
                ROOK_MOVES.put(square, rookMovesByBlockerBitmask);
            }
        }

        /**
         * Gets the blocker bitmask representing the {@link Square}s where a blocking piece can impact the movement of a
         * bishop located on the specified {@link Square}.
         *
         * @param square The {@link Square} where the bishop is located. Cannot be null.
         * @return The blocker bitmask representing the {@link Square}s where a blocking piece can impact the
         * movement of a bishop located on the specified {@link Square}.
         */
        private static long getBishopBlockerBitmaskForSquare (@Nonnull Square square) {
            long bishopBlockerBitmask = BISHOP_SLIDES.getLong(square);

            // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
            bishopBlockerBitmask &= ~A.bitmask;
            bishopBlockerBitmask &= ~H.bitmask;
            bishopBlockerBitmask &= ~ONE.bitmask;
            bishopBlockerBitmask &= ~EIGHT.bitmask;

            return bishopBlockerBitmask;
        }

        /**
         * Gets the blocker bitmask representing the {@link Square}s where a blocking piece can impact the movement of a
         * rook located on the specified {@link Square}.
         *
         * @param square The {@link Square} where the rook is located. Cannot be null.
         * @return The blocker bitmask representing the {@link Square}s where a blocking piece can impact the
         * movement of
         * a rook located on the specified {@link Square}.
         */
        private static long getRookBlockerBitmaskForSquare (@Nonnull Square square) {
            long rookBlockerBitmask = ROOK_SLIDES.getLong(square);

            // Exclude the final squares in the sliding rays, since Magic Bitboard move generation will handle this.
            rookBlockerBitmask &= (~A.bitmask | square.file.bitmask);
            rookBlockerBitmask &= (~H.bitmask | square.file.bitmask);
            rookBlockerBitmask &= (~ONE.bitmask | square.rank.bitmask);
            rookBlockerBitmask &= (~EIGHT.bitmask | square.rank.bitmask);

            return rookBlockerBitmask;
        }

        /**
         * Gets the set of all blocker bitmask permutations for the specified blocker bitmask.
         *
         * @param blockerBitmask The blocker bitmask to get all blocker permutations for.
         * @return A {@link LongSet} representing the set of all blocker bitmask permutations for the specified blocker
         * bitmask. Will never be null.
         */
        @Nonnull
        private static LongSet getBlockerBitmaskPermutationsByBlockerBitmask (long blockerBitmask) {
            // Total number of possible blocker bitmasks to generate = 2^(number of high bits) in blocker bitmask.
            int highBitsInBlockerBitmask = BitboardUtils.countHighBitsInBitmask(blockerBitmask);
            int totalBlockerOccupancyBitmasks = 1 << highBitsInBlockerBitmask;

            // Determine individual values of high bits in blocker bitmask.
            LongList bitValues = new LongArrayList(highBitsInBlockerBitmask);
            for (int i = 0; blockerBitmask >>> i != EMPTY_BITMASK; i++) {
                long currentBitValue = blockerBitmask >>> i;
                if ((currentBitValue & 1L) == 1L) {
                    bitValues.add(blockerBitmask & (1L << i));
                }
            }

            // Iterate over all possible combinations of blockers for the blocker bitmask.
            LongSet blockerOccupancyBitmasks = new LongOpenHashSet(totalBlockerOccupancyBitmasks);
            for (int i = 0; i < totalBlockerOccupancyBitmasks; i++) {
                long occupancyBitmask = blockerBitmask;
                for (int k = 0; i >>> k != 0; k++) {
                    if (((i >>> k) & 1) == 1) {
                        occupancyBitmask -= bitValues.getLong(k);
                    }
                }
                blockerOccupancyBitmasks.add(occupancyBitmask);
            }

            return blockerOccupancyBitmasks;
        }

        /**
         * Gets the bitmask representing the moves a bishop on the specified {@link Square} can make, given a specified
         * blocker bitmask representing the presence of blocking pieces that could impact the bishop's movement.
         *
         * @param square         The {@link Square} where the bishop is located. Cannot be null.
         * @param blockerBitmask The blocker bitmask representing squares on the board where piece's that could hinder
         *                       the bishop's movement exist.
         * @return The bitmask representing the moves a bishop on the specified {@link Square} can make, taking into
         * consideration potential blocking pieces.
         */
        private static long getBishopMovementForSquareAndBlockerBitmask (@Nonnull Square square, long blockerBitmask) {
            long diagonalsBitmask = square.diagonal.bitmask | square.antiDiagonal.bitmask;
            long bishopMovesBitmask = diagonalsBitmask ^= square.bitmask;

            // Modify northwest bishop movements to account for blockers.
            boolean blockerFound = false;
            for (long bitmask = square.bitmask << 7; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask <<= 7) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify northeast bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask << 9; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask <<= 9) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify southwest bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask >>> 7; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask >>>= 7) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            // Modify southeast bishop movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask >>> 9; (bitmask & diagonalsBitmask) != EMPTY_BITMASK; bitmask >>>= 9) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    bishopMovesBitmask ^= bitmask;
                }
            }

            return bishopMovesBitmask;
        }

        /**
         * Gets the bitmask representing the moves a rook on the specified {@link Square} can make, given a specified
         * blocker bitmask representing the presence of blocking pieces that could impact the rook's movement.
         *
         * @param square         The {@link Square} where the rook is located. Cannot be null.
         * @param blockerBitmask The blocker bitmask representing squares on the board where piece's that could hinder
         *                       the rook's movement exist.
         * @return The bitmask representing the moves a rook on the specified {@link Square} can make, taking into
         * consideration potential blocking pieces.
         */
        private static long getRookMovementForSquareAndBlockerBitmask (@Nonnull Square square, long blockerBitmask) {
            long rankBitmask = square.rank.bitmask;
            long fileBitmask = square.file.bitmask;
            long rookMovesBitmask = rankBitmask ^ fileBitmask;

            // Modify west rook movements to account for blockers.
            boolean blockerFound = false;
            for (long bitmask = square.bitmask << 1; (bitmask & rankBitmask) != EMPTY_BITMASK; bitmask <<= 1) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify east rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask >>> 1; (bitmask & rankBitmask) != EMPTY_BITMASK; bitmask >>>= 1) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify north rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask << 8; (bitmask & fileBitmask) != EMPTY_BITMASK; bitmask <<= 8) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            // Modify south rook movements to account for blockers.
            blockerFound = false;
            for (long bitmask = square.bitmask >>> 8; (bitmask & fileBitmask) != EMPTY_BITMASK; bitmask >>>= 8) {
                if (!blockerFound) {
                    if ((blockerBitmask & bitmask) != EMPTY_BITMASK) {
                        blockerFound = true;
                    }
                } else {
                    rookMovesBitmask ^= bitmask;
                }
            }

            return rookMovesBitmask;
        }
    }
}
