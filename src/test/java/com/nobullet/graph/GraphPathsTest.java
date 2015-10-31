package com.nobullet.graph;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static com.nobullet.graph.GraphTest.listOfVertices;
import static com.nobullet.graph.GraphTest.newGraphFromBook;
import org.junit.Test;

/**
 * Tests for path finding algorithms.
 */
public class GraphPathsTest {

    static final Key k0 = Key.of("0");
    static final Key k1 = Key.of("1");
    static final Key k2 = Key.of("2");
    static final Key k3 = Key.of("3");
    static final Key k4 = Key.of("4");
    static final Key k5 = Key.of("5");
    static final Key k6 = Key.of("6");
    static final Key k7 = Key.of("7");
    static final Key k8 = Key.of("8");
    static final Key k9 = Key.of("9");

    static final Key v1 = Key.of("v1");
    static final Key v2 = Key.of("v2");
    static final Key v3 = Key.of("v3");
    static final Key v4 = Key.of("v4");
    static final Key v5 = Key.of("v5");
    static final Key v6 = Key.of("v6");
    static final Key v7 = Key.of("v7");

    @Test
    public void testDijkstra_small() {
        Graph graph = new Graph().
                addEdge(k0, k1).
                addEdge(k1, k2).
                addEdge(k2, k3).
                addEdge(k3, k4).
                addEdge(k4, k5).
                addEdge(k0, k5, 20.0D);

        assertListsEqual(listOfVertices(k0, k1, k2, k3, k4, k5),
                graph.shortestPathDijkstra(k0, k5).getPath());
    }

    @Test
    public void testDijkstra() {
        Graph graph = newGraphFromBook(false);
        assertListsEqual(listOfVertices(v1, v4, v7), graph.shortestPathDijkstra(v1, v7).getPath());

        assertListsEqual(listOfVertices(v1, v4, v5), graph.shortestPathDijkstra(v1, v5).getPath());

        assertListsEqual(listOfVertices(v3, v1, v4, v5), graph.shortestPathDijkstra(v3, v5).getPath());
    }

    @Test
    public void testAStar_defaultsToDijkstraWithNoPositionInfo() {
        Graph graph = newGraphFromBook(false);
        assertListsEqual(listOfVertices(v1, v4, v7), graph.shortestPathAStar(v1, v7).getPath());

        assertListsEqual(listOfVertices(v1, v4, v5), graph.shortestPathAStar(v1, v5).getPath());

        assertListsEqual(listOfVertices(v3, v1, v4, v5), graph.shortestPathAStar(v3, v5).getPath());
    }

    @Test
    public void testAStar() {
        Graph graph = newGraphFromBook(true);
        assertListsEqual(listOfVertices(v1, v4, v7), graph.shortestPathAStar(v1, v7).getPath());

        assertListsEqual(listOfVertices(v1, v4, v5), graph.shortestPathAStar(v1, v5).getPath());

        assertListsEqual(listOfVertices(v3, v1, v4, v5), graph.shortestPathAStar(v3, v5).getPath());
    }
}
