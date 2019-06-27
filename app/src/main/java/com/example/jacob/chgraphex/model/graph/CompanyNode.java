package com.example.jacob.chgraphex.model.graph;

import com.example.jacob.chgraphex.model.searchResults.CompanyItem;
import com.example.jacob.chgraphex.model.searchResults.Item;

import java.io.Serializable;

public class CompanyNode extends Node implements Serializable {

    /**
     * Constructor for company node
     * @param item - Item
     */
    public CompanyNode(Item item) {
        super(item);
        setType(0);
    }
}
