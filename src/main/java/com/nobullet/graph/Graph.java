package com.nobullet.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiFunction;

/**
 * Graph. E - number of edges. V - number of vertices.
 */
public class Graph implements Cloneable {

    static final BiFunction<Vertex, Vertex, Double> DIJKSTRA_HEURISTIC = (Vertex next, Vertex goal) -> 0.0D;
    static final BiFunction<Vertex, Vertex, Double> A_STAR_HEURISTIC
            = (Vertex next, Vertex goal) -> next.distanceTo(goal).orElse(0.0D);
    static final Vertex NOWHERE = new Vertex("__NOWHERE__");

    final Map<String, Vertex> vertices;
    final Map<String, Vertex> verticesUnmodifiable;

    public Graph() {
        this.vertices = new HashMap<>();
        this.verticesUnmodifiable = Collections.unmodifiableMap(this.vertices);
    }

    /**
     * Copy constructor.
     *
     * @param source Source to copy.
     */
    public Graph(Graph source) {
        this();
        for (Map.Entry<String, Vertex> graphEntry : source.vertices.entrySet()) {
            this.vertices.put(graphEntry.getKey(), new Vertex(graphEntry.getValue()));
        }
        for (Vertex sourceVertex : source.vertices.values()) {
            for (Edge sourceEdge : sourceVertex.getOutgoingEdges()) {
                addEdgeInternal(
                        sourceEdge.getFrom().getKey(),
                        sourceEdge.getTo().getKey(),
                        sourceEdge.getCost(),
                        sourceEdge.getData().orElseGet(() -> null));
            }
        }
    }

    public Collection<Vertex> getVertices() {
        return this.verticesUnmodifiable.values();
    }

    public Vertex getVertex(String key) {
        return this.vertices.get(key);
    }

    public Graph addVertex(String key) {
        Vertex vertex = getVertex(key);
        if (vertex != null) {
            return this;
        }
        vertex = new Vertex(key);
        this.vertices.put(key, vertex);
        return this;
    }
    
    public Graph removeVertex(String key) {
        return removeVertex(getVertex(key));
    }
    
    public Graph removeVertex(Vertex v) {
        for (Vertex other : vertices.values()) {
            if (other.hasEdge(v)) {
                other.removeEdgeTo(v);
            }
        }
        v.clear();
        this.vertices.remove(v.getKey());
        return this;
    }

    private void addEdgeInternal(String fromKey, String toKey, double cost, Object data) {
        if (fromKey.equals(toKey)) {
            throw new IllegalStateException("Can't add cycle edge for: " + fromKey);
        }
        this.vertices.get(fromKey).addEdge(this.vertices.get(toKey), cost, data);
    }

    public Graph addEdge(String fromKey, String toKey) {
        addEdge(fromKey, toKey, 1.0D);
        return this;
    }

    public Graph addEdge(String fromKey, String toKey, double cost) {
        addVertex(fromKey);
        addVertex(toKey);
        addEdgeInternal(fromKey, toKey, cost, null);
        return this;
    }

    public boolean hasEdge(String fromKey, String toKey) {
        Vertex vertex1 = getVertex(fromKey);
        Vertex vertex2 = getVertex(toKey);
        return vertex1 != null && vertex1.hasEdge(vertex2);
    }

    public Edge getEdge(String fromKey, String toKey) {
        Vertex vertex1 = getVertex(fromKey);
        Vertex vertex2 = getVertex(toKey);
        return vertex1.getEdge(vertex2);
    }

    public Edge getEdge(Vertex from, Vertex to) {
        return from.getEdge(to);
    }
    
    public Graph removeEdge(String fromKey, String toKey) {
        return removeEdge(getVertex(fromKey), getVertex(toKey));
    }
    
