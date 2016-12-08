package android.rycsoft.ve.cashflow.uil.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.InstrumentoFinanciero;
import android.rycsoft.ve.cashflow.utils.UIElementHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InstrumentoFinancieroActivity extends InputActivity {
    public static final String INSTRUMENTO_FINANCIERO_TYPE_NAME = "instrumento_financiero_tipo";
    public static final String INSTRUMENTO_FINANCIERO_TDC = "TDC";
    public static final String INSTRUMENTO_FINANCIERO_CUENTA = "Cuenta";
    private EditText et_instrumentofinanciero_nombre;
    private EditText et_instrumentofinanciero_limite;
    private Spinner sp_instrumentofinanciero_tipo;
    private Spinner sp_instrumentofinanciero_recordar_fecha_de_pago;
    private Spinner sp_instrumentofinanciero_dia_de_corte;
    private Spinner sp_instrumentofinanciero_dia_de_pago;
    private String mTipo;

    public InstrumentoFinancieroActivity() {
        super(R.layout.fragment_instrumento_financiero_cuenta);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INSTRUMENTO_FINANCIERO_TYPE_NAME)) {
            mTipo = intent.getStringExtra(INSTRUMENTO_FINANCIERO_TYPE_NAME);
            if (INSTRUMENTO_FINANCIERO_TDC.equals(mTipo)) {
                super.setContentView(R.layout.fragment_instrumento_financiero_tdc);
            } else if (INSTRUMENTO_FINANCIERO_CUENTA.equals(mTipo)) {
                super.setContentView(R.layout.fragment_instrumento_financiero_cuenta);
            }
        }
    }

    @Override
    public String[] getProjection() {
        return new String[]{
                InstrumentoFinanciero._ID,
                InstrumentoFinanciero.NOMBRE,
                InstrumentoFinanciero.LIMITE,
                InstrumentoFinanciero.TIPO,
                InstrumentoFinanciero.FECHA_DE_CORTE,
                InstrumentoFinanciero.FECHA_DE_PAGO,
                InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_instrumentofinanciero_nombre.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.instrumentofinanciero_nombre_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(InstrumentoFinanciero.NOMBRE, et_instrumentofinanciero_nombre.getText().toString());
        if (INSTRUMENTO_FINANCIERO_TDC.equals(mTipo)) {
            values.put(InstrumentoFinanciero.LIMITE, et_instrumentofinanciero_limite.getText().toString());
            values.put(InstrumentoFinanciero.FECHA_DE_CORTE, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_instrumentofinanciero_dia_de_corte));
            values.put(InstrumentoFinanciero.FECHA_DE_PAGO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_instrumentofinanciero_dia_de_pago));
            values.put(InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_instrumentofinanciero_recordar_fecha_de_pago));
        } else if (INSTRUMENTO_FINANCIERO_CUENTA.equals(mTipo)) {
            values.put(InstrumentoFinanciero.LIMITE, 0);
            values.put(InstrumentoFinanciero.FECHA_DE_CORTE, 0);
            values.put(InstrumentoFinanciero.FECHA_DE_PAGO, 0);
            values.put(InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO, "");
        }
        values.put(InstrumentoFinanciero.TIPO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_instrumentofinanciero_tipo));
        values.put(InstrumentoFinanciero.PERSONA_ID, GlobalValues.getCurrentPerson().getId());

        return values;
    }

    @Override
    protected void initializeViews() {
        et_instrumentofinanciero_nombre = ((EditText) findViewById(R.id.et_instrumentofinanciero_nombre));
        sp_instrumentofinanciero_tipo = ((Spinner) findViewById(R.id.sp_instrumentofinanciero_tipo));
        UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_tipo, mTipo);
        getSupportActionBar().setSubtitle(mTipo);
        if (INSTRUMENTO_FINANCIERO_TDC.equals(mTipo)) {
            et_instrumentofinanciero_limite = ((EditText) findViewById(R.id.et_instrumentofinanciero_limite));
            sp_instrumentofinanciero_recordar_fecha_de_pago = (Spinner) findViewById(R.id.sp_instrumentofinanciero_recordar_dia_de_pago);
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_recordar_fecha_de_pago, Utils.getResourceAsString(R.string.recordar_dias_antes_default_value));

            sp_instrumentofinanciero_dia_de_corte = ((Spinner) findViewById(R.id.sp_instrumentofinanciero_dia_de_corte));
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_dia_de_corte, Utils.getResourceAsString(R.string.dias_del_mes_default_value));

            sp_instrumentofinanciero_dia_de_pago = ((Spinner) findViewById(R.id.sp_instrumentofinanciero_dia_de_pago));
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_dia_de_pago, Utils.getResourceAsString(R.string.dias_del_mes_default_value));
        }
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        et_instrumentofinanciero_nombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.NOMBRE)));
        if (INSTRUMENTO_FINANCIERO_TDC.equals(mTipo)) {
            et_instrumentofinanciero_limite.setText(cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.LIMITE)));
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_recordar_fecha_de_pago, cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.RECORDAR_FECHA_DE_PAGO)));
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_dia_de_corte, cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.FECHA_DE_CORTE)));
            UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_dia_de_pago, cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.FECHA_DE_PAGO)));
        }
        UIElementHelper.spinnerSetSelection(sp_instrumentofinanciero_tipo, cursor.getString(cursor.getColumnIndexOrThrow(InstrumentoFinanciero.TIPO)));

    }

    @Override
    protected void setViewDefaultValue() {
    }
}
