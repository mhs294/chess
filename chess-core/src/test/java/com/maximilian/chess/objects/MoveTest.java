package com.maximilian.chess.objects;

import com.maximilian.chess.enums.Color;
import com.maximilian.chess.enums.Type;
import com.maximilian.chess.enums.Square;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.maximilian.chess.enums.Color.BLACK;
import static com.maximilian.chess.enums.Color.WHITE;
import static com.maximilian.chess.enums.Type.BISHOP;
import static com.maximilian.chess.enums.Type.KING;
import static com.maximilian.chess.enums.Type.KNIGHT;
import static com.maximilian.chess.enums.Type.PAWN;
import static com.maximilian.chess.enums.Type.QUEEN;
import static com.maximilian.chess.enums.Type.ROOK;
import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A4;
import static com.maximilian.chess.enums.Square.B2;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C4;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D1;
import static com.maximilian.chess.enums.Square.D7;
import static com.maximilian.chess.enums.Square.D8;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F1;
import static com.maximilian.chess.enums.Square.F3;
import static com.maximilian.chess.enums.Square.F4;
import static com.maximilian.chess.enums.Square.F7;
import static com.maximilian.chess.enums.Square.F8;
import static com.maximilian.chess.enums.Square.G1;
import static com.maximilian.chess.enums.Square.G4;
import static com.maximilian.chess.enums.Square.G8;
import static com.maximilian.chess.enums.Square.H4;
import static com.maximilian.chess.enums.Square.H8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for {@link Move}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class MoveTest {
    @Test
    public void testStandardMove () {
        Color color = WHITE;
        Type type = BISHOP;
        Square start = F1;
        Square end = C4;
        Move move = Move.create(color, type, start, end);

        assertEquals(color, move.color());
        assertEquals(type, move.piece());
        assertEquals(start, move.start());
        assertEquals(end, move.end());

        assertNull(move.capturedPiece());
        assertNull(move.enPassantCaptureSquare());
        assertNull(move.promoteTo());
    }

    @Test
    public void testMoveWithEnPassantCapture () {
        Color color = BLACK;
        Square start = G4;
        Square end = F3;
        Square enPassant = F4;
        Move move = Move.createEnPassant(color, start, end, enPassant);

        assertEquals(color, move.color());
        assertEquals(PAWN, move.piece());
        assertEquals(start, move.start());
        assertEquals(end, move.end());
        assertEquals(PAWN, move.capturedPiece());
        assertEquals(enPassant, move.enPassantCaptureSquare());

        assertNull(move.promoteTo());
    }

    @Test
    public void testMoveWithNormalCapture () {
        Color color = WHITE;
        Type type = KNIGHT;
        Type capturedType = ROOK;
        Square start = F7;
        Square end = H8;
        Move move = Move.createCapture(color, type, capturedType, start, end);

        assertEquals(color, move.color());
        assertEquals(type, move.piece());
        assertEquals(capturedType, move.capturedPiece());
        assertEquals(start, move.start());
        assertEquals(end, move.end());

        assertNull(move.enPassantCaptureSquare());
        assertNull(move.promoteTo());
    }

    @Test
    public void testMoveWithPromotion () {
        Color color = BLACK;
        Square start = B2;
        Square end = A1;
        Type promoteTo = QUEEN;
        Move move = Move.createPromotion(color, start, end, promoteTo);

        assertEquals(color, move.color());
        assertEquals(PAWN, move.piece());
        assertEquals(start, move.start());
        assertEquals(end, move.end());
        assertEquals(promoteTo, move.promoteTo());

        assertNull(move.capturedPiece());
        assertNull(move.enPassantCaptureSquare());
    }

    @Test
    public void testMoveWithPromotionAndCapture () {
        Color color = WHITE;
        Type capturedType = BISHOP;
        Square start = D7;
        Square end = C8;
        Type promoteTo = QUEEN;
        Move move = Move.createCapturePromotion(color, capturedType, start, end, promoteTo);

        assertEquals(color, move.color());
        assertEquals(PAWN, move.piece());
        assertEquals(capturedType, move.capturedPiece());
        assertEquals(start, move.start());
        assertEquals(end, move.end());
        assertEquals(promoteTo, move.promoteTo());

        assertNull(move.enPassantCaptureSquare());
    }

    @Test
    public void testCastlingMoves () {
        assertTrue(Move.create(WHITE, KING, E1, G1).isCastling());
        assertTrue(Move.create(WHITE, KING, E1, C1).isCastling());
        assertTrue(Move.create(BLACK, KING, E8, G8).isCastling());
        assertTrue(Move.create(BLACK, KING, E8, C8).isCastling());

        assertFalse(Move.create(WHITE, KING, E1, F1).isCastling());
        assertFalse(Move.create(BLACK, KING, E8, F8).isCastling());
        assertFalse(Move.create(WHITE, QUEEN, D1, A4).isCastling());
        assertFalse(Move.create(BLACK, QUEEN, D8, H4).isCastling());
    }
}
