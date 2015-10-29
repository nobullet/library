package com.nobullet.graph;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * Basic tests.
 */
public class GraphTest {

    static final Logger logger = Logger.getLogger(GraphTest.class.getName());

    @Test
    public void testGraph() {
        Graph graph = newBasicGraph();
        verifyBasicGraph(graph);
        // Make a copy and delete something from the copy of original basic graph.
        Graph copy = new Graph(graph).
                removeVertex("1").
                removeVertex("2");
        // Verify original graph.
        verifyBasicGraph(graph);

        // Verify copy.
        assertTrue(copy.hasEdge("5", "6"));
        assertTrue(copy.hasEdge("5", "7"));
        assertTrue(copy.hasEdge("6", "7"));

        assertFalse(copy.hasEdge("1", "2"));
        assertFalse(copy.hasEdge("1", "7"));
        assertFalse(copy.hasEdge("2", "7"));
        assertFalse(copy.hasEdge("3", "7"));
        assertFalse(copy.hasEdge("4", "7"));
    }

    @Test
    public void testGraphWithCycle() {
        Graph graph = newBasicGraph();
        verifyBasicGraph(graph);

        // Add cycle.
        graph.addEdge("1", "2").
                addEdge("2", "1");
        // Make a copy and delete something from the copy of original basic graph.
        Graph copy = new Graph(graph).
                removeVertex("1");
        // Verify original graph.
        verifyBasicGraph(graph);

        // Verify copy.
        assertTrue(copy.hasVertex("2"));
        assertTrue(copy.hasEdge("5", "6"));
        assertTrue(copy.hasEdge("5", "7"));
        assertTrue(copy.hasEdge("6", "7"));

        assertFalse(copy.hasVertex("1"));
        assertFalse(copy.hasEdge("1", "2"));
        assertFalse(copy.hasEdge("2", "1"));
        assertFalse(copy.hasEdge("1", "7"));
        assertFalse(copy.hasEdge("2", "7"));
        assertFalse(copy.hasEdge("3", "7"));
        assertFalse(copy.hasEdge("4", "7"));
    }

