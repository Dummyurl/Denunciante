package fragments;


import android.app.SearchManager;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chocoyo.labs.adapters.progress.AdapterProgress;
import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.MainActivity;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.adapter.ComplaintsAdapter;
import com.uca.apps.isi.taken.adapter.OfflineAdapter;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Complaint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeContainer;

    private  List<Complaint> complaints;
    private ComplaintsAdapter complaintsAdapter = null;
    private TextView searchText;

    public ComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        init(view);

        swipeContainer = view.findViewById(R.id.swipe_refresh);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorAccent,
                R.color.colorPrimary);
        setHasOptionsMenu(true);


        return view;
    }

    /**
     * @param view
     * Initialize the views in cardView
     */

    private void init(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AdapterProgress());

        try {
            getComplaints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this method is to get the complaint from the API
     */

    private void getComplaints() {


            Call<List<Complaint>> call = Api.instance().getComplaints(Remember.getString(getString(R.string.key_access_token),""));

            call.enqueue(new Callback<List<Complaint>>() {
                @Override
                public void onResponse(@NonNull Call<List<Complaint>> call, @NonNull Response<List<Complaint>> response) {

                    if (response.body() != null) {
                        complaints = response.body();
                        complaintsAdapter = new ComplaintsAdapter(response.body(),false);
                        recyclerView.setAdapter(complaintsAdapter);
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Complaint>> call, @NonNull Throwable t) {
                    Log.e("Error de conexión",t.getMessage());
                    recyclerView.setAdapter(new OfflineAdapter("Error, sin conexión a internet"));
                    swipeContainer.setRefreshing(false);
                }
            });


    }

    @Override
    public void onRefresh() {
        getComplaints();
        searchText.setText("");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getContext().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setHint(R.string.search_view_hint);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Boolean result = false;

        final List<Complaint> filteredModelList = filter(complaints, newText);
        if (filteredModelList.size() > 0) {
            complaintsAdapter.setFilter(filteredModelList);
            result = true;
        }

        return result;
    }

    private List<Complaint> filter(List<Complaint> models, String query) {

        query = query.toLowerCase();


        List<Complaint> filteredModelList = new ArrayList<>();

        if (models != null){

            if (!models.isEmpty()){
                for (Complaint model : models) {
                    final String text = model.getTitle().toLowerCase();
                    if (text.contains(query)) {
                        filteredModelList.add(model);
                    }
                }
                complaintsAdapter = new ComplaintsAdapter(filteredModelList,false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(complaintsAdapter);
                complaintsAdapter.notifyDataSetChanged();
            }

        }
        return filteredModelList;
    }
}