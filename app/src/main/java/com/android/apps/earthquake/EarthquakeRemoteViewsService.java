package com.android.apps.earthquake;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class EarthquakeRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new EarthquakeRemoteViewsFactory(this);
    }

    class EarthquakeRemoteViewsFactory implements RemoteViewsFactory {
        private Context mContext;
        private List<Earthquake> mEarthquakes;

        public EarthquakeRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mEarthquakes = EarthquakeDatabaseAccessor.getInstance(mContext)
                    .earthquakeDAO().loadAllEarthquakesBlocking();
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if(mEarthquakes == null) return 0;
            return mEarthquakes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if(mEarthquakes != null) {
                //요청된 지진을 추출한다.
                Earthquake earthquake = mEarthquakes.get(position);

                //보여줄 값을 추출한다.
                String id = earthquake.getId();
                String magnitude = String.valueOf(earthquake.getMagnitude());
                String details = earthquake.getDetails();

                //새 원격 뷰 객체를 만들고
                //이를 사용해 리스트 내 각 지진을 나타내기 위한 레이아웃을 배치한다.
                RemoteViews rv = new RemoteViews(mContext.getPackageName(),
                        R.layout.quake_widget);
                rv.setTextViewText(R.id.widget_magnitude, magnitude);
                rv.setTextViewText(R.id.widget_details, details);

                //메인 액티비티를 열 펜딩 인텐트를 생성한다.
                Intent intent = new Intent(mContext, EarthquakeMainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(mContext, 0, intent, 0);

                rv.setOnClickPendingIntent(R.id.widget_magnitude, pendingIntent);
                rv.setOnClickPendingIntent(R.id.widget_details, pendingIntent);

                return rv;
            } else {
                return null;
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if(mEarthquakes == null) return position;
            return mEarthquakes.get(position).getDate().getTime();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
