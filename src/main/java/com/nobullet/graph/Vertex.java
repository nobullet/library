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

    Collection<Edge> getOutgoingEdges() {
        return adjacentUnmodifiable.values();
    }

    int getOutgoingEdgesNumber() {
        return adjacent.size();
    }

    Set<Vertex> getAdjacentVertices() {
        return adjacentUnmodifiable.keySet();
    }

    Set<Key> getAdjacentVerticesKeys() {
        return adjacentUnmodifiable.keySet().stream().map(vertex -> vertex.getKey()).collect(Collectors.toSet());
    }

    Vertex removeEdgeTo(Vertex to) {
        Edge existing = this.adjacent.get(to);
        if (existing != null) {
            existing.clear();
            this.adjacent.remove(to);
        }
        return this;
    }

    boolean hasEdge(Vertex to) {
        return adjacent.containsKey(to);
    }

    Edge getEdge(Vertex to) {
        return adjacent.get(to);
    }

    Vertex addEdge(Vertex to, double cost) {
        addEdge(to, cost, null);
        return this;
    }

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

    Key getKey() {
        return key;
    }

    Optional<VertexPosition> getPosition() {
        return position;
    }

    void setPosition(VertexPosition position) {
        this.position = Optional.ofNullable(position);
    }

    Optional<Object> getData() {
        return data;
    }

    void setData(Object data) {
        this.data = Optional.ofNullable(data);
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
