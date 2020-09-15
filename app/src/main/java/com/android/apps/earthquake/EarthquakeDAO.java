package com.android.apps.earthquake;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EarthquakeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //만약 같은 항목이 있으면 기존 항목을 교체
    public void insertEarthquakes(List<Earthquake> earthquakes); //List 추가

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEarthquake(Earthquake earthquake); //단일 추가

    @Delete
    public void deleteEarthquake(Earthquake earthquake);

    @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
    public LiveData<List<Earthquake>> loadAllEarthquakes();
}
