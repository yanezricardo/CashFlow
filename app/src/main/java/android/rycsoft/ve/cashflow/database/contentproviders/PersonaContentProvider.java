package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.database.models.Persona;
import android.rycsoft.ve.cashflow.database.models.UserModel;

public class PersonaContentProvider extends ContentProviderBase {
    static {
        uriMatcher.addURI(Persona.AUTHORITY, Persona.CONTENT_PATH, QUERY_SCOPE_TABLE);
        uriMatcher.addURI(Persona.AUTHORITY, Persona.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
    }

    @Override
    protected String getDefaultSortOrder() {
        return Persona.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String getTableName() {
        return Persona.TABLE_NAME;
    }

    @Override
    protected String getIdColumnName() {
        return Persona._ID;
    }

    @Override
    protected Map<String, String> getDefaultProjection() {
        return Persona.getProjectionMap();
    }

    @Override
    protected String getContentType() {
        return Persona.CONTENT_TYPE;
    }

    @Override
    protected String getContentItemType() {
        return Persona.CONTENT_ITEM_TYPE;
    }

    @Override
    protected Uri getDefaultContentUri() {
        return Persona.CONTENT_URI;
    }

    public static UserModel getById(int id) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Persona.TABLE_NAME);
        qb.setProjectionMap(Persona.getProjectionMap());

        String[] projection = new String[]{Persona._ID, Persona.NOMBRE, Persona.EMAIL, Persona.COLOR};
        SQLiteDatabase db = new DatabaseHelper(App.getContext()).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, Persona._ID + "=?", new String[]{String.valueOf(id)}, null, null, Persona.DEFAULT_SORT_ORDER);
        UserModel user = null;
        if (cursor != null && cursor.moveToNext()) {
            user = new UserModel();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Persona._ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(Persona.NOMBRE)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Persona.EMAIL)));
            user.setColor(Color.parseColor(cursor.getString(cursor.getColumnIndexOrThrow(Persona.COLOR))));
        }
        db.close();
        return user;
    }

    public static ArrayList<UserModel> getAll(Context context, String[] projection) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Persona.TABLE_NAME);
        qb.setProjectionMap(Persona.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, null, null, null, null, Persona.DEFAULT_SORT_ORDER);

        ArrayList<UserModel> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                UserModel user = new UserModel();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Persona._ID)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(Persona.NOMBRE)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Persona.EMAIL)));
                user.setColor(Color.parseColor(cursor.getString(cursor.getColumnIndexOrThrow(Persona.COLOR))));
                list.add(user);
            }
        }
        db.close();
        return list;
    }

    public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Persona.TABLE_NAME);
        qb.setProjectionMap(Persona.getProjectionMap());

        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[]{fieldValue}, null, null, Persona.DEFAULT_SORT_ORDER);
        db.close();
        return cursor;
    }
}
