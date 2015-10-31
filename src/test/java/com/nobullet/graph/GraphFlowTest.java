package com.nobullet.graph;

import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Ignore;
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
    @Ignore
    public void testFlow() throws Graph.CycleException, Graph.NegativeEdgeCostException {
        Graph graph = newFromBook();
        Optional<Graph> flow = graph.maximumFlow(s, t);
        assertTrue("Has flow", flow.isPresent());
    }
    
    /**
     * Graph 
     * @return 
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
}
