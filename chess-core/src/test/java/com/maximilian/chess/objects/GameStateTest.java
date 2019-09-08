package com.maximilian.chess.objects;

import com.maximilian.chess.enums.CastlingRight;
import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.exception.IllegalMoveException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumSet;
import java.util.Set;

import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.Piece.KING;
import static com.maximilian.chess.enums.Piece.PAWN;
import static com.maximilian.chess.enums.Piece.QUEEN;
import static com.maximilian.chess.enums.Piece.ROOK;
import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A5;
import static com.maximilian.chess.enums.Square.A7;
import static com.maximilian.chess.enums.Square.A8;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C3;
import static com.maximilian.chess.enums.Square.C4;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D4;
import static com.maximilian.chess.enums.Square.D7;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E2;
import static com.maximilian.chess.enums.Square.E3;
import static com.maximilian.chess.enums.Square.E4;
import static com.maximilian.chess.enums.Square.E5;
import static com.maximilian.chess.enums.Square.E6;
import static com.maximilian.chess.enums.Square.E7;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F2;
import static com.maximilian.chess.enums.Square.F5;
import static com.maximilian.chess.enums.Square.F6;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H1;
import static com.maximilian.chess.enums.Square.H8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for {@link GameState}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class GameStateTest {
    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void testCreateStartingGameState () {
        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();

        assertEquals(board.hashCode(), gameState.boardHash());
        assertEquals(WHITE, gameState.colorToMove());
        assertEquals(EnumSet.allOf(CastlingRight.class), gameState.castlingRights());
        assertNull(gameState.enPassantSquare());
        assertEquals(0, gameState.halfMoveClock());
        assertEquals(1, gameState.fullMoveNumber());
        assertNull(gameState.previousGameState());
    }

    @Test
    public void testDeepCopy () {
        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();

        GameState newGameState = GameState.builder()
                .withBoard(board)
                .withColorToMove(BLACK)
                .withCastlingRights(EnumSet.allOf(CastlingRight.class))
                .withEnPassantSquare(E3)
                .withHalfMoveClock(0)
                .withFullMoveNumber(1)
                .withPreviousGameState(gameState)
                .build();
        GameState gameStateCopy = newGameState.deepCopy();

        assertNotSame(newGameState, gameStateCopy);
        assertEquals(newGameState.boardHash(), gameStateCopy.boardHash());
        assertEquals(newGameState.castlingRights(), gameStateCopy.castlingRights());
        assertEquals(newGameState.enPassantSquare(), gameStateCopy.enPassantSquare());
        assertEquals(newGameState.halfMoveClock(), gameStateCopy.halfMoveClock());
        assertEquals(newGameState.fullMoveNumber(), gameStateCopy.fullMoveNumber());
        assertEquals(newGameState.previousGameState(), gameStateCopy.previousGameState());
    }

    @Test
    public void testPreviousHalfMoveClockNullPreviousGameState () {
        GameState gameState = GameState.builder().build();

        assertEquals(0, gameState.previousHalfMoveClock());
    }

    @Test
    public void testPreviousHalfMoveClockNonNullPreviousGameState () {
        int expectedPreviousHalfMoveClock = 12;
        GameState previousGameState = GameState.builder().withHalfMoveClock(expectedPreviousHalfMoveClock).build();
        GameState gameState = GameState.builder().withPreviousGameState(previousGameState).build();

        assertEquals(expectedPreviousHalfMoveClock, gameState.previousHalfMoveClock());
    }

    @Test
    public void testCanDeclareDrawTrueBecauseThreefoldRepetition () {
        /*
         * Tests the threefold repetition rule against these moves:
         *
         * 1. Kf1   Kd8
         * 2. Ke1   Ke8
         * 3. Kf1   Kd8
         * 4. Ke1   Ke8
         * 5. (1/2-1/2)
         */
        Board boardWhiteToMoveToF1 = Board.builder()
                .setWhiteKing(E1)
                .addWhiteRooks(E2, F2)
                .setBlackKing(E8)
                .addBlackRooks(E7, D7)
                .build();
        Board boardBlackToMoveToD8 = Board.builder()
                .setWhiteKing(F1)
                .addWhiteRooks(E2, F2)
                .setBlackKing(E8)
                .addBlackRooks(E7, D7)
                .build();
        Board boardWhiteToMoveToE1 = Board.builder()
                .setWhiteKing(F1)
                .addWhiteRooks(E2, F2)
                .setBlackKing(D8)
                .addBlackRooks(E7, D7)
                .build();
        Board boardBlackToMoveToE8 = Board.builder()
                .setWhiteKing(F1)
                .addWhiteRooks(E2, F2)
                .setBlackKing(D8)
                .addBlackRooks(E7, D7)
                .build();

        GameState gameStateStart = GameState.builder().withBoard(boardWhiteToMoveToF1).withColorToMove(WHITE).build();
        GameState gameStateAfterWhiteMoveOne = GameState.builder()
                .withBoard(boardBlackToMoveToD8)
                .withColorToMove(BLACK)
                .withPreviousGameState(gameStateStart)
                .build();
        GameState gameStateAfterBlackToMoveOne = GameState.builder()
                .withBoard(boardWhiteToMoveToE1)
                .withColorToMove(WHITE)
                .withPreviousGameState(gameStateAfterWhiteMoveOne)
                .build();
        GameState gameStateAfterWhiteToMoveTwo = GameState.builder()
                .withBoard(boardBlackToMoveToE8)
                .withColorToMove(BLACK)
                .withPreviousGameState(gameStateAfterBlackToMoveOne)
                .build();
        GameState gameStateAfterBlackToMoveTwo = GameState.builder()
                .withBoard(boardWhiteToMoveToF1)
                .withColorToMove(WHITE)
                .withPreviousGameState(gameStateAfterWhiteToMoveTwo)
                .build();
        GameState gameStateAfterWhiteToMoveThree = GameState.builder()
                .withBoard(boardBlackToMoveToD8)
                .withColorToMove(BLACK)
                .withPreviousGameState(gameStateAfterBlackToMoveTwo)
                .build();
        GameState gameStateAfterBlackToMoveThree = GameState.builder()
                .withBoard(boardWhiteToMoveToE1)
                .withColorToMove(WHITE)
                .withPreviousGameState(gameStateAfterWhiteToMoveThree)
                .build();
        GameState gameStateAfterWhiteToMoveFour = GameState.builder()
                .withBoard(boardBlackToMoveToE8)
                .withColorToMove(BLACK)
                .withPreviousGameState(gameStateAfterBlackToMoveThree)
                .build();
        GameState thirdRepeatedGameState = GameState.builder()
                .withBoard(boardWhiteToMoveToF1)
                .withColorToMove(WHITE)
                .withPreviousGameState(gameStateAfterWhiteToMoveFour)
                .build();

        assertTrue(thirdRepeatedGameState.canDeclareDraw());
    }

    @Test
    public void testCanDeclareDrawTrueBecauseFiftyMoveRule () {
        GameState gameState = GameState.builder().withHalfMoveClock(50).build();

        assertTrue(gameState.canDeclareDraw());
    }

    @Test
    public void testCanDeclareDrawFalse () {
        Board board = Board.builder()
                .setWhiteKing(E1)
                .addWhiteRooks(E2, F2)
                .setBlackKing(E8)
                .addBlackRooks(E7, D7)
                .build();
        GameState gameStateOne = GameState.builder()
                .withBoard(board)
                .withColorToMove(WHITE)
                .withHalfMoveClock(48)
                .build();
        GameState gameStateTwo = GameState.builder()
                .withBoard(board)
                .withColorToMove(WHITE)
                .withHalfMoveClock(49)
                .withPreviousGameState(gameStateOne)
                .build();

        assertFalse(gameStateTwo.canDeclareDraw());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDoMoveFailureBecauseNullBoard () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("board cannot be null.");

        GameState gameState = GameState.createStartingGameState();
        gameState.doMove(null, Move.create(WHITE, PAWN, E2, E4));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDoMoveFailureBecauseNullMove () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("move cannot be null.");

        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();
        gameState.doMove(board, null);
    }

    @Test
    public void testDoMoveFailureBecauseMoveSpecifiesWrongColorToMove () {
        Color colorToMove = BLACK;

        exception.expect(IllegalMoveException.class);
        exception.expectMessage("It is not " + colorToMove + "'s turn to move.");

        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();
        gameState.doMove(board, Move.create(colorToMove, PAWN, E8, E6));
    }

    @Test
    public void testDoMoveFailureBecauseIllegalEnPassantMove () {
        Square enPassantSquare = C3;

        exception.expect(IllegalMoveException.class);
        exception.expectMessage(
                "Illegal en passant square specified for move (legal en passant = " + enPassantSquare + ").");

        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addWhitePawns(C4, E4).addBlackPawns(D4).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withEnPassantSquare(enPassantSquare)
                .withColorToMove(BLACK)
                .build();
        gameState.doMove(board, Move.createEnPassant(BLACK, D4, E3, E4));
    }

    @Test
    public void testDoMoveFailureBecauseWhiteDoesNotHaveKingsideCastlingRights () {
        Set<CastlingRight> castlingRights = EnumSet.of(CastlingRight.WHITE_QUEENSIDE);

        exception.expect(IllegalMoveException.class);
        exception.expectMessage(
                "White does not have kingside castling rights (castling rights = " + castlingRights + ").");

        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(castlingRights)
                .withColorToMove(WHITE)
                .build();
        gameState.doMove(board, Move.WHITE_KINGSIDE_CASTLE);
    }

    @Test
    public void testDoMoveFailureBecauseWhiteDoesNotHaveQueensideCastlingRights () {
        Set<CastlingRight> castlingRights = EnumSet.of(CastlingRight.WHITE_KINGSIDE);

        exception.expect(IllegalMoveException.class);
        exception.expectMessage(
                "White does not have queenside castling rights (castling rights = " + castlingRights + ").");

        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(castlingRights)
                .withColorToMove(WHITE)
                .build();
        gameState.doMove(board, Move.WHITE_QUEENSIDE_CASTLE);
    }

    @Test
    public void testDoMoveFailureBecauseBlackDoesNotHaveKingsideCastlingRights () {
        Set<CastlingRight> castlingRights = EnumSet.of(CastlingRight.BLACK_QUEENSIDE);

        exception.expect(IllegalMoveException.class);
        exception.expectMessage(
                "Black does not have kingside castling rights (castling rights = " + castlingRights + ").");

        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(castlingRights)
                .withColorToMove(BLACK)
                .build();
        gameState.doMove(board, Move.BLACK_KINGSIDE_CASTLE);
    }

    @Test
    public void testDoMoveFailureBecauseBlackDoesNotHaveQueensideCastlingRights () {
        Set<CastlingRight> castlingRights = EnumSet.of(CastlingRight.BLACK_KINGSIDE);

        exception.expect(IllegalMoveException.class);
        exception.expectMessage(
                "Black does not have queenside castling rights (castling rights = " + castlingRights + ").");

        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(castlingRights)
                .withColorToMove(BLACK)
                .build();
        gameState.doMove(board, Move.BLACK_QUEENSIDE_CASTLE);
    }

    @Test
    public void testDoMoveSuccessHalfMoveResetOnCapture () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder().withBoard(board).withColorToMove(WHITE).withHalfMoveClock(24).build();
        GameState newGameState = gameState.doMove(board, Move.createCapture(WHITE, ROOK, ROOK, A1, A8));

        assertEquals(0, newGameState.halfMoveClock());
    }

    @Test
    public void testDoMoveSuccessHalfMoveResetOnPawnAdvance () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackPawns(A7).build();
        GameState gameState = GameState.builder().withBoard(board).withColorToMove(BLACK).withHalfMoveClock(24).build();
        GameState newGameState = gameState.doMove(board, Move.create(BLACK, PAWN, A7, A5));

        assertEquals(0, newGameState.halfMoveClock());
    }

    @Test
    public void testDoMoveSuccessHalfMoveIncrement () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder().withBoard(board).withColorToMove(WHITE).withHalfMoveClock(24).build();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, ROOK, A1, B1));

        assertEquals(25, newGameState.halfMoveClock());
    }

    @Test
    public void testDoMoveSuccessFullMoveSameBecauseWhiteMoved () {
        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, PAWN, E2, E4));

        assertEquals(1, newGameState.fullMoveNumber());
    }

    @Test
    public void testDoMoveSuccessFullMoveIncrementBecauseBlackMoved () {
        GameState gameState = GameState.createStartingGameState();
        Board board = Board.createStartingBoard();
        GameState gameStateAfterWhiteMove = gameState.doMove(board, Move.create(WHITE, PAWN, E2, E4));
        GameState gameStateAfterBlackMove = gameStateAfterWhiteMove.doMove(board, Move.create(BLACK, PAWN, E7, E5));

        assertEquals(2, gameStateAfterBlackMove.fullMoveNumber());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesAllCastlingRightsNonCastlingMove () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, KING, E1, E2));

        assertEquals(EnumSet.noneOf(CastlingRight.class), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesAllCastlingRightsCastlingMove () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.WHITE_KINGSIDE_CASTLE);

        assertEquals(EnumSet.noneOf(CastlingRight.class), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesKingsideCastlingRightsByMovingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, ROOK, H1, G1));

        assertEquals(EnumSet.of(CastlingRight.WHITE_QUEENSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesQueensideCastlingRightsByMovingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, ROOK, A1, B1));

        assertEquals(EnumSet.of(CastlingRight.WHITE_KINGSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesKingsideCastlingRightsByBlackCapturingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).addBlackRooks(H8).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.createCapture(BLACK, ROOK, ROOK, H8, H1));

        assertEquals(EnumSet.of(CastlingRight.WHITE_QUEENSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteLosesQueensideCastlingRightsByBlackCapturingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1, H1).addBlackRooks(A8).setBlackKing(E8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.WHITE_KINGSIDE, CastlingRight.WHITE_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.createCapture(BLACK, ROOK, ROOK, A8, A1));

        assertEquals(EnumSet.of(CastlingRight.WHITE_KINGSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesAllCastlingRightsNonCastlingMove () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(BLACK, KING, E8, E7));

        assertEquals(EnumSet.noneOf(CastlingRight.class), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesAllCastlingRightsCastlingMove () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.BLACK_KINGSIDE_CASTLE);

        assertEquals(EnumSet.noneOf(CastlingRight.class), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesKingsideCastlingRightsByMovingRook () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(BLACK, ROOK, H8, G8));

        assertEquals(EnumSet.of(CastlingRight.BLACK_QUEENSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesQueensideCastlingRightsByMovingRook () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(BLACK)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(BLACK, ROOK, A8, B8));

        assertEquals(EnumSet.of(CastlingRight.BLACK_KINGSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesKingsideCastlingRightsByWhiteCapturingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(H1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.createCapture(WHITE, ROOK, ROOK, H1, H8));

        assertEquals(EnumSet.of(CastlingRight.BLACK_QUEENSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessBlackLosesQueensideCastlingRightsByWhiteCapturingRook () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1).setBlackKing(E8).addBlackRooks(A8, H8).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withCastlingRights(EnumSet.of(CastlingRight.BLACK_KINGSIDE, CastlingRight.BLACK_QUEENSIDE))
                .withColorToMove(WHITE)
                .build();
        GameState newGameState = gameState.doMove(board, Move.createCapture(WHITE, ROOK, ROOK, A1, A8));

        assertEquals(EnumSet.of(CastlingRight.BLACK_KINGSIDE), newGameState.castlingRights());
    }

    @Test
    public void testDoMoveSuccessWhiteSetsEnPassantSquareForBlack () {
        Board board = Board.createStartingBoard();
        GameState gameState = GameState.createStartingGameState();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, PAWN, E2, E4));

        assertEquals(E3, newGameState.enPassantSquare());
    }

    @Test
    public void testDoMoveSuccessBlackSetsEnPassantSquareForWhite () {
        Board board = Board.createStartingBoard();
        GameState gameState = GameState.createStartingGameState();
        GameState gameStateAfterWhiteMove = gameState.doMove(board, Move.create(WHITE, PAWN, E2, E4));
        GameState gameStateAfterBlackMove = gameStateAfterWhiteMove.doMove(board, Move.create(BLACK, PAWN, E7, E5));

        assertEquals(E6, gameStateAfterBlackMove.enPassantSquare());
    }

    @Test
    public void testDoMoveSuccessEnPassantSquareCleared () {
        Board board = Board.builder().setWhiteKing(E1).addWhitePawns(F5).setBlackKing(E8).addBlackPawns(E5).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withColorToMove(WHITE)
                .withEnPassantSquare(E6)
                .build();
        GameState newGameState = gameState.doMove(board, Move.create(WHITE, PAWN, F5, F6));

        assertNull(newGameState.enPassantSquare());
    }

    @Test
    public void testDoMoveSuccessEnPassantCapture () {
        Board board = Board.builder().setWhiteKing(E1).addWhitePawns(F5).setBlackKing(E8).addBlackPawns(E5).build();
        GameState gameState = GameState.builder()
                .withBoard(board)
                .withColorToMove(WHITE)
                .withEnPassantSquare(E6)
                .build();
        GameState newGameState = gameState.doMove(board, Move.createEnPassant(WHITE, F5, E6, E5));

        assertNull(newGameState.enPassantSquare());
        assertEquals(0, board.blackPawnsCount());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testUndoMoveFailureBecauseNullBoard () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("board cannot be null.");

        GameState startingGameState = GameState.createStartingGameState();
        startingGameState.undoMove(null, Move.create(WHITE, PAWN, E2, E4));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testUndoMoveFailureBecauseNullMove () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("move cannot be null.");

        GameState startingGameState = GameState.createStartingGameState();
        Board startingBoard = Board.createStartingBoard();
        startingGameState.undoMove(startingBoard, null);
    }

    @Test
    public void testUndoMoveFailureBecauseNoPreviousGameStatesExist () {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("There are no previous game states - move cannot be undone.");

        GameState startingGameState = GameState.createStartingGameState();
        Board startingBoard = Board.createStartingBoard();
        startingGameState.undoMove(startingBoard, Move.create(WHITE, PAWN, E2, E4));
    }

    @Test
    public void testUndoMoveSuccess () {
        Board previousBoard = Board.builder().setWhiteKing(E1).addWhiteQueens(D1).setBlackKing(E8).build();
        Board currentBoard = Board.builder().setWhiteKing(E1).addWhiteQueens(D8).setBlackKing(E8).build();
        GameState previousGameState = GameState.builder()
                .withBoard(previousBoard)
                .withColorToMove(WHITE)
                .withHalfMoveClock(4)
                .build();
        GameState currentGameState = GameState.builder()
                .withBoard(currentBoard)
                .withColorToMove(BLACK)
                .withHalfMoveClock(5)
                .withPreviousGameState(previousGameState)
                .build();
        GameState gameState = currentGameState.undoMove(currentBoard, Move.create(WHITE, QUEEN, D1, D8));

        assertEquals(previousGameState, gameState);
    }
}
