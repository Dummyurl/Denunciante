package com.uca.apps.isi.taken.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tumblr.remember.Remember;

import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Category;
import com.uca.apps.isi.taken.models.Complaint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailsComplaint extends AppCompatActivity implements OnMapReadyCallback{

    public static final String COMPLAINT_ID = "complaintID";

    private int complaintID;
    private Complaint complaint;
    private SimpleDraweeView imageComplaint;
    private TextView title;
    private TextView description;
    private SimpleDraweeView iconCategory;
    private TextView categoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_complaint);

        //This method shows the button to back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        complaintID = getIntent().getIntExtra(COMPLAINT_ID,0);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void initViews() {

        imageComplaint = (SimpleDraweeView) findViewById(R.id.imageComplaint);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        iconCategory = (SimpleDraweeView) findViewById(R.id.iconCategory);
        categoryText = (TextView) findViewById(R.id.category);

        Call<Complaint> call = Api.instance().getComplaint(complaintID, Remember.getString(getString(R.string.key_access_token),""));

        call.enqueue(new Callback<Complaint>() {
            @Override
            public void onResponse(Call<Complaint> call, Response<Complaint> response) {

                if(response.body() != null){

                    try{
                        imageComplaint.setImageURI(response.body().getPictures().get(0).getUrl());
                        Category category = response.body().getCategory();
                        iconCategory.setImageURI(category.getIcon());
                        categoryText.setText(category.getName().toString());
                    }catch (Exception e){
                        Log.e(getString(R.string.error_message),e.getMessage());
                    }

                    complaint = response.body();
                    title.setText(response.body().getTitle());
                    description.setText(response.body().getDescription());

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapComplaint);
                    mapFragment.getMapAsync(DetailsComplaint.this);
                }
                else{
                    Log.e(getString(R.string.error_message),response.message());
                }
            }

            @Override
            public void onFailure(Call<Complaint> call, Throwable t) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(complaint.getLocation().getLat(), complaint.getLocation().getLng());
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.setMinZoomPreference(15);
    }
}
