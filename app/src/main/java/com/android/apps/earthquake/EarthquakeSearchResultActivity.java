package com.android.apps.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeSearchResultActivity extends AppCompatActivity {
    private ArrayList<Earthquake> mEarthquakes = new ArrayList<>();
    private EarthquakeRecyclerViewAdapter mEarthquakeAdapter =
            new EarthquakeRecyclerViewAdapter(mEarthquakes);

    MutableLiveData<String> searchQuery;
    LiveData<List<Earthquake>> searchResults;
    LiveData<Earthquake> selectedSearchSuggestion;
    //선택된 검색 제안의 ID를 라이브 데이터에 저장
    MutableLiveData<String> selectedSearchSuggestionId;

    private void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    //콘텐츠 프로바이더 URI에서 추출된 지진 ID를 기준으로 라이브 데이터 수정
    private void setSelectedSearchSuggestion(Uri dataString) {
        String id = dataString.getPathSegments().get(1);
        selectedSearchSuggestionId.setValue(id);
    }

    final Observer<Earthquake> selectedSearchSuggestionObserver
            = selectedSearchSuggestion -> {
        //선택된 검색 제안에 일치되도록 검색 쿼리를 변경한다.
        if(selectedSearchSuggestion != null) {
            setSearchQuery(selectedSearchSuggestion.getDetails());
        }
    };

    //리사이클러 뷰가 보여주는 지진 List를 변경한다.
    private final Observer<List<Earthquake>> searchQueryResultObserver =
            updatedEarthquakes -> {
                //변경된 검색 쿼리 결과로 UI를 변경한다.
                mEarthquakes.clear();
                if(updatedEarthquakes != null) {
                    mEarthquakes.addAll(updatedEarthquakes);
                }
                mEarthquakeAdapter.notifyDataSetChanged();
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_search_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //검색 결과를 보여줄 리사이클러 뷰에 지진 리사이클러 뷰 어댑터를 적용
        RecyclerView recyclerView = findViewById(R.id.search_result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mEarthquakeAdapter);

        //검색 쿼리 라이브 데이터를 초기화한다.
        searchQuery = new MutableLiveData<>();
        searchQuery.setValue(null);

        //검색 쿼리 라이브 데이터를 검색 결과 라이브 데이터에 연결한다.
        //검색 쿼리가 변경되면 데이터베이스에
        //쿼리를 수행해 검색 결과가 변경되도록 변환 Map을 구성한다.
        searchResults = Transformations.switchMap(searchQuery,
                query -> EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDAO()
                        .searchEarthquakes("%" + query + "%"));

        //검색 결과 라이브 데이터의 변경 내용을 관찰한다.
        searchResults.observe(EarthquakeSearchResultActivity.this,
                searchQueryResultObserver);

        //선택된 검색 제안 ID 라이브 데이터를 초기화한다.
        selectedSearchSuggestionId = new MutableLiveData<>();
        selectedSearchSuggestionId.setValue(null);

        //선택된 검색 제안 ID 라이브 데이터를 연결한다.
        //선택된 검색 제안의 ID가 변경되면 데이터베이스에 쿼리를 수행해
        //해당 지진 데이터를 반환하는 라이브 데이터를 변경하도록 변환 Map을 구성한다.
        selectedSearchSuggestion = Transformations.switchMap(selectedSearchSuggestionId,
                id -> EarthquakeDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .earthquakeDAO()
                        .getEarthquake(id));

        //액티비티가 검색 제안에 따라 시작되면
        if(Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            selectedSearchSuggestion.observe(this,
                    selectedSearchSuggestionObserver);
            setSelectedSearchSuggestion(getIntent().getData());
        } else {
            //액티비티가 검색 쿼리로부터 시작되면
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }

        //검색어를 추출하고 검색 쿼리 라이브 데이터를 변경한다.
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        setSearchQuery(query);
    }

    @Override
    //새 검색 요청 인텐트를 받으면 검색 쿼리를 변경하도록 하는 메서드
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //검색 액티비티가 존재하고, 다른 검색이 수행 중이면
        //시작 인텐트를 새로 받은 검색 인텐트로 설정하고
        //새 검색을 수행한다.
        setIntent(intent);

        if(Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            //선택된 검색 제안 ID를 변경한다.
            setSelectedSearchSuggestion(getIntent().getData());
        } else {
            //검색 쿼리를 추출하고 searchQuery 라이브 데이터를 변경한다.
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            setSearchQuery(query);
        }

        //검색 쿼리를 추출하고 searchQuery 라이브 데이터를 변경한다.
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        setSearchQuery(query);
    }
}