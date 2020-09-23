package com.android.apps.earthquake;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EarthquakeMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int mMinimumMagnitude = 0;

    Map<String, Marker> mMarkers = new HashMap<>();
    List<Earthquake> mEarthquake;
    EarthquakeViewModel earthquakeViewModel;

    private SharedPreferences.OnSharedPreferenceChangeListener mPListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(PreferencesActivity.PREF_MIN_MAG.equals(key)) {
                //마커를 다시 배치한다.
                List<Earthquake> earthquakes
                        = earthquakeViewModel.getEarthquakes().getValue();

                if(earthquakes != null) setEarthquakeMarkers(earthquakes);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_map, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //이 Fragment의 EarthquakeViewModel을 얻는다.
        earthquakeViewModel = ViewModelProviders.of(getActivity())
                                        .get(EarthquakeViewModel.class);
        //EarthquakeViewModel에서 데이터를 가져오고 변경된 것이 있는지 관찰한다.
        earthquakeViewModel.getEarthquakes()
                .observe(this, new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(@Nullable List<Earthquake> earthquakes) {
                        //변경된 데이터베이스 결과로 UI를 업데이트한다.
                        if(earthquakes != null) {
                            setEarthquakeMarkers(earthquakes);
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //SupportMapFragment를 얻고 구글 지도 객체를 요청한다.
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                                            .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //사용자가 지진의 최소 진도 값을 변경할 때마다 마커를 갱신하는 리스너를 생성 및 등록
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(mPListener);
    }

    //지진 리스트에 저장된 지진 데이터를 반복 처리
    //각 지진 데이터의 마커를 생성하고 삭제
    public void setEarthquakeMarkers(List<Earthquake> earthquakes) {
        updateFromPreferences();

        mEarthquake = earthquakes;
        if(mMap == null || earthquakes == null) return;
        Map<String, Earthquake> newEarthquakes = new HashMap<>();

        //각 지진에 해당하는 마커를 추가한다.
        for(Earthquake earthquake : earthquakes) {
            if(earthquake.getMagnitude() >= mMinimumMagnitude) {
                newEarthquakes.put(earthquake.getId(), earthquake);

                if(!mMarkers.containsKey(earthquake.getId())) {
                    Location location = earthquake.getLocation();
                    Marker marker = mMap.addMarker(
                            new MarkerOptions()
                            .position(new LatLng(location.getLatitude(),
                                                location.getLongitude()))
                            .title("M:" + earthquake.getMagnitude()));
                    mMarkers.put(earthquake.getId(), marker);
                }
            }
        }

        //더 이상 보여주지 않을 지진 데이터의 마커를 모두 삭제한다.
        for(Iterator<String> iterator = mMarkers.keySet().iterator();
            iterator.hasNext();) {
            String earthquakeID = iterator.next();
            if(!newEarthquakes.containsKey(earthquakeID)) {
                mMarkers.get(earthquakeID).remove();
                iterator.remove();
            }
        }
    }

    private void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //공유 프레퍼런스에서 선택한 진도 이상의 지진 데이터를 보여주기 위함
        mMinimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
    }
}
