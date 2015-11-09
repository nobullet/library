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

    /**
     * Constructs edge for given vertices.
     *
     * @param from From vertex.
     * @param to To vertex.
     */
    Edge(Vertex from, Vertex to) {
        this(from, to, 1.0D, null);
    }

    /**
     * Constructs edge for given vertices.
     *
     * @param from From vertex.
     * @param to To vertex.
     * @param cost Cost.
     */
    Edge(Vertex from, Vertex to, double cost) {
        this(from, to, cost, null);
    }

    /**
     * Constructs edge for given vertices.
     *
     * @param from From vertex.
     * @param to To vertex.
     * @param cost Cost.
     * @param data Edge data.
     */
    Edge(Vertex from, Vertex to, double cost, Object data) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.data = Optional.ofNullable(data);
    }

    /**
     * Returns from vertex.
     *
     * @return From vertex.
     */
    Vertex getFrom() {
        return from;
    }

    /**
     * Returns to vertex.
     *
     * @return To vertex.
     */
    Vertex getTo() {
        return to;
    }

    /**
     * Returns cost of the edge.
     *
     * @return
     */
    double getCost() {
        return cost;
    }

    /**
     * Sets the edge cost.
     *
     * @param cost Cost for edge.
     */
    void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Returns data.
     *
     * @return data.
     */
    Optional<Object> getData() {
        return data;
    }

    /**
     * Sets the data. Data can be null.
     *
     * @param data Data. Nulllable.
     */
    void setData(Object data) {
        this.data = Optional.ofNullable(data);
    }

    /**
     * Clears the vertex.
     */
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
