package com.uca.apps.isi.taken.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.activities.DetailsComplaint;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Complaint;

import java.util.ArrayList;
import java.util.List;

import fragments.MyComplaintsFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {

    public boolean moreButtonVisibility = false;
    public static final int REQUEST_CODE = 2901;

    private List<Complaint> complaints;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Context context;
        public TextView title;
        public CardView complaint;
        public SimpleDraweeView imageComplaint;
        public TextView category;
        public TextView name;
        public ImageButton moreImageButton;

        public ViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            title = view.findViewById(R.id.title);
            complaint = view.findViewById(R.id.card_view);
            imageComplaint = view.findViewById(R.id.pictureCard);
            category = view.findViewById(R.id.category);
            name = view.findViewById(R.id.name);
            moreImageButton = view.findViewById(R.id.moreImageButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ComplaintsAdapter(List<Complaint> complaints, boolean visibility) {
        this.complaints = complaints;
        this.moreButtonVisibility = visibility;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ComplaintsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(parent.getContext(),view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Complaint complaint = complaints.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {

            if (moreButtonVisibility) {
                holder.moreImageButton.setVisibility(View.VISIBLE);

                holder.moreImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.moreImageButton, complaint);
                    }
                });

            } else {
                holder.moreImageButton.setVisibility(View.GONE);
            }

            holder.complaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    Intent intent = new Intent(view.getContext(), DetailsComplaint.class);

                    intent.putExtra(DetailsComplaint.COMPLAINT_ID, complaint.getId());
                    holder.context.startActivity(intent);


                }
            });

            holder.imageComplaint.setImageURI(complaint.getPictures().get(0).getUrl());
            holder.category.setText(complaint.getCategory().getName());
            holder.title.setText(complaint.getTitle());

        } catch (Exception e) {
            Log.e("ERROR_COMPLAINT: ", e.getMessage());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return complaints.size();
    }

    private void showPopupMenu(View view, Complaint complaint) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.more_options_complaint_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(complaint, view));
        popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {


        private View view;
        private Complaint complaint;

        MyMenuItemClickListener( Complaint complaint, View view) {

            this.complaint = complaint;
            this.view = view;

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.deleteComplaint:
                    showDeleteMessage(view, complaint);
                    break;
                default:
            }
            return false;
        }
    }

    private void showDeleteMessage(View view, final Complaint complaint) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(view.getContext().getString(R.string.delete_confirmation_title));
        builder.setMessage(view.getContext().getString(R.string.delete_confirmation_description));

        String positiveText = view.getContext().getString(R.string.positive_option_alert_dialog);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // this make http request to create an complaint
                        Call<ResponseBody> call = Api.instance().deleteComplaint(complaint.getId(), Remember.getString("access_token",""));
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                if (response.body() != null){
                                    Log.i("ERROR: ",response.message());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

                            }
                        });

                    }
                });

        String negativeText = view.getContext().getString(R.string.negative_option_alert_dialog);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Eliminar denuncia cancelada");
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void setFilter(List<Complaint> complaintsOnFilter) {
        ArrayList<Complaint> complaintsToFilter = new ArrayList<>();
        complaintsToFilter.addAll(complaintsOnFilter);
        notifyDataSetChanged();
    }
}