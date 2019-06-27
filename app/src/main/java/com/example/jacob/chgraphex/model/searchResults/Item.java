package com.example.jacob.chgraphex.model.searchResults;

import java.io.Serializable;

public abstract class Item implements Serializable {

    private String title = "";
    private String id = "";
    private String address = "";
    private String link = "";

    /**
     * Constructor for item
     * @param title - String
     */
    public Item(String title) {
        this.title = title;
    }

    /**
     * Getter for title
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for title
     * @param title - String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for id
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for address
     * @return String
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter for address
     * @param address - String
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
