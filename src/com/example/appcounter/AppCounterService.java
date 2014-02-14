package com.example.appcounter;

import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * User: huhwook
 * Date: 2014. 1. 27.
 * Time: 오후 6:02
 */
public class AppCounterService extends Service {

    private final String LOG_NAME = AppCounterService.class.getSimpleName();

    private static Context mContext = null;
    private String recentComponentName;
    private ActivityManager mActivityManager;

    private static DatabaseAppCounter db;

    @Override
    public void onCreate() {
        super.onCreate();
        if(mContext != this) {
            db = DatabaseAppCounter.getInstance(this.getApplicationContext());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_NAME, "onStartCommand() ");

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RecentTaskInfo> info = mActivityManager.getRecentTasks(1, Intent.FLAG_ACTIVITY_NEW_TASK);
        if (info != null) {
            ActivityManager.RecentTaskInfo recent = info.get(0);
            Intent mIntent = recent.baseIntent;
            ComponentName name = mIntent.getComponent();

            if (name.getPackageName().equals(recentComponentName)) {
            } else {
                Log.d(LOG_NAME, "onStartCommand() - name.getPackageName(): " + name.getPackageName());
                recentComponentName = name.getPackageName();
                db.insertAppInfo(name.getPackageName());
                Log.d(LOG_NAME, "Sqlite Insert - packageName: " + recentComponentName);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_NAME, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
