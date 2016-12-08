package android.rycsoft.ve.cashflow.database.contentproviders;

import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.Ingreso;
import android.rycsoft.ve.cashflow.database.models.PresupuestoDetalle;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.support.v4.app.FragmentActivity;

public class IngresoContentProvider extends ContentProviderBase {
	static {
		uriMatcher.addURI(Ingreso.AUTHORITY, Ingreso.CONTENT_PATH, QUERY_SCOPE_TABLE);
		uriMatcher.addURI(Ingreso.AUTHORITY, Ingreso.CONTENT_PATH + "/#", QUERY_SCOPE_TABLE_ID);
	}
	
	@Override
	protected String getDefaultSortOrder() {
		return Ingreso.DEFAULT_SORT_ORDER;
	}

	@Override
	protected String getTableName() {
		return Ingreso.TABLE_NAME;
	}

	@Override
	protected String getIdColumnName() {
		return Ingreso._ID;
	}

	@Override
	protected Map<String, String> getDefaultProjection() {
		return Ingreso.getProjectionMap();
	}

	@Override
	protected String getContentType() {
		return Ingreso.CONTENT_TYPE;
	}

	@Override
	protected String getContentItemType() {
		return Ingreso.CONTENT_ITEM_TYPE;
	}

	@Override
	protected Uri getDefaultContentUri() {
		return Ingreso.CONTENT_URI;
	}

	public static Cursor getById(Context context, String[] projection, String id) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Ingreso.TABLE_NAME);
		qb.setProjectionMap(Ingreso.getProjectionMap());
		
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		Cursor cursor = qb.query(db, projection, Ingreso._ID + "=?", new String[] { id }, null, null, Ingreso.DEFAULT_SORT_ORDER);
		db.close();
		return cursor;
	}
	
	public static Cursor getAll(Context context, String[] projection) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Ingreso.TABLE_NAME);
		qb.setProjectionMap(Ingreso.getProjectionMap());
		
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		Cursor cursor = qb.query(db, projection, null, null, null, null, Ingreso.DEFAULT_SORT_ORDER);
		db.close();
		return cursor;
	}

	public static Cursor getByField(Context context, String[] projection, String fieldName, String fieldValue) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Ingreso.TABLE_NAME);
		qb.setProjectionMap(Ingreso.getProjectionMap());
		
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		Cursor cursor = qb.query(db, projection, fieldName + "=?", new String[] { fieldValue }, null, null, Ingreso.DEFAULT_SORT_ORDER);
		db.close();
		return cursor;
	}

	public static double getSaldo(Context context, int categoriaId, long fechaInicio, long fechaFin) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(Ingreso.TABLE_NAME);
		qb.setProjectionMap(Ingreso.getProjectionMap());

        fechaFin = DateTimeHelper.getDate(DateTimeHelper.addDayOfYear(DateTimeHelper.createDate(fechaFin), 1));

		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        String projection = Ingreso.CATEGORIA_ID + "=? AND " + Ingreso.FECHA + ">=? AND " + Ingreso.FECHA + "<?";
		String[] selection = new String[] { String.valueOf(categoriaId), String.valueOf(fechaInicio), String.valueOf(fechaFin) };

		Cursor cursor = qb.query(db, null, projection, selection, null, null, Ingreso.DEFAULT_SORT_ORDER);
		double result = 0;
		while(cursor.moveToNext()) {
			result = result + cursor.getDouble(cursor.getColumnIndex(Ingreso.MONTO));
		}
		db.close();
		cursor.close();
		return result;
	}

	public static double getTotalIngreso(Context context, long fechaInicio, long fechaFin) {
		SQLiteQueryBuilder qbIngreso = new SQLiteQueryBuilder();
		qbIngreso.setTables(Ingreso.TABLE_NAME);
		qbIngreso.setProjectionMap(Ingreso.getProjectionMap());
		SQLiteDatabase dbIngreso = new DatabaseHelper(context).getReadableDatabase();

		SQLiteQueryBuilder qbCategoria = new SQLiteQueryBuilder();
		qbCategoria.setTables(Categoria.TABLE_NAME);
		qbCategoria.setProjectionMap(Categoria.getProjectionMap());
		SQLiteDatabase dbCategoria = new DatabaseHelper(context).getReadableDatabase();

		double result = 0;

        fechaFin = DateTimeHelper.getDate(DateTimeHelper.addDayOfYear(DateTimeHelper.createDate(fechaFin), 1));

        Cursor cursorCategorias = qbCategoria.query(dbCategoria, null, Categoria.TIPO + "=?", new String[] { "Ingreso" }, null, null, Categoria.DEFAULT_SORT_ORDER);
		while(cursorCategorias.moveToNext()) {
			int categoriaId = cursorCategorias.getInt(cursorCategorias.getColumnIndex(Categoria._ID));
			String projection = Ingreso.CATEGORIA_ID + "=? AND (" + Ingreso.FECHA + ">=? AND " + Ingreso.FECHA + "<?)";
			String[] selection = new String[] { String.valueOf(categoriaId), String.valueOf(fechaInicio), String.valueOf(fechaFin) };

			Cursor cursor = qbIngreso.query(dbIngreso, null, projection, selection, null, null, Ingreso.DEFAULT_SORT_ORDER);
			while(cursor.moveToNext()) {
				result = result + cursor.getDouble(cursor.getColumnIndex(Ingreso.MONTO));
			}
			cursor.close();
		}
		cursorCategorias.close();
		dbIngreso.close();
		dbCategoria.close();

		return result;
	}

	/*
	public static double GetTotalPresupuesto(Context context, long fechaInicio, long fechaFin, String categoriaTipo) {
		SQLiteQueryBuilder qbCategoria = new SQLiteQueryBuilder();
		qbCategoria.setTables(Categoria.TABLE_NAME);
		qbCategoria.setProjectionMap(Categoria.getProjectionMap());
		SQLiteDatabase dbCategoria = new DatabaseHelper(context).getReadableDatabase();

		double result = 0;

		Cursor cursorCategorias = qbCategoria.query(dbCategoria, null, Categoria.TIPO + "=?", new String[] { categoriaTipo }, null, null, Categoria.DEFAULT_SORT_ORDER);
		while(cursorCategorias.moveToNext()) {
			result = result + cursorCategorias.getDouble(cursorCategorias.getColumnIndex(Categoria.PRESUPUESTO));
		}
		cursorCategorias.close();
		dbCategoria.close();

		return result;
	}*/
}
