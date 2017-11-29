package com.uca.apps.isi.taken.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.uca.apps.isi.taken.MainActivity;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.activities.DetailsComplaint;
import com.uca.apps.isi.taken.activities.NewComplaintActivity;
import com.uca.apps.isi.taken.models.Category;
import com.uca.apps.isi.taken.models.Complaint;

import java.util.List;

/**
 * Created by Mario Arce on 10/11/2017.
 */

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public SimpleDraweeView iconCategory;
        public TextView category;
        public LinearLayout itemCategory;

        public ViewHolder(View view) {
            super(view);
            iconCategory = view.findViewById(R.id.iconCategory);
            category = view.findViewById(R.id.categoryText);
            itemCategory = view.findViewById(R.id.itemMenu);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryAdapter(List<Category> categories, Activity activity) {
        this.categories = categories;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CategoryAdapter.ViewHolder viewholder = new CategoryAdapter.ViewHolder(view);
        return viewholder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {

        final Category category = categories.get(position);
        holder.category.setText(category.getName());
        holder.iconCategory.setImageURI(category.getIcon());
        holder.itemCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(),NewComplaintActivity.class);
                intent.putExtra(NewComplaintActivity.CATEGORY_COMPLAINT,category.getId());
                activity.startActivityForResult(intent, MainActivity.REQUEST_CODE);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categories.size();
    }
}
