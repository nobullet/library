package com.nobullet.graph;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Graph. E - number of edges. V - number of vertices.
 */
public class Graph implements Cloneable {

    static final BiFunction<Vertex, Vertex, Double> DIJKSTRA_HEURISTIC = (Vertex next, Vertex goal) -> 0.0D;
    static final BiFunction<Vertex, Vertex, Double> A_STAR_HEURISTIC
            = (Vertex next, Vertex goal) -> next.distanceTo(goal).orElse(0.0D);
    static final Vertex NOWHERE = new Vertex(Key.of("__NOWHERE__" + UUID.randomUUID().toString()));

    final Map<Key, Vertex> vertices;
    final Map<Key, Vertex> verticesUnmodifiable;

    /**
     * Constructs empty graph.
     */
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
        for (Map.Entry<Key, Vertex> graphEntry : source.vertices.entrySet()) {
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

    /**
     * Returns set of vertices in graph.
     *
     * @return Set of vertices in graph.
     */
    public Set<Key> getVertices() {
        return this.verticesUnmodifiable.keySet();
    }

    /**
     * Traverses all edges from current graph.
     *
     * @param visitor Visitor to be notified about graph visit.
     * @return Current graph.
     */
    public Graph traverseEdges(EdgeVisitor visitor) {
        traverseEdges(edge
                -> visitor.visit(edge.getFrom().getKey(), edge.getTo().getKey(), edge.getCost(), edge.getData()));
        return this;
    }

    /**
     * Adds vertex by key. Silently updates data if there is a vertex for given key.
     *
     * @param key Vertex key to add.
     * @return Current graph.
     */
    public Graph addVertex(Key key) {
        Vertex vertex = this.vertices.get(key);
        if (vertex == null) {
            vertex = new Vertex(key);
            this.vertices.put(key, vertex);
        }
        return this;
    }

    /**
     * Adds vertex by key. Silently updates data if there is a vertex for given key.
     *
     * @param key Vertex key to add.
     * @param data Vertex data.
     * @return Current graph.
     */
    public Graph addVertex(Key key, Object data) {
        Vertex vertex = this.vertices.get(key);
        if (vertex == null) {
            vertex = new Vertex(key, data);
            this.vertices.put(key, vertex);
        } else {
            vertex.setData(data);
        }
        return this;
    }

    /**
     * Adds vertex by key. Silently updates data and position if there is a vertex for given key.
     *
     * @param key Vertex key to add.
     * @param data Vertex data.
     * @param position Vertex position.
     * @return Current graph.
     */
    public Graph addVertex(Key key, Object data, VertexPosition position) {
        Vertex vertex = this.vertices.get(key);
        if (vertex == null) {
            vertex = new Vertex(key, position, data);
            this.vertices.put(key, vertex);
        } else {
            vertex.setPosition(position);
            vertex.setData(data);
        }
        return this;
    }

    /**
     * Removes vertex by key. Silently returns if there is no vertex for given key.
     *
     * @param key Vertex key to remove.
     * @return Current graph.
     */
    public Graph removeVertex(Key key) {
        return removeVertexInternal(this.vertices.get(key));
    }

    /**
     * Checks if the given vertex belongs to graph.
     *
     * @param vertexKey Vertex key.
     * @return Whether the given vertex belongs to graph.
     */
    public boolean hasVertex(Key vertexKey) {
        return vertices.containsKey(vertexKey);
    }

    /**
     * Returns optional with vertex position.
     *
     * @param vertexKey Vertex key.
     * @return Optional with vertex position.
     */
    public Optional<VertexPosition> getVertexPosition(Key vertexKey) {
        Vertex from = this.vertices.get(vertexKey);
        return from != null ? from.getPosition() : Optional.empty();
    }

    /**
     * Sets vertex position.
     *
     * @param vertexKey Vertex key.
     * @param position Position.
     * @return Current graph.
     */
    public Graph setVertexPosition(Key vertexKey, VertexPosition position) {
        getVertex(vertexKey).setPosition(position);
        return this;
    }

    /**
     * Gets vertex data. Ï
     *
     * @param vertexKey Vertex key.
     * @return Optional data.
     */
    public Optional<Object> getVertexData(Key vertexKey) {
        Vertex from = this.vertices.get(vertexKey);
        return from != null ? from.getData() : Optional.empty();
    }

    /**
     * Sets vertex data.
     *
     * @param vertexKey Vertex key.
     * @param data Data to set.
     * @return Current graph.
     */
    public Graph setVertexData(Key vertexKey, Object data) {
        getVertex(vertexKey).setData(data);
        return this;
    }

    /**
     * Checks if the graph has edge from vertex defined by fromKey to vertex defined by toKey.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @return Whether the graph has edge from vertex defined by fromKey to vertex defined by toKey.
     */
    public boolean hasEdge(Key fromKey, Key toKey) {
        Vertex from = this.vertices.get(fromKey);
        Vertex to = this.vertices.get(toKey);
        return from != null && to != null && from.hasEdge(to);
    }

    /**
     * Adds edge between two vertices defined by keys of cost 1.0D.
     *
     * @param fromKey From vertex key.
     * @param toKey To vertex key.
     * @return Current graph.
     */
    public Graph addEdge(Key fromKey, Key toKey) {
        return addEdge(fromKey, toKey, 1.0D, null);
    }

    /**
     * Adds edge between two vertices defined by keys of cost 1.0D.
     *
     * @param fromKey From vertex key.
     * @param toKey To vertex key.
     * @param cost Cost of the edge.
     * @return Current graph.
     */
    public Graph addEdge(Key fromKey, Key toKey, double cost) {
        return addEdge(fromKey, toKey, cost, null);
    }

    /**
     * Adds edge between two vertices defined by keys of cost 1.0D.
     *
     * @param fromKey From vertex key.
     * @param toKey To vertex key.
     * @param cost Cost of the edge.
     * @param data Edge data.
     * @return Current graph.
     */
    public Graph addEdge(Key fromKey, Key toKey, double cost, Object data) {
        addVertex(fromKey);
        addVertex(toKey);
        addEdgeInternal(fromKey, toKey, cost, data);
        return this;
    }

    /**
     * Returns edge cost.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @return Edge cost.
     * @throws NullPointerException If there is no such edge.
     */
    public double getEdgeCost(Key fromKey, Key toKey) {
        return getEdge(fromKey, toKey).getCost();
    }

    /**
     * Sets edge cost.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @param cost Edge cost.
     * @return Current graph.
     * @throws NullPointerException If there is no such edge.
     */
    public Graph setEdgeCost(Key fromKey, Key toKey, double cost) {
        getEdge(fromKey, toKey).setCost(cost);
        return this;
    }

    /**
     * Returns edge data.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @return Edge data.
     * @throws NullPointerException If there is no such edge.
     */
    public Optional<Object> getEdgeData(Key fromKey, Key toKey) {
        Vertex from = this.vertices.get(fromKey);
        Vertex to = this.vertices.get(toKey);
        return from != null && to != null && from.hasEdge(to) ? from.getEdge(to).getData() : Optional.empty();
    }

    /**
     * Sets edge data.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @param data Edge data.
     * @return Current graph.
     * @throws NullPointerException If there is no such edge.
     */
    public Graph setEdgeData(Key fromKey, Key toKey, Object data) {
        getEdge(fromKey, toKey).setData(data);
        return this;
    }

    /**
     * Removes edge between from and to vertices.
     *
     * @param fromKey From key.
     * @param toKey To Key.
     * @return Current graph.
     */
    public Graph removeEdge(Key fromKey, Key toKey) {
        if (fromKey.equals(toKey)) {
            return this;
        }
        Vertex from = this.vertices.get(fromKey);
        Vertex to = this.vertices.get(toKey);
        if (from != null && to != null) {
            from.removeEdgeTo(to);
        }
        return this;
    }

    /**
     * Returns keys of adjacent vertices to vertex defined by fromKey.
     *
     * @param fromKey From key.
     * @return Set of adjacent vertices.
     */
    public Set<Key> getAdjacentVertices(Key fromKey) {
        return getVertex(fromKey).getAdjacentVerticesKeys();
    }

    /**
     * Visits the vertices of the graph in topological order. Complexity: O(|E| + |V|) (2 * |V| + 3 * |V|).
     *
     * @param visitor Visitor to accept the vertices.
     * @throws com.nobullet.graph.Graph.CycleException If a cycle found.
     */
    public void topologicalSort(VertexVisitor visitor) throws CycleException {
        Map<Vertex, MutableLong> indegrees = new HashMap<>();
        // Calculate ingoing degrees for each vertex. Compexity: O(|V| + |E|).
        // Result is a map of indegrees (null instead of counter means 0).
        for (Vertex vertex : vertices.values()) {
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
        for (Vertex vertex : vertices.values()) {
            if (!indegrees.containsKey(vertex)) {
                queue.add(vertex);
            }
        }
        int counter = 0;
        // For each adjacent vertex in the queue decrement the counter and put adjacent vertex in the queue
        // if it's counter is 0. Complexity is O(|V| + |E|).
        while (!queue.isEmpty()) {
            Vertex vertex = queue.remove();
            visitor.visit(vertex.getKey(), counter++);
            for (Vertex adjacent : vertex.getAdjacentVertices()) {
                MutableLong adjacentCounter = indegrees.get(adjacent);
                if (adjacentCounter.decrementAndGet() == 0) {
                    queue.add(adjacent);
                    indegrees.remove(adjacent);
                }
            }
        }
        indegrees.clear();
        if (counter != vertices.size()) {
            throw new CycleException("Graph has cycle: expected number of vertices is " + counter + " but graph has "
                    + vertices.size());
        }
    }

    /**
     * A-star shortest path algorithm.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @return List of vertices that create the path.
     */
    public Path shortestPathAStar(Key sourceKey, Key targetKey) {
        return priorityFirstSearchInternal(sourceKey, targetKey, A_STAR_HEURISTIC);
    }

    /**
     * Dijkstra's shortest path algorithm.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @return List of vertices that create the path.
     */
    public Path shortestPathDijkstra(Key sourceKey, Key targetKey) {
        return priorityFirstSearchInternal(sourceKey, targetKey, DIJKSTRA_HEURISTIC);
    }

    /**
     * Template method for path algorithms.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @param heuristic Heuristic function which accepts two keys and returns heuristic value for this pair.
     * @return List of vertices that create the path.
     */
    public Path priorityFirstSearch(Key sourceKey, Key targetKey, BiFunction<Key, Key, Double> heuristic) {
        return priorityFirstSearchInternal(sourceKey, targetKey, (v1, v2) -> heuristic.apply(v1.getKey(), v2.getKey()));
    }

    /**
     * Template method for path algorithms.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @param heuristic Heuristic function.
     * @return List of vertices that create the path.
     */
    private Path priorityFirstSearchInternal(Key sourceKey, Key targetKey, BiFunction<Vertex, Vertex, Double> heuristic) {
        Vertex source = getVertex(sourceKey);
        Vertex target = getVertex(targetKey);

        VertexWithPriority frontierVertex;
        PriorityQueue<VertexWithPriority> frontier = new PriorityQueue<>();
        frontier.add(new VertexWithPriority(source, 0.0D));

        Map<Vertex, Double> costSoFar = new HashMap<>();
        costSoFar.put(source, 0.0D);

        Map<Vertex, Vertex> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);

        while (!frontier.isEmpty()) {
            frontierVertex = frontier.poll();
            Vertex current = frontierVertex.getVertex();
            frontierVertex.clear();
            // Early exit for shortest paths.
            if (current.equals(target)) {
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
                    double newPriority = newCost + heuristic.apply(target, next);
                    frontier.offer(new VertexWithPriority(next, newPriority));
                }
            }
        }
        Path path = reconstructByCameFrom(source, target, cameFrom);
        // Clean up.
        while (!frontier.isEmpty()) {
            frontier.poll().clear();
        }
        frontier.clear();
        cameFrom.clear();
        costSoFar.clear();
        return path;
    }

