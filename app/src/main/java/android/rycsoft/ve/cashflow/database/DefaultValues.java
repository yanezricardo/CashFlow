package android.rycsoft.ve.cashflow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.DatabaseHelper;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.InstrumentoFinanciero;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.RandomColor;
import android.rycsoft.ve.cashflow.utils.Utils;

public class DefaultValues {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DefaultValues(Context context) {
        mContext = context;
        mDatabase = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean databaseIsEmpty() {
        if (mDatabase == null || mContext == null) {
            return true;
        }
        boolean result = false;
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + InstrumentoFinanciero.TABLE_NAME, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor = mDatabase.rawQuery("SELECT * FROM " + Categoria.TABLE_NAME, null);
        if (result && cursor.moveToNext()) {
            result = true;
        }
        return !result;
    }

    public void insertDefaultValues() {
        if (mDatabase == null || mContext == null) {
            return;
        }
        mDatabase.insert(InstrumentoFinanciero.TABLE_NAME, null, instrumentoFinancieroContentValues(Utils.getResourceAsString(R.string.default_values_tdc_nombre), Utils.getResourceAsString(R.string.default_values_tdc_tipo)));
        mDatabase.insert(InstrumentoFinanciero.TABLE_NAME, null, instrumentoFinancieroContentValues(Utils.getResourceAsString(R.string.default_values_cuenta_bancaria_nombre), Utils.getResourceAsString(R.string.default_values_cuenta_bancaria_tipo)));

        String[] categoria_egresos = mContext.getResources().getStringArray(R.array.default_value_categorias_egreso);
        for (String categoria : categoria_egresos) {
            mDatabase.insert(Categoria.TABLE_NAME, null, categoriaContentValues(categoria, Utils.getResourceAsString(R.string.default_values_egreso_nombre)));
        }
        String[] categoria_ingresos = mContext.getResources().getStringArray(R.array.default_value_categorias_ingreso);
        for (String categoria : categoria_ingresos) {
            mDatabase.insert(Categoria.TABLE_NAME, null, categoriaContentValues(categoria, Utils.getResourceAsString(R.string.default_values_ingreso_nombre)));
        }
        mDatabase.close();
    }

    private ContentValues instrumentoFinancieroContentValues(String nombre, String tipo) {
        ContentValues values = new ContentValues();
        values.put(InstrumentoFinanciero.NOMBRE, nombre);
        values.put(InstrumentoFinanciero.FECHA_DE_CORTE, DateTimeHelper.getDayOfMonth(DateTimeHelper.getNow()));
        values.put(InstrumentoFinanciero.FECHA_DE_PAGO, DateTimeHelper.getDayOfMonth(DateTimeHelper.getNow()));
        values.put(InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO, Utils.getResourceAsString(R.string.recordar_dias_antes_default_value));
        values.put(InstrumentoFinanciero.LIMITE, 0);
        values.put(InstrumentoFinanciero.TIPO, tipo);
        values.put(InstrumentoFinanciero.PERSONA_ID, GlobalValues.getCurrentPerson().getId());
        return values;
    }

    protected ContentValues categoriaContentValues(String nombre, String tipo) {
        ContentValues values = new ContentValues();
        values.put(Categoria.NOMBRE, nombre);
        values.put(Categoria.TIPO, tipo);
        values.put(Categoria.PERSONA_ID, GlobalValues.getCurrentPerson().getId());
        values.put(Categoria.COLOR, Utils.colorToString(RandomColor.MATERIAL.getRandomColor()));
        return values;
    }
}