    public Graph removeEdge(Vertex v1, Vertex v2) {
        v1.removeEdgeTo(v2);
        return this;
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

    /**
     * A-star shortest path algorithm.
     *
     * @param source Source vertex.
     * @param target Target vertex.
     * @return List of vertices that create the path.
     */
    public List<Vertex> shortestPathAStar(Vertex source, Vertex target) {
        return shortestPathTemplate(source, target, A_STAR_HEURISTIC);
    }

    /**
     * Dijkstra's shortest path algorithm.
     *
     * @param source Source vertex.
     * @param target Target vertex.
     * @return List of vertices that create the path.
     */
    public List<Vertex> shortestPathDijkstra(Vertex source, Vertex target) {
        return shortestPathTemplate(source, target, DIJKSTRA_HEURISTIC);
    }

    /**
     * Template for A* and Dijkstra algorithms.
     *
     * @param source Source vertex.
     * @param target Target vertex.
     * @param heuristic Heuristic function.
     * @return List of vertices that create the path.
     */
    public List<Vertex> shortestPathTemplate(Vertex source, Vertex target,
            BiFunction<Vertex, Vertex, Double> heuristic) {
        checkVertex(source);
        checkVertex(target);

        PriorityQueue<VertexWithPriority> frontier = new PriorityQueue<>();
        frontier.add(new VertexWithPriority(source, 0.0D));

        Map<Vertex, Double> costSoFar = new HashMap<>();
        costSoFar.put(source, 0.0D);

        Map<Vertex, Vertex> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);

        boolean found = false;
        while (!frontier.isEmpty()) {
            Vertex current = frontier.poll().getVertex();
            // Early exit.
            if (current.equals(target)) {
                found = true;
                break;
            }
            // For all the neighbors.
            for (Edge adjacentEdge : current.getOutgoingEdges()) {
                // Get neighbor.
                Vertex next = adjacentEdge.getTo();
                // Cost to next = previously calculated cost of travel to current + cost of the edge to neighbor.
                double newCost = costSoFar.get(current) + adjacentEdge.getCost();
                // If neighbor has not been visited yet or 'new' cost to next is better, re-submit the neighbor to
                // frontier with new priority (newCost + heuristic), so the newly or revisited vertex could 
                // be reconsidered again.
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    // Remember the cost.
                    costSoFar.put(next, newCost);
                    // Remember the step.
                    cameFrom.put(next, current);
                    // Submit or re-submit to frontier with new cost.
                    // Add result of heuristic function invocation so vertices closer to target considered
                    // earlier.
                    frontier.offer(new VertexWithPriority(next, newCost + heuristic.apply(target, next)));
                }
            }
        }

        List<Vertex> path = null;
        if (!found) {
            return Collections.emptyList();
        } else {
            // Reconstruct the path.
            path = new LinkedList<>();
            Vertex current = target;
            while (cameFrom.containsKey(current)) {
                path.add(current);
                current = cameFrom.get(current);
            }
            Collections.reverse(path);
        }
        // Clean up.
        cameFrom.clear();
        costSoFar.clear();
        frontier.clear();
        return path;
    }

    public void maximumFlow(Vertex source, Vertex sink) {
        Graph flow = new Graph(this);
        Graph residual = new Graph(this);
    }

    /**
     * Checks if the given vertex belongs to graph.
     *
     * @param vertex Vertex to check.
     * @throws IllegalArgumentException If vertex is not in graph.
     */
    public void checkVertex(Vertex vertex) {
        if (!hasVertex(vertex)) {
            throw new IllegalArgumentException(
                    String.format("Given vertext (%s) doesn't belong to graph.", vertex));
        }
    }

    /**
     * Checks if the given vertex belongs to graph.
     *
     * @param vertexKey Vertex key.
     * @return Whether the given vertex belongs to graph.
     */
    public boolean hasVertex(String vertexKey) {
        return verticesUnmodifiable.containsKey(vertexKey);
    }

    /**
     * Checks if the given vertex belongs to graph.
     *
     * @param vertex Vertex.
     * @return Whether the given vertex belongs to graph.
     */
    public boolean hasVertex(Vertex vertex) {
        return verticesUnmodifiable.containsKey(vertex.getKey());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Graph(this);
    }

    /**
     * Vertex with external priority. For shortest path algorithms like Dijkstra or AStar.
     */
    static class VertexWithPriority implements Comparable<VertexWithPriority> {

        private final Vertex vertext;
        private final double priority;

        /**
         * Constructs prioritized vertex with given arguments.
         *
         * @param vertex Vertex.
         * @param priority Priority.
         */
        public VertexWithPriority(Vertex vertex, double priority) {
            this.vertext = vertex;
            this.priority = priority;
        }

        /**
         * Vertex.
         *
         * @return Vertex.
         */
        public Vertex getVertex() {
            return vertext;
        }

        /**
         * Priority.
         *
         * @return Priority.
         */
        public double getPriority() {
            return priority;
        }

        @Override
        public int compareTo(VertexWithPriority o) {
            return Double.compare(priority, o.priority);
        }

        @Override
        public String toString() {
            return "VP{vertext=" + vertext + ", priority=" + priority + '}';
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
