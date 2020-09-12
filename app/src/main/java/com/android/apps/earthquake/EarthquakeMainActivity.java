package com.android.apps.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EarthquakeMainActivity extends AppCompatActivity
    implements EarthquakeListFragment.OnListFragmentInteractionListener {

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    EarthquakeListFragment mEarthquakeListFragment;
    EarthquakeViewModel earthquakeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_main);

        FragmentManager fm = getSupportFragmentManager();

        //구성 변경이 생긴 후에 안드로이드는 이전에 추가된 프래그먼트를 자동으로 추가한다.
        //따라서 자동으로 다시 시작된 경우가 아닐 때만 추가해야 한다.
        if(savedInstanceState == null) {
            FragmentTransaction ft = fm.beginTransaction();
            mEarthquakeListFragment = new EarthquakeListFragment();
            ft.add(R.id.main_activity_frame,
                    mEarthquakeListFragment, TAG_LIST_FRAGMENT);
            ft.commitNow();
        } else {
            mEarthquakeListFragment =
                    (EarthquakeListFragment)fm.findFragmentByTag(TAG_LIST_FRAGMENT);
        }

        //이 액티비티의 지진 뷰 모델을 가져온다.
        earthquakeViewModel = ViewModelProviders.of(this).get(EarthquakeViewModel.class);
    }

    @Override
    public void onListFragmentRefreshRequested() {
        updateEarthquakes();
    }

    private void updateEarthquakes() {
        //USGS 피드로부터 가져온 지진 데이터로 뷰 모델을 변경하도록 요청한다.
        earthquakeViewModel.loadEarthquakes();
    }
}