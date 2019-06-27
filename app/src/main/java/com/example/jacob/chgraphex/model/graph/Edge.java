package com.example.jacob.chgraphex.model.graph;

import java.io.Serializable;

public class Edge implements Serializable {

    private Node start;
    private Node end;
    private String link;

    /**
     * Constructor for edge
     * @param start - Node - starting
     * @param end - Node - ending
     */
    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Constructor for the edge
     * @param start - Node - starting
     * @param end - Node - ending
     * @param link - String - linking text
     */
    public Edge(Node start, Node end, String link) {
        this.start = start;
        this.end = end;
        this.link = link;
    }

    /**
     * Getter for start node
     * @return start Node
     */
    public Node getStart() {
        return start;
    }

    /**
     * Setter for start node
     * @param start - Node
     */
    public void setStart(Node start) {
        this.start = start;
    }

    /**
     * Getter for ending node
     * @return Node - end
     */
    public Node getEnd() {
        return end;
    }

    /**
     * Setter for ending node
     * @param end - Node
     */
    public void setEnd(Node end) {
        this.end = end;
    }

    /**
     * Check contains node
     * @param n Node to check for
     * @return boolean true -> contains
     */
    public boolean contains(Node n){
        if ( this.getEnd() == n || this.getStart() == n){
            return true;
        }
        return false;
    }

    /**
     * Getter for link
     * @return String name of the linking
     */
    public String getLink() {
        return link;
    }

    /**
     * Setter for link
     * @param link String name of the linking
     */
    public void setLink(String link) {
        this.link = link;
    }
}
