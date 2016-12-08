package android.rycsoft.ve.cashflow.database.models;

import java.util.HashMap;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class InstrumentoFinanciero {
    public static final String TABLE_NAME = "InstrumentoFinanciero";
    public static final String CONTENT_PATH = "InstrumentoFinanciero";
    public static final String AUTHORITY = "android.rycsoft.ve.cashflow.database.models.InstrumentoFinanciero";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;

    public static final String _ID = "_id";
    public static final String NOMBRE = "nombre";
    public static final String LIMITE = "limite";
    public static final String TIPO = "tipo";
    public static final String FECHA_DE_CORTE = "fecha_de_corte";
    public static final String FECHA_DE_PAGO = "fecha_de_pago";
    public static final String RECORDAR_FECHA_DE_PAGO = "recordar_fecha_de_pago";
    public static final String PERSONA_ID = "persona_id";

    public long id;
    public String nombre;
    public double limite;
    public String tipo;
    public long fechaDeCorte;
    public long fechaDePago;
    public String recordarFechaDePago;
    public int persona_id;

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static String getCreateScript() {
        StringBuilder builder = new StringBuilder();
        builder.append("create table ");
        builder.append(TABLE_NAME + "( ");
        builder.append(_ID + " integer primary key autoincrement, ");
        builder.append(NOMBRE + " text not null, ");
        builder.append(LIMITE + " real not null, ");
        builder.append(TIPO + " text not null, ");
        builder.append(FECHA_DE_CORTE + " long not null, ");
        builder.append(FECHA_DE_PAGO + " long not null, ");
        builder.append(RECORDAR_FECHA_DE_PAGO + " text not null, ");
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
        Log.w(InstrumentoFinanciero.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        onDrop(database);
        onCreate(database);
    }

    public static HashMap<String, String> getProjectionMap() {
        HashMap<String, String> projectionMap = new HashMap<>();
        projectionMap.put(_ID, _ID);
        projectionMap.put(NOMBRE, NOMBRE);
        projectionMap.put(LIMITE, LIMITE);
        projectionMap.put(TIPO, TIPO);
        projectionMap.put(FECHA_DE_CORTE, FECHA_DE_CORTE);
        projectionMap.put(FECHA_DE_PAGO, FECHA_DE_PAGO);
        projectionMap.put(RECORDAR_FECHA_DE_PAGO, RECORDAR_FECHA_DE_PAGO);
        projectionMap.put(PERSONA_ID, PERSONA_ID);

        return projectionMap;
    }
}
