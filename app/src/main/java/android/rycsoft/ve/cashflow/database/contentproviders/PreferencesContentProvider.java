package android.rycsoft.ve.cashflow.database.contentproviders;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.database.models.Preferences;
import android.rycsoft.ve.cashflow.database.models.UserModel;

import java.util.Map;

public class PreferencesContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(Preferences.AUTHORITY, Preferences.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(Preferences.AUTHORITY, Preferences.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return Preferences.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return Preferences.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return Preferences._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return Preferences.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return Preferences.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return Preferences.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return Preferences.CONTENT_URI;
    }

    public static int getLastUserID() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Preferences.TABLE_NAME);
        qb.setProjectionMap(Preferences.getProjectionMap());

        String[] projection = new String[]{Preferences._ID, Preferences.LAST_USER_ID};
        SQLiteDatabase db = new DatabaseHelper(App.getContext()).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Preferences._ID + "=?", null, null, null, Preferences.DEFAULT_SORT_ORDER);

        int id = 0;
        if (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(Preferences.LAST_USER_ID));
        }
        db.close();
        return id;
    }

    public static void savePreferences() {
        ContentValues values = new ContentValues();
        values.put(Preferences.LAST_USER_ID, GlobalValues.getCurrentPerson().getId());

        PreferencesContentProvider provider = new PreferencesContentProvider();
        provider.setContext(App.getContext());
        if (provider.onCreate()) {
            provider.deleteAll();
            provider.insert(Preferences.CONTENT_URI, values);
        }
    }

    private void deleteAll() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Preferences.TABLE_NAME);
        qb.setProjectionMap(Preferences.getProjectionMap());

        try {
            SQLiteDatabase db = new DatabaseHelper(App.getContext()).getWritableDatabase();
            db.execSQL("delete from " + Preferences.TABLE_NAME);
            db.close();
        } catch (SQLiteException ex) {

        }
    }

    public static void loadPreferences() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Preferences.TABLE_NAME);
        qb.setProjectionMap(Preferences.getProjectionMap());

        try {
            SQLiteDatabase db = new DatabaseHelper(App.getContext()).getWritableDatabase();
            Cursor cursor = qb.query(db, null, null, null, null, null, null);

            int lastUserId = 0;
            if (cursor.moveToNext()) {
                lastUserId = cursor.getInt(cursor.getColumnIndexOrThrow(Preferences.LAST_USER_ID));
            }

            if (lastUserId > 0) {
                UserModel usuario = new PersonaContentProvider().getById(lastUserId);
                if (usuario != null) {
                    GlobalValues.setCurrentPerson(usuario);
                }
            }
            db.close();
        } catch (SQLiteException ex) {

        }
    }

}
