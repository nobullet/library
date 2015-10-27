package com.nobullet.graph;

/**
 * Edge between two vertices.
 */
public class Edge {

    Vertex from;
    Vertex to;
    double cost;
    Object data;

    public Edge(Vertex from, Vertex to) {
        this(from, to, 1.0D, null);
    }

    public Edge(Vertex from, Vertex to, double cost) {
        this(from, to, cost, null);
    }

    public Edge(Vertex from, Vertex to, double cost, Object data) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.data = data;
    }

    public Vertex getFrom() {
        return from;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public Vertex getTo() {
        return to;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double weight) {
        this.cost = weight;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public void clear() {
        this.from = null;
        this.to = null;
        this.data = null;
    }
}
