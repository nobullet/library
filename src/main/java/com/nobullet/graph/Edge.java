package com.nobullet.graph;

import java.util.Objects;
import java.util.Optional;

/**
 * Edge between two vertices.
 */
class Edge {

    Vertex from;
    Vertex to;
    double cost;
    Optional<Object> data;

    Edge(Vertex from, Vertex to) {
        this(from, to, 1.0D, null);
    }

    Edge(Vertex from, Vertex to, double cost) {
        this(from, to, cost, null);
    }

    Edge(Vertex from, Vertex to, double cost, Object data) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.data = Optional.ofNullable(data);
    }

    Vertex getFrom() {
        return from;
    }

    Vertex getTo() {
        return to;
    }

    double getCost() {
        return cost;
    }

    void setCost(double weight) {
        this.cost = weight;
    }

    Optional<Object> getData() {
        return data;
    }

    void setData(Object data) {
        this.data = Optional.ofNullable(data);
    }
    
    void clear() {
        this.from = null;
        this.to = null;
        this.data = null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.from);
        hash = 17 * hash + Objects.hashCode(this.to);
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.cost) ^ (Double.doubleToLongBits(this.cost) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (Double.doubleToLongBits(this.cost) != Double.doubleToLongBits(other.cost)) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        return Objects.equals(this.to, other.to);
    }
}
