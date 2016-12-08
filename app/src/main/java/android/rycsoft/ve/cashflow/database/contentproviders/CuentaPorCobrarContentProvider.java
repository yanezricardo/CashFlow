package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.CuentaPorCobrar;

public class CuentaPorCobrarContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(CuentaPorCobrar.AUTHORITY, CuentaPorCobrar.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(CuentaPorCobrar.AUTHORITY, CuentaPorCobrar.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return CuentaPorCobrar.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return CuentaPorCobrar.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return CuentaPorCobrar._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return CuentaPorCobrar.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return CuentaPorCobrar.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return CuentaPorCobrar.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return CuentaPorCobrar.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorCobrar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorCobrar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, CuentaPorCobrar._ID + "=?", new String[]{id}, null, null, CuentaPorCobrar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorCobrar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorCobrar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, CuentaPorCobrar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorCobrar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorCobrar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, CuentaPorCobrar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }
}
