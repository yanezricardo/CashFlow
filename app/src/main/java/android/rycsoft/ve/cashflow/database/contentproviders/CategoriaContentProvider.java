package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.uil.activities.EgresoListActivity;

public class CategoriaContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(Categoria.AUTHORITY, Categoria.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(Categoria.AUTHORITY, Categoria.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return Categoria.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return Categoria.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return Categoria._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return Categoria.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return Categoria.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return Categoria.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return Categoria.CONTENT_URI;
    }

    public static Cursor getById(Context context, String[] projection, String id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Categoria._ID + "=?", new String[]{id}, null, null, Categoria.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static Cursor getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, Categoria.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static List<Categoria> getAll(Context context) {
        return getAll(context, null, null);
    }

    public static List<Categoria> getAll(Context context, String selection, String[] selectionArgs) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, null, selection, selectionArgs, null, null, Categoria.DEFAULT_SORT_ORDER);

        ArrayList<Categoria> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Categoria categoria = new Categoria();
                categoria.ID = cursor.getInt(cursor.getColumnIndexOrThrow(Categoria._ID));
                categoria.Nombre = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE));
                categoria.Tipo = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.TIPO));
                categoria.Color = Color.parseColor(cursor.getString(cursor.getColumnIndexOrThrow(Categoria.COLOR)));
                categoria.PersonaID = cursor.getInt(cursor.getColumnIndexOrThrow(Categoria.PERSONA_ID));
                list.add(categoria);
            }
        }
        return list;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, Categoria.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }

    public static String getNombreById(Context context, long categoriaId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        String[] projection = new String[]{Categoria._ID, Categoria.NOMBRE};
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Categoria._ID + "=?", new String[]{Long.toString(categoriaId)}, null, null, Categoria.DEFAULT_SORT_ORDER);
        String result = "";
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE));
        }
        db.close();
        return result;
    }

    public static Categoria getCategoriaById(Context context, long categoriaId) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Categoria.TABLE_NAME);
        qb.setProjectionMap(Categoria.getProjectionMap());

        String[] projection = new String[]{Categoria._ID, Categoria.NOMBRE, Categoria.COLOR, Categoria.TIPO, Categoria.PERSONA_ID};
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Categoria._ID + "=?", new String[]{Long.toString(categoriaId)}, null, null, Categoria.DEFAULT_SORT_ORDER);
        Categoria categoria = null;
        if (cursor.moveToNext()) {
            categoria = new Categoria();
            categoria.ID = cursor.getInt(cursor.getColumnIndexOrThrow(Categoria._ID));
            categoria.Nombre = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE));
            categoria.Tipo = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.TIPO));
            categoria.Color = Color.parseColor(cursor.getString(cursor.getColumnIndexOrThrow(Categoria.COLOR)));
            categoria.PersonaID = cursor.getInt(cursor.getColumnIndexOrThrow(Categoria.PERSONA_ID));
        }
        db.close();
        return categoria;
    }
}
