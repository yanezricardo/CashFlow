package android.rycsoft.ve.cashflow.database.models;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class CuentaPorCobrar {
	public static final String TABLE_NAME = "CuentaPorCobrar";
	public static final String CONTENT_PATH = "CuentaPorCobrar";
	public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.CuentaPorCobrar";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;
	
	public static final String _ID = "_id";
	public static final String FECHA = "fecha";
	public static final String MONTO = "monto";
	public static final String DESCRIPCION = "descripcion";
	public static final String DEUDOR = "deudor";
	public static final String FECHA_DE_COBRO = "fecha_de_cobro";
	public static final String RECORDAR_FECHA_DE_COBRO = "recordar_fecha_de_cobro";
	public static final String PERSONA_ID = "persona_id";


	public static final String DEFAULT_SORT_ORDER = "_id ASC";
	
	private static String getCreateScript() {
		StringBuilder builder = new StringBuilder();
		builder.append("create table ");
		builder.append(TABLE_NAME + "( ");
		builder.append(_ID + " integer primary key autoincrement, ");
		builder.append(FECHA + " long not null, ");
		builder.append(MONTO + " real not null, ");
		builder.append(DESCRIPCION + " text not null, ");
		builder.append(DEUDOR + " text not null, ");
		builder.append(FECHA_DE_COBRO + " long not null, ");
		builder.append(RECORDAR_FECHA_DE_COBRO + " text not null, ");
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
		Log.w(CuentaPorCobrar.class.getName(), "Upgrading database from version "
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
		projectionMap.put(DEUDOR, DEUDOR);
		projectionMap.put(FECHA_DE_COBRO, FECHA_DE_COBRO);
		projectionMap.put(RECORDAR_FECHA_DE_COBRO, RECORDAR_FECHA_DE_COBRO);
		projectionMap.put(PERSONA_ID, PERSONA_ID);

		return projectionMap;
	}
}
