package com.example.appcounter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: huhwook
 * Date: 2014. 2. 5.
 * Time: 오후 5:19
 */
public class DatabaseAppCounter {
    private static final String LOG_NAME = DatabaseAppCounter.class.getSimpleName();

    public static String TableName = "AppCounter";

    public static class Column {

        public static final String IDX = "idx";
        public static final String PACKAGE_NAME = "package_name";
        public static final String EXECUTE_TIME = "execute_time";
    }
    public class AppInfo {

        private String packageName; // 이름
        private Date executeTime;   // 실행시간
        private long executeCount;  // 실행횟수
        public String getPackageName() { return packageName; }

        public void setPackageName(String packageName) {this.packageName = packageName;}
        public Date getExecuteTime() { return executeTime; }
        public void setExecuteTime(String executeTime) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.executeTime = dateFormat.parse(executeTime);
            } catch (ParseException e) {
                Log.e(LOG_NAME, "setExecuteTime - " + e.getMessage());
            }
        }
        public long getExecuteCount() { return executeCount; }
        public void setExecuteCount(long executeCount) { this.executeCount = executeCount; }
    }






    private static Context mContext;
    private static SQLiteDatabase db;
    private static DatabaseHelper dbHelper;
    private static DatabaseAppCounter instance;

    private DatabaseAppCounter() {}
    public synchronized static DatabaseAppCounter getInstance(Context applicationContext) {

        if (instance == null) {
            instance = new DatabaseAppCounter();
            mContext = applicationContext;
            dbHelper = new DatabaseHelper(applicationContext);
            db = dbHelper.getWritableDatabase();
        }
        return instance;
    }

    public void insertAppInfo(String packageName) {
        db.execSQL(new StringBuilder()
                .append("INSERT INTO " + DatabaseAppCounter.TableName + " VALUES ( ")
                .append("null, ")
                .append("'" + packageName + "', ")
                .append("strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime') )").toString());
    }

    public AppInfo getOrderByApp(String order) {
        if (order == null ||
                "ASC".equals(order.toUpperCase()) == false &&
                "DESC".equals(order.toUpperCase()) == false) {
            Log.d(LOG_NAME, "getOrderByApp - param(order) is wrong: " + order);
            return null;
        }
        Cursor cursor = db.rawQuery(new StringBuilder()
                .append("SELECT " + Column.PACKAGE_NAME + "," + Column.EXECUTE_TIME)
                .append(" FROM " + DatabaseAppCounter.TableName)
                .append(" WHERE " + Column.EXECUTE_TIME + " IS NOT NULL ")
                .append(" ORDER BY ")
                .append(Column.EXECUTE_TIME)
                .append(" " + order)
                .append(" LIMIT 1").toString(), null);
        AppInfo appInfo = null;
        if (cursor.moveToNext()) {
            appInfo = new AppInfo();
            appInfo.setPackageName(cursor.getString(cursor.getColumnIndex(Column.PACKAGE_NAME)));
            appInfo.setExecuteTime(cursor.getString(cursor.getColumnIndex(Column.EXECUTE_TIME)));
        }
        return appInfo;
    }

    public List<AppInfo> getAppCountList() {
        List<AppInfo> appCountList = new ArrayList<AppInfo>();

        Cursor cursor = db.rawQuery(new StringBuilder()
                .append(" SELECT " + Column.PACKAGE_NAME + ", COUNT(*) AS COUNT")
                .append(" FROM " + DatabaseAppCounter.TableName)
                .append(" GROUP BY " + Column.PACKAGE_NAME)
                .append(" ORDER BY COUNT DESC")
                .toString(), null);

        AppInfo appInfo = null;
        while (cursor.moveToNext()) {
            appInfo = new AppInfo();
            appInfo.setPackageName(cursor.getString(cursor.getColumnIndex(Column.PACKAGE_NAME)));
            appInfo.setExecuteCount(cursor.getLong(cursor.getColumnIndex("COUNT")));
            appCountList.add(appInfo);
        }
        return appCountList;
    }







    static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, "AppCounter.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(new StringBuilder()
                    .append("CREATE TABLE " + DatabaseAppCounter.TableName + "(")
                    .append(DatabaseAppCounter.Column.IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append(DatabaseAppCounter.Column.PACKAGE_NAME + " TEXT, ")
                    .append(DatabaseAppCounter.Column.EXECUTE_TIME + " TEXT) ").toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseAppCounter.TableName);
        }
    }
}
