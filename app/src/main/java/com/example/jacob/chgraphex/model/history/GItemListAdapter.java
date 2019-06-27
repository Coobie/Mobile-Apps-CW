package com.example.jacob.chgraphex.model.history;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jacob.chgraphex.GraphViewActivity;
import com.example.jacob.chgraphex.R;
import com.example.jacob.chgraphex.model.graph.Graph;
import com.example.jacob.chgraphex.model.graph.GraphBuilder;
import com.example.jacob.chgraphex.model.searchResults.CompanyItem;
import com.example.jacob.chgraphex.model.searchResults.Item;
import com.example.jacob.chgraphex.model.searchResults.OfficerItem;

import java.io.Serializable;
import java.util.List;

public class GItemListAdapter extends RecyclerView.Adapter<GItemListAdapter.GItemViewHolder> {

    private Context context;

    class GItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView gItemItemView;
        private TextView itemId;
        private TextView itemType;
        private ImageView image;
        private ConstraintLayout lay;

        private GItemViewHolder(View itemView) {
            super(itemView);
            gItemItemView = itemView.findViewById(R.id.history_item_title);
            image = itemView.findViewById(R.id.history_image);
            lay = itemView.findViewById(R.id.history_item_l);
            itemId = itemView.findViewById(R.id.history_item_id);
            itemType = itemView.findViewById(R.id.history_item_type);
            lay.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.setClicked(getLayoutPosition());
            runner.execute();
        }
    }

    private final LayoutInflater mInflater;
    private List<GItem> mGItems; // Cached copy of gItems

    public GItemListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public GItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        context = itemView.getContext();
        return new GItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GItemViewHolder holder, int position) {
        if (mGItems != null) {
            GItem current = mGItems.get(position);
            String t = "";
            holder.image.setImageResource(R.drawable.ic_info_outline_black);
            switch (current.getType()) {
                case 0:
                    holder.image.setImageResource(R.drawable.ic_company_icon);
                    t = "Company";
                    break;
                case 1:
                    holder.image.setImageResource(R.drawable.ic_officer_icon);
                    t = "Officer";
                    break;
            }
            holder.itemType.setText("("+t+")");
            holder.itemId.setText("ID: "+current.getmGItem());
            holder.gItemItemView.setText(current.getTitle());
        } else {
            // Covers the case of data not being ready yet.
            holder.gItemItemView.setText("No GItem");
        }
    }

    public void setGItems(List<GItem> gItems) {
        mGItems = gItems;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mGItems has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mGItems != null)
            return mGItems.size();
        else return 0;
    }


    /**
     * Background processing class for api request
     */
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private int clicked;

        /**
         * Setter for clicked
         *
         * @param clicked
         */
        protected void setClicked(int clicked) {
            this.clicked = clicked;
        }

        /**
         * Background task
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {

            //Send data to graph view
            GItem i = mGItems.get(clicked);

            Item item;

            switch (i.getType()) {
                case 0:
                    item = new CompanyItem(i.getTitle(), i.getmGItem());
                    item.setAddress(i.getAddress());
                    break;
                case 1:
                    item = new OfficerItem(i.getTitle(), i.getmGItem());
                    item.setAddress(i.getAddress());
                    break;
                default:
                        return null;
            }

            GraphBuilder gb = new GraphBuilder(item);
            Graph g = gb.generateGraph();

            //Send to graph activity

            Intent intent = new Intent(context, GraphViewActivity.class);
            intent.putExtra("graph", (Serializable) g);
            context.startActivity(intent);

            return "";
        }
    }
}
