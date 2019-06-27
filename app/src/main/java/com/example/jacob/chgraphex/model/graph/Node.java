package com.example.jacob.chgraphex.model.graph;

import com.example.jacob.chgraphex.model.searchResults.Item;

import java.io.Serializable;

public abstract class Node implements Serializable {

    private Item item;

    private int x;

    private int y;

    private int type;

    private boolean root = false;

    private boolean exploded = false;

    /**
     * Constructor
     * @param item - Item held in node
     */
    public Node(Item item) {
        this.item = item;
    }

    /**
     * Getter for the item
     * @return Item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Setter for the item
     * @param item - Item
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Getter for x
     * @return int x - coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Setter for x
     * @param x - int - coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Getter for y
     * @return int y - coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for y
     * @param y - int - coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Checks if node is root
     * @return boolean - true=root
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Sets node as root
     * @param root boolean - true=root
     */
    public void setRoot(boolean root) {
        this.root = root;
        this.setExploded(root);
    }

    /**
     * Getter for type
     * @return int - 0=company, 1=officer
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for type
     * @param type - 0=company, 1=officer
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Check node is exploded
     * @return boolean - true = exploded
     */
    public boolean isExploded() {
        return exploded;
    }

    /**
     * Set node is exploded
     * @param exploded - boolean exploded?
     */
    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
