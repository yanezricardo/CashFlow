package android.rycsoft.ve.cashflow.database.contentproviders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.rycsoft.ve.cashflow.database.DefaultValues;
import android.rycsoft.ve.cashflow.database.models.*;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "cashflow.db";
	public static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase database) {
		Persona.onCreate(database);
		Categoria.onCreate(database);
		InstrumentoFinanciero.onCreate(database);
		Ingreso.onCreate(database);
		Egreso.onCreate(database);
		CuentaPorCobrar.onCreate(database);
		CuentaPorPagar.onCreate(database);
		Presupuesto.onCreate(database);
		PresupuestoDetalle.onCreate(database);
		Preferences.onCreate(database);
	}

	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Persona.onUpgrade(database, oldVersion, newVersion);
		Categoria.onUpgrade(database, oldVersion, newVersion);
		InstrumentoFinanciero.onUpgrade(database, oldVersion, newVersion);
		Ingreso.onUpgrade(database, oldVersion, newVersion);
		Egreso.onUpgrade(database, oldVersion, newVersion);
		CuentaPorCobrar.onUpgrade(database, oldVersion, newVersion);
		CuentaPorPagar.onUpgrade(database, oldVersion, newVersion);
		Presupuesto.onUpgrade(database, oldVersion, newVersion);
		PresupuestoDetalle.onUpgrade(database, oldVersion, newVersion);
		Preferences.onUpgrade(database, oldVersion, newVersion);

	}

	public static void createDatabaseIfNecesary(Context context) {
		if (existDb(context)) {
			return;
		}
		SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
		db.close();
	}

	private static boolean existDb(Context context) {
		boolean result = false;
		SQLiteDatabase db = null;
		try {
			String path = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
			db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			result = true;
		} catch(SQLiteException ex) { 
			result = false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return result;
	}
}
