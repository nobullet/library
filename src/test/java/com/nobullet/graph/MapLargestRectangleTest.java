package com.nobullet.graph;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for largest rectangle problem.
 */
public class MapLargestRectangleTest {

    private static final String BIG_MATRIX
            = "--------------\n"
            + "|            |\n"
            + "|            |\n"
            + "|            |\n"
            + "|   15       |\n"
            + "|   26       |\n"
            + "|   379      |\n"
            + "|   48 #     |\n"
            + "|       #    |\n"
            + "|            |\n"
            + "|            |\n"
            + "--------------\n";


    @Test
    public void testLargestRectangle() {
        Map.Rectangle expected = Map.Rectangle.of(3, 3, 2, 4);
        Map.Rectangle rectangle = Map.fromString(BIG_MATRIX).
                findLargestRectangle();
        assertEquals(expected, rectangle);

        expected = Map.Rectangle.of(7, 8, 3, 2);
        rectangle = Map.fromString(
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000999",
                "0000000111"
        ).findLargestRectangle();
        assertEquals(expected, rectangle);

        expected = Map.Rectangle.of(2, 2, 3, 3);
        rectangle = Map.fromString(
                "0000000000",
                "0000000000",
                "0013200000",
                "0065400000",
                "0089100000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000999",
                "0000000111"
        ).findLargestRectangle();
        assertEquals(expected, rectangle);

        expected = Map.Rectangle.of(2, 2, 3, 3);
        rectangle = Map.fromString(
                "0000000000",
                "0000000000",
                "0013200000",
                "0065400000",
                "0089100000",
                "0000000000",
                "0000000000",
                "0000000000",
                "0000000999",
                "0000000111"
        ).findLargestRectangle();
        assertEquals(expected, rectangle);

        expected = Map.Rectangle.of(3, 6, 4, 3);
        rectangle = Map.fromString(
                "  1       ",
                "  22      ",
                "  132     ",
                "  6542    ",
                "  89111   ",
                "     226  ",
                "   444467 ",
                "   5555   ",
                "   6666999",
                "        111"
        ).findLargestRectangle();
        assertEquals(expected, rectangle);

        expected = Map.Rectangle.of(3, 6, 6, 4);
        rectangle = Map.fromString(
                "431       ",
                "  22      ",
                "  132     ",
                "  6542    ",
                "  89111   ",
                "     226  ",
                "   444467 ",
                "   555511 ",
                "1111111111",
                "1111111111"
        ).findLargestRectangle();
        assertEquals(expected, rectangle);
    }


    @Test
    public void testToStringAndAssistantMatrix() {
        Map map = Map.fromString(BIG_MATRIX);

        // Rectangle of 0's.
        long[][] assistant = map.buildAssistantMatrix(true);
        assertEquals(6, assistant[5][6]);
        assertEquals(6, assistant[5][7]);
        assertEquals(6, assistant[5][8]);
        assertEquals(6, assistant[5][9]);
        assertEquals(4, assistant[3][2]);
        assertEquals(0, assistant[3][3]);
        assertEquals(0, assistant[3][4]);
        assertEquals(0, assistant[4][3]);
        assertEquals(0, assistant[4][4]);


        assistant = map.buildAssistantMatrix(Map.Direction.RIGHT2LEFT, false);
        assertEquals(0, assistant[5][2]);
        assertEquals(3, assistant[5][3]);
        assertEquals(2, assistant[5][4]);
        assertEquals(1, assistant[5][5]);
        assertEquals(0, assistant[5][6]);

        assistant = map.buildAssistantMatrix(Map.Direction.LEFT2RIGHT, false);
        assertEquals(0, assistant[5][6]);
        assertEquals(3, assistant[5][5]);
        assertEquals(2, assistant[5][4]);
        assertEquals(1, assistant[5][3]);
        assertEquals(0, assistant[5][2]);

        assistant = map.buildAssistantMatrix(Map.Direction.TOP2BOTTOM, false);
        assertEquals(3, assistant[5][3]);
        assertEquals(4, assistant[6][3]);
        assertEquals(0, assistant[7][3]);

        assistant = map.buildAssistantMatrix(Map.Direction.BOTTOM2TOP, false);
        assertEquals(4, assistant[3][3]);
        assertEquals(3, assistant[4][3]);
        assertEquals(2, assistant[5][3]);
        assertEquals(1, assistant[6][3]);
        assertEquals(0, assistant[7][3]);
    }
}
