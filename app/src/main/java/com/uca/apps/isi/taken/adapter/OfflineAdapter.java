package com.uca.apps.isi.taken.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uca.apps.isi.taken.R;

/**
 * Created by Mario Arce on 19/11/2017.
 */

public class OfflineAdapter extends RecyclerView.Adapter<OfflineAdapter.ViewHolder> {

    private String errorMessage;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView message;

        public ViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.errorMessage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OfflineAdapter(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OfflineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.without_connection, parent, false);
        // set the view's size, margins, paddings and layout parameters
        OfflineAdapter.ViewHolder viewholder = new OfflineAdapter.ViewHolder(view);
        return viewholder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(OfflineAdapter.ViewHolder holder, int position) {

        holder.message.setText(errorMessage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}
