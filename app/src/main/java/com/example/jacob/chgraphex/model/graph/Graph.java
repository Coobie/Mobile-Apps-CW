package com.example.jacob.chgraphex.model.graph;

import java.io.Serializable;
import java.util.List;

public class Graph implements Serializable {

    private List<Node> nodes;
    private List<Edge> edges;

    /**
     * Default constructor
     */
    public Graph() {
    }

    /**
     * Getter for nodes
     * @return list of Node - nodes in graph
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Setter for nodes
     * @param nodes - list Node
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * Getter for edges
     * @return list Edge - edges in the graph
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Setter for Edges
     * @param edges - list of Edges
     */
    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
