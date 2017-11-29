package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chocoyo.labs.adapters.progress.AdapterProgress;
import com.tumblr.remember.Remember;
import com.uca.apps.isi.taken.R;
import com.uca.apps.isi.taken.adapter.ComplaintsAdapter;
import com.uca.apps.isi.taken.adapter.OfflineAdapter;
import com.uca.apps.isi.taken.api.Api;
import com.uca.apps.isi.taken.models.Complaint;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyComplaintsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    public MyComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        init(view);

        swipeContainer = view.findViewById(R.id.my_swipe_refresh);

        swipeContainer.setOnRefreshListener(this);

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

        getComplaint();
    }

    /**
     * this method is to get the complaint from the API
     */

    private void getComplaint() {

        Call<List<Complaint>> call = Api.instance().getMyComplaints(Remember.getString(getString(R.string.key_access_token), ""));
        call.enqueue(new Callback<List<Complaint>>() {
            @Override
            public void onResponse(Call<List<Complaint>> call, Response<List<Complaint>> response) {
                if (response.body() != null) {

                    ComplaintsAdapter complaintsAdapter = new ComplaintsAdapter(response.body(),true);
                    recyclerView.setAdapter(complaintsAdapter);
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Complaint>> call, Throwable t) {
                Log.e(getString(R.string.error_message), t.getMessage());
                recyclerView.setAdapter(new OfflineAdapter(getString(R.string.message_without_connection)));
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getComplaint();
    }
}
