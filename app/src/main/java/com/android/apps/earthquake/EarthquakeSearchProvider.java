package com.android.apps.earthquake;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//검색 뷰의 검색 제안을 생성하는데 사용할 콘텐트 프로바이더
public class EarthquakeSearchProvider extends ContentProvider {
    private static final int SEARCH_SUGGESTIONS = 1;

    //UriMather 객체를 할당. 검색 요청을 파악한다.
    //서로 다른 URI 패턴이 사용된 요청을 처리하기 위한 메서드
    private static final UriMatcher uriMather;
    static {
        uriMather = new UriMatcher(UriMatcher.NO_MATCH);
        uriMather.addURI("com.android.provider.earthquake",
                SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGESTIONS);
        uriMather.addURI("com.android.provider.earthquake",
                SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGESTIONS);
        uriMather.addURI("com.android.provider.earthquake",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH_SUGGESTIONS);
        uriMather.addURI("com.android.provider.earthquake",
                SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH_SUGGESTIONS);
    }

    @Override
    public boolean onCreate() {
        //Room 데이터베이스의 인스턴스 참조
        EarthquakeDatabaseAccessor.getInstance(getContext().getApplicationContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        //받은 URI가 검색 제안의 요청 형태인지 확인한 후 그렇다면 현재의 부분 쿼리를 사용하여 Room 쿼리
        if(uriMather.match(uri) == SEARCH_SUGGESTIONS) {
            String searchQuery = "%" + uri.getLastPathSegment() + "%";

            EarthquakeDAO earthquakeDAO = EarthquakeDatabaseAccessor
                                            .getInstance(getContext().getApplicationContext())
                                            .earthquakeDAO();

            Cursor c = earthquakeDAO.generateSearchSuggestions(searchQuery);

            //검색 제안의 커서 반환
            return c;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMather.match(uri)) {
            //검색 제안의 MIME 유형 반환
            case SEARCH_SUGGESTIONS:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