    /**
     * Unweighted depth-first search in graph for path between two vertices.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @return List of vertices that create the path.
     */
    public Path depthFirstSearch(Key sourceKey, Key targetKey) {
        return unweightedFirstSearch(sourceKey, targetKey, true);
    }

    /**
     * Unweighted breadth-first search in graph for path between two vertices.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @return List of vertices that create the path.
     */
    public Path breadthFirstSearch(Key sourceKey, Key targetKey) {
        return unweightedFirstSearch(sourceKey, targetKey, false);
    }

    /**
     * Unweighted breadth/depth-first search in graph for path between two vertices.
     *
     * @param sourceKey Source vertex key.
     * @param targetKey Target vertex key.
     * @param depthFirst Whether it depth-first search (breadth-first otherwise).
     * @return List of vertices that create the path.
     */
    private Path unweightedFirstSearch(Key sourceKey, Key targetKey, boolean depthFirst) {
        Vertex source = getVertex(sourceKey);
        Vertex target = getVertex(targetKey);

        Deque<Vertex> frontier = new LinkedList<>();
        frontier.add(source);

        Map<Vertex, Double> costSoFar = new HashMap<>();
        costSoFar.put(source, 0.0D);

        Map<Vertex, Vertex> cameFrom = new HashMap<>();
        cameFrom.put(source, NOWHERE);

        while (!frontier.isEmpty()) {
            Vertex current = depthFirst ? frontier.removeLast() : frontier.removeFirst();

            if (current.equals(target)) {
                break;
            }

            for (Edge outgoingEdge : current.getOutgoingEdges()) {
                Vertex next = outgoingEdge.getTo();
                if (!cameFrom.containsKey(next)) {
                    cameFrom.put(next, current);
                    costSoFar.put(next, costSoFar.get(current) + outgoingEdge.getCost());
                    frontier.addLast(next);
                }
            }
        }

        Path path = reconstructByCameFrom(source, target, cameFrom);
        // Clean up.
        while (!frontier.isEmpty()) {
            frontier.poll().clear();
        }
        frontier.clear();
        cameFrom.clear();
        costSoFar.clear();
        return path;
    }

