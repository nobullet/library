package com.nobullet.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
    static final Key k000 = Key.of("000");
    static final Key k00 = Key.of("00");
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
    static final Key k10 = Key.of("10");
    static final Key v1 = Key.of("v1");
    static final Key v2 = Key.of("v2");
    static final Key v3 = Key.of("v3");
    static final Key v4 = Key.of("v4");
    static final Key v5 = Key.of("v5");
    static final Key v6 = Key.of("v6");
    static final Key v7 = Key.of("v7");

    @Test
    public void testEquals() {
        assertEquals(newBasicGraph(), newBasicGraph());
        assertNotEquals("Graphs must be different. Edge 7 -> 8 is lost.",
                newBasicGraph().removeVertex(k7).addVertex(k7), newBasicGraph());

        assertEquals(newGraphFromBook(false), newGraphFromBook(false));
        assertEquals(newGraphFromBook(true), newGraphFromBook(true));
        assertEquals(newGraphFromBook(true), newGraphFromBook(false));
    }

    @Test
    public void testHashCode() {
        assertEquals(newBasicGraph().hashCode(), newBasicGraph().hashCode());
        assertNotEquals("Graphs hash codes must be different. Edge 7 -> 8 is lost.",
                newBasicGraph().removeVertex(k7).addVertex(k7).hashCode(), newBasicGraph().hashCode());

        assertEquals(newGraphFromBook(false).hashCode(), newGraphFromBook(false).hashCode());
        assertEquals(newGraphFromBook(true).hashCode(), newGraphFromBook(true).hashCode());
        assertEquals(newGraphFromBook(true).hashCode(), newGraphFromBook(false).hashCode());
    }

    @Test
    public void testGraph() {
        Graph graph = newBasicGraph();
        verifyBasicGraph(graph);
        // Make a copy and delete something from the copy of original basic graph.
        Graph copy = new Graph(graph).
                removeVertex(k1).
                removeVertex(k2);
        // Verify original graph.
        verifyBasicGraph(graph);

        // Verify copy.
        assertTrue(copy.hasEdge(k5, k6));
        assertTrue(copy.hasEdge(k5, k7));
        assertTrue(copy.hasEdge(k6, k7));

        assertFalse(copy.hasEdge(k1, k2));
        assertFalse(copy.hasEdge(k1, k7));
        assertFalse(copy.hasEdge(k2, k7));
        assertFalse(copy.hasEdge(k3, k7));
        assertFalse(copy.hasEdge(k4, k7));
    }

    @Test
    public void testGraphWithCycle() {
        Graph graph = newBasicGraph();
        verifyBasicGraph(graph);

        // Add cycle.
        graph.addEdge(k1, k2).
                addEdge(k2, k1);
        // Make a copy and delete something from the copy of original basic graph.
        Graph copy = new Graph(graph).
                removeVertex(k1);
        // Verify original graph.
        verifyBasicGraph(graph);

        // Verify copy.
        assertTrue(copy.hasVertex(k2));
        assertTrue(copy.hasEdge(k5, k6));
        assertTrue(copy.hasEdge(k5, k7));
        assertTrue(copy.hasEdge(k6, k7));

        assertFalse(copy.hasVertex(k1));
        assertFalse(copy.hasEdge(k1, k2));
        assertFalse(copy.hasEdge(k2, k1));
        assertFalse(copy.hasEdge(k1, k7));
        assertFalse(copy.hasEdge(k2, k7));
        assertFalse(copy.hasEdge(k3, k7));
        assertFalse(copy.hasEdge(k4, k7));
    }

    @Test
    public void testTopologicalSort() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        Graph graph = newBasicGraph();
        graph.topologicalSort((vertexKey, order) -> path.add(vertexKey + ":" + order));
        Collections.sort(path);
        StringJoiner joiner = new StringJoiner(",");
        path.stream().forEach(s -> joiner.add(s));
        assertEquals("000:2,00:0,0:1,10:12,1:3,2:4,3:5,4:6,5:7,6:8,7:9,8:10,9:11", joiner.toString());
    }

    @Test(expected = Graph.CycleException.class)
    public void testTopologicalSortCycleDetection() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        new Graph().
                addEdge(k0, k1).
                addEdge(k1, k2).
                addEdge(k2, k3).
                addEdge(k3, k1).
                topologicalSort((key, order) -> path.add(key + ":" + order));
    }

    static List<Key> listOfVertices(Key... args) {
        List<Key> result = Lists.newArrayListWithExpectedSize(args.length);
        for (Key arg : args) {
            result.add(arg);
        }
        return result;
    }

    static void verifyBasicGraph(Graph graph) {
        assertTrue(graph.hasEdge(k1, k2));
        assertTrue(graph.hasEdge(k2, k3));
        assertTrue(graph.hasEdge(k2, k4));
        assertTrue(graph.hasEdge(k2, k5));
        assertTrue(graph.hasEdge(k5, k6));
        assertTrue(graph.hasEdge(k5, k7));
        assertTrue(graph.hasEdge(k6, k7));

        assertFalse(graph.hasEdge(k1, k7));
        assertFalse(graph.hasEdge(k2, k7));
        assertFalse(graph.hasEdge(k3, k7));
        assertFalse(graph.hasEdge(k4, k7));
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
                addEdge(v1, v2, 2.0D).
                addEdge(v1, v4, 1.0D).
                addEdge(v2, v4, 3.0D).
                addEdge(v2, v5, 10.0D).
                addEdge(v3, v1, 4.0D).
                addEdge(v3, v6, 5.0D).
                addEdge(v4, v3, 2.0D).
                addEdge(v4, v5, 2.0D).
                addEdge(v4, v6, 8.0D).
                addEdge(v4, v7, 4.0D).
                addEdge(v5, v7, 6.0D).
                addEdge(v7, v6, 1.0D);
        if (withPositions) {
            graph.setVertexPosition(v1, VertexPosition.new2D(5, 10));
            // Making v2 as a very far point so A-star chooses v4 before prefering v2.
            graph.setVertexPosition(v2, VertexPosition.new2D(1500, 1000));
            graph.setVertexPosition(v3, VertexPosition.new2D(0, 5));
            graph.setVertexPosition(v4, VertexPosition.new2D(10, 10));
            graph.setVertexPosition(v5, VertexPosition.new2D(20, 5));
            graph.setVertexPosition(v6, VertexPosition.new2D(5, 0));
            graph.setVertexPosition(v7, VertexPosition.new2D(15, 0));
        }
        return graph;
    }

    static Graph newBasicGraph() {
        return new Graph().
                addEdge(k1, k2).
                addEdge(k2, k3).
                addEdge(k2, k4).
                addEdge(k2, k5).
                addEdge(k5, k6).
                addEdge(k6, k7).
                addEdge(k5, k7).
                addEdge(k7, k8).
                addEdge(k8, k9).
                addEdge(k9, k10).
                addEdge(k0, k10).
                addEdge(k00, k10).
                addEdge(k000, k10);
    }
}
