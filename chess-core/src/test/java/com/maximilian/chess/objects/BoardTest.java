package com.maximilian.chess.objects;

import com.google.common.collect.Sets;
import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Piece;
import com.maximilian.chess.enums.Square;
import com.maximilian.chess.exception.IllegalMoveException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumSet;
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
import static com.maximilian.chess.enums.Square.A3;
import static com.maximilian.chess.enums.Square.A4;
import static com.maximilian.chess.enums.Square.A5;
import static com.maximilian.chess.enums.Square.A6;
import static com.maximilian.chess.enums.Square.A7;
import static com.maximilian.chess.enums.Square.A8;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B2;
import static com.maximilian.chess.enums.Square.B3;
import static com.maximilian.chess.enums.Square.B4;
import static com.maximilian.chess.enums.Square.B5;
import static com.maximilian.chess.enums.Square.B6;
import static com.maximilian.chess.enums.Square.B7;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C2;
import static com.maximilian.chess.enums.Square.C3;
import static com.maximilian.chess.enums.Square.C4;
import static com.maximilian.chess.enums.Square.C5;
import static com.maximilian.chess.enums.Square.C6;
import static com.maximilian.chess.enums.Square.C7;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D2;
import static com.maximilian.chess.enums.Square.D3;
import static com.maximilian.chess.enums.Square.D4;
import static com.maximilian.chess.enums.Square.D5;
import static com.maximilian.chess.enums.Square.D6;
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
import static com.maximilian.chess.enums.Square.F3;
import static com.maximilian.chess.enums.Square.F4;
import static com.maximilian.chess.enums.Square.F5;
import static com.maximilian.chess.enums.Square.F6;
import static com.maximilian.chess.enums.Square.F7;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G2;
import static com.maximilian.chess.enums.Square.G3;
import static com.maximilian.chess.enums.Square.G4;
import static com.maximilian.chess.enums.Square.G5;
import static com.maximilian.chess.enums.Square.G6;
import static com.maximilian.chess.enums.Square.G7;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H1;
import static com.maximilian.chess.enums.Square.H2;
import static com.maximilian.chess.enums.Square.H3;
import static com.maximilian.chess.enums.Square.H4;
import static com.maximilian.chess.enums.Square.H5;
import static com.maximilian.chess.enums.Square.H6;
import static com.maximilian.chess.enums.Square.H7;
import static com.maximilian.chess.enums.Square.H8;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/**
 * Unit test class for {@link Board}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class BoardTest {
    private static final Board STARTING_BOARD = Board.createStartingBoard();

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void testDeepCopy () {
        Board startingBoardDeepCopy = STARTING_BOARD.deepCopy();

        assertNotSame(STARTING_BOARD, startingBoardDeepCopy);
        assertEquals(STARTING_BOARD, startingBoardDeepCopy);
    }

    @Test
    public void testToMap () {
        final Map<Square, Pair<Color, Piece>> startingBoardMap = STARTING_BOARD.toMap();

        // Assert white pieces
        EnumSet.of(A2, B2, C2, D2, E2, F2, G2, H2)
                .forEach(sq -> assertEquals(Pair.of(WHITE, PAWN), startingBoardMap.get(sq)));
        EnumSet.of(B1, G1).forEach(sq -> assertEquals(Pair.of(WHITE, KNIGHT), startingBoardMap.get(sq)));
        EnumSet.of(C1, F1).forEach(sq -> assertEquals(Pair.of(WHITE, BISHOP), startingBoardMap.get(sq)));
        EnumSet.of(A1, H1).forEach(sq -> assertEquals(Pair.of(WHITE, ROOK), startingBoardMap.get(sq)));
        assertEquals(Pair.of(WHITE, QUEEN), startingBoardMap.get(D1));
        assertEquals(Pair.of(WHITE, KING), startingBoardMap.get(E1));

        // Assert black pieces
        EnumSet.of(A7, B7, C7, D7, E7, F7, G7, H7)
                .forEach(sq -> assertEquals(Pair.of(BLACK, PAWN), startingBoardMap.get(sq)));
        EnumSet.of(B8, G8).forEach(sq -> assertEquals(Pair.of(BLACK, KNIGHT), startingBoardMap.get(sq)));
        EnumSet.of(C8, F8).forEach(sq -> assertEquals(Pair.of(BLACK, BISHOP), startingBoardMap.get(sq)));
        EnumSet.of(A8, H8).forEach(sq -> assertEquals(Pair.of(BLACK, ROOK), startingBoardMap.get(sq)));
        assertEquals(Pair.of(BLACK, QUEEN), startingBoardMap.get(D8));
        assertEquals(Pair.of(BLACK, KING), startingBoardMap.get(E8));

        // Assert vacant squares
        EnumSet.of(A3, A4, A5, A6, B3, B4, B5, B6, C3, C4, C5, C6, D3, D4, D5, D6, E3, E4, E5, E6, F3, F4, F5, F6, G3,
                G4, G5, G6, H3, H4, H5, H6).forEach(sq -> assertNull(startingBoardMap.get(sq)));
    }

    @Test
    public void testWhitePawnSquares () {
        assertEquals(Sets.newHashSet(A2, B2, C2, D2, E2, F2, G2, H2), STARTING_BOARD.whitePawnSquares());
    }

    @Test
    public void testWhiteKnightSquares () {
        assertEquals(Sets.newHashSet(B1, G1), STARTING_BOARD.whiteKnightSquares());
    }

    @Test
    public void testWhiteBishopSquares () {
        assertEquals(Sets.newHashSet(C1, F1), STARTING_BOARD.whiteBishopSquares());
    }

    @Test
    public void testWhiteRookSquares () {
        assertEquals(Sets.newHashSet(A1, H1), STARTING_BOARD.whiteRookSquares());
    }

    @Test
    public void testWhiteQueenSquares () {
        assertEquals(Sets.newHashSet(D1), STARTING_BOARD.whiteQueenSquares());
    }

    @Test
    public void testWhiteKingSquare () {
        assertEquals(E1, STARTING_BOARD.whiteKingSquare());
    }

    @Test
    public void testBlackPawnSquares () {
        assertEquals(Sets.newHashSet(A7, B7, C7, D7, E7, F7, G7, H7), STARTING_BOARD.blackPawnSquares());
    }

    @Test
    public void testBlackKnightSquares () {
        assertEquals(Sets.newHashSet(B8, G8), STARTING_BOARD.blackKnightSquares());
    }

    @Test
    public void testBlackBishopSquares () {
        assertEquals(Sets.newHashSet(C8, F8), STARTING_BOARD.blackBishopSquares());
    }

    @Test
    public void testBlackRookSquares () {
        assertEquals(Sets.newHashSet(A8, H8), STARTING_BOARD.blackRookSquares());
    }

    @Test
    public void testBlackQueenSquares () {
        assertEquals(Sets.newHashSet(D8), STARTING_BOARD.blackQueenSquares());
    }

    @Test
    public void testBlackKingSquare () {
        assertEquals(E8, STARTING_BOARD.blackKingSquare());
    }

    @Test
    public void testVacant () {
        long expectedVacantBitmask = EMPTY_BITMASK;
        Set<Square> expectedVacantSquares = EnumSet.of(A3, A4, A5, A6, B3, B4, B5, B6, C3, C4, C5, C6, D3, D4, D5, D6,
                E3, E4, E5, E6, F3, F4, F5, F6, G3, G4, G5, G6, H3, H4, H5, H6);
        for (Square square : expectedVacantSquares) {
            expectedVacantBitmask |= square.bitmask();
        }

        assertEquals(expectedVacantBitmask, STARTING_BOARD.vacantBitmask());
        assertEquals(expectedVacantSquares, STARTING_BOARD.vacantSquares());
    }

    @Test
    public void testWhiteOccupied () {
        long expectedWhiteOccupiedBitmask = EMPTY_BITMASK;
        Set<Square> expectedWhiteOccupiedSquares = EnumSet.of(A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2, E2, F2,
                G2, H2);
        for (Square square : expectedWhiteOccupiedSquares) {
            expectedWhiteOccupiedBitmask |= square.bitmask();
        }

        assertEquals(expectedWhiteOccupiedBitmask, STARTING_BOARD.whiteOccupiedBitmask());
        assertEquals(expectedWhiteOccupiedSquares, STARTING_BOARD.whiteOccupiedSquares());
    }

    @Test
    public void testBlackOccupied () {
        long expectedBlackOccupiedBitmask = EMPTY_BITMASK;
        Set<Square> expectedBlackOccupiedSquares = EnumSet.of(A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8,
                G8, H8);
        for (Square square : expectedBlackOccupiedSquares) {
            expectedBlackOccupiedBitmask |= square.bitmask();
        }

        assertEquals(expectedBlackOccupiedBitmask, STARTING_BOARD.blackOccupiedBitmask());
        assertEquals(expectedBlackOccupiedSquares, STARTING_BOARD.blackOccupiedSquares());
    }

    @Test
    public void testAllOccupied () {
        long expectedOccupiedBitmask = EMPTY_BITMASK;
        Set<Square> expectedOccupiedSquares = EnumSet.of(A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2, E2, F2, G2, H2,
                A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8, G8, H8);
        for (Square square : expectedOccupiedSquares) {
            expectedOccupiedBitmask |= square.bitmask();
        }

        assertEquals(expectedOccupiedBitmask, STARTING_BOARD.occupiedBitmask());
        assertEquals(expectedOccupiedSquares, STARTING_BOARD.occupiedSquares());
    }

    @Test
    public void testWhitePawnsCount () {
        assertEquals(8, STARTING_BOARD.whitePawnsCount());
    }

    @Test
    public void testWhiteKnightsCount () {
        assertEquals(2, STARTING_BOARD.whiteKnightsCount());
    }

    @Test
    public void testWhiteBishopsCount () {
        assertEquals(2, STARTING_BOARD.whiteBishopsCount());
    }

    @Test
    public void testWhiteRooksCount () {
        assertEquals(2, STARTING_BOARD.whiteRooksCount());
    }

    @Test
    public void testWhiteQueensCount () {
        assertEquals(1, STARTING_BOARD.whiteQueensCount());
    }

    @Test
    public void testWhitePiecesCount () {
        assertEquals(16, STARTING_BOARD.whitePiecesCount());
    }

    @Test
    public void testBlackPawnsCount () {
        assertEquals(8, STARTING_BOARD.blackPawnsCount());
    }

    @Test
    public void testBlackKnightsCount () {
        assertEquals(2, STARTING_BOARD.blackKnightsCount());
    }

    @Test
    public void testBlackBishopsCount () {
        assertEquals(2, STARTING_BOARD.blackBishopsCount());
    }

    @Test
    public void testBlackRooksCount () {
        assertEquals(2, STARTING_BOARD.blackRooksCount());
    }

    @Test
    public void testBlackQueensCount () {
        assertEquals(1, STARTING_BOARD.blackQueensCount());
    }

    @Test
    public void testBlackPiecesCount () {
        assertEquals(16, STARTING_BOARD.blackPiecesCount());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDoMoveFailureBecauseNullMove () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("move cannot be null.");

        STARTING_BOARD.doMove(null);
    }

    @Test
    public void testDoMoveFailureBecauseSameStartAndEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("move start and end cannot be identical.");

        Move move = Move.create(WHITE, PAWN, E2, E2);
        STARTING_BOARD.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseCapturedKing () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("capturedPiece cannot be KING.");

        Board board = Board.builder().addWhitePawns(E4).setBlackKing(F5).build();
        Move move = Move.createCapture(WHITE, PAWN, KING, E4, F5);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseIllegalPromoteToPawn () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("promoteTo cannot be PAWN or KING.");

        Board board = Board.builder().addBlackPawns(A2).build();
        Move move = Move.createPromotion(BLACK, A2, A1, PAWN);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseIllegalPromoteToKing () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("promoteTo cannot be PAWN or KING.");

        Board board = Board.builder().addBlackPawns(A2).build();
        Move move = Move.createPromotion(BLACK, A2, A1, KING);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseMovedPieceIsNotAtStartSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's piece was not at the specified starting square.");

        Board board = Board.builder().addBlackBishops(F8).build();
        Move move = Move.create(BLACK, BISHOP, C8, B7);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongPieceIsAtStartSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's piece was not at the specified starting square.");

        Board board = Board.builder().addBlackBishops(F8).build();
        Move move = Move.create(BLACK, ROOK, F8, B8);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongColorPieceIsAtStartSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's piece was not at the specified starting square.");

        Board board = Board.builder().addWhiteBishops(F8).build();
        Move move = Move.create(BLACK, BISHOP, F8, B8);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecausePieceIsNotAtNormalCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addWhiteKnights(B4).addBlackBishops(A6).build();
        Move move = Move.createCapture(WHITE, KNIGHT, BISHOP, B4, D5);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongPieceIsAtNormalCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addWhiteKnights(B4).addBlackBishops(A6).build();
        Move move = Move.createCapture(WHITE, KNIGHT, ROOK, B4, A6);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongColorPieceIsAtNormalCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addWhiteKnights(B4).addWhiteBishops(A6).build();
        Move move = Move.createCapture(WHITE, KNIGHT, BISHOP, B4, A6);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecausePieceIsNotAtEnPassantCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addWhitePawns(F4).addBlackPawns(E4).build();
        Move move = Move.createEnPassant(BLACK, E4, D3, D4);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongPieceIsAtEnPassantCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addWhiteKnights(F4).addBlackPawns(E4).build();
        Move move = Move.createEnPassant(BLACK, E4, F3, F4);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseWrongColorPieceIsAtEnPassantCaptureEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece was not at the specified square.");

        Board board = Board.builder().addBlackPawns(E4, F4).build();
        Move move = Move.createEnPassant(BLACK, E4, F3, F4);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseEndSquareIsOccupiedBySameColor () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("Could not add the move's piece to the specified ending square.");

        Board board = Board.builder().addWhiteRooks(A1, A8).build();
        Move move = Move.create(WHITE, ROOK, A1, A8);
        board.doMove(move);
    }

    @Test
    public void testDoMoveFailureBecauseCastlingRookIsNotAtStartSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The rook could not be removed from its starting square for castling.");

        Board board = Board.builder().setWhiteKing(E1).build();
        board.doMove(Move.WHITE_KINGSIDE_CASTLE);
    }

    @Test
    public void testDoMoveFailureBecauseNonRookIsAtCastlingRookStartSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The rook could not be removed from its starting square for castling.");

        Board board = Board.builder().setWhiteKing(E1).addWhiteQueens(H1).build();
        board.doMove(Move.WHITE_KINGSIDE_CASTLE);
    }

    @Test
    public void testDoMoveFailureBecauseCastlingRookEndSquareIsOccupiedBySameColor () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The rook could not be added to its ending square for castling.");

        Board board = Board.builder().addWhiteQueens(D1).addWhiteRooks(A1).setWhiteKing(E1).build();
        board.doMove(Move.WHITE_QUEENSIDE_CASTLE);
    }

    @Test
    public void testDoMoveNormal () {
        Board board = Board.builder().setBlackKing(E8).build();
        Move move = Move.create(BLACK, KING, E8, F8);
        board.doMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(BLACK, KING), boardMap.get(F8));
    }

    @Test
    public void testDoMoveNormalCapture () {
        Board board = Board.builder().addWhiteBishops(E4).addBlackPawns(C6).build();
        Move move = Move.createCapture(WHITE, BISHOP, PAWN, E4, C6);
        board.doMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(WHITE, BISHOP), boardMap.get(C6));
    }

    @Test
    public void testDoMoveEnPassantCapture () {
        Board board = Board.builder().addWhitePawns(E4).addBlackPawns(F4).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.doMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(BLACK, PAWN), boardMap.get(E3));
    }

    @Test
    public void testDoMovePromotion () {
        Board board = Board.builder().addWhitePawns(A7).build();
        Move move = Move.createPromotion(WHITE, A7, A8, QUEEN);
        board.doMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(WHITE, QUEEN), boardMap.get(A8));
    }

    @Test
    public void testDoMoveCaptureAndPromotion () {
        Board board = Board.builder().addBlackPawns(F2).addWhiteKnights(G1).build();
        Move move = Move.createCapturePromotion(BLACK, KNIGHT, F2, G1, QUEEN);
        board.doMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(BLACK, QUEEN), boardMap.get(G1));
    }

    @Test
    public void testDoMoveWhiteKingsideCastle () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(H1).build();
        board.doMove(Move.WHITE_KINGSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(WHITE, KING), boardMap.get(G1));
        assertEquals(Pair.of(WHITE, ROOK), boardMap.get(F1));
    }

    @Test
    public void testDoMoveWhiteQueensideCastle () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteRooks(A1).build();
        board.doMove(Move.WHITE_QUEENSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(WHITE, KING), boardMap.get(C1));
        assertEquals(Pair.of(WHITE, ROOK), boardMap.get(D1));
    }

    @Test
    public void testDoMoveBlackKingsideCastle () {
        Board board = Board.builder().setBlackKing(E8).addBlackRooks(H8).build();
        board.doMove(Move.BLACK_KINGSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, KING), boardMap.get(G8));
        assertEquals(Pair.of(BLACK, ROOK), boardMap.get(F8));
    }

    @Test
    public void testDoMoveBlackQueensideCastle () {
        Board board = Board.builder().setBlackKing(E8).addBlackRooks(A8).build();
        board.doMove(Move.BLACK_QUEENSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, KING), boardMap.get(C8));
        assertEquals(Pair.of(BLACK, ROOK), boardMap.get(D8));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testUndoMoveFailureBecauseNullMove () {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("move cannot be null.");

        STARTING_BOARD.undoMove(null);
    }

    @Test
    public void testUndoMoveFailureBecauseSameStartAndEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("move start and end cannot be identical.");

        Move move = Move.create(WHITE, PAWN, E2, E2);
        STARTING_BOARD.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseCapturedKing () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("capturedPiece cannot be KING.");

        Board board = Board.builder().addWhitePawns(E4).setBlackKing(F5).build();
        Move move = Move.createCapture(WHITE, PAWN, KING, E4, F5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseIllegalPromoteToPawn () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("promoteTo cannot be PAWN or KING.");

        Board board = Board.builder().addBlackPawns(A2).build();
        Move move = Move.createPromotion(BLACK, A2, A1, PAWN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseIllegalPromoteToKing () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("promoteTo cannot be PAWN or KING.");

        Board board = Board.builder().addBlackPawns(A2).build();
        Move move = Move.createPromotion(BLACK, A2, A1, KING);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseStartSquareIsOccupied () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("Could not add the move's piece to the specified starting square.");

        Board board = Board.builder().addWhiteKnights(C3, D5).build();
        Move move = Move.create(WHITE, KNIGHT, C3, D5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecausePieceIsNotAtEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteKnights(F1).build();
        Move move = Move.create(WHITE, KNIGHT, C3, B1);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseWrongPieceIsAtEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteBishops(B1).build();
        Move move = Move.create(WHITE, KNIGHT, C3, B1);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseWrongColorPieceIsAtEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackKnights(B1).build();
        Move move = Move.create(WHITE, KNIGHT, C3, B1);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseNormalCaptureEndSquareIsVacant () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteRooks(F3).build();
        Move move = Move.createCapture(WHITE, ROOK, ROOK, F3, F5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseNormalCaptureEndSquareIsOccupiedByWrongPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteBishops(F5).build();
        Move move = Move.createCapture(WHITE, ROOK, ROOK, F3, F5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseNormalCaptureEndSquareIsOccupiedByWrongColorPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackRooks(F5).build();
        Move move = Move.createCapture(WHITE, ROOK, ROOK, F3, F5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseNormalCaptureStartSquareIsOccupied () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("Could not add the move's piece to the specified starting square.");

        Board board = Board.builder().addWhiteRooks(F3, F5).build();
        Move move = Move.createCapture(WHITE, ROOK, ROOK, F3, F5);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseEnPassantEndingSquareIsVacant () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhitePawns(E4).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseEnPassantEndingSquareIsOccuipedByWrongPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackKnights(E3).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseEnPassantEndingSquareIsOccuipedByWrongColorPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhitePawns(E3).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseEnPassantStartSquareIsOccupied () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("Could not add the move's piece to the specified starting square.");

        Board board = Board.builder().addBlackPawns(E3, F4).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseEnPassantCapturedSquareIsOccupied () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's captured piece could not be added to its original square.");

        Board board = Board.builder().addBlackPawns(E3).addWhitePawns(E4).build();
        Move move = Move.createEnPassant(BLACK, F4, E3, E4);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecausePromotionEndingSquareIsVacant () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackPawns(F2).build();
        Move move = Move.createPromotion(BLACK, F2, F1, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecausePromotionEndingSquareIsOccupiedByWrongPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackRooks(F1).build();
        Move move = Move.createPromotion(BLACK, F2, F1, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecausePromotionEndingSquareIsOccupiedByWrongColorPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteQueens(F1).build();
        Move move = Move.createPromotion(BLACK, F2, F1, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseCapturePromotionEndingSquareIsVacant () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhitePawns(E7).build();
        Move move = Move.createCapturePromotion(WHITE, KNIGHT, E7, F8, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseCapturePromotionEndingSquareIsOccupiedByWrongPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addWhiteRooks(F8).build();
        Move move = Move.createCapturePromotion(WHITE, KNIGHT, E7, F8, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseCapturePromotionEndingSquareIsOccupiedByWrongColorPiece () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The move's ending piece was not at the specified ending square.");

        Board board = Board.builder().addBlackQueens(F8).build();
        Move move = Move.createCapturePromotion(WHITE, KNIGHT, E7, F8, QUEEN);
        board.undoMove(move);
    }

    @Test
    public void testUndoMoveFailureBecauseCastlingRookIsNotAtEndSquare () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The rook could not be removed from its ending square for castling.");

        Board board = Board.builder().setBlackKing(G8).addBlackQueens(F8).build();
        board.undoMove(Move.BLACK_KINGSIDE_CASTLE);
    }

    @Test
    public void testUndoMoveFailureBecauseCastlingRookStartSquareIsOccupiedBySameColor () {
        exception.expect(IllegalMoveException.class);
        exception.expectMessage("The rook could not be added to its starting square for castling.");

        Board board = Board.builder().addBlackRooks(A8, D8).setBlackKing(C8).build();
        board.undoMove(Move.BLACK_QUEENSIDE_CASTLE);
    }

    @Test
    public void testUndoMoveNormal () {
        Board board = Board.builder().addWhitePawns(E4).build();
        Move move = Move.create(WHITE, PAWN, E2, E4);
        board.undoMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(WHITE, PAWN), boardMap.get(E2));
    }

    @Test
    public void testUndoMoveNormalCapture () {
        Board board = Board.builder().addBlackBishops(F3).build();
        Move move = Move.createCapture(BLACK, BISHOP, KNIGHT, B7, F3);
        board.undoMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, BISHOP), boardMap.get(B7));
        assertEquals(Pair.of(WHITE, KNIGHT), boardMap.get(F3));
    }

    @Test
    public void testUndoMoveEnPassantCapture () {
        Board board = Board.builder().addBlackPawns(E3).build();
        Move move = Move.createEnPassant(BLACK, D4, E3, E4);
        board.undoMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, PAWN), boardMap.get(D4));
        assertEquals(Pair.of(WHITE, PAWN), boardMap.get(E4));
    }

    @Test
    public void testUndoMovePromotion () {
        Board board = Board.builder().addWhiteQueens(B8).build();
        Move move = Move.createPromotion(WHITE, B7, B8, QUEEN);
        board.undoMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(1, boardMap.size());
        assertEquals(Pair.of(WHITE, PAWN), boardMap.get(B7));
    }

    @Test
    public void testUndoMoveCaptureAndPromotion () {
        Board board = Board.builder().addWhiteQueens(B8).build();
        Move move = Move.createCapturePromotion(WHITE, KNIGHT, C7, B8, QUEEN);
        board.undoMove(move);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(WHITE, PAWN), boardMap.get(C7));
        assertEquals(Pair.of(BLACK, KNIGHT), boardMap.get(B8));
    }

    @Test
    public void testUndoMoveWhiteKingsideCastle () {
        Board board = Board.builder().setWhiteKing(G1).addWhiteRooks(F1).build();
        board.undoMove(Move.WHITE_KINGSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(WHITE, KING), boardMap.get(E1));
        assertEquals(Pair.of(WHITE, ROOK), boardMap.get(H1));
    }

    @Test
    public void testUndoMoveWhiteQueensideCastle () {
        Board board = Board.builder().setWhiteKing(C1).addWhiteRooks(D1).build();
        board.undoMove(Move.WHITE_QUEENSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(WHITE, KING), boardMap.get(E1));
        assertEquals(Pair.of(WHITE, ROOK), boardMap.get(A1));
    }

    @Test
    public void testUndoMoveBlackKingsideCastle () {
        Board board = Board.builder().setBlackKing(G8).addBlackRooks(F8).build();
        board.undoMove(Move.BLACK_KINGSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, KING), boardMap.get(E8));
        assertEquals(Pair.of(BLACK, ROOK), boardMap.get(H8));
    }

    @Test
    public void testUndoMoveBlackQueensideCastle () {
        Board board = Board.builder().setBlackKing(C8).addBlackRooks(D8).build();
        board.undoMove(Move.BLACK_QUEENSIDE_CASTLE);
        Map<Square, Pair<Color, Piece>> boardMap = board.toMap();

        assertEquals(2, boardMap.size());
        assertEquals(Pair.of(BLACK, KING), boardMap.get(E8));
        assertEquals(Pair.of(BLACK, ROOK), boardMap.get(A8));
    }

    @Test
    public void testToString () {
        System.out.println(STARTING_BOARD.toString());
    }
}
