package com.nobullet.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Graph vertex. Not thread safe.
 */
class Vertex implements Cloneable {

    Key key;
    Map<Vertex, Edge> adjacent;
    Map<Vertex, Edge> adjacentUnmodifiable;
    Optional<Object> data;
    Optional<VertexPosition> position;

    /**
     * Constructs vertex.
     *
     * @param key Vertex unique key.
     */
    Vertex(Key key) {
        this(key, null, null, Collections.emptyMap());
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param data Vertex data.
     */
    Vertex(Key key, Object data) {
        this(key, null, data, Collections.emptyMap());
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param position Position.
     * @param data Vertex data.
     */
    Vertex(Key key, VertexPosition position, Object data) {
        this(key, position, data, Collections.emptyMap());
    }

    /**
     * Copy constructor. Copies everything but the edges.
     *
     * @param source Source to copy.
     */
    Vertex(Vertex source) {
        this(source.getKey(), source.getPosition().orElseGet(() -> null), source.getData().orElseGet(() -> null));
    }

    /**
     * Constructs vertex with given data.
     *
     * @param key Vertex unique key.
     * @param data Vertex data.
     * @param adjacent Adjacent edges.
     */
    Vertex(Key key, VertexPosition position, Object data, Map<Vertex, Edge> adjacent) {
        this.key = key;
        this.adjacent = new HashMap<>(adjacent);
        this.adjacentUnmodifiable = Collections.unmodifiableMap(this.adjacent);
        this.position = Optional.ofNullable(position);
        this.data = Optional.ofNullable(data);
    }

    /**
     * Collection of outgoing edges.
     *
     * @return Outgoing edges.
     */
    Collection<Edge> getOutgoingEdges() {
        return adjacentUnmodifiable.values();
    }

    /**
     * Number of outgoing edges.
     *
     * @return Number of outgoing edges.
     */
    int getOutgoingEdgesNumber() {
        return adjacent.size();
    }

    /**
     * Adjacent vertices.
     *
     * @return Adjacent vertices.
     */
    Set<Vertex> getAdjacentVertices() {
        return adjacentUnmodifiable.keySet();
    }

    /**
     * Adjacent vertices keys.
     *
     * @return Adjacent vertices keys.
     */
    Set<Key> getAdjacentVerticesKeys() {
        return adjacentUnmodifiable.keySet().stream().map(vertex -> vertex.getKey()).collect(Collectors.toSet());
    }

    /**
     * Removes edge to vertex.
     *
     * @param to To vertex.
     * @return Current vertex.
     */
    Vertex removeEdgeTo(Vertex to) {
        Edge existing = this.adjacent.get(to);
        if (existing != null) {
            existing.clear();
            this.adjacent.remove(to);
        }
        return this;
    }

    /**
     * Checks if the vertex has edge to other vertex.
     *
     * @param to To vertex.
     * @return Whether the vertex has edge to other vertex.
     */
    boolean hasEdge(Vertex to) {
        return adjacent.containsKey(to);
    }

    /**
     * Returns an edge to other vertex if it exists. Or returns null if it doesn't.
     *
     * @param to To vertex.
     * @return Edge to other vertex or null.
     */
    Edge getEdge(Vertex to) {
        return adjacent.get(to);
    }

    /**
     * Adds edge to other vertex with given cost if there was no edge. Updates cost if edge previously existed.
     *
     * @param to To vertex.
     * @param cost Cost.
     * @return Current vertex.
     */
    Vertex addEdge(Vertex to, double cost) {
        addEdge(to, cost, null);
        return this;
    }

    /**
     * Adds edge to other vertex with given cost if there was no edge. Updates cost if edge previously existed.
     *
     * @param to To vertex.
     * @param cost Cost.
     * @param data Vertex data. Is not updated if edge exists.
     * @return Current vertex.
     */
    Vertex addEdge(Vertex to, double cost, Object data) {
        if (equals(to)) {
            throw new IllegalStateException("Can't add an edge to itself.");
        }
        Edge edge = this.adjacent.get(to);
        if (edge == null) {
            edge = new Edge(this, to, cost, data);
            this.adjacent.put(to, edge);
        } else {
            edge.setCost(cost);
        }
        return this;
    }

    /**
     * Returns vertex key.
     *
     * @return Vertex key.
     */
    Key getKey() {
        return key;
    }

    /**
     * Returns position for vertex.
     *
     * @return Position for vertex.
     */
    Optional<VertexPosition> getPosition() {
        return position;
    }

    /**
     * Sets position for vertex.
     *
     * @param position Position.
     * @return Current vertex.
     */
    Vertex setPosition(VertexPosition position) {
        this.position = Optional.ofNullable(position);
        return this;
    }

    /**
     * Gets the data for vertex.
     *
     * @return Data for vertex.
     */
    Optional<Object> getData() {
        return data;
    }

    /**
     * Sets the data for vertex.
     *
     * @param data Data.
     * @return Current vertex.
     */
    Vertex setData(Object data) {
        this.data = Optional.ofNullable(data);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        return Objects.equals(this.key, other.key);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Vertex(this);
    }

    /**
     * Clears the vertex.
     */
    void clear() {
        // Clean up edges.
        for (Map.Entry<Vertex, Edge> vertexEntry : this.adjacent.entrySet()) {
            vertexEntry.getValue().clear();
        }
        this.adjacent.clear();
        this.data = Optional.empty();
        this.position = Optional.empty();
    }

    /**
     * Calculates a distance to given vertex.
     *
     * @param vertex Other vertex.
     * @return Distance as an {@link Optional} of double.
     */
    Optional<Double> distanceTo(Vertex vertex) {
        if (position.isPresent() && vertex.getPosition().isPresent()) {
            return position.get().distanceTo(vertex.getPosition().get());
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "{key:\"" + key + "\"}";
    }
}
