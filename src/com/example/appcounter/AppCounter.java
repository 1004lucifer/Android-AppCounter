package com.example.appcounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AppCounter extends Activity {

    private static final String LOG_NAME = AppCounter.class.getSimpleName();

    private DatabaseAppCounter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        db = DatabaseAppCounter.getInstance(this.getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCountTime();
        setAppCountList();
        setDaemonStatus();
    }

    private void setAppCountList() {
        List<DatabaseAppCounter.AppInfo> appCountList = db.getAppCountList();

        List<DatabaseAppCounter.AppInfo> installedAppCountList = new ArrayList<DatabaseAppCounter.AppInfo>();
        // check Deleted App
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installedPackage = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (DatabaseAppCounter.AppInfo appInfo : appCountList) {
            for (ApplicationInfo applicationInfo : installedPackage) {
                if (appInfo.getPackageName().equals(applicationInfo.packageName)) {
                    installedAppCountList.add(appInfo);
                }
            }
        }

        AppListAdapter appListAdapter = new AppListAdapter(this, R.layout.appcount_list_view, installedAppCountList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(appListAdapter);
    }

    private void setCountTime() {
        TextView countTime = (TextView) findViewById(R.id.countTime);

        DatabaseAppCounter.AppInfo firstAppInfo = db.getOrderByApp("ASC");
        DatabaseAppCounter.AppInfo lastAppInfo = db.getOrderByApp("DESC");

        if (firstAppInfo != null && firstAppInfo.getExecuteTime() != null &&
                lastAppInfo != null && lastAppInfo.getExecuteTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String firstTime = sdf.format(firstAppInfo.getExecuteTime());
            String lastTime = sdf.format(lastAppInfo.getExecuteTime());
            countTime.setText(firstTime + " ~ " + lastTime);
        } else {
            countTime.setText(R.string.countTime_noData);
        }

    }

    private void setDaemonStatus() {
        boolean status = ServiceCaller.getInstance().isCall(this.getApplicationContext());

        Button startBtn = (Button) findViewById(R.id.start);
        startBtn.setEnabled(status == false);
        Button endBtn = (Button) findViewById(R.id.end);
        endBtn.setEnabled(status);
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_start_title)
                        .setMessage(R.string.alert_start_message)
                        .setNegativeButton(R.string.alert_start_negativeButton, null)
                        .setPositiveButton(R.string.alert_start_positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ServiceCaller.getInstance().startCall(AppCounter.this.getApplicationContext());
                                setDaemonStatus();
                            }
                        })
                        .show();
                break;
            case R.id.end:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_stop_title)
                        .setMessage(R.string.alert_stop_message)
                        .setNegativeButton(R.string.alert_stop_negativeButton, null)
                        .setPositiveButton(R.string.alert_stop_positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ServiceCaller.getInstance().stopCall(AppCounter.this.getApplicationContext());
                                setDaemonStatus();
                            }
                        })
                        .show();
                break;
        }
    }
}
