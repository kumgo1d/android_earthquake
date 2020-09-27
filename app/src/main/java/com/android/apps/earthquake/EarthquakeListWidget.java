package com.android.apps.earthquake;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

//위젯의 활성화 및 업데이트에 표준 패턴을 구현한다.
public class EarthquakeListWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        PendingResult pendingResult = goAsync();
        updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }

    @Override
    public void onEnabled(Context context) {
        final PendingResult pendingResult = goAsync();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName earthquakeListWidget =
                new ComponentName(context, EarthquakeListWidget.class);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(earthquakeListWidget);

        updateAppWidgets(context, appWidgetManager, appWidgetIds, pendingResult);
    }

    @Override
    //업데이트 요청 인텐트를 리스닝한다.
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);

        if(EarthquakeWidget.NEW_QUAKE_BROADCAST.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName earthquakeListWidget =
                    new ComponentName(context, EarthquakeListWidget.class);
            int[] appWidgetIds =
                    appWidgetManager.getAppWidgetIds(earthquakeListWidget);

            //EarthquakeListWidget에 업데이트돼야 한다는 것을 알린다.
            final PendingResult pendingResult = goAsync();
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
                    R.id.widget_list_view);
        }
    }

    static void updateAppWidgets(final Context context,
                                 final AppWidgetManager appWidgetManager,
                                 final int[] appWidgetIds,
                                 final PendingResult pendingResult) {
        Thread thread = new Thread() {
            public void run() {
                for(int appWidgetId: appWidgetIds) {
                    //리스트 뷰에 보이는 뷰들을 제공하는
                    //지진 원격 뷰 서비스를 시작하는 인텐트를 생성한다.
                    Intent intent =
                            new Intent(context, EarthquakeRemoteViewsService.class);

                    //앱 위젯 ID를 인텐트 엑스트라에 추가한다.
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId);
                    //앱 위젯 레이아웃의 RemoteViews 인스턴스를 생성한다.
                    RemoteViews views
                            = new RemoteViews(context.getPackageName(), R.layout.quake_collection_widget);
                    //RemoteViews 어댑터를 사용하도록 RemoteViews 객체를 설정한다.
                    views.setRemoteAdapter(R.id.widget_list_view, intent);
                    //컬렉션에 항목이 없을 때 빈 뷰가 표시된다.
                    views.setEmptyView(R.id.widget_list_view,
                            R.id.widget_empty_text);

                    //변경된 원격 뷰를 사용해
                    //위젯을 업데이트하라고 앱 위젯 매니저에 알린다.
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
                if(pendingResult != null) {
                    pendingResult.finish();
                }
            }
        };
        thread.start();
    }
}
