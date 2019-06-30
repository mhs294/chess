package com.maximilian.chess.util;

import com.maximilian.chess.enums.Square;
import com.maximilian.chess.objects.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.maximilian.chess.enums.Square.E5;
import static org.junit.Assert.assertEquals;

/**
 * Unit test class for {@link BitboardUtils}.
 *
 * @author Maximilian Schroeder
 */
@RunWith(JUnit4.class)
public class BitboardUtilsTest {
    @Test
    public void testCountHighBitsInBitmask () {
        assertEquals(0, BitboardUtils.countHighBitsInBitmask(Board.EMPTY_BITMASK));
        assertEquals(32, BitboardUtils.countHighBitsInBitmask(Board.LIGHT_SQUARES_BITMASK));
        assertEquals(32, BitboardUtils.countHighBitsInBitmask(Board.DARK_SQUARES_BITMASK));
        assertEquals(64, BitboardUtils.countHighBitsInBitmask(Board.FULL_BITMASK));
    }

    @Test
    public void testGetSquaresFromBitmask () {
        assertEquals(EnumSet.noneOf(Square.class), BitboardUtils.getSquaresFromBitmask(Board.EMPTY_BITMASK));
        assertEquals(EnumSet.allOf(Square.class), BitboardUtils.getSquaresFromBitmask(Board.FULL_BITMASK));
        Set<Square> lightSquares = Arrays.stream(Square.values())
                .filter(square -> square.type() == Square.Type.LIGHT)
                .collect(Collectors.toSet());
        assertEquals(lightSquares, BitboardUtils.getSquaresFromBitmask(Board.LIGHT_SQUARES_BITMASK));
        Set<Square> darkSquares = Arrays.stream(Square.values())
                .filter(square -> square.type() == Square.Type.DARK)
                .collect(Collectors.toSet());
        assertEquals(darkSquares, BitboardUtils.getSquaresFromBitmask(Board.DARK_SQUARES_BITMASK));
    }

    @Test
    public void testBitboardToBoardString () {
        System.out.println("Empty:\n" + BitboardUtils.bitboardToBoardString(Board.EMPTY_BITMASK) + "\n");
        System.out.println("Dark Squares:\n" + BitboardUtils.bitboardToBoardString(Board.DARK_SQUARES_BITMASK) + "\n");
        System.out.println(
                "Light Squares:\n" + BitboardUtils.bitboardToBoardString(Board.LIGHT_SQUARES_BITMASK) + "\n");
        System.out.println("Full:\n" + BitboardUtils.bitboardToBoardString(Board.FULL_BITMASK));
        System.out.println("E5:\n" + BitboardUtils.bitboardToBoardString(E5.bitmask()));
    }
}
