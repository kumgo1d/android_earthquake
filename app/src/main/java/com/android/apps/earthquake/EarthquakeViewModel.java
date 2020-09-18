package com.android.apps.earthquake;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String TAG = "EarthquakeUpdate";

    //지진 데이터가 저장된 List를 참조하는 MutableLiveData
    private LiveData<List<Earthquake>> earthquakes;

    public EarthquakeViewModel(Application application) {
        super(application);
    }

    //지진 LiveData가 list에 이미 채워졌는지 확인
    //채워지지 않았다면 피드에서 데이터를 가져온다.
    public LiveData<List<Earthquake>> getEarthquakes() {
        if(earthquakes == null) {
            earthquakes = EarthquakeDatabaseAccessor
                    .getInstance(getApplication())
                    .earthquakeDAO()
                    .loadAllEarthquakes();

            loadEarthquakes();
        }
        return earthquakes;
    }

    //피드로부터 지진 데이터를 가져와서 비동기로 로드한다.
    //백그라운드에서 데이터 파싱
    public void loadEarthquakes() {
        //Async 대신 scheduleUpdateJob을 호출해 작업을 스케줄링한다.
        EarthquakeUpdateJobService.scheduleUpdateJob(getApplication());
    }
}