    /**
     * Builds maximum flow Graph from given graph. Returns empty optional if there is no flow or sink is not reachable.
     * Uses Ford-Fulkerson/Edmonds–Karp maximal flow algorithm.
     *
     * @param sourceKey Source key to start from.
     * @param sinkKey Sink key to finish.
     * @return Optional of flow graph.
     * @throws NegativeEdgeCostException When graph has negative cost edge.
     */
    public Optional<Graph> maximumFlow(Key sourceKey, Key sinkKey) throws NegativeEdgeCostException {
        if (!hasVertex(sourceKey) || !hasVertex(sinkKey)) {
            return Optional.empty();
        }
        Graph flow = new Graph();
        Graph residual = new Graph(this);
        List<Edge> residualEdges = new LinkedList<>();
        Path path;
        do {
            path = residual.breadthFirstSearch(sourceKey, sinkKey);
            Vertex previous = null;
            Vertex current = null;
            double minEdgeLength = 0.0D;
            // Find edge with minimal cost in path and collect edges in list.
            for (Key currentKey : path.getPath()) {
                current = residual.getVertex(currentKey);
                if (previous != null) {
                    Edge edgeInPath = previous.getEdge(current);
                    if (edgeInPath.getCost() < 0.0D) {
                        throw new NegativeEdgeCostException(String.format("%s->%s edge has negative cost.",
                                previous.getKey(), currentKey));
                    }
                    residualEdges.add(edgeInPath);
                    if (minEdgeLength == 0.0D || edgeInPath.getCost() < minEdgeLength) {
                        minEdgeLength = edgeInPath.getCost();
                    }
                }
                previous = current;
            }
            for (Edge residualEdge : residualEdges) {
                Vertex residualFrom = residualEdge.getFrom();
                Vertex residualTo = residualEdge.getTo();
                double newCost = residualEdge.getCost() - minEdgeLength;
                if (newCost > 0.0D) {
                    // Subtract flow.
                    residualEdge.setCost(newCost);
                } else {
                    // Remove edge.
                    residualFrom.removeEdgeTo(residualTo);
                }
                // Get reverse edge.
                Edge reverseEdge = residualTo.getEdge(residualFrom);
                if (reverseEdge == null) {
                    // Create new if it doesn't exist.
                    residual.addEdge(residualTo.getKey(), residualFrom.getKey(), minEdgeLength);
                } else {
                    // Or update cost.
                    reverseEdge.setCost(reverseEdge.getCost() + minEdgeLength);
                }
                // Update flow graph.
                if (!flow.hasEdge(residualFrom.getKey(), residualTo.getKey())) {
                    // Add flow edge.
                    flow.addEdge(residualFrom.getKey(), residualTo.getKey(), minEdgeLength);
                } else {
                    // Or update cost.
                    Edge flowEdge = flow.getEdge(residualFrom.getKey(), residualTo.getKey());
                    flowEdge.setCost(flowEdge.getCost() + minEdgeLength);
                }
            }
            residualEdges.clear();
        } while (!path.isEmpty());
        if (!flow.hasVertex(sinkKey)) {
            flow.clear();
            residual.clear();
            return Optional.empty();
        }
        residual.clear();
        return Optional.of(flow);
    }

