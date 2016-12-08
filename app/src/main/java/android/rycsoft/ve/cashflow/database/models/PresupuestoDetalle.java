package android.rycsoft.ve.cashflow.database.models;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PresupuestoDetalle {
	public static final String TABLE_NAME = "PresupuestoDetalle";
	public static final String CONTENT_PATH = "PresupuestoDetalle";
	public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.PresupuestoDetalle";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;
	
	public static final String _ID = "_id";
	public static final String MONTO = "monto";
	public static final String CATEGORIA_ID = "categoria_id";
	public static final String PRESUPUESTO_ID = "presupuesto_id";


	public static final String DEFAULT_SORT_ORDER = "_id ASC";
	
	private static String getCreateScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("create table ");
		builder.append(TABLE_NAME + "( ");
		builder.append(_ID + " integer primary key autoincrement, ");
		builder.append(MONTO + " real not null, ");
		builder.append(CATEGORIA_ID + " integer not null, ");
		builder.append(PRESUPUESTO_ID + " integer not null ");

		builder.append(");");
		return builder.toString();
	}
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(getCreateScript());
	}
	
	public static void onDrop(SQLiteDatabase database) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(PresupuestoDetalle.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		onDrop(database);
		onCreate(database);
	}
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> projectionMap = new HashMap<>();
		projectionMap.put(_ID, _ID);
		projectionMap.put(MONTO, MONTO);
		projectionMap.put(CATEGORIA_ID, CATEGORIA_ID);
		projectionMap.put(PRESUPUESTO_ID, PRESUPUESTO_ID);

		return projectionMap;
	}
}
