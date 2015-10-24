package com.nobullet.graph;

/**
 * Edge between two vertices.
 */
public class Edge {

    private Vertex from;
    private Vertex to;
    private double weight;
    private Object data;

    public Edge(Vertex from, Vertex to) {
        this(from, to, 1.0D, null);
    }

    public Edge(Vertex from, Vertex to, double weight) {
        this(from, to, weight, null);
    }

    public Edge(Vertex from, Vertex to, double weight, Object data) {
        this.from = from;
        this.to = to;
        this.weight = weight;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