    /**
     * Removes all edges and vertices from current graph.
     */
    public void clear() {
        vertices.values().stream().forEach(vertex -> vertex.clear());
        vertices.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        traverseEdges(edge -> sb
                .append(edge.getFrom().getKey())
                .append("--(")
                .append(edge.getCost())
                .append(")-->")
                .append(edge.getTo().getKey())
                .append(",\n")
        );
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append("}").toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Graph(this);
    }

    @Override
    public int hashCode() {
        MutableLong hashCode = new MutableLong(5);
        hashCode.multiplyAndGet(23);
        hashCode.addAndGet(Objects.hashCode(this.vertices.keySet()));
        this.traverseEdges(edge -> {
            hashCode.multiplyAndGet(23);
            hashCode.addAndGet(edge.hashCode());
        });
        return hashCode.getIntegerValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Graph other = (Graph) obj;
        Set<Key> thisKeys = this.vertices.keySet();
        Set<Key> otherKeys = other.vertices.keySet();
        // Check has same vertex keys.
        if (otherKeys.size() != thisKeys.size() || !thisKeys.containsAll(otherKeys)) {
            return false;
        }
        // Check edges.
        Set<Edge> thisEdges = new HashSet<>((int) (thisKeys.size() * 1.5F));
        Set<Edge> otherEdges = new HashSet<>((int) (thisKeys.size() * 1.5F));
        this.traverseEdges(thisEdges::add);
        other.traverseEdges(otherEdges::add);
        boolean result = thisEdges.equals(otherEdges);
        thisEdges.clear();
        otherEdges.clear();
        return result;
    }

