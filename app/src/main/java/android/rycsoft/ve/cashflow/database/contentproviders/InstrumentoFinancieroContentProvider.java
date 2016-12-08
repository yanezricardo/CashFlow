package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.InstrumentoFinanciero;

public class InstrumentoFinancieroContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(InstrumentoFinanciero.AUTHORITY, InstrumentoFinanciero.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(InstrumentoFinanciero.AUTHORITY, InstrumentoFinanciero.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return InstrumentoFinanciero.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return InstrumentoFinanciero.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return InstrumentoFinanciero._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return InstrumentoFinanciero.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return InstrumentoFinanciero.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return InstrumentoFinanciero.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return InstrumentoFinanciero.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(InstrumentoFinanciero.TABLE_NAME);
        qb.setProjectionMap(InstrumentoFinanciero.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, InstrumentoFinanciero._ID + "=?", new String[]{id}, null, null, InstrumentoFinanciero.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static List<InstrumentoFinanciero> getAll(Context context) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(InstrumentoFinanciero.TABLE_NAME);
        qb.setProjectionMap(InstrumentoFinanciero.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db,
                new String[] {InstrumentoFinanciero._ID,
                        InstrumentoFinanciero.NOMBRE,
                        InstrumentoFinanciero.TIPO,
                        InstrumentoFinanciero.LIMITE,
                        InstrumentoFinanciero.FECHA_DE_CORTE,
                        InstrumentoFinanciero.FECHA_DE_PAGO,
                        InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO,
                        InstrumentoFinanciero.PERSONA_ID},
                null, null, null, null, InstrumentoFinanciero.DEFAULT_SORT_ORDER);

        List<InstrumentoFinanciero> list = new ArrayList<>();
        while (cursor.moveToNext()){
            InstrumentoFinanciero instrumento = new InstrumentoFinanciero();
            instrumento.id = cursor.getLong(cursor.getColumnIndexOrThrow(InstrumentoFinanciero._ID));
            instrumento.nombre = cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.NOMBRE));
            instrumento.limite = cursor.getDouble(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.LIMITE));
            instrumento.tipo = cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.TIPO));
            instrumento.fechaDeCorte = cursor.getLong(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.FECHA_DE_CORTE));
            instrumento.fechaDePago = cursor.getLong(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.FECHA_DE_PAGO));
            instrumento.recordarFechaDePago = cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO));
            instrumento.persona_id = cursor.getInt(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.PERSONA_ID));
            list.add(instrumento);
        }

        db.close();
        return list;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(InstrumentoFinanciero.TABLE_NAME);
        qb.setProjectionMap(InstrumentoFinanciero.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, InstrumentoFinanciero.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static long getIdByNombre(Context context, String nombre) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(InstrumentoFinanciero.TABLE_NAME);
        qb.setProjectionMap(InstrumentoFinanciero.getProjectionMap());

        String[] projection = new String[]{InstrumentoFinanciero._ID};
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, InstrumentoFinanciero.NOMBRE + "=?", new String[]{nombre}, null, null, InstrumentoFinanciero.DEFAULT_SORT_ORDER);
        long id = 0;
        if(cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow(InstrumentoFinanciero._ID));
        }
        db.close();
        return id;
    }


    public static String getNombreById(Context context, long id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(InstrumentoFinanciero.TABLE_NAME);
        qb.setProjectionMap(InstrumentoFinanciero.getProjectionMap());

        String[] projection = new String[]{InstrumentoFinanciero._ID, InstrumentoFinanciero.NOMBRE};
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, InstrumentoFinanciero._ID + "=?", new String[]{Long.toString(id)}, null, null, InstrumentoFinanciero.DEFAULT_SORT_ORDER);
        String nombre = "";
        if(cursor.moveToNext()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.NOMBRE));
        }
        db.close();
        return nombre;
    }
}
