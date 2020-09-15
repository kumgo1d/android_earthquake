package com.android.apps.earthquake;

import android.content.Context;

import androidx.room.Room;

//데이터베이스와 상호작용
public class EarthquakeDatabaseAccessor {
    private static EarthquakeDatabase EarthquakeDatabaseInstance;
    private static final String EARTHQUAKE_DB_NAME = "earthquake_db";

    private EarthquakeDatabaseAccessor() {}

    public static EarthquakeDatabase getInstance(Context context) {
        //싱글톤
        if(EarthquakeDatabaseInstance == null) {
            //SQLite 데이터베이스 생성
            //해당 Room 데이터베이스 인스턴스를 반환
            EarthquakeDatabaseInstance = Room.databaseBuilder(context,
                    EarthquakeDatabase.class, EARTHQUAKE_DB_NAME).build();
        }
        return EarthquakeDatabaseInstance;
    }
}
