package android.rycsoft.ve.cashflow.uil.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.CuentaPorCobrar;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.rycsoft.ve.cashflow.utils.UIElementHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Date;

public class CuentaPorCobrarActivity extends InputActivity {
    private Button btn_cuenta_por_cobrar_fecha;
    private Button btn_cuenta_por_cobrar_fecha_de_cobro;
    private EditText et_cuenta_por_cobrar_monto;
    private EditText et_cuenta_por_cobrar_descripcion;
    private EditText et_cuenta_por_cobrar_deudor;
    private Spinner sp_cuentaporcobrar_recordar_fecha_de_cobro;
    private Date _selectedFecha;
    private Date _selectedFechaDeCobro;

    public CuentaPorCobrarActivity() {
        super(R.layout.fragment_cuenta_por_cobrar);
    }

    @Override
    public String[] getProjection() {
        return new String[] {
                CuentaPorCobrar._ID,
                CuentaPorCobrar.FECHA,
                CuentaPorCobrar.MONTO,
                CuentaPorCobrar.DESCRIPCION,
                CuentaPorCobrar.DEUDOR,
                CuentaPorCobrar.FECHA_DE_COBRO,
                CuentaPorCobrar.RECORDAR_FECHA_DE_COBRO,
        };
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_cuenta_por_cobrar_monto.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.cuentaporcobrar_monto_required_message), Toast.LENGTH_LONG).show();
            }
        } else if (TextUtils.isEmpty(et_cuenta_por_cobrar_deudor.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.cuentaporcobrar_deudor_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(CuentaPorCobrar.FECHA, DateTimeHelper.getDate(_selectedFecha));
        values.put(CuentaPorCobrar.FECHA_DE_COBRO, DateTimeHelper.getDate(_selectedFechaDeCobro));
        values.put(CuentaPorCobrar.MONTO, LocalizationHelper.parseToCurrency(et_cuenta_por_cobrar_monto.getText().toString()));
        values.put(CuentaPorCobrar.DESCRIPCION, et_cuenta_por_cobrar_descripcion.getText().toString());
        values.put(CuentaPorCobrar.DEUDOR, et_cuenta_por_cobrar_deudor.getText().toString());
        values.put(CuentaPorCobrar.RECORDAR_FECHA_DE_COBRO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_cuentaporcobrar_recordar_fecha_de_cobro));
        values.put(CuentaPorCobrar.PERSONA_ID, GlobalValues.getCurrentPerson().getId());

        return values;
    }

    @Override
    protected void initializeViews() {
        _selectedFecha = DateTimeHelper.getNow();
        _selectedFechaDeCobro = DateTimeHelper.getNow();
        btn_cuenta_por_cobrar_fecha = ((Button) findViewById(R.id.btn_cuenta_por_cobrar_fecha));
        btn_cuenta_por_cobrar_fecha_de_cobro = ((Button) findViewById(R.id.btn_cuenta_por_cobrar_fecha_de_cobro));
        et_cuenta_por_cobrar_monto = ((EditText)findViewById(R.id.et_cuenta_por_cobrar_monto));
        et_cuenta_por_cobrar_descripcion = ((EditText)findViewById(R.id.et_cuenta_por_cobrar_descripcion));
        et_cuenta_por_cobrar_deudor = ((EditText)findViewById(R.id.et_cuenta_por_cobrar_deudor));
        sp_cuentaporcobrar_recordar_fecha_de_cobro = ((Spinner)findViewById(R.id.sp_cuentaporcobrar_recordar_fecha_de_cobro));
        UIElementHelper.spinnerSetSelection(sp_cuentaporcobrar_recordar_fecha_de_cobro, Utils.getResourceAsString(R.string.recordar_dias_antes_default_value));
        btn_cuenta_por_cobrar_fecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFecha();
            }
        });
        btn_cuenta_por_cobrar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
        btn_cuenta_por_cobrar_fecha_de_cobro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFechaDeCobro();
            }
        });
        btn_cuenta_por_cobrar_fecha_de_cobro.setText(DateTimeHelper.parseToString(_selectedFechaDeCobro));
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        double monto = cursor.getFloat(cursor.getColumnIndexOrThrow(CuentaPorCobrar.MONTO));
        if (monto != 0) {
            et_cuenta_por_cobrar_monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        }
        _selectedFecha = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA)));
        _selectedFechaDeCobro = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA_DE_COBRO)));
        btn_cuenta_por_cobrar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
        btn_cuenta_por_cobrar_fecha_de_cobro.setText(DateTimeHelper.parseToString(_selectedFechaDeCobro));
        et_cuenta_por_cobrar_descripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorCobrar.DESCRIPCION)));
        et_cuenta_por_cobrar_deudor.setText(cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorCobrar.DEUDOR)));
        UIElementHelper.spinnerSetSelection(sp_cuentaporcobrar_recordar_fecha_de_cobro, cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorCobrar.RECORDAR_FECHA_DE_COBRO)));

    }

    @Override
    protected void setViewDefaultValue() {
        _selectedFechaDeCobro = DateTimeHelper.getNextFortnight();
    }

    public void chooseFecha() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFecha = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_cuenta_por_cobrar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFecha);
        int month = DateTimeHelper.getMonth(_selectedFecha);
        int day = DateTimeHelper.getDayOfMonth(_selectedFecha);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }

    public void chooseFechaDeCobro() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFechaDeCobro = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_cuenta_por_cobrar_fecha_de_cobro.setText(DateTimeHelper.parseToString(_selectedFechaDeCobro));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFechaDeCobro);
        int month = DateTimeHelper.getMonth(_selectedFechaDeCobro);
        int day = DateTimeHelper.getDayOfMonth(_selectedFechaDeCobro);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }
}
