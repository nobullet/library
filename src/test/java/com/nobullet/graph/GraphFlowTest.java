package com.nobullet.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.nobullet.graph.Graph.MutableDouble;
import java.util.Optional;
import org.junit.Test;

/**
 * Test maximal flow.
 */
public class GraphFlowTest {

    static final Key s = Key.of("s");
    static final Key a = Key.of("a");
    static final Key b = Key.of("b");
    static final Key c = Key.of("c");
    static final Key d = Key.of("d");
    static final Key t = Key.of("t");

    @Test
    public void testFlow() throws Graph.CycleException, Graph.NegativeEdgeCostException {
        Graph graph = newFromBook();
        Optional<Graph> flow = graph.maximumFlow(s, t);
        assertTrue("Has flow", flow.isPresent());
        Graph flowGraph = flow.get();
        assertEquals("Graphs are the samex.", newMaximalFlowForGraphFromBook(), flow.get());
        MutableDouble flowCounter = new MutableDouble();
        flowGraph.getAdjacentVertices(s).stream().forEach(key -> flowCounter.addAndGet(flowGraph.getEdgeCost(s, key)));
        assertEquals("5.0 is expected flow.", 5.0D, flowCounter.getValue(), 0.000000000001D);
    }

    /**
     * Graph graph from book "Data Structures and Algorithm Analysis in Java", 2nd e., by Mark Allen Weiss, page 346.
     *
     * @return Graph from book.
     */
    static Graph newFromBook() {
        return new Graph()
                .addEdge(s, b, 2.0D)
                .addEdge(s, a, 3.0D)
                .addEdge(a, b, 1.0D)
                .addEdge(a, d, 4.0D)
                .addEdge(b, d, 2.0D)
                .addEdge(a, c, 3.0D)
                .addEdge(c, t, 2.0D)
                .addEdge(d, t, 3.0D);
    }

    /**
     * Maximal flow graph of the graph above.
     *
     * @return Maximal flow graph.
     */
    static Graph newMaximalFlowForGraphFromBook() {
        return new Graph()
                .addEdge(s, b, 2.0D)
                .addEdge(s, a, 3.0D)
                .addEdge(a, d, 1.0D)
                .addEdge(b, d, 2.0D)
                .addEdge(a, c, 2.0D)
                .addEdge(c, t, 2.0D)
                .addEdge(d, t, 3.0D);
    }
}
