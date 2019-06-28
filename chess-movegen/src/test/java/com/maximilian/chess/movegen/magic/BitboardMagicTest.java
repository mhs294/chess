package com.maximilian.chess.movegen.magic;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.maximilian.chess.enums.Square.A1;
import static com.maximilian.chess.enums.Square.A2;
import static com.maximilian.chess.enums.Square.A3;
import static com.maximilian.chess.enums.Square.A4;
import static com.maximilian.chess.enums.Square.A5;
import static com.maximilian.chess.enums.Square.A6;
import static com.maximilian.chess.enums.Square.A7;
import static com.maximilian.chess.enums.Square.B1;
import static com.maximilian.chess.enums.Square.B2;
import static com.maximilian.chess.enums.Square.B3;
import static com.maximilian.chess.enums.Square.B4;
import static com.maximilian.chess.enums.Square.B5;
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
import static com.maximilian.chess.movegen.magic.BitboardMagic.BISHOP_BLOCKER_MASKS;
import static com.maximilian.chess.movegen.magic.BitboardMagic.ROOK_BLOCKER_MASKS;
import static com.maximilian.chess.util.BitboardUtils.getSquaresFromBitmask;
import static org.junit.Assert.assertEquals;

/**
 * Unit test class for {@link BitboardMagic}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class BitboardMagicTest {
    @Test
    public void blockerMaskForBishop () {
        assertEquals(Sets.newHashSet(B2, C3, D4, E5, F6, G7), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(A1)));
        assertEquals(Sets.newHashSet(B7, C6, D5, E4, F3, G2), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(H1)));
        assertEquals(Sets.newHashSet(B3, B5, C2, C6, D7), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(A4)));
        assertEquals(Sets.newHashSet(E7, F2, F6, G3, G5), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(H4)));
        assertEquals(Sets.newHashSet(B2, D2, E3, F4, G5), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(C1)));
        assertEquals(Sets.newHashSet(B7, D7, E6, F5, G4), getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(C8)));
        assertEquals(Sets.newHashSet(B3, B7, C4, C6, E4, E6, F3, F7, G2),
                getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(D5)));
        assertEquals(Sets.newHashSet(B2, C3, C7, D4, D6, F4, F6, G3, G7),
                getSquaresFromBitmask(BISHOP_BLOCKER_MASKS.getLong(E5)));
    }

    @Test
    public void blockerMaskForRook () {
        assertEquals(Sets.newHashSet(A2, A3, A4, A5, A6, A7, B1, C1, D1, E1, F1, G1),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(A1)));
        assertEquals(Sets.newHashSet(B8, C8, D8, E8, F8, G8, H2, H3, H4, H5, H6, H7),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(H8)));
        assertEquals(Sets.newHashSet(B1, C1, D2, D3, D4, D5, D6, D7, E1, F1, G1),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(D1)));
        assertEquals(Sets.newHashSet(B8, C8, D8, E2, E3, E4, E5, E6, E7, F8, G8),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(E8)));
        assertEquals(Sets.newHashSet(A2, A3, A5, A6, A7, B4, C4, D4, E4, F4, G4),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(A4)));
        assertEquals(Sets.newHashSet(B5, C5, D5, E5, F5, G5, H2, H3, H4, H6, H7),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(H5)));
        assertEquals(Sets.newHashSet(B4, C4, D2, D3, D5, D6, D7, E4, F4, G4),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(D4)));
        assertEquals(Sets.newHashSet(B5, C5, D5, E2, E3, E4, E6, E7, F5, G5),
                getSquaresFromBitmask(ROOK_BLOCKER_MASKS.getLong(E5)));
    }
}
