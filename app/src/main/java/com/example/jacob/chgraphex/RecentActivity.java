package com.example.jacob.chgraphex;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jacob.chgraphex.model.history.GItem;
import com.example.jacob.chgraphex.model.history.GItemListAdapter;
import com.example.jacob.chgraphex.model.history.GItemViewModel;

import java.util.List;

public class RecentActivity extends AppCompatActivity {

    private GItemViewModel mGItemViewModel;

    /**
     * On create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Set the image for the fab
        fab.setImageResource(R.drawable.ic_search_nodes);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecentActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final GItemListAdapter adapter = new GItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGItemViewModel = ViewModelProviders.of(this).get(GItemViewModel.class);

        mGItemViewModel.getAllGItems().observe(this, new Observer<List<GItem>>() {
            @Override
            public void onChanged(@Nullable final List<GItem> gItems) {
                // Update the cached copy of the gItems in the adapter.
                adapter.setGItems(gItems);
            }
        });
    }

    /**
     * On create options menu
     *
     * @param menu - menu to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recent, menu);
        return true;
    }

    /**
     * Handles options in menu
     *
     * @param item - MenuItem - selected item
     * @return - boolean - handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_history) {
            mGItemViewModel.deleteAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
