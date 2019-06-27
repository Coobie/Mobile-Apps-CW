package com.example.jacob.chgraphex;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jacob.chgraphex.apiRelated.CHRequest;
import com.example.jacob.chgraphex.model.searchResults.Item;

import java.io.Serializable;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    String test = "";
    Boolean broken = false;
    EditText search;

    /**
     * On create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button button = findViewById(R.id.search_button);
        button.setText("Search " + getResources().getString(R.string.companies));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
            }
        });

        Switch sw = findViewById(R.id.switch_search_type);

        sw.getTrackDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        sw.getThumbDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        getSupportActionBar().setTitle("Search for Company");

        final TextView sO = findViewById(R.id.search_off);
        final TextView sC = findViewById(R.id.search_comp);
        sC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        search = findViewById(R.id.search_text);
        search.requestFocus();
        search.setHint("Search Companies");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    button.setText("Search " + getResources().getString(R.string.officers));
                    sO.setTypeface(null, Typeface.BOLD);
                    sC.setTypeface(null, Typeface.NORMAL);
                    sO.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    sC.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
                    getSupportActionBar().setTitle("Search for Officer");
                    search.setHint("Search Officers");

                } else {
                    button.setText("Search " + getResources().getString(R.string.companies));
                    sC.setTypeface(null, Typeface.BOLD);
                    sO.setTypeface(null, Typeface.NORMAL);
                    sC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    sO.setTextColor(getResources().getColor(android.R.color.tab_indicator_text));
                    getSupportActionBar().setTitle("Search for Company");
                    search.setHint("Search Companies");
                }
            }
        });

        if (broken) {
            Toast.makeText(this, "Internet connection is required", Toast.LENGTH_LONG).show();
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
    }

    /**
     * Support nav up method
     *
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        closeKeyboard();
        return true;
    }

    /**
     * Class for async background task
     */
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        /**
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {

            EditText search_terms = (EditText) findViewById(R.id.search_text);
            //Log.d("tested",search_terms.getText().toString());

            Switch sw = (Switch) findViewById(R.id.switch_search_type);
            int status = 0;
            if (sw.isChecked()) {
                status = 1;
            }

            List<Item> t;
            try {
                CHRequest c = new CHRequest();
                t = c.newSearch(search_terms.getText().toString(), status);

            } catch (Exception e) {
                Log.d("ERROR", "ERROR");
                e.printStackTrace();
                t = null;
                Toast.makeText(getApplicationContext(), "Please try again later", Toast.LENGTH_LONG).show();
            }
            if (t != null) {
                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                intent.putExtra("all_items", (Serializable) t);
                intent.putExtra("search_term", search_terms.getText().toString());
                startActivity(intent);
                return "";
            } else {
                broken = true;
                return "";
            }
        }
    }
}

