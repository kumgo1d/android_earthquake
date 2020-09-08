package com.android.apps.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EarthquakeMainActivity extends AppCompatActivity {

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    EarthquakeListFragment mEarthquakeListFragment;

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

        Date now = Calendar.getInstance().getTime();
        List<Earthquake> dummyQuakes = new ArrayList<>(0);
        dummyQuakes.add(new Earthquake("0", now, "San Jose", null, 7.3, null));
        dummyQuakes.add(new Earthquake("1", now, "LA", null, 5.3, null));

        mEarthquakeListFragment.setEarthquakes(dummyQuakes);
    }
}