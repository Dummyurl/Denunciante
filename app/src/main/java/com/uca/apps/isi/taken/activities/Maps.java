package com.uca.apps.isi.taken.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uca.apps.isi.taken.models.Location;

public class Maps extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PLACE_PICKER = 100;
    public static final String LOCATION = "location";

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        location = new Location();

        try {
            startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PLACE_PICKER && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(getApplicationContext(), data);

            location.setLat(place.getLatLng().latitude);
            location.setLng(place.getLatLng().longitude);

            getIntent().putExtra(LOCATION,location);
            setResult(RESULT_OK, getIntent());
            supportFinishAfterTransition();

        }else{

            supportFinishAfterTransition();
        }
    }
}
