package com.nobullet.graph;

import static com.nobullet.MoreAssertions.assertStringsSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 * Tests for {@link Map}.
 */
public class MapTest {

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
    public void testToString() {
        Map map = new Map(12, 10)
                .set(3, 3, 1)
                .set(3, 4, 2)
                .set(3, 5, 3)
                .set(3, 6, 4)
                .set(4, 3, 5)
                .set(4, 4, 6)
                .set(4, 5, 7)
                .set(4, 6, 8)
                .set(5, 5, 9)
                .set(6, 6, 10)
                .set(7, 7, -11);
        String mapAsString = map.toString();
        assertStringsSame(BIG_MATRIX, mapAsString);
        assertEquals(BIG_MATRIX, mapAsString);
    }

    @Test
    public void testMapFromString() {
        String mapAsString = BIG_MATRIX;
        Map map = Map.fromString(mapAsString);
        assertEquals(mapAsString, map.toString());

        mapAsString
                = "-----\n"
                + "|123|\n"
                + "|456|\n"
                + "|7#9|\n"
                + "-----\n";
        map = Map.fromString("123", "456", "7#9");
        assertEquals(mapAsString, map.toString());

        mapAsString
                = "-------\n"
                + "|1 2 3|\n"
                + "|4 5 6|\n"
                + "|7 # 9|\n"
                + "-------\n";
        map = Map.fromString(
                "10203",
                "405 6",
                "7 # 9"
        );
        assertEquals(mapAsString, map.toString());
        assertEquals(map, Map.fromString(map.toString()));

        mapAsString
                = "-------\n"
                + "|# 2 3|\n"
                + "|4 # 6|\n"
                + "|7 8 #|\n"
                + "-------\n";
        map = Map.fromString("11| |2|0|3", "4|0|15| |6", "7| |8|0|90");
        assertEquals(mapAsString, map.toString());
        assertNotEquals("# may hide different numbers.", map, Map.fromString(map.toString()));
    }
}
