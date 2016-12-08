package android.rycsoft.ve.cashflow.uil.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.CuentaPorPagar;
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


public class CuentaPorPagarActivity extends InputActivity {
    private Button btn_cuenta_por_pagar_fecha;
    private Button btn_cuenta_por_pagar_fecha_de_pago;
    private EditText et_cuenta_por_pagar_monto;
    private EditText et_cuenta_por_pagar_descripcion;
    private EditText et_cuenta_por_pagar_acreedor;
    private Spinner sp_cuentaporpagar_recordar_fecha_de_pago;
    private Date _selectedFecha;
    private Date _selectedFechaDePago;

    public CuentaPorPagarActivity() {
        super(R.layout.fragment_cuenta_por_pagar);
    }

    @Override
    public String[] getProjection() {
        return new String[]{
                CuentaPorPagar._ID,
                CuentaPorPagar.FECHA,
                CuentaPorPagar.MONTO,
                CuentaPorPagar.DESCRIPCION,
                CuentaPorPagar.ACREEDOR,
                CuentaPorPagar.FECHA_DE_PAGO,
                CuentaPorPagar.RECORDAR_FECHA_DE_PAGO
        };
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_cuenta_por_pagar_monto.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.cuentaporpagar_monto_required_message), Toast.LENGTH_LONG).show();
            }
        } else if (TextUtils.isEmpty(et_cuenta_por_pagar_acreedor.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.cuentaporpagar_acreedor_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(CuentaPorPagar.FECHA, DateTimeHelper.getDate(_selectedFecha));
        values.put(CuentaPorPagar.FECHA_DE_PAGO, DateTimeHelper.getDate(_selectedFechaDePago));
        values.put(CuentaPorPagar.MONTO, LocalizationHelper.parseToCurrency(et_cuenta_por_pagar_monto.getText().toString()));
        values.put(CuentaPorPagar.DESCRIPCION, et_cuenta_por_pagar_descripcion.getText().toString());
        values.put(CuentaPorPagar.ACREEDOR, et_cuenta_por_pagar_acreedor.getText().toString());
        values.put(CuentaPorPagar.RECORDAR_FECHA_DE_PAGO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_cuentaporpagar_recordar_fecha_de_pago));
        values.put(CuentaPorPagar.PERSONA_ID, GlobalValues.getCurrentPerson().getId());

        return values;
    }

    @Override
    protected void initializeViews() {
        _selectedFecha = DateTimeHelper.getNow();
        _selectedFechaDePago = DateTimeHelper.getNow();
        btn_cuenta_por_pagar_fecha = ((Button) findViewById(R.id.btn_cuenta_por_pagar_fecha));
        btn_cuenta_por_pagar_fecha_de_pago = ((Button) findViewById(R.id.btn_cuenta_por_pagar_fecha_de_pago));
        et_cuenta_por_pagar_monto = ((EditText) findViewById(R.id.et_cuenta_por_pagar_monto));
        et_cuenta_por_pagar_descripcion = ((EditText) findViewById(R.id.et_cuenta_por_pagar_descripcion));
        et_cuenta_por_pagar_acreedor = ((EditText) findViewById(R.id.et_cuenta_por_pagar_acreedor));
        sp_cuentaporpagar_recordar_fecha_de_pago = ((Spinner) findViewById(R.id.sp_cuentaporpagar_recordar_fecha_de_pago));
        UIElementHelper.spinnerSetSelection(sp_cuentaporpagar_recordar_fecha_de_pago, Utils.getResourceAsString(R.string.recordar_dias_antes_default_value));
        btn_cuenta_por_pagar_fecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFecha();
            }
        });
        btn_cuenta_por_pagar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
        btn_cuenta_por_pagar_fecha_de_pago.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFechaDePago();
            }
        });
        btn_cuenta_por_pagar_fecha_de_pago.setText(DateTimeHelper.parseToString(_selectedFechaDePago));
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        double monto = cursor.getFloat(cursor.getColumnIndexOrThrow(CuentaPorPagar.MONTO));
        if (monto != 0) {
            et_cuenta_por_pagar_monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        }
        _selectedFecha = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA)));
        _selectedFechaDePago = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA_DE_PAGO)));
        btn_cuenta_por_pagar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
        btn_cuenta_por_pagar_fecha_de_pago.setText(DateTimeHelper.parseToString(_selectedFechaDePago));
        et_cuenta_por_pagar_descripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorPagar.DESCRIPCION)));
        et_cuenta_por_pagar_acreedor.setText(cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorPagar.ACREEDOR)));
        UIElementHelper.spinnerSetSelection(sp_cuentaporpagar_recordar_fecha_de_pago, cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorPagar.RECORDAR_FECHA_DE_PAGO)));

    }

    @Override
    protected void setViewDefaultValue() {
        _selectedFechaDePago = DateTimeHelper.getNextFortnight();
    }

    public void chooseFecha() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFecha = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_cuenta_por_pagar_fecha.setText(DateTimeHelper.parseToString(_selectedFecha));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFecha);
        int month = DateTimeHelper.getMonth(_selectedFecha);
        int day = DateTimeHelper.getDayOfMonth(_selectedFecha);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }

    public void chooseFechaDePago() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFechaDePago = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_cuenta_por_pagar_fecha_de_pago.setText(DateTimeHelper.parseToString(_selectedFechaDePago));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFechaDePago);
        int month = DateTimeHelper.getMonth(_selectedFechaDePago);
        int day = DateTimeHelper.getDayOfMonth(_selectedFechaDePago);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }
}
