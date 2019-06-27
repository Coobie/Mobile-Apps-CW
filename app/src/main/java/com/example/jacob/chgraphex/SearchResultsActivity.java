package com.example.jacob.chgraphex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.jacob.chgraphex.model.searchResults.Item;
import com.example.jacob.chgraphex.model.searchResults.ItemAdapter;

import java.util.List;


public class SearchResultsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tv;

    /**
     * On create method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        getSupportActionBar().setTitle("Results for: "+getIntent().getExtras().getString("search_term"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Item> items = (List<Item>) getIntent().getSerializableExtra("all_items");

        tv = findViewById(R.id.search_results_title);
        String tx = items.size() + " Results found";
        if (items.isEmpty()){tx += " :(";}
        tv.setText(tx);
        ItemAdapter itemArrayAdapter = new ItemAdapter(R.layout.layout_individual_item, items);
        recyclerView = (RecyclerView) findViewById(R.id.view_all_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

    }

    /**
     * Support nav up method
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
