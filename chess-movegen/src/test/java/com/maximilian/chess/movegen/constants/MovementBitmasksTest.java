package com.maximilian.chess.movegen.constants;

import com.maximilian.chess.enums.Square;
import com.maximilian.chess.util.BitboardUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.EnumSet;
import java.util.Set;

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
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BISHOP_BLOCKER_MASKS;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BISHOP_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BLACK_PAWN_ADVANCES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.BLACK_PAWN_CAPTURES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.KING_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.KNIGHT_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.ROOK_BLOCKER_MASKS;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.ROOK_MOVES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.WHITE_PAWN_ADVANCES;
import static com.maximilian.chess.movegen.constants.MovementBitmasks.WHITE_PAWN_CAPTURES;
import static com.maximilian.chess.objects.Board.EMPTY_BITMASK;
import static com.maximilian.chess.util.BitboardUtils.getSquaresFromBitmask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for {@link MovementBitmasks}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class MovementBitmasksTest {
    @Test
    public void movesForWhitePawnOnRankTwo () {
        assertEquals(EnumSet.of(E3, E4), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(E2)));
    }

    @Test
    public void movesForWhitePawnPastRankTwo () {
        assertEquals(EnumSet.of(A4), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(A3)));
        assertEquals(EnumSet.of(B5), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(B4)));
        assertEquals(EnumSet.of(C6), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(C5)));
        assertEquals(EnumSet.of(D7), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(D6)));
        assertEquals(EnumSet.of(F8), getSquaresFromBitmask(WHITE_PAWN_ADVANCES.getLong(F7)));
    }

    @Test
    public void capturesForWhitePawn () {
        assertEquals(EnumSet.of(B3), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(A2)));
        assertEquals(EnumSet.of(A4, C4), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(B3)));
        assertEquals(EnumSet.of(B5, D5), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(C4)));
        assertEquals(EnumSet.of(C6, E6), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(D5)));
        assertEquals(EnumSet.of(D7, F7), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(E6)));
        assertEquals(EnumSet.of(G8), getSquaresFromBitmask(WHITE_PAWN_CAPTURES.getLong(H7)));
    }

    @Test
    public void movesForBlackPawnOnRankSeven () {
        assertEquals(EnumSet.of(E6, E5), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(E7)));
    }

    @Test
    public void movesForBlackPawnPastRankSeven () {
        assertEquals(EnumSet.of(A5), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(A6)));
        assertEquals(EnumSet.of(B4), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(B5)));
        assertEquals(EnumSet.of(C3), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(C4)));
        assertEquals(EnumSet.of(D2), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(D3)));
        assertEquals(EnumSet.of(F1), getSquaresFromBitmask(BLACK_PAWN_ADVANCES.getLong(F2)));
    }

    @Test
    public void capturesForBlackPawn () {
        assertEquals(EnumSet.of(B6), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(A7)));
        assertEquals(EnumSet.of(A5, C5), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(B6)));
        assertEquals(EnumSet.of(B4, D4), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(C5)));
        assertEquals(EnumSet.of(C3, E3), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(D4)));
        assertEquals(EnumSet.of(D2, F2), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(E3)));
        assertEquals(EnumSet.of(G1), getSquaresFromBitmask(BLACK_PAWN_CAPTURES.getLong(H2)));
    }

    @Test
    public void movesForKnight () {
        assertEquals(EnumSet.of(B3, C2), getSquaresFromBitmask(KNIGHT_MOVES.getLong(A1)));
        assertEquals(EnumSet.of(G6, F7), getSquaresFromBitmask(KNIGHT_MOVES.getLong(H8)));
        assertEquals(EnumSet.of(A3, C3, D2), getSquaresFromBitmask(KNIGHT_MOVES.getLong(B1)));
        assertEquals(EnumSet.of(E7, F6, H6), getSquaresFromBitmask(KNIGHT_MOVES.getLong(G8)));
        assertEquals(EnumSet.of(B2, B6, C3, C5), getSquaresFromBitmask(KNIGHT_MOVES.getLong(A4)));
        assertEquals(EnumSet.of(G3, G7, F4, F6), getSquaresFromBitmask(KNIGHT_MOVES.getLong(H5)));
        assertEquals(EnumSet.of(B2, C3, E3, F2), getSquaresFromBitmask(KNIGHT_MOVES.getLong(D1)));
        assertEquals(EnumSet.of(C7, D6, F6, G7), getSquaresFromBitmask(KNIGHT_MOVES.getLong(E8)));
        assertEquals(EnumSet.of(A1, A3, B4, D4, E1, E3), getSquaresFromBitmask(KNIGHT_MOVES.getLong(C2)));
        assertEquals(EnumSet.of(D6, D8, E5, G5, H6, H8), getSquaresFromBitmask(KNIGHT_MOVES.getLong(F7)));
        assertEquals(EnumSet.of(B3, B5, C2, C6, E2, E6, F3, F5), getSquaresFromBitmask(KNIGHT_MOVES.getLong(D4)));
        assertEquals(EnumSet.of(C4, C6, D3, D7, F3, F7, G4, G6), getSquaresFromBitmask(KNIGHT_MOVES.getLong(E5)));
    }

    @Test
    public void movesForKing () {
        assertEquals(EnumSet.of(A2, B1, B2), getSquaresFromBitmask(KING_MOVES.getLong(A1)));
        assertEquals(EnumSet.of(A7, B7, B8), getSquaresFromBitmask(KING_MOVES.getLong(A8)));
        assertEquals(EnumSet.of(G1, G2, H2), getSquaresFromBitmask(KING_MOVES.getLong(H1)));
        assertEquals(EnumSet.of(G7, G8, H7), getSquaresFromBitmask(KING_MOVES.getLong(H8)));
        assertEquals(EnumSet.of(C1, C2, D2, E1, E2), getSquaresFromBitmask(KING_MOVES.getLong(D1)));
        assertEquals(EnumSet.of(D7, D8, E7, F7, F8), getSquaresFromBitmask(KING_MOVES.getLong(E8)));
        assertEquals(EnumSet.of(A3, A5, B3, B4, B5), getSquaresFromBitmask(KING_MOVES.getLong(A4)));
        assertEquals(EnumSet.of(G4, G5, G6, H4, H6), getSquaresFromBitmask(KING_MOVES.getLong(H5)));
        assertEquals(EnumSet.of(C3, C4, C5, D3, D5, E3, E4, E5), getSquaresFromBitmask(KING_MOVES.getLong(D4)));
        assertEquals(EnumSet.of(D4, D5, D6, E4, E6, F4, F5, F6), getSquaresFromBitmask(KING_MOVES.getLong(E5)));
    }

    @Test
    public void blockersForBishop () {
        assertEquals(EnumSet.of(B2, C3, D4, E5, F6, G7), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(A1)));
        assertEquals(EnumSet.of(B7, C6, D5, E4, F3, G2), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(H1)));
        assertEquals(EnumSet.of(B3, B5, C2, C6, D7), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(A4)));
        assertEquals(EnumSet.of(E7, F2, F6, G3, G5), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(H4)));
        assertEquals(EnumSet.of(B2, D2, E3, F4, G5), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(C1)));
        assertEquals(EnumSet.of(B7, D7, E6, F5, G4), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(C8)));
        assertEquals(EnumSet.of(B3, B7, C4, C6, E4, E6, F3, F7, G2),
                getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(D5)));
        assertEquals(EnumSet.of(B2, C3, C7, D4, D6, F4, F6, G3, G7),
                getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(E5)));
    }

    @Test
    public void blockersForRook () {
        assertEquals(EnumSet.of(A2, A3, A4, A5, A6, A7, B1, C1, D1, E1, F1, G1),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(A1)));
        assertEquals(EnumSet.of(B8, C8, D8, E8, F8, G8, H2, H3, H4, H5, H6, H7),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(H8)));
        assertEquals(EnumSet.of(B1, C1, D2, D3, D4, D5, D6, D7, E1, F1, G1),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(D1)));
        assertEquals(EnumSet.of(B8, C8, D8, E2, E3, E4, E5, E6, E7, F8, G8),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(E8)));
        assertEquals(EnumSet.of(A2, A3, A5, A6, A7, B4, C4, D4, E4, F4, G4),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(A4)));
        assertEquals(EnumSet.of(B5, C5, D5, E5, F5, G5, H2, H3, H4, H6, H7),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(H5)));
        assertEquals(EnumSet.of(B4, C4, D2, D3, D5, D6, D7, E4, F4, G4),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(D4)));
        assertEquals(EnumSet.of(B5, C5, D5, E2, E3, E4, E6, E7, F5, G5),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(E5)));
    }

    @Test
    public void blockerPermutations () {
        LongSet bishopBlockerBitmaskPermutations = BISHOP_MOVES.get(A1)
                .keySet();
        assertEquals(64, bishopBlockerBitmaskPermutations.size());
        Set<Square> expectedBishopSquares = EnumSet.of(B2, C3, D4, E5, F6, G7);
        for (long blockerBitmask : bishopBlockerBitmaskPermutations) {
            Set<Square> blockerSquares = BitboardUtils.getSquaresFromBitmask(blockerBitmask);
            assertTrue(expectedBishopSquares.containsAll(blockerSquares));
        }

        LongSet rookBlockerBitmaskPermutations = ROOK_MOVES.get(A1)
                .keySet();
        assertEquals(4096, rookBlockerBitmaskPermutations.size());
        Set<Square> expectedSquares = EnumSet.of(A2, A3, A4, A5, A6, A7, B1, C1, D1, E1, F1, G1);
        for (long blockerBitmask : rookBlockerBitmaskPermutations) {
            Set<Square> blockerSquares = BitboardUtils.getSquaresFromBitmask(blockerBitmask);
            assertTrue(expectedSquares.containsAll(blockerSquares));
        }
    }

    @Test
    public void movesForBishopNoBlockers () {
        assertEquals(EnumSet.of(B2, C3, D4, E5, F6, G7, H8), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A1)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(B7, C6, D5, E4, F3, G2, H1), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A8)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(A1, B2, C3, D4, E5, F6, G7), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H8)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(A8, B7, C6, D5, E4, F3, G2), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H1)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(B5, C6, D7, E8, B3, C2, D1), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A4)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(C2, B3, A4, E2, F3, G4, H5), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(D1)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(D7, C6, B5, A4, F7, G6, H5), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(E8)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(G6, F7, E8, G4, F3, E2, D1), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H5)
                .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(D5, C6, B7, A8, F5, G6, H7, D3, C2, B1, F3, G2, H1),
                BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(E4)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(C5, B6, A7, E5, F6, G7, H8, C3, B2, A1, E3, F2, G1),
                BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(D4)
                        .get(EMPTY_BITMASK)));
    }

    @Test
    public void movesForBishopWithBlockers () {
        long blockerBitmask = BitboardUtils.getBitmaskFromSquares(
                EnumSet.of(C6, D6, E6, F6, F5, F4, F3, E3, D3, C3, C4, C5));
        assertEquals(EnumSet.of(B2, C3), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A1)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(A1))));
        assertEquals(EnumSet.of(B7, C6), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A8)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(A8))));
        assertEquals(EnumSet.of(F6, G7), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H8)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(H8))));
        assertEquals(EnumSet.of(F3, G2), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H1)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(H1))));
        assertEquals(EnumSet.of(B5, C6, B3, C2, D1), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(A4)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(A4))));
        assertEquals(EnumSet.of(C2, B3, A4, E2, F3), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(D1)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(D1))));
        assertEquals(EnumSet.of(D7, C6, F7, G6, H5), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(E8)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(E8))));
        assertEquals(EnumSet.of(G6, F7, E8, G4, F3), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(H5)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(H5))));
        assertEquals(EnumSet.of(D5, C6, F5, D3, F3), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(E4)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(E4))));
        assertEquals(EnumSet.of(C5, E5, F6, C3, E3), BitboardUtils.getSquaresFromBitmask(BISHOP_MOVES.get(D4)
                .get(blockerBitmask & BISHOP_BLOCKER_MASKS.getLong(D4))));
    }

    @Test
    public void movesForRookNoBlockers () {
        assertEquals(EnumSet.of(A2, A3, A4, A5, A6, A7, A8, B1, C1, D1, E1, F1, G1, H1),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(A1)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(A7, A6, A5, A4, A3, A2, A1, B8, C8, D8, E8, F8, G8, H8),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(A8)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(H7, H6, H5, H4, H3, H2, H1, G8, F8, E8, D8, C8, B8, A8),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H8)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(H2, H3, H4, H5, H6, H7, H8, G1, F1, E1, D1, C1, B1, A1),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H1)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(A3, A2, A1, A5, A6, A7, A8, B4, C4, D4, E4, F4, G4, H4),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(A4)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(C1, B1, A1, E1, F1, G1, H1, D2, D3, D4, D5, D6, D7, D8),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(D1)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(D8, C8, B8, A8, F8, G8, H8, E7, E6, E5, E4, E3, E2, E1),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(E8)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(H4, H3, H2, H1, H6, H7, H8, G5, F5, E5, D5, C5, B5, A5),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H5)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(D4, C4, B4, A4, F4, G4, H4, E3, E2, E1, E5, E6, E7, E8),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(E4)
                        .get(EMPTY_BITMASK)));
        assertEquals(EnumSet.of(D3, D2, D1, D5, D6, D7, D8, C4, B4, A4, E4, F4, G4, H4),
                BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(D4)
                        .get(EMPTY_BITMASK)));
    }

    @Test
    public void movesForRookWithBlockers () {
        long blockerBitmask = BitboardUtils.getBitmaskFromSquares(
                EnumSet.of(A5, B5, C5, D5, E5, F5, G5, H5, F1, F2, F3, F4, F6, F7, F8));
        assertEquals(EnumSet.of(A2, A3, A4, A5, B1, C1, D1, E1, F1), BitboardUtils.getSquaresFromBitmask(
                ROOK_MOVES.get(A1)
                        .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(A1))));
        assertEquals(EnumSet.of(A7, A6, A5, B8, C8, D8, E8, F8), BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(A8)
                .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(A8))));
        assertEquals(EnumSet.of(H7, H6, H5, G8, F8), BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H8)
                .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(H8))));
        assertEquals(EnumSet.of(H2, H3, H4, H5, G1, F1), BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H1)
                .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(H1))));
        assertEquals(EnumSet.of(A5, A3, A2, A1, B4, C4, D4, E4, F4), BitboardUtils.getSquaresFromBitmask(
                ROOK_MOVES.get(A4)
                        .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(A4))));
        assertEquals(EnumSet.of(D2, D3, D4, D5, E1, F1, C1, B1, A1), BitboardUtils.getSquaresFromBitmask(
                ROOK_MOVES.get(D1)
                        .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(D1))));
        assertEquals(EnumSet.of(E7, E6, E5, D8, C8, B8, A8, F8), BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(E8)
                .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(E8))));
        assertEquals(EnumSet.of(H5, H3, H2, H1, G4, F4), BitboardUtils.getSquaresFromBitmask(ROOK_MOVES.get(H4)
                .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(H4))));
        assertEquals(EnumSet.of(E3, E2, E1, E5, D4, C4, B4, A4, F4), BitboardUtils.getSquaresFromBitmask(
                ROOK_MOVES.get(E4)
                        .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(E4))));
        assertEquals(EnumSet.of(D3, D2, D1, D5, C4, B4, A4, E4, F4), BitboardUtils.getSquaresFromBitmask(
                ROOK_MOVES.get(D4)
                        .get(blockerBitmask & ROOK_BLOCKER_MASKS.getLong(D4))));
    }
}
