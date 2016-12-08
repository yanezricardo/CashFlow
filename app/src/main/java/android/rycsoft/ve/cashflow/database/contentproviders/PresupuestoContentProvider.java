package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.Presupuesto;
import android.rycsoft.ve.cashflow.database.models.PresupuestoDetalle;
import android.rycsoft.ve.cashflow.uil.activities.PresupuestoActivity;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;


public class PresupuestoContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(Presupuesto.AUTHORITY, Presupuesto.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(Presupuesto.AUTHORITY, Presupuesto.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return Presupuesto.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return Presupuesto.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return Presupuesto._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return Presupuesto.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return Presupuesto.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return Presupuesto.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return Presupuesto.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Presupuesto.TABLE_NAME);
        qb.setProjectionMap(Presupuesto.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Presupuesto._ID + "=?", new String[]{id}, null, null, Presupuesto.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Presupuesto.TABLE_NAME);
        qb.setProjectionMap(Presupuesto.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, Presupuesto.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Presupuesto.TABLE_NAME);
        qb.setProjectionMap(Presupuesto.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, Presupuesto.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Presupuesto getPresupuesto(Context context, long fechaInicio) {
        Presupuesto result = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Presupuesto.TABLE_NAME);
        qb.setProjectionMap(Presupuesto.getProjectionMap());

        String selection = Presupuesto.PERSONA_ID + "=? AND " + Presupuesto.FECHA_DESDE + "<=? AND " + Presupuesto.FECHA_HASTA + ">=?";
        String[] selectionArgs = new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId()), String.valueOf(fechaInicio), String.valueOf(fechaInicio)};

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, null, selection, selectionArgs, null, null, Presupuesto.DEFAULT_SORT_ORDER);
        if (cursor.moveToNext()) {
            result = new Presupuesto();
            result.ID = cursor.getInt(cursor.getColumnIndexOrThrow(Presupuesto._ID));
            result.FechaDesde = cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_DESDE));
            result.FechaHasta = cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_HASTA));
            result.PersonaID = cursor.getInt(cursor.getColumnIndexOrThrow(Presupuesto.PERSONA_ID));
        }
        db.close();
        return result;
    }

    public static double getTotalPresupuesto(Context context, int categoriaId) {
        double result = 0;
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        long fechaInicio = DateTimeHelper.getDate(GlobalValues.getDateFrom());
        long fechaFin = DateTimeHelper.getDate(GlobalValues.getDateTo());

        String presupuestoSelection = Presupuesto.PERSONA_ID + "=? AND (" +
                "(" + Presupuesto.FECHA_DESDE + " BETWEEN ? AND ?) OR " +
                "(" + Presupuesto.FECHA_HASTA + " BETWEEN ? AND ?))";
        String[] presupuestoSelectionArgs = new String[]{
                String.valueOf(GlobalValues.getCurrentPerson().getId()),
                String.valueOf(fechaInicio), String.valueOf(fechaFin),
                String.valueOf(fechaInicio), String.valueOf(fechaFin)};

        SQLiteQueryBuilder qbPresupuesto = new SQLiteQueryBuilder();
        qbPresupuesto.setTables(Presupuesto.TABLE_NAME);
        qbPresupuesto.setProjectionMap(Presupuesto.getProjectionMap());
        Cursor cursorPresupuesto = qbPresupuesto.query(db, new String[]{Presupuesto._ID}, presupuestoSelection, presupuestoSelectionArgs, null, null, Presupuesto.DEFAULT_SORT_ORDER);
        while (cursorPresupuesto.moveToNext()) {
            int presupuestoId = cursorPresupuesto.getInt(cursorPresupuesto.getColumnIndexOrThrow(Presupuesto._ID));

            SQLiteQueryBuilder qbDetalle = new SQLiteQueryBuilder();
            qbDetalle.setTables(PresupuestoDetalle.TABLE_NAME);
            qbDetalle.setProjectionMap(PresupuestoDetalle.getProjectionMap());

            String selection = PresupuestoDetalle.CATEGORIA_ID + "=? AND " + PresupuestoDetalle.PRESUPUESTO_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(categoriaId), String.valueOf(presupuestoId)};

            Cursor cursor = qbDetalle.query(db, null, selection, selectionArgs, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
            while (cursor.moveToNext()) {
                result = result + cursor.getDouble(cursor.getColumnIndex(PresupuestoDetalle.MONTO));
            }
            cursor.close();
        }
        db.close();
        cursorPresupuesto.close();

        return result;
    }

    public static Presupuesto getUltimoPresupuesto(Context context) {
        Presupuesto result = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Presupuesto.TABLE_NAME);
        qb.setProjectionMap(Presupuesto.getProjectionMap());

        String selection = Presupuesto.PERSONA_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId())};

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, null, selection, selectionArgs, null, null, Presupuesto.FECHA_HASTA + " DESC limit 1");
        if (cursor.moveToNext()) {
            result = new Presupuesto();
            result.ID = cursor.getInt(cursor.getColumnIndexOrThrow(Presupuesto._ID));
            result.FechaDesde = cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_DESDE));
            result.FechaHasta = cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_HASTA));
            result.PersonaID = cursor.getInt(cursor.getColumnIndexOrThrow(Presupuesto.PERSONA_ID));
        }
        db.close();
        return result;
    }

    public static double getTotalIngresos(Context context, int presupuestoId) {
        double result = 0;
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        SQLiteQueryBuilder qbCategoria = new SQLiteQueryBuilder();
        qbCategoria.setTables(Categoria.TABLE_NAME);
        qbCategoria.setProjectionMap(Categoria.getProjectionMap());

        SQLiteQueryBuilder qbDetalle = new SQLiteQueryBuilder();
        qbDetalle.setTables(PresupuestoDetalle.TABLE_NAME);
        qbDetalle.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        Cursor cursorCategoria = qbCategoria.query(db, new String[]{Categoria._ID}, Categoria.TIPO + "=?", new String[]{"Ingreso"}, null, null, Categoria.DEFAULT_SORT_ORDER);
        while (cursorCategoria.moveToNext()) {
            int categoriaId = cursorCategoria.getInt(cursorCategoria.getColumnIndexOrThrow(Categoria._ID));

            String selection = PresupuestoDetalle.CATEGORIA_ID + "=? AND " + PresupuestoDetalle.PRESUPUESTO_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(categoriaId), String.valueOf(presupuestoId)};

            Cursor cursor = qbDetalle.query(db, null, selection, selectionArgs, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
            while (cursor.moveToNext()) {
                result = result + cursor.getDouble(cursor.getColumnIndex(PresupuestoDetalle.MONTO));
            }
            cursor.close();
        }
        db.close();
        cursorCategoria.close();

        return result;
    }

    public static double getTotalEgresos(Context context, int presupuestoId) {
        double result = 0;
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        SQLiteQueryBuilder qbCategoria = new SQLiteQueryBuilder();
        qbCategoria.setTables(Categoria.TABLE_NAME);
        qbCategoria.setProjectionMap(Categoria.getProjectionMap());

        SQLiteQueryBuilder qbDetalle = new SQLiteQueryBuilder();
        qbDetalle.setTables(PresupuestoDetalle.TABLE_NAME);
        qbDetalle.setProjectionMap(PresupuestoDetalle.getProjectionMap());

        Cursor cursorCategoria = qbCategoria.query(db, new String[]{Categoria._ID}, Categoria.TIPO + "=?", new String[]{"Egreso"}, null, null, Categoria.DEFAULT_SORT_ORDER);
        while (cursorCategoria.moveToNext()) {
            int categoriaId = cursorCategoria.getInt(cursorCategoria.getColumnIndexOrThrow(Categoria._ID));

            String selection = PresupuestoDetalle.CATEGORIA_ID + "=? AND " + PresupuestoDetalle.PRESUPUESTO_ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(categoriaId), String.valueOf(presupuestoId)};

            Cursor cursor = qbDetalle.query(db, null, selection, selectionArgs, null, null, PresupuestoDetalle.DEFAULT_SORT_ORDER);
            while (cursor.moveToNext()) {
                result = result + cursor.getDouble(cursor.getColumnIndex(PresupuestoDetalle.MONTO));
            }
            cursor.close();
        }
        db.close();
        cursorCategoria.close();

        return result;
    }
}
