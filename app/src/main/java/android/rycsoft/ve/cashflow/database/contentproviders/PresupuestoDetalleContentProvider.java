package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.PresupuestoDetalle;
import android.rycsoft.ve.cashflow.uil.activities.PresupuestoActivity;

public class PresupuestoDetalleContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(PresupuestoDetalle.AUTHORITY, PresupuestoDetalle.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(PresupuestoDetalle.AUTHORITY, PresupuestoDetalle.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return PresupuestoDetalle.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return PresupuestoDetalle.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return PresupuestoDetalle._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return PresupuestoDetalle.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return PresupuestoDetalle.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return PresupuestoDetalle.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return PresupuestoDetalle.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PresupuestoDetalle.TABLE_NAME);
        qb.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, PresupuestoDetalle._ID + "=?", new String[]{id}, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PresupuestoDetalle.TABLE_NAME);
        qb.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PresupuestoDetalle.TABLE_NAME);
        qb.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static double getMonto(Context context, String presupuestoId, String categoriaId) {
        double result = 0;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PresupuestoDetalle.TABLE_NAME);
        qb.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db,
                new String[]{PresupuestoDetalle.MONTO},
                PresupuestoDetalle.PRESUPUESTO_ID + "=? AND " + PresupuestoDetalle.CATEGORIA_ID + "=?",
                new String[]{presupuestoId, categoriaId}, null, null,
                PresupuestoDetalle.DEFAULT_SORT_ORDER);
        if (cursor.moveToNext()) {
            result = cursor.getDouble(cursor.getColumnIndexOrThrow(PresupuestoDetalle.MONTO));
        }
        db.close();
        return result;
    }
}
