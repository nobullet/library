package com.nobullet.graph;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for Greedy algorithms (BFS, Dijkstra).
 */
public class MapPathsTest {

    @Test
    public void testBFSNodes() {
        Map map = Map.fromString(
                " # ",
                " # ",
                "   ");
        Map.Path path = map.shortestPathBFS(new Map.Cell(0, 0), new Map.Cell(2, 0));
        assertEquals(Map.fromString(
                "1#7",
                "2#6",
                "345"), path.asMap());
    }

    @Test
    public void testBFSNodesBig() {
        Map map = Map.fromString(
                " 111111  1",
                " 1       1",
                "  132  211",
                "  6  2   1",
                "  89 11  1",
                "       6 1",
                " 23  44  1",
                "   5 55  1",
                "11       1",
                "1111   111");
        Map.Path path = map.shortestPathBFS(new Map.Cell(0, 0), new Map.Cell(8, 0));
        assertEquals(Map.fromString(""
                + "------------\n"
                + "|1###### 2#|\n"
                + "|2#    891#|\n"
                + "|34### 7###|\n"
                + "| 5#  #65 #|\n"
                + "| 6## ##43#|\n"
                + "| 7891  #2#|\n"
                + "| ## 2## 1#|\n"
                + "|   #3## 9#|\n"
                + "|##  45678#|\n"
                + "|####   ###|\n"
                + "------------"
        ), path.asMap());
    }

    @Test
    public void testDijkstraNodesBigShortPath() {
        Map map = Map.fromString(
                " 111111  1",
                " 1       1",
                "  132  211",
                "  6  2   1",
                "  89 11  1",
                "       6 1",
                " 23  44  1",
                "   5 55  1",
                "11       1",
                "1111   111");
        Map.Path path = map.shortestPathDijkstra(new Map.Cell(0, 0), new Map.Cell(8, 0));
        assertEquals(Map.fromString(""
                + "------------\n"
                + "|123456789#|\n"
                + "| #       #|\n"
                + "|  ###  ###|\n"
                + "|  #  #   #|\n"
                + "|  ## ##  #|\n"
                + "|       # #|\n"
                + "| ##  ##  #|\n"
                + "|   # ##  #|\n"
                + "|##       #|\n"
                + "|####   ###|\n"
                + "------------"
        ), path.asMap());
    }

    @Test
    public void testDijkstraNodesBigLongerPath() {
        Map map = Map.fromString(
                " 1234321 1",
                " 1       1",
                "  132  211",
                "  6  2   1",
                "  89 11  1",
                "       6 1",
                " 23  44  1",
                "   5 55  1",
                "11       1",
                "1111   111");
        Map.Path path = map.shortestPathDijkstra(new Map.Cell(0, 0), new Map.Cell(8, 0));
        assertEquals(Map.fromString(""
                + "------------\n"
                + "|1#######2#|\n"
                + "|234567891#|\n"
                + "|  ###  ###|\n"
                + "|  #  #   #|\n"
                + "|  ## ##  #|\n"
                + "|       # #|\n"
                + "| ##  ##  #|\n"
                + "|   # ##  #|\n"
                + "|##       #|\n"
                + "|####   ###|\n"
                + "------------"
        ), path.asMap());
    }

    @Test
    public void testDijkstraNodesBigLongestPath() {
        Map map = Map.fromString(
                " 9191919 1",
                " 919     1",
                "  999  999",
                "  9  9   1",
                "  99 99  1",
                "       9 1",
                " 99  99  1",
                "   5 55  1",
                "11       1",
                "1111   111");
        Map.Path path = map.shortestPathDijkstra(new Map.Cell(0, 0), new Map.Cell(8, 0));
        assertEquals(Map.fromString(""
                + "------------\n"
                + "|1#######2#|\n"
                + "|2###  891#|\n"
                + "|34### 7###|\n"
                + "| 5#  #654#|\n"
                + "| 6## ## 3#|\n"
                + "| 7891  #2#|\n"
                + "| ## 2##91#|\n"
                + "|   #3##8 #|\n"
                + "|##  4567 #|\n"
                + "|####   ###|\n"
                + "------------"
        ), path.asMap());
    }

    @Test
    public void testAStarLongestPath() {
        Map map = Map.fromString(
                " 9191919 1",
                " 919     1",
                "  999  999",
                "  9  9   1",
                "  99 99  1",
                "       9 1",
                " 99  99  1",
                "   5 55  1",
                "11       1",
                "1111   111");
        Map.Path pathAStar = map.shortestPathAStar(new Map.Cell(0, 0), new Map.Cell(8, 0));
        Map expected = Map.fromString(""
                + "------------\n"
                + "|1#######2#|\n"
                + "|2###  891#|\n"
                + "|3 ### 7###|\n"
                + "|4 #  #65 #|\n"
                + "|5 ## ##43#|\n"
                + "|6      #2#|\n"
                + "|7##  ##91#|\n"
                + "|891# ##8 #|\n"
                + "|##234567 #|\n"
                + "|####   ###|\n"
                + "------------"
        );
        assertEquals(expected, pathAStar.asMap());
    }

    @Test
    public void testAStarHighWalls() {
        Map map = withVeryHighWalls(Map.fromString(
                " 1111111 1",
                " 111     1",
                "  111  111",
                "  1  1   1",
                "  11 11  1",
                "       1 1",
                " 11  11  1",
                "   1 11  1",
                "11 1 1   1",
                "1  1   1  "));
        Map.Path pathAStar = map.shortestPathAStar(new Map.Cell(0, 0), new Map.Cell(8, 0));
        assertEquals(30, pathAStar.getCost());
        Map expected = Map.fromString(""
                + "------------\n"
                + "|1#######4#|\n"
                + "|2###  123#|\n"
                + "|3 ### 9###|\n"
                + "|4 #  #87 #|\n"
                + "|5 ## ##65#|\n"
                + "|6789   #4#|\n"
                + "| ##12##23#|\n"
                + "|   #3##1 #|\n"
                + "|## #4#89 #|\n"
                + "|#  #567#  |\n"
                + "------------"
        );
        assertEquals(expected, pathAStar.asMap());
    }

    @Test
    public void testNumberOfPlains() {
        Map map = Map.fromString(""
                + " 1 \n"
                + "1 1\n"
                + " 1 \n");
        assertEquals(4, map.getNumberOfPlains());

        map = Map.fromString(""
                + " 11 11\n"
                + "  111 \n"
                + "1 1 2 \n"
                + "111 2 \n");
        assertEquals(2, map.getNumberOfPlains());
        
        map = Map.fromString(""
                + "     111   \n"
                + " 333 1 11 5\n"
                + " 3 3 1  1 5\n"
                + " 333 1111  \n");
        assertEquals(3, map.getNumberOfPlains());
        
        map = withVeryHighWalls(Map.fromString(
                " 1111111 1",
                " 111     1",
                "  111  111",
                "  1  1   1",
                "  11 11  1",
                "       1 1",
                " 11  11  1",
                "   1 11  1",
                "11 1 1   1",
                "1  1   1  "));
        assertEquals(9, map.getNumberOfPlains());
    }

    private static Map withVeryHighWalls(Map map) {
        for (int w = 0; w < map.getWidth(); w++) {
            for (int h = 0; h < map.getHeight(); h++) {
                if (map.get(w, h) > Map.Constants.EMPTY_CELL_VALUE) {
                    map.set(w, h, Map.Constants.UNREACHABLE_CELL);
                }
            }
        }
        return map;
    }
}
