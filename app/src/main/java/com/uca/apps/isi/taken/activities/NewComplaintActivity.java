package com.uca.apps.isi.taken.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chocoyo.labs.adapters.progress.AdapterProgress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.adapter.NewComplaintViewPagerAdapter;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Complaint;
import com.uca.apps.isi.taken.models.ComplaintCreate;
import com.uca.apps.isi.taken.models.Location;
import com.uca.apps.isi.taken.models.Picture;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewComplaintActivity extends AppCompatActivity {

    public static final String CATEGORY_COMPLAINT = "categories";
    public static final int REQUEST_CODE = 2906;

    private StorageReference mStorageRef;
    private Button maps;
    private EditText title;
    private EditText description;
    private Button create;
    private int complaintId;
    private int categoryId;
    private int position;
    private Location location;


    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private Activity activity;


    ArrayList<Uri> selectedUriList = new ArrayList<>();

    RecyclerView recyclerView;
    AlertDialog publication;
    View view;
    TextView messagePost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_new_complaint);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryId = getIntent().getIntExtra(CATEGORY_COMPLAINT,0);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        activity = this;
        initViews();
        initActions();
        chooseLocation();
        position = 1;

        requestPermissions();

        //imageComplaint = (ImageView) findViewById(R.id.imageComplaint);

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new NewComplaintViewPagerAdapter(NewComplaintActivity.this, selectedUriList);

        viewPager.setAdapter(pagerAdapter);

    }
    public void handleAction(View view) {
        uploadImages();
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

    /**
     * To init views on variables
     */
    private void initViews() {
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        create = (Button) findViewById(R.id.create);
        maps = (Button) findViewById(R.id.maps);
    }

    /**
     * Logic button action
     */
    private void initActions() {
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedUriList.isEmpty()){

                    if(location != null){
                        create();
                    }else{
                        callMapActivity();
                        Toast.makeText(getApplicationContext(),getString(R.string.activity_new_complaint_empty_location)
                                ,Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),getString(R.string.activity_new_complaint_not_select_images)
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is for create new complaints in the API
     */

    private void create() {

        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.activity_new_complaint__message_empty_title), Toast.LENGTH_LONG).show();
        } else if (description.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.activity_new_complaint_message_empty_description), Toast.LENGTH_LONG).show();
        } else {

            publication = buildDialog();
            recyclerView.setAdapter(new AdapterProgress());
            publication.show();
            publication.setCancelable(false);
            messagePost.setText(getString(R.string.activity_new_complaint_starting_publication));

            // This instance new complaint with data
            ComplaintCreate complaint = new ComplaintCreate();
            complaint.setTitle(title.getText().toString());
            complaint.setDescription(description.getText().toString());
            complaint.setCategoryId(categoryId);
            complaint.setCreatedAt("2017-11-08t23:00:02.892Z");
            complaint.setLocation(location);

            // this make http request to create an complaint
            Call<Complaint> call = Api.instance().createComplaint(complaint, Remember.getString(getString(R.string.key_access_token),""));

            call.enqueue(new Callback<Complaint>() {
                @Override
                public void onResponse(@NonNull Call<Complaint> call, @NonNull Response<Complaint> response) {
                    if (response.body() != null) {

                        Complaint complaintResult  = response.body();
                        complaintId = complaintResult.getId();
                        assert complaintResult != null;

                        messagePost.setText(String.format(getString(R.string.activity_new_complaint_sending_message),
                                position, selectedUriList.size()));
                        uploadModelPicture();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<Complaint> call, @NonNull Throwable t) {

                    Log.e(getString(R.string.error_message),t.getMessage());
                }
            });

        }
    }

    /**
     * This method defines which image will be published in firebase
     */

    private void uploadModelPicture(){

        for (int i = 0; i < selectedUriList.size();i++){
            upload(selectedUriList.get(i));
        }
    }

    private void requestPermissions() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage(getString(R.string.without_permits))
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

    }

    private void upload(Uri file) {

        String nameImage = RandomStringUtils.randomAlphanumeric(8);

        final StorageReference riversRef = mStorageRef.child(
                String.format(getString(R.string.activity_new_complaint_route_image),nameImage));

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                addImage(position,taskSnapshot.getMetadata().getDownloadUrl().toString());
                messagePost.setText(String.format(getString(R.string.activity_new_complaint_sending_message),
                        position, selectedUriList.size()));
                position++;

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteComplaint();
                        publication.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.error_publishing_complaint), Toast.LENGTH_LONG).show();
                        Log.e(getString(R.string.error_message),e.getMessage());
                    }
                });
    }

    /**
     * This method allows to publish a model Picture in the api
     **/

    private void addImage(int position,String urlImage){

        if(position <= selectedUriList.size()){
            Picture picture = new Picture();
            picture.setTitle("");
            picture.setComplaintId(complaintId);
            picture.setUrl(urlImage);

            Call<Picture> call = Api.instance().createPicture(picture, Remember.getString(getString(R.string.key_access_token),""));

            call.enqueue(new Callback<Picture>() {
                @Override
                public void onResponse(@NonNull Call<Picture> call, @NonNull Response<Picture> response) {

                }

                @Override
                public void onFailure(@NonNull Call<Picture> call, @NonNull Throwable t) {

                    Log.i(getString(R.id.errorMessage),t.getMessage());
                }
            });

        }

        if (position == selectedUriList.size()){
            setResult(RESULT_OK, getIntent());
            supportFinishAfterTransition();
        }
    }

    /**
     * This method allows to select the images to be published using the library
     **/

    private void uploadImages(){

        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(this)
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(ArrayList<Uri> uriList) {
                        selectedUriList = uriList;
                        pagerAdapter = new NewComplaintViewPagerAdapter(NewComplaintActivity.this, selectedUriList);

                        viewPager.setAdapter(pagerAdapter);
                        pagerAdapter.notifyDataSetChanged();
                    }
                })
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText(getString(R.string.activity_new_complaint_complete_select_images))
                .setEmptySelectionText(getString(R.string.activity_new_complaint_empty_select_images))
                .setSelectedUriList(selectedUriList)
                .setSelectMaxCount(6)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());



    }

    private void deleteComplaint(){

        Call<ResponseBody> call = Api.instance().deleteComplaint(complaintId, Remember.getString(getString(R.string.key_access_token),""));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null){
                    Log.i(getString(R.string.message_delete_complaint),response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(getString(R.string.error_message),t.getMessage());
            }
        });
    }

    private AlertDialog buildDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.category_post, null);
        recyclerView = view.findViewById(R.id.shippingProgress);
        messagePost = view.findViewById(R.id.messagePost);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        builder.setNegativeButton(getString(R.string.activity_new_complaint_title_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Toast.makeText(getApplicationContext(),getString(R.string.activity_new_complaint_message_cancel),
                        Toast.LENGTH_LONG).show();
            }
        });
        builder.setView(view);
        builder.setTitle(getString(R.string.activity_new_complaint_title_dialog));

        return builder.create();
    }

    private void chooseLocation() {

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMapActivity();
            }
        });
    }

    private void callMapActivity(){
        location = new Location();
        Intent intent = new Intent(getApplicationContext(), Maps.class);
        activity.startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            location = (Location) data.getExtras().getSerializable(Maps.LOCATION);
        }
    }
}