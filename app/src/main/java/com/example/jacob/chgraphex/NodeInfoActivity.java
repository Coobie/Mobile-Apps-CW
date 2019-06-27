package com.example.jacob.chgraphex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.jacob.chgraphex.model.graph.Edge;
import com.example.jacob.chgraphex.model.graph.Node;
import com.example.jacob.chgraphex.model.searchResults.OfficerItem;

import java.util.ArrayList;
import java.util.List;

public class NodeInfoActivity extends AppCompatActivity {

    private Node node;
    private List<Edge> edges;

    /**
     * On create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_node_info);
        this.node = (Node) getIntent().getSerializableExtra("node_item");
        this.edges = (List<Edge>) getIntent().getSerializableExtra("edges");

        TextView title = (TextView) findViewById(R.id.node_title);
        title.setText(node.getItem().getTitle());

        TextView info = (TextView) findViewById(R.id.node_info);
        info.setText(textForView(node));

        TextView links = findViewById(R.id.node_links);
        links.setText(this.linksForNode(node));

        TextView type = findViewById(R.id.node_type);
        switch (node.getType()) {
            case 0:
                type.setText("(Company)");
                break;
            case 1:
                type.setText("(Officer)");
                break;
        }

        TextView linksTitle = findViewById(R.id.node_links_title);
        switch (node.getType()) {
            case 0: //Company
                linksTitle.setText("Officers");
                break;
            case 1:
                linksTitle.setText("Companies");
                break;
        }

    }

    /**
     *
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Info about node
     *
     * @param n - Node
     * @return String - text to display
     */
    private String textForView(Node n) {
        String r = "";
        if (n.getType() == 0) {
            r += "Company Number: ";
        } else if (n.getType() == 1) {
            r += "Officer Number: ";
        }
        r += n.getItem().getId() + "\n\n";

        if (n.getType() == 1){
            OfficerItem i = (OfficerItem) n.getItem();
            if (!i.getNationality().equals("")){
                r += "Nationality: "+i.getNationality()+"\n";
            }
        }

        if (n.getItem().getAddress() != "") {
            r += "Address: \n" + n.getItem().getAddress();
        }

        return r;
    }

    private String linksForNode(Node n){
        String r = "";
        List<Edge> edges = new ArrayList<>();
        for (Edge e : this.edges) {
            if (e.getStart().getItem().getId().equals(n.getItem().getId()) || e.getEnd().getItem().getId().equals(n.getItem().getId())) {
                edges.add(e);
            }
        }

        switch (n.getType()) {
            case 0: //Company
                for (Edge e : edges) {
                    r += "- ";
                    if (e.getStart().getItem().getId().equals(n.getItem().getId())){
                        r += e.getEnd().getItem().getTitle();
                    }else if (e.getEnd().getItem().getId().equals(n.getItem().getId())){
                        r += e.getStart().getItem().getTitle();
                    }
                    r += " ("+e.getLink()+")\n";
                }
                break;

            case 1: //Officer
                for (Edge e : edges) {
                    r += "- ";
                    if (e.getStart().getItem().getId().equals(n.getItem().getId())){
                        r += e.getEnd().getItem().getTitle();
                    }else if (e.getEnd().getItem().getId().equals(n.getItem().getId())){
                        r += e.getStart().getItem().getTitle();
                    }
                    r += " ("+e.getLink()+")\n";
                }
                break;
        }

        return r;
    }
}
