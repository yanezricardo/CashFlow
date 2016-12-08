package android.rycsoft.ve.cashflow.database.models;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class Egreso {
	public static final String TABLE_NAME = "Egreso";
	public static final String CONTENT_PATH = "Egreso";
	public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.Egreso";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;
	
	public static final String _ID = "_id";
	public static final String FECHA = "fecha";
	public static final String MONTO = "monto";
	public static final String DESCRIPCION = "descripcion";
	public static final String INSTRUMENTO_FINANCIERO_ID = "instrumento_financiero_id";
	public static final String CATEGORIA_ID = "categoria_id";


	public static final String DEFAULT_SORT_ORDER = "fecha DESC";
	
	private static String getCreateScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("create table ");
		builder.append(TABLE_NAME + "( ");
		builder.append(_ID + " integer primary key autoincrement, ");
		builder.append(FECHA + " long not null, ");
		builder.append(MONTO + " real not null, ");
		builder.append(DESCRIPCION + " text not null, ");
		builder.append(INSTRUMENTO_FINANCIERO_ID + " integer not null, ");
		builder.append(CATEGORIA_ID + " integer not null ");

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
		Log.w(Egreso.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		onDrop(database);
		onCreate(database);
	}
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> projectionMap = new HashMap<>();
		projectionMap.put(_ID, _ID);
		projectionMap.put(FECHA, FECHA);
		projectionMap.put(MONTO, MONTO);
		projectionMap.put(DESCRIPCION, DESCRIPCION);
		projectionMap.put(INSTRUMENTO_FINANCIERO_ID, INSTRUMENTO_FINANCIERO_ID);
		projectionMap.put(CATEGORIA_ID, CATEGORIA_ID);

		return projectionMap;
	}
}
