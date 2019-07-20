package com.maximilian.chess.util;

import com.maximilian.chess.objects.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B8;
import static com.maximilian.chess.enums.Square.C1;
import static com.maximilian.chess.enums.Square.C8;
import static com.maximilian.chess.enums.Square.D4;
import static com.maximilian.chess.enums.Square.E1;
import static com.maximilian.chess.enums.Square.E3;
import static com.maximilian.chess.enums.Square.E8;
import static com.maximilian.chess.enums.Square.F6;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for {@link StalemateUtils}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class StalemateUtilsTest {
    @Test
    public void testWhiteKingVsBlackKingAndKnightStalemate () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackKnights(B8).build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testWhiteKingVsBlackKingAndBishopStalemate () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).addBlackKnights(C8).build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testBlackKingVsWhiteKingAndKnightStalemate () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteKnights(B1).setBlackKing(E8).build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testBlackKingVsWhiteKingAndBishopStalemate () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteBishops(C1).setBlackKing(E8).build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testOnlyKingsAndBishopsStalemateBecauseBishopsAreAllOnSameColorSquares () {
        Board board = Board.builder()
                .setWhiteKing(E1)
                .addWhiteBishops(C1, E3)
                .setBlackKing(E8)
                .addBlackBishops(D4, F6)
                .build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testOnlyKingsAndBishopsNotStalemateBecauseBishopsAreOnDifferentColorSquares () {
        Board board = Board.builder().setWhiteKing(E1).addWhiteBishops(C1).setBlackKing(E8).addBlackBishops(C8).build();

        assertFalse(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testOnlyKingsStalemate () {
        Board board = Board.builder().setWhiteKing(E1).setBlackKing(E8).build();

        assertTrue(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }

    @Test
    public void testNonStalemate () {
        Board board = Board.createStartingBoard();

        assertFalse(StalemateUtils.isStalemateByInsufficientMaterial(board));
    }
}