    /**
     * Reconstructs the path by given arguments.
     *
     * @param source Source.
     * @param target Targe vertex.
     * @param cameFrom Came from map.
     * @return Path to target.
     */
    private Path reconstructByCameFrom(Vertex source, Vertex target, Map<Vertex, Vertex> cameFrom) {
        if (!cameFrom.containsKey(target)) {
            return new Path(source.getKey(), target.getKey(), Collections.emptyList(), 0.0D);
        }
        // Reconstruct the path.
        double cost = 0.0D;
        List<Key> path = new LinkedList<>();
        Vertex current = target;
        Vertex previous = null;
        while (cameFrom.containsKey(current)) {
            path.add(current.getKey());
            previous = cameFrom.get(current);
            if (previous != null && previous != NOWHERE) {
                cost += previous.getEdge(current).getCost();
            }
            current = previous;
        }
        Collections.reverse(path);
        return new Path(source.getKey(), target.getKey(), path, cost);
    }

    /**
     * Returns vertex by key. Throws {@link NullPointerException} if there is no vertex for key.
     *
     * @param key Vertex key.
     * @return Vertex for key or {@link NullPointerException} if there is no vertex for key.
     */
    private Vertex getVertex(Key key) {
        Vertex vertex = this.vertices.get(key);
        if (vertex == null) {
            throw new NullPointerException(String.format("No vertex for key %s.", key));
        }
        return vertex;
    }

    /**
     * Returns edge between two vertices.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @return Edge between two vertices.
     * @throws NullPointerException If there is no such edge.
     */
    private Edge getEdge(Key fromKey, Key toKey) {
        Edge existingEdge = getVertex(fromKey).getEdge(getVertex(toKey));
        if (existingEdge == null) {
            throw new NullPointerException(String.format("No edge found for: %s -> %s .", fromKey, toKey));
        }
        return existingEdge;
    }

    /**
     * Adds edge between two vertices overwriting cost if vertex exists if necessary.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @param cost Cost.
     * @param data Edge data. Used if there was no edge for these vertices.
     * @return Current graph.
     */
    private Graph addEdgeInternal(Key fromKey, Key toKey, double cost, Object data) {
        if (fromKey.equals(toKey)) {
            throw new IllegalStateException("Can't add cycle edge for: " + fromKey);
        }
        Vertex from = this.vertices.get(fromKey);
        Vertex to = this.vertices.get(toKey);
        checkFromTo(fromKey, toKey, from, to);
        from.addEdge(to, cost, data);
        return this;
    }

    /**
     * Removes vertex from graph removing all the outgoing and incoming edges.
     *
     * @param v Vertex to remove.
     * @return Current graph.
     */
    private Graph removeVertexInternal(Vertex v) {
        if (v == null) {
            return this;
        }
        for (Vertex other : vertices.values()) {
            if (other.hasEdge(v)) {
                other.removeEdgeTo(v);
            }
        }
        v.clear();
        this.vertices.remove(v.getKey());
        return this;
    }

    /**
     * Traverses all edges in graph calling {@link Consumer#accept(java.lang.Object)} on each edge.
     *
     * @param consumer Consumer for an edge.
     */
    private void traverseEdges(Consumer<Edge> consumer) {
        for (Vertex vertex : vertices.values()) {
            for (Edge edge : vertex.getOutgoingEdges()) {
                consumer.accept(edge);
            }
        }
    }

