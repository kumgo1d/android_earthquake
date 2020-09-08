package com.android.apps.earthquake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EarthquakeRecyclerViewAdapter extends
        RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {

    private final List<Earthquake> mEarthquakes;

    public EarthquakeRecyclerViewAdapter(List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_earthquake, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.earthquake = mEarthquakes.get(position);
        holder.detailsView.setText(mEarthquakes.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mEarthquakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View parentView;
        public final TextView detailsView;
        public Earthquake earthquake;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            detailsView = (TextView)
                    itemView.findViewById(R.id.list_item_earthquake_details);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + detailsView.getText() +"'";
        }
    }
}
