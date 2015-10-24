package com.nobullet.graph;

/**
 * Graph visitor.
 */
public interface GraphVisitor {

    /**
     * Invoked when algorithms visit a vertex in graph.
     *
     * @param vertex Vertex.
     * @param order Topological order of the vertex when possible.
     */
    void visit(Vertex vertex, int order);
}
