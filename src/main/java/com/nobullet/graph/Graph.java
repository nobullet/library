package com.nobullet.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Graph. E - number of edges. V - number of vertices.
 */
public class Graph {

    private final Map<String, Vertex> vertices;
    private final Map<String, Vertex> verticesUnmodifieble;

    public Graph() {
        this.vertices = new HashMap<>();
        this.verticesUnmodifieble = Collections.unmodifiableMap(this.vertices);
    }

    public Collection<Vertex> getVertices() {
        return this.verticesUnmodifieble.values();
    }

    public Vertex getVertex(String name) {
        return this.vertices.get(name);
    }

    public Vertex addVertex(String name) {
        Vertex vertex = getVertex(name);
        if (vertex != null) {
            return vertex;
        }
        vertex = new Vertex(name);
        this.vertices.put(name, vertex);
        return vertex;
    }

    public Edge addEdge(String name1, String name2) {
        return addEdge(name1, name2, 1.0D);
    }

    public Edge addEdge(String name1, String name2, double weight) {
        if (name1.equals(name2)) {
            return null;
        }
        Vertex vertex1 = addVertex(name1);
        Vertex vertex2 = addVertex(name2);
        return vertex1.addEdge(vertex2, weight);
    }

    public boolean hasEdge(String name1, String name2) {
        Vertex vertex1 = getVertex(name1);
        Vertex vertex2 = getVertex(name2);
        return vertex1 != null && vertex1.hasEdge(vertex2);
    }

    /**
     * Visits the vertices of the graph in topological order. Complexity: O(|E| + |V|) (2 * |V| + 3 * |V|).
     *
     * @param visitor Visitor to accept the vertices.
     * @throws com.nobullet.graph.Graph.CycleException If a cycle found.
     */
    public void topologicalSort(GraphVisitor visitor) throws CycleException {
        Map<Vertex, MutableLong> indegrees = new HashMap<>();
        // Calculate ingoing degrees for each vertex. Compexity: O(|V| + |E|).
        // Result is a map of indegrees (null instead of counter means 0).
        for (Vertex vertex : getVertices()) {
            for (Edge edge : vertex.getOutgoingEdges()) {
                Vertex to = edge.getTo();
                MutableLong indegreeCounter = indegrees.get(to);
                if (indegreeCounter == null) {
                    indegrees.put(to, new MutableLong(1L));
                } else {
                    indegreeCounter.incrementAndGet();
                }
            }
        }

        Queue<Vertex> queue = new LinkedList<>();
        // Finds all vertices with indegree of 0 and puts them into the queue. Complexicity: O(|V|).
        for (Vertex vertex : getVertices()) {
            if (!indegrees.containsKey(vertex)) {
                queue.add(vertex);
            }
        }
        int counter = 0;
        // For each adjacent vertex in the queue decrement the counter and put adjacent vertex in the queue
        // if it's counter is 0. Complexity is O(|V| + |E|).
        while (!queue.isEmpty()) {
            Vertex vertex = queue.remove();
            visitor.visit(vertex, counter++);
            for (Vertex adjacent : vertex.getAdjacentVertices()) {
                MutableLong adjacentCounter = indegrees.get(adjacent);
                if (adjacentCounter.decrementAndGet() == 0) {
                    queue.add(adjacent);
                    indegrees.remove(adjacent);
                }
            }
        }
        if (counter != vertices.size()) {
            throw new CycleException("Graph has cycle: expected number of vertices is " + counter + " but graph has "
                    + vertices.size());
        }
    }

    public static class CycleException extends Exception {

        public CycleException() {
        }

        public CycleException(String message) {
            super(message);
        }
    }

    private static class MutableLong {

        private long value;

        public MutableLong() {
            this.value = 0L;
        }

        public MutableLong(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public long incrementAndGet() {
            return ++this.value;
        }

        public long decrementAndGet() {
            return --this.value;
        }

        @Override
        public String toString() {
            return "MutableLong{" + value + '}';
        }
    }
}
