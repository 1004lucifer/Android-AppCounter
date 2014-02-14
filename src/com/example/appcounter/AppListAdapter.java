package com.example.appcounter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * User: huhwook
 * Date: 2014. 2. 7.
 * Time: 오후 5:04
 */
public class AppListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int mLayout;
    private List<DatabaseAppCounter.AppInfo> mList;
    private Context mContext;
    private PackageManager mPackageManager;
    private static final String LOG_NAME = AppListAdapter.class.getSimpleName();

    public AppListAdapter(Context context, int layout, List<DatabaseAppCounter.AppInfo> appCountList) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = layout;
        mList = appCountList;
        mPackageManager = context.getPackageManager();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DatabaseAppCounter.AppInfo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        DatabaseAppCounter.AppInfo appInfo = mList.get(position);
        if (view == null) {
            view = mInflater.inflate(mLayout, parent, false);
        }
        TextView appIndex = (TextView) view.findViewById(R.id.appIndex);
        ImageView appImage = (ImageView) view.findViewById(R.id.appImage);
        TextView appName = (TextView) view.findViewById(R.id.appName);
        TextView appCount = (TextView) view.findViewById(R.id.appCount);

        try {
            appIndex.setText(position + 1 + "");
            appImage.setImageDrawable(mPackageManager.getApplicationIcon(appInfo.getPackageName()));
            appName.setText(mPackageManager.getApplicationLabel(mPackageManager.getApplicationInfo(appInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES)));
            appCount.setText(appInfo.getExecuteCount() + "");
        } catch (Exception e) {
            Log.e(LOG_NAME, "getView Exception - " + e.getMessage());
        }

        return view;
    }
}
