package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.CuentaPorPagar;

public class CuentaPorPagarContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(CuentaPorPagar.AUTHORITY, CuentaPorPagar.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(CuentaPorPagar.AUTHORITY, CuentaPorPagar.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return CuentaPorPagar.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return CuentaPorPagar.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return CuentaPorPagar._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return CuentaPorPagar.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return CuentaPorPagar.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return CuentaPorPagar.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return CuentaPorPagar.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorPagar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorPagar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, CuentaPorPagar._ID + "=?", new String[]{id}, null, null, CuentaPorPagar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorPagar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorPagar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, CuentaPorPagar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CuentaPorPagar.TABLE_NAME);
        qb.setProjectionMap(CuentaPorPagar.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, CuentaPorPagar.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }
}
