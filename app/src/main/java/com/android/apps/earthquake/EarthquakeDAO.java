package com.android.apps.earthquake;

import android.database.Cursor;

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

    @Query("SELECT mId as _id, " +
            "mDetails as suggest_text_1, " +
            "mId as suggest_intent_data_id " +
            "FROM earthquake " +
            "WHERE mDetails LIKE :query " +
            "ORDER BY mDate DESC")
    //매개변수로 전달받은 부분 쿼리를 기반으로 하는 검색 제안 커서를 반환하는 새 쿼리 메서드
    public Cursor generateSearchSuggestions(String query);

    @Query("SELECT * " +
            "FROM earthquake " +
            "WHERE mDetails LIKE :query " +
            "ORDER BY mDate DESC")
    //검색 결과 전체를 LiveData로 반환
    public LiveData<List<Earthquake>> searchEarthquakes(String query);

    @Query("SELECT * " +
            "FROM earthquake " +
            "WHERE mId = :id " +
            "LIMIT 1")
    //검색 제안을 선택하는 것을 처리하기 위한 메서드
    public LiveData<Earthquake> getEarthquake(String id);

    //onRunJob이 실행되는 백그라운드 스레드로부터 호출될 때
    //동기식으로 모든 지진 데이터를 반환
    @Query("SELECT * FROM earthquake ORDER BY mDate DESC")
    List<Earthquake> loadAllEarthquakesBlocking();
}
