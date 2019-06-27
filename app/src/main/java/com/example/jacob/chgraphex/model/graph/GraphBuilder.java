package com.example.jacob.chgraphex.model.graph;

import android.util.Log;

import com.example.jacob.chgraphex.apiRelated.CHRequest;
import com.example.jacob.chgraphex.model.searchResults.*;

import java.util.ArrayList;
import java.util.List;

public class GraphBuilder {

    private Graph graph;

    private Item initial;

    /**
     * Default Constructor
     */
    public GraphBuilder(){}

    /**
     * Constructor
     * @param initial - Item (root)
     */
    public GraphBuilder(Item initial) {
        this.initial = initial;
    }

    /**
     * Method for making the graph
     * @return Graph
     */
    public Graph generateGraph() {

        try {
            if (initial instanceof CompanyItem) {
                graph = this.generateGraphCompany();
            } else if (initial instanceof OfficerItem) {
                graph = this.generateGraphOfficer();
            }
        } catch (Exception e){
            Log.d("ERROR", "ERROR");
            e.printStackTrace();
        }

        return graph;
    }

    /**
     * Method for generating graph based off company starting node
     * @return Graph
     * @throws Exception
     */
    private Graph generateGraphCompany() throws Exception{

        //Look up officers in company

        CHRequest c = new CHRequest();

        List<OfficerItem> officers = c.getOfficers((CompanyItem) initial);

        Graph g = new Graph();

        //Add nodes
        List<Node> nodes = new ArrayList<>();

        //List of edges
        List<Edge> edges = new ArrayList<>();

        //Add root node (company)
        CompanyNode root = new CompanyNode(initial);
        root.setRoot(true);
        nodes.add(root);

        //Loop over officers and add nodes
        for (OfficerItem o: officers) {
            OfficerNode n = new OfficerNode(o);
            nodes.add(n);
            edges.add(new Edge(root,n,o.getLink()));
        }

        g.setNodes(nodes);
        g.setEdges(edges);

        return g;
    }

    /**
     * Method for generating graph based off officer starting node
     * @return Graph
     * @throws Exception
     */
    private Graph generateGraphOfficer() throws Exception {

        //Look up appointments
        CHRequest x = new CHRequest();

        List<CompanyItem> companies = x.getCompanies((OfficerItem) initial);

        Graph g = new Graph();

        //Add nodes
        List<Node> nodes = new ArrayList<>();

        List<Edge> edges = new ArrayList<>();

        //Add root node (officer)
        OfficerNode root = new OfficerNode(initial);
        root.setRoot(true);
        nodes.add(root);

        //Loop over companies and add nodes
        for (CompanyItem c: companies) {
            CompanyNode n = new CompanyNode(c);
            nodes.add(n);
            edges.add(new Edge(root,n,c.getLink()));
        }

        g.setNodes(nodes);
        g.setEdges(edges);

        return g;
    }

    /**
     * Method for expanding the graph
     * @param g - Graph (starting graph)
     * @param n - Node - node to expand
     * @return Graph (expanded)
     */
    public Graph expandGraph(Graph g, Node n){
        graph = g;
        List<Node> extraNodes = graph.getNodes();
        List<Edge> extraEdges = graph.getEdges();
        CHRequest ch = new CHRequest();
        try{
            switch (n.getType()){
                case 0:
                    //Type is company node
                    List<OfficerItem> officers = ch.getOfficers((CompanyItem) n.getItem());

                    for (OfficerItem i: officers){
                        OfficerNode q = new OfficerNode(i);
                        boolean found = false;
                        Node fNode = null;
                        for (Node a: graph.getNodes()){
                            if (a.getItem().getId().equals(i.getId())){
                                found = true;
                                fNode = a;
                                break;
                            }
                        }
                        if (!found) { //Officer is new node
                            extraNodes.add(q);
                            extraEdges.add(new Edge(n, q,i.getLink()));
                        }
                        else { //Node already exists
                            //Check whether edge exists
                            boolean eFound = false;
                            for (Edge e: graph.getEdges()){
                                Node n1 = e.getStart();
                                Node n2 = e.getEnd();
                                if (
                                        (fNode.getItem().getId().equals(n1.getItem().getId()) && n.getItem().getId().equals(n2.getItem().getId())) ||
                                                (fNode.getItem().getId().equals(n2.getItem().getId()) && n.getItem().getId().equals(n1.getItem().getId()))

                                ) {
                                    eFound = true;
                                    break;
                                }
                            }
                            if (!eFound){
                                extraEdges.add(new Edge(n, fNode,i.getLink()));
                            }
                        }
                    }

                    break;
                case 1:
                    //Type is officer node
                    List<CompanyItem> companies = ch.getCompanies((OfficerItem) n.getItem());

                    for (CompanyItem i: companies){
                        CompanyNode q = new CompanyNode(i);
                        boolean found = false;
                        Node fNode = null;
                        for (Node a: graph.getNodes()){
                            if (a.getItem().getId().equals(i.getId())){
                                found = true;
                                fNode = a;
                                break;
                            }
                        }
                        if (!found) { //Company is new node
                            extraNodes.add(q);
                            extraEdges.add(new Edge(n, q,i.getLink()));
                        }
                        else { //Node already exists
                            //Check whether edge exists
                            boolean eFound = false;
                            for (Edge e: graph.getEdges()){
                                Node n1 = e.getStart();
                                Node n2 = e.getEnd();
                                if (
                                        (fNode.getItem().getId().equals(n1.getItem().getId()) && n.getItem().getId().equals(n2.getItem().getId())) ||
                                                (fNode.getItem().getId().equals(n2.getItem().getId()) && n.getItem().getId().equals(n1.getItem().getId()))

                                ) {
                                    eFound = true;
                                    break;
                                }
                            }
                            if (!eFound){
                                extraEdges.add(new Edge(n, fNode,i.getLink()));
                            }

                        }
                    }

                    break;
            }
            n.setExploded(true);
            graph.setNodes(extraNodes);
            graph.setEdges(extraEdges);
        } catch (Exception e){
            e.printStackTrace();
        }

        return graph;
    }

}
