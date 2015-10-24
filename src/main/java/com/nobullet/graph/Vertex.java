package com.nobullet.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Graph vertex. Not thread safe.
 */
public class Vertex {

    private final String name;
    private final Map<Vertex, Edge> adjacent;
    private final Map<Vertex, Edge> adjacentUnmodifieble;
    private Object data;

    public Vertex(String name) {
        this(name, Collections.emptyMap(), null);
    }

    public Vertex(String name, Object data) {
        this(name, Collections.emptyMap(), data);
    }

    Vertex(String name, Map<Vertex, Edge> adjacent, Object data) {
        this.name = name;
        this.adjacent = new HashMap<>(adjacent);
        this.adjacentUnmodifieble = Collections.unmodifiableMap(this.adjacent);
        this.data = data;
    }

    public Collection<Edge> getOutgoingEdges() {
        return adjacentUnmodifieble.values();
    }

    public int getOutgoingEdgesNumber() {
        return adjacent.size();
    }

    public Set<Vertex> getAdjacentVertices() {
        return adjacentUnmodifieble.keySet();
    }

    public Edge removeAdjacent(Vertex to) {
        return this.adjacent.remove(to);
    }

    public boolean removeEdge(Edge edge) {
        return this.adjacent.remove(edge.getTo()) != null;
    }

    public boolean hasEdge(Vertex to) {
        return adjacent.containsKey(to);
    }
    
    public Edge getEdge(Vertex to) {
        return adjacent.get(to);
    }
    
    public Edge addEdge(Vertex to, double weight) {
        if (this.equals(to)) {
            return null;
        }
        Edge edge = this.adjacent.get(to);
        if (edge == null) {
            edge = new Edge(this, to, weight);
            this.adjacent.put(to, edge);
        }
        edge.setWeight(weight);
        return edge;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        return Objects.equals(this.name, other.name);
    }

    public void clear() {
        this.adjacent.clear();
        this.data = null;
    }

    @Override
    public String toString() {
        return "Vertex{" + "name=" + name + '}';
    }
}
