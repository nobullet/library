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
    public void testEdgesPresence() {
        Graph graph = createBasicGraph();

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

    @Test
    public void testTopologicalSort() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        Graph graph = createBasicGraph();
        graph.topologicalSort((vertex, order) -> path.add(vertex.getKey() + ":" + order));
        Collections.sort(path);
        StringJoiner joiner = new StringJoiner(",");
        path.stream().forEach(s -> joiner.add(s));
        assertEquals("000:2,00:0,0:1,10:12,1:3,2:4,3:5,4:6,5:7,6:8,7:9,8:10,9:11", joiner.toString());
    }

    @Test(expected = Graph.CycleException.class)
    public void testTopologicalSortCycleDetection() throws Graph.CycleException {
        List<String> path = new ArrayList<>();
        Graph graph = new Graph();
        graph.addEdge("0", "1");
        graph.addEdge("1", "2");
        graph.addEdge("2", "3");
        graph.addEdge("3", "1");
        graph.topologicalSort((vertex, order) -> path.add(vertex.getKey() + ":" + order));
    }

    @Test
    public void testDijkstra() {
        Graph graph = createGraphFromBook(false);
        assertListsEqual(listOfVertices("v1", "v4", "v7"), 
                graph.shortestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v7")));
        
        assertListsEqual(listOfVertices("v1", "v4", "v5"), 
                graph.shortestPathDijkstra(graph.getVertex("v1"), graph.getVertex("v5")));
        
        assertListsEqual(listOfVertices("v3", "v1", "v4", "v5"), 
                graph.shortestPathDijkstra(graph.getVertex("v3"), graph.getVertex("v5")));
    }
    
    @Test
    public void testAStar_defaultsToDijkstra() {
        Graph graph = createGraphFromBook(false);
        assertListsEqual(listOfVertices("v1", "v4", "v7"), 
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v7")));
        
        assertListsEqual(listOfVertices("v1", "v4", "v5"), 
                graph.shortestPathAStar(graph.getVertex("v1"), graph.getVertex("v5")));
        
        assertListsEqual(listOfVertices("v3", "v1", "v4", "v5"), 
                graph.shortestPathAStar(graph.getVertex("v3"), graph.getVertex("v5")));
    }
    
    @Test
    public void testAStar() {
        Graph graph = createGraphFromBook(true);
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
    
    static Graph createBasicGraph() {
        Graph graph = new Graph();
        graph.addEdge("1", "2");
        graph.addEdge("2", "3");
        graph.addEdge("2", "4");
        graph.addEdge("2", "5");
        graph.addEdge("5", "6");
        graph.addEdge("6", "7");
        graph.addEdge("5", "7");
        graph.addEdge("7", "8");
        graph.addEdge("8", "9");
        graph.addEdge("9", "10");
        graph.addEdge("0", "10");
        graph.addEdge("00", "10");
        graph.addEdge("000", "10");
        return graph;
    }

    /**
     * From page 335 of "Data structures And Algorithm Analysis", by Mark Allen Weiss.
     * @param withPositions
     * @return 
     */
    static Graph createGraphFromBook(boolean withPositions) {
        Graph graph = new Graph();
        graph.addEdge("v1", "v2", 2.0D);
        graph.addEdge("v1", "v4", 1.0D);
        graph.addEdge("v2", "v4", 3.0D);
        graph.addEdge("v2", "v5", 10.0D);
        graph.addEdge("v3", "v1", 4.0D);
        graph.addEdge("v3", "v6", 5.0D);
        graph.addEdge("v4", "v3", 2.0D);
        graph.addEdge("v4", "v5", 2.0D);
        graph.addEdge("v4", "v6", 8.0D);
        graph.addEdge("v4", "v7", 4.0D);
        graph.addEdge("v5", "v7", 6.0D);
        graph.addEdge("v7", "v6", 1.0D);
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
}
