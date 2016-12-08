package android.rycsoft.ve.cashflow.database.models;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

public class Preferences {
    public static final String TABLE_NAME = "Preferences";
    public static final String CONTENT_PATH = "Preferences";
    public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.Preferences";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
    public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;

    public static final String _ID = "_id";
    public static final String LAST_USER_ID = "last_user_id";

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static String getCreateScript() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table ");
        builder.append(TABLE_NAME + "( ");
        builder.append(_ID + " integer primary key autoincrement, ");
        builder.append(LAST_USER_ID + " text not null ");

        builder.append(");");
        return builder.toString();
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(getCreateScript());
    }

    public static void onDrop(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(Preferences.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        onDrop(database);
        onCreate(database);
    }

    public static HashMap<String, String> getProjectionMap() {
        HashMap<String, String> projectionMap = new HashMap<>();
        projectionMap.put(_ID, _ID);
        projectionMap.put(LAST_USER_ID, LAST_USER_ID);

        return projectionMap;
    }
}

