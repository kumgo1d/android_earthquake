package com.android.apps.earthquake;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {

    //실제 업데이트는 부모 액티비티를 통해 소통하는 지진 뷰 모델이 수행한다.
    public interface OnListFragmentInteractionListener {
        void onListFragmentRefreshRequested();
    }

    private ArrayList<Earthquake> mEarthquakeData =
            new ArrayList<Earthquake>();
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeToRefreshView;
    private OnListFragmentInteractionListener mListener;
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =
            new EarthquakeRecyclerViewAdapter(mEarthquakeData);

    protected EarthquakeViewModel earthquakeViewModel;

    public EarthquakeListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list,
                container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mSwipeToRefreshView = view.findViewById(R.id.swiperefresh);
        return view;
    }

    @Override
    //지진 뷰 모델의 현재 인스턴스를 가져온다.
    //뷰 모델에서 반환된 라이브 데이터에 옵저버를 추가한다.
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //부모 액티비티의 지진 뷰 모델을 가져온다.
        earthquakeViewModel = ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);
        //뷰 모델의 데이터를 가져온다. 변경 내용이 있는지 확인한다.
        earthquakeViewModel.getEarthquakes()
                .observe(this, new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(@Nullable List<Earthquake> earthquakes) {
                        //뷰 모델이 변경되면 지진 List를 변경한다.
                        if(earthquakes != null) {
                            setEarthquakes(earthquakes);
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //리사이클러 뷰 어댑터를 설정한다.
        Context context = view.getContext();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mEarthquakeAdapter);

        //리프레시 리스너를 설정한다.
        mSwipeToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquakes();
            }
        });
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        for(Earthquake earthquake : earthquakes) {
            if(!mEarthquakeData.contains(earthquake)) {
                mEarthquakeData.add(earthquake);
                mEarthquakeAdapter.notifyItemInserted(mEarthquakeData.indexOf(earthquake));
            }
        }
        mSwipeToRefreshView.setRefreshing(false); //새로고침 표시가 나타나지 않게
    }

    protected void updateEarthquakes() {
        if(mListener != null) {
            mListener.onListFragmentRefreshRequested();
        }
    }
}
