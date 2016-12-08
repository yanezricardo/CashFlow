package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Map;

import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.text.TextUtils;

public abstract class ContentProviderBase extends ContentProvider {
    protected static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    protected static final int QUERY_SCOPE_TABLE = 1;
    protected static final int QUERY_SCOPE_TABLE_ID = 2;

    protected DatabaseHelper database;

    private Context _context;
    public void setContext(Context context) {
        _context = context;
    }
    private Context getInternalContext() {
        if(getContext() == null) {
            return _context;
        }
        return getContext();
    }

    @Override
    public boolean onCreate() {
        if(_context == null){
            _context = super.getContext();
        }
        database = new DatabaseHelper(_context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = sortOrder;

        switch (uriMatcher.match(uri)) {
            case QUERY_SCOPE_TABLE:
                qb.setTables(getTableName());
                qb.setProjectionMap(getDefaultProjection());
                break;

            case QUERY_SCOPE_TABLE_ID:
                qb.setTables(getTableName());
                qb.setProjectionMap(getDefaultProjection());
                qb.appendWhere(getIdColumnName() + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = getDefaultSortOrder();
        }
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        String result;
        switch (uriMatcher.match(uri)) {
            case QUERY_SCOPE_TABLE:
                result = getContentType();
                break;
            case QUERY_SCOPE_TABLE_ID:
                result = getContentItemType();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (database == null) {
            return null;
        }
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        long rowId;
        switch (uriType) {
            case QUERY_SCOPE_TABLE:
                rowId = db.insert(getTableName(), null, new ContentValues(values));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Uri contentUri = ContentUris.withAppendedId(getDefaultContentUri(), rowId);
        getInternalContext().getContentResolver().notifyChange(contentUri, null);

        BackupManager.dataChanged(App.getContext().getPackageName());
        return contentUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUERY_SCOPE_TABLE:
                count = db.delete(getTableName(), where, whereArgs);
                break;

            case QUERY_SCOPE_TABLE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(getTableName(), getIdColumnName() + "=" + id +
                        (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        BackupManager.dataChanged(App.getContext().getPackageName());
        return count;
    }


    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case QUERY_SCOPE_TABLE:
                count = db.update(getTableName(), values, where, whereArgs);
                break;

            case QUERY_SCOPE_TABLE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(getTableName(), values, getIdColumnName() + "=" + id +
                        (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        BackupManager.dataChanged(App.getContext().getPackageName());
        return count;
    }

    protected abstract String getDefaultSortOrder();

    protected abstract String getTableName();

    protected abstract String getIdColumnName();

    protected abstract Map<String, String> getDefaultProjection();

    protected abstract String getContentType();

    protected abstract String getContentItemType();

    protected abstract Uri getDefaultContentUri();
}
