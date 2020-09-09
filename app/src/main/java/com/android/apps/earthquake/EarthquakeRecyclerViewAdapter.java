package com.android.apps.earthquake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.apps.earthquake.databinding.ListItemEarthquakeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter extends
        RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {

    //Earthquake list data
    private final List<Earthquake> mEarthquakes;

    //시간과 경도 데이터 형식
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);
    private static final NumberFormat MAGNITUDE_FORMAT =
            new DecimalFormat("0.0");

    //생성자에서 list data 초기화
    public EarthquakeRecyclerViewAdapter(List<Earthquake> earthquakeData) {
        mEarthquakes = earthquakeData;
    }

    @Override
    //바인딩 클래스 인스턴스를 생성
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemEarthquakeBinding binding = ListItemEarthquakeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    //바인딩 수행
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Earthquake earthquake = mEarthquakes.get(position);
        holder.binding.setEarthquake(earthquake);
        holder.binding.executePendingBindings(); //바인딩 즉시 수행
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //바인딩 클래스 인스턴스를 인자로 받아 형식 변수를 한 번 초기화
        public final ListItemEarthquakeBinding binding;

        public ViewHolder(ListItemEarthquakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setTimeformat(TIME_FORMAT);
            binding.setMagnitudeformat(MAGNITUDE_FORMAT);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + binding.details.getText() +"'";
        }
    }
}