    /**
     * Check whether given vertices with correspondent keys exist.
     *
     * @param fromKey From key.
     * @param toKey To key.
     * @param from From vertex.
     * @param to To Vertex.
     */
    private void checkFromTo(Key fromKey, Key toKey, Vertex from, Vertex to) {
        if (from == null) {
            throw new IllegalArgumentException("Can't find vertex for key: " + fromKey);
        }
        if (to == null) {
            throw new IllegalArgumentException("Can't find vertex for key: " + toKey);
        }
    }

    /**
     * Vertex visitor.
     */
    public interface VertexVisitor {

        /**
         * Invoked when algorithms visit a vertex in graph.
         *
         * @param vertexKey Vertex key.
         * @param order Topological order of the vertex when possible.
         */
        void visit(Key vertexKey, int order);
    }

    /**
     * Edge visitor.
     */
    public interface EdgeVisitor {

        /**
         * Invoked when algorithms visit an edge in graph.
         *
         * @param fromKey Edge from.
         * @param toKey Edge to.
         * @param cost Cost of the edge.
         * @param data Optional data from edge.
         */
        void visit(Key fromKey, Key toKey, double cost, Optional<Object> data);
    }

    /**
     * Cycle exception.
     */
    public static class CycleException extends Exception {

        public CycleException() {
        }

        public CycleException(String message) {
            super(message);
        }
    }

    /**
     * Negative edge cost exception.
     */
    public static class NegativeEdgeCostException extends Exception {

        public NegativeEdgeCostException() {
        }

        public NegativeEdgeCostException(String message) {
            super(message);
        }
    }

    /**
     * Vertex with external priority. For shortest path algorithms like Dijkstra or AStar.
     */
    private static class VertexWithPriority implements Comparable<VertexWithPriority> {

        Vertex vertex;
        double priority;

        /**
         * Constructs prioritized vertex with given arguments.
         *
         * @param vertex Vertex.
         * @param priority Priority.
         */
        public VertexWithPriority(Vertex vertex, double priority) {
            this.vertex = vertex;
            this.priority = priority;
        }

        /**
         * Vertex.
         *
         * @return Vertex.
         */
        public Vertex getVertex() {
            return vertex;
        }

        /**
         * Priority.
         *
         * @return Priority.
         */
        public double getPriority() {
            return priority;
        }

        public void clear() {
            this.vertex = null;
        }

        @Override
        public int compareTo(VertexWithPriority o) {
            return Double.compare(priority, o.priority);
        }

        @Override
        public String toString() {
            return "VP{vertex=" + vertex + ", priority=" + priority + '}';
        }
    }

    /**
     * Mutable long for internals (topological quickSort).
     */
    static class MutableLong {

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

        public MutableLong setValue(long value) {
            this.value = value;
            return this;
        }

        public int getIntegerValue() {
            return Long.valueOf(value).intValue();
        }

        public long incrementAndGet() {
            return ++this.value;
        }

        public long decrementAndGet() {
            return --this.value;
        }

        public long addAndGet(long other) {
            this.value += other;
            return this.value;
        }

        public long multiplyAndGet(long other) {
            this.value *= other;
            return this.value;
        }

        public long divideAndGet(long other) {
            this.value /= other;
            return this.value;
        }

        public long subtractAndGet(long other) {
            this.value -= other;
            return this.value;
        }

        @Override
        public String toString() {
            return "MutableLong{" + value + '}';
        }
    }

    /**
     * Mutable double for internals (shortest/longest paths).
     */
    static class MutableDouble {

        private double value;

        public MutableDouble() {
            this.value = 0.0D;
        }

        public MutableDouble(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public MutableDouble setValue(double value) {
            this.value = value;
            return this;
        }

        public double incrementAndGet() {
            this.value += 1.0D;
            return this.value;
        }

        public double decrementAndGet() {
            this.value -= 1.0D;
            return this.value;
        }

        public double addAndGet(double other) {
            this.value += other;
            return this.value;
        }

        public double multiplyAndGet(double other) {
            this.value *= other;
            return this.value;
        }

        public double divideAndGet(double other) {
            this.value /= other;
            return this.value;
        }

        public double subtractAndGet(double other) {
            this.value -= other;
            return this.value;
        }

        @Override
        public String toString() {
            return "MutableDouble{" + value + '}';
        }
    }
}