    @Test
    public void testTopologicalSort() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        Graph graph = newBasicGraph();
        graph.topologicalSort((vertex, order) -> path.add(vertex.getKey() + ":" + order));
        Collections.sort(path);
        StringJoiner joiner = new StringJoiner(",");
        path.stream().forEach(s -> joiner.add(s));
        assertEquals("000:2,00:0,0:1,10:12,1:3,2:4,3:5,4:6,5:7,6:8,7:9,8:10,9:11", joiner.toString());
    }

    @Test(expected = Graph.CycleException.class)
    public void testTopologicalSortCycleDetection() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        new Graph().
                addEdge("0", "1").
                addEdge("1", "2").
                addEdge("2", "3").
                addEdge("3", "1").
                topologicalSort((vertex, order) -> path.add(vertex.getKey() + ":" + order));
    }

    @Test
    public void testDijkstra_small() {
        Graph graph = new Graph().
                addEdge("0", "1").
                addEdge("1", "2").
                addEdge("2", "3").
                addEdge("3", "4").
                addEdge("4", "5").
                addEdge("0", "5", 20.0D);

        assertListsEqual(listOfVertices("0", "1", "2", "3", "4", "5"),
                graph.shortestPathDijkstra(graph.getVertex("0"), graph.getVertex("5")));
    }

    @Test
    public void testDijkstra() {
        Graph graph = newGraphFromBook(false);
        assertListsEqual(listOfVertices("v1", "v4", "v7"),
                graph.shortestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v7")));

        assertListsEqual(listOfVertices("v1", "v4", "v5"),
                graph.shortestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v5")));

        assertListsEqual(listOfVertices("v3", "v1", "v4", "v5"),
                graph.shortestPathDijkstra(graph.getVertex("v3"), graph.getVertex("v5")));
    }

    @Test(expected = Graph.CycleException.class)
    public void testDijkstra_longestPath_hasCycle() throws Graph.CycleException {
        Graph graph = newGraphFromBook(false);
        graph.longestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v7"));
    }

    @Test
    public void testDijkstra_longestPath() throws Graph.CycleException {
        Graph graph = newGraphFromBook(false);
        graph.removeEdge("v4", "v3"); // break both cycles.
        
        assertListsEqual(listOfVertices("v1", "v2", "v5", "v7"),
                graph.longestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v7")));

        assertListsEqual(listOfVertices("v1", "v2", "v5"),
                graph.longestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v5")));

        assertListsEqual(listOfVertices("v3", "v1", "v2", "v5", "v7", "v6"),
                graph.longestPathDijkstra(graph.getVertex("v3"), graph.getVertex("v6")));
    }

    @Test
    public void testAStar_defaultsToDijkstra() {
        Graph graph = newGraphFromBook(false);
        assertListsEqual(listOfVertices("v1", "v4", "v7"),
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v7")));

        assertListsEqual(listOfVertices("v1", "v4", "v5"),
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v5")));

        assertListsEqual(listOfVertices("v3", "v1", "v4", "v5"),
                graph.shortestPathAStar(graph.getVertex("v3"), graph.getVertex("v5")));
    }

    @Test
    public void testAStar() {
        Graph graph = newGraphFromBook(true);
        assertListsEqual(listOfVertices("v1", "v4", "v7"),
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v7")));

        assertListsEqual(listOfVertices("v1", "v4", "v5"),
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v5")));

        assertListsEqual(listOfVertices("v3", "v1", "v4", "v5"),
                graph.shortestPathAStar(graph.getVertex("v3"), graph.getVertex("v5")));
    }

    static List<Vertex> listOfVertices(String... args) {
        List<Vertex> result = Lists.newArrayListWithExpectedSize(args.length);
        for (String arg : args) {
            result.add(new Vertex(arg));
        }
        return result;
    }

    static void verifyBasicGraph(Graph graph) {
        assertTrue(graph.hasEdge("1", "2"));
        assertTrue(graph.hasEdge("2", "3"));
        assertTrue(graph.hasEdge("2", "4"));
        assertTrue(graph.hasEdge("2", "5"));
        assertTrue(graph.hasEdge("5", "6"));
        assertTrue(graph.hasEdge("5", "7"));
        assertTrue(graph.hasEdge("6", "7"));

        assertFalse(graph.hasEdge("1", "7"));
        assertFalse(graph.hasEdge("2", "7"));
        assertFalse(graph.hasEdge("3", "7"));
        assertFalse(graph.hasEdge("4", "7"));
    }

    /**
     * From page 335 of "Data structures And Algorithm Analysis", by Mark Allen Weiss.<p>
     * Graph has cycles: v1->v4->v3, v1->v2->v4->v3.
     *
     * @param withPositions Whether to add dummy positions to the graph.
     * @return Graph example.
     */
    static Graph newGraphFromBook(boolean withPositions) {
        Graph graph = new Graph().
                addEdge("v1", "v2", 2.0D).
                addEdge("v1", "v4", 1.0D).
                addEdge("v2", "v4", 3.0D).
                addEdge("v2", "v5", 10.0D).
                addEdge("v3", "v1", 4.0D).
                addEdge("v3", "v6", 5.0D).
                addEdge("v4", "v3", 2.0D).
                addEdge("v4", "v5", 2.0D).
                addEdge("v4", "v6", 8.0D).
                addEdge("v4", "v7", 4.0D).
                addEdge("v5", "v7", 6.0D).
                addEdge("v7", "v6", 1.0D);
        if (withPositions) {
            graph.getVertex("v1").setPosition(Vertex.Position.new2D(5, 10));
            // Making v2 as a very far point so A-star chooses v4 before prefering v2.
            graph.getVertex("v2").setPosition(Vertex.Position.new2D(1500, 1000));
            graph.getVertex("v3").setPosition(Vertex.Position.new2D(0, 5));
            graph.getVertex("v4").setPosition(Vertex.Position.new2D(10, 10));
            graph.getVertex("v5").setPosition(Vertex.Position.new2D(20, 5));
            graph.getVertex("v6").setPosition(Vertex.Position.new2D(5, 0));
            graph.getVertex("v7").setPosition(Vertex.Position.new2D(15, 0));
        }
        return graph;
    }

    static Graph newBasicGraph() {
        return new Graph().
                addEdge("1", "2").
                addEdge("2", "3").
                addEdge("2", "4").
                addEdge("2", "5").
                addEdge("5", "6").
                addEdge("6", "7").
                addEdge("5", "7").
                addEdge("7", "8").
                addEdge("8", "9").
                addEdge("9", "10").
                addEdge("0", "10").
                addEdge("00", "10").
                addEdge("000", "10");
    }
}
