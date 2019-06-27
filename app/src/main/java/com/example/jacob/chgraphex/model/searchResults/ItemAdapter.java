package com.example.jacob.chgraphex.model.searchResults;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jacob.chgraphex.GraphViewActivity;
import com.example.jacob.chgraphex.R;
import com.example.jacob.chgraphex.RecentActivity;
import com.example.jacob.chgraphex.SearchResultsActivity;
import com.example.jacob.chgraphex.model.graph.Graph;
import com.example.jacob.chgraphex.model.graph.GraphBuilder;
import com.example.jacob.chgraphex.model.graph.OfficerNode;
import com.example.jacob.chgraphex.model.history.GItem;
import com.example.jacob.chgraphex.model.history.GItemViewModel;

import java.io.Serializable;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private int listItemLayout;
    private List<Item> itemList;
    private Context context;

    /**
     * Constructor
     * @param layoutId - id of layout
     * @param itemList - list of Item to show
     */
    public ItemAdapter(int layoutId, List<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    /**
     * Get the size of the list
     * @return int - size of list
     */
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    /**
     * specify the row layout file and click for each row
     * @param parent - view
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        context = view.getContext();
        return myViewHolder;
    }

    /**
     * load data in each row element
     * @param holder
     * @param listPosition
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getTitle());
        holder.itemId.setText("ID: "+itemList.get(listPosition).getId());

        if (itemList.get(listPosition) instanceof CompanyItem) {
            holder.itemIcon.setImageResource(R.drawable.ic_company_icon);
        } else if (itemList.get(listPosition) instanceof OfficerItem) {
            holder.itemIcon.setImageResource(R.drawable.ic_officer_icon);
        }
    }

    /**
     * Static inner class to initialize the views of rows
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        private TextView itemId;
        private ImageView itemIcon;

        /**
         * Constructor
         * @param itemView
         */
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.row_item);
            itemId = (TextView) itemView.findViewById(R.id.row_item_id);
            itemIcon = itemView.findViewById(R.id.row_item_icon);
        }

        /**
         * On click for recycler view
         * @param view
         */
        @Override
        public void onClick(View view) {
            //Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());

            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.setClicked(getLayoutPosition());
            runner.execute();
        }
    }

    /**
     * Background processing class for api request
     */
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private int clicked;

        /**
         * Setter for clicked
         * @param clicked
         */
        protected void setClicked(int clicked){
            this.clicked = clicked;
        }

        /**
         * Background task
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {

            //Send data to graph view

            GraphBuilder gb = new GraphBuilder(itemList.get(clicked));
            Graph g = gb.generateGraph();

            //Send to graph activity

            Intent intent = new Intent(context, GraphViewActivity.class);
            intent.putExtra("graph", (Serializable) g);
            context.startActivity(intent);

            return "";
        }
    }
}

