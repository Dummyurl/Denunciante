package com.uca.apps.isi.taken;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chocoyo.labs.adapters.progress.AdapterProgress;
import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.activities.Profile;
import com.uca.apps.isi.taken.activities.SingInActivity;
import com.uca.apps.isi.taken.adapter.CategoryAdapter;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Category;
import com.uca.apps.isi.taken.models.Complaint;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.ComplaintsFragment;
import fragments.MyComplaintsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE = 2905;
    LayoutInflater inflater;
    View child;
    CategoryAdapter categoryAdapter;
    RecyclerView recyclerView;
    Activity activity;
    Dialog categoriesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isAuthenticated();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = this;
        refreshComplaints();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                child = inflater.inflate(R.layout.categories_view,null);
                recyclerView = child.findViewById(R.id.listView_context_menu);
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));

                recyclerView.setAdapter(new AdapterProgress());
                categoriesDialog = new Dialog(activity);
                categoriesDialog.setContentView(child);
                categoriesDialog.show();


                Call<List<Category>> call = Api.instance().getCategories(
                        Remember.getString(getString(R.string.key_access_token),""));
                call.enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {

                        if(response.body() != null){
                            categoryAdapter = new CategoryAdapter(response.body(),activity);
                            recyclerView.setAdapter(categoryAdapter);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                        categoriesDialog.dismiss();
                        Toast.makeText(getApplicationContext(),getString(R.string.message_without_connection),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAvatarEvent(navigationView.getHeaderView(0));
    }


    /**
     * Initialize SignInActivity and finish the current activity
     */

    private void backToSignInActivity()
    {
        Intent intent = new Intent(getApplicationContext(), SingInActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Validate if the access_token is empty and call backToSignInActivity
     */
    private void isAuthenticated()
    {
        if(Remember.getString(getString(R.string.key_access_token),"").isEmpty())
        {
            backToSignInActivity();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment;
        Class fragmentClass =null;

        switch(item.getItemId()) {
            case R.id.nav_complaints:
                fragmentClass = ComplaintsFragment.class;
                break;
            case R.id.nav_my_complaints:
                fragmentClass = MyComplaintsFragment.class;
                break;
            case R.id.nav_log_out:
                Remember.clear();
                backToSignInActivity();

                break;
            default:
                fragmentClass = MyComplaintsFragment.class;
        }

        try {
            if(fragmentClass!=null)
            {
                fragment = (Fragment) fragmentClass.newInstance();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        categoriesDialog.dismiss();
        refreshComplaints();
    }

    private void refreshComplaints(){
        Fragment fragment;
        Class fragmentClass;
        fragmentClass = ComplaintsFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initAvatarEvent(View view) {
        CircleImageView entry =  view.findViewById(R.id.profile_image);
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });


    }

}