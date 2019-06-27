package com.example.jacob.chgraphex.model.graph;

import com.example.jacob.chgraphex.model.graph.Node;
import com.example.jacob.chgraphex.model.searchResults.Item;
import com.example.jacob.chgraphex.model.searchResults.OfficerItem;

import java.io.Serializable;

public class OfficerNode extends Node implements Serializable {

    /**
     * Constructor for officer node
     * @param item - Item
     */
    public OfficerNode(Item item) {
        super(item);
        setType(1);
    }
}
