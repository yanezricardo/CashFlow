package android.rycsoft.ve.cashflow.database.models;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class Presupuesto {
	public static final String TABLE_NAME = "Presupuesto";
	public static final String CONTENT_PATH = "Presupuesto";
	public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.Presupuesto";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;
	
	public static final String _ID = "_id";
	public static final String FECHA_DESDE = "fecha_desde";
	public static final String FECHA_HASTA = "fecha_hasta";
	public static final String PERSONA_ID = "persona_id";

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	public int ID;
	public long FechaDesde;
	public long FechaHasta;
	public int PersonaID;

	private static String getCreateScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("create table ");
		builder.append(TABLE_NAME + "( ");
		builder.append(_ID + " integer primary key autoincrement, ");
		builder.append(FECHA_DESDE + " long not null, ");
		builder.append(FECHA_HASTA + " long not null, ");
		builder.append(PERSONA_ID + " integer not null ");

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
		Log.w(Presupuesto.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		onDrop(database);
		onCreate(database);
	}
	
	public static HashMap<String, String> getProjectionMap() {
		HashMap<String, String> projectionMap = new HashMap<>();
		projectionMap.put(_ID, _ID);
		projectionMap.put(FECHA_DESDE, FECHA_DESDE);
		projectionMap.put(FECHA_HASTA, FECHA_HASTA);
		projectionMap.put(PERSONA_ID, PERSONA_ID);

		return projectionMap;
	}
}
