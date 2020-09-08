package com.android.apps.earthquake;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {

    private ArrayList<Earthquake> mEarthquakeData =
            new ArrayList<Earthquake>();
    private RecyclerView mRecyclerView;
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =
            new EarthquakeRecyclerViewAdapter(mEarthquakeData);

    public EarthquakeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list,
                container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //리사이클러 뷰 어댑터를 설정한다.
        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mEarthquakeAdapter);
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        for(Earthquake earthquake : earthquakes) {
            if(!mEarthquakeData.contains(earthquake)) {
                mEarthquakeData.add(earthquake);
                mEarthquakeAdapter.notifyItemInserted(mEarthquakeData.indexOf(earthquake));
            }
        }
    }
}
