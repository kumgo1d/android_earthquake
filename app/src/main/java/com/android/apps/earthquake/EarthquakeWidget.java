package com.android.apps.earthquake;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class EarthquakeWidget extends AppWidgetProvider {
    public static final String NEW_QUAKE_BROADCAST =
            "com.paad.earthquake.NEW_QUAKE_BROADCAST"; //인텐트에서 사용할 액션 문자열

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(NEW_QUAKE_BROADCAST.equals(intent.getAction())) {
            PendingResult pendingResult = goAsync();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName earthquakeWidget =
                    new ComponentName(context, EarthquakeWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeWidget);

            updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
        }
    }

    //첫 위젯이 추가될 때
    //사용 가능한 모든 위젯이 비활성 상태에서 활성 상태가될 때 호출된다.
    @Override
    public void onEnabled(Context context) {
        final PendingResult pendingResult = goAsync();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName earthquakeWidget =
                new ComponentName(context, EarthquakeWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(earthquakeWidget);

        updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        PendingResult pendingResult = goAsync(); //위젯 업데이트를 비동기로 처리한다.
        updateAppWidgets(context, appWidgetManager,
                appWidgetIds, pendingResult);
    }

    //위젯을 업데이트할 백그라운드 스레드를 생성한다.
    static void updateAppWidgets(final Context context,
                                 final AppWidgetManager appWidgetManager,
                                 final int[] appWidgetIds,
                                 final PendingResult pendingResult) {
        Thread thread = new Thread() {
            public void run() {
                Earthquake lastEarthquake
                        = EarthquakeDatabaseAccessor.getInstance(context)
                        .earthquakeDAO().getLatestEarthquake();

                boolean lastEarthquakeExists = lastEarthquake != null;

                String lastMag = lastEarthquakeExists ?
                        String.valueOf(lastEarthquake.getMagnitude()) :
                        context.getString(R.string.widget_blank_magnitude);

                String details = lastEarthquakeExists ?
                        lastEarthquake.getDetails() :
                        context.getString(R.string.widget_blank_details);
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.quake_widget);

                views.setTextViewText(R.id.widget_magnitude, lastMag);
                views.setTextViewText(R.id.widget_details, details);

                //메인 액티비티를 열 펜딩 인텐트를 만든다.
                Intent intent = new Intent(context, EarthquakeMainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(context, 0, intent, 0);

                views.setOnClickPendingIntent(R.id.widget_magnitude, pendingIntent);
                views.setOnClickPendingIntent(R.id.widget_details, pendingIntent);

                //추가된 모든 위젯을 업데이트한다.
                for(int appWidgetId : appWidgetIds) {
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

                pendingResult.finish(); //비동기 작업이 완료됐다고 리시버에 알린다.
            }
        };
        thread.start();
    }
}
