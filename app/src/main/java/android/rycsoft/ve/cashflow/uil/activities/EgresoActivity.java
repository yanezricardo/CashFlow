package android.rycsoft.ve.cashflow.uil.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.CategoriaContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.InstrumentoFinancieroContentProvider;
import android.rycsoft.ve.cashflow.database.models.Egreso;
import android.rycsoft.ve.cashflow.database.models.InstrumentoFinanciero;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class EgresoActivity extends InputActivity {
    private Button btn_egreso_fecha;
    private EditText et_egreso_monto;
    private EditText et_egreso_descripcion;
    private Spinner sp_egreso_instrumentofinanciero;
    private Date _selectedDate;
    private static long categoriaId;

    public EgresoActivity() {
        super(R.layout.fragment_egreso);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra(Egreso.CATEGORIA_ID)) {
            categoriaId = getIntent().getExtras().getLong(Egreso.CATEGORIA_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActionBarSubtitle() {
        return CategoriaContentProvider.getNombreById(this, categoriaId);
    }

    @Override
    public String[] getProjection() {
        return new String[]{Egreso._ID, Egreso.FECHA, Egreso.MONTO, Egreso.DESCRIPCION, Egreso.CATEGORIA_ID, Egreso.INSTRUMENTO_FINANCIERO_ID};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_egreso_monto.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.egreso_monto_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        long instrumentoId = 0;
        InstrumentoFinanciero instrumento = (InstrumentoFinanciero) sp_egreso_instrumentofinanciero.getSelectedItem();
        if (instrumento != null) {
            instrumentoId = instrumento.id;
        }
        ContentValues values = new ContentValues();
        values.put(Egreso.FECHA, DateTimeHelper.getDate(_selectedDate));
        values.put(Egreso.MONTO, LocalizationHelper.parseToCurrency(et_egreso_monto.getText().toString()));
        values.put(Egreso.DESCRIPCION, et_egreso_descripcion.getText().toString());
        values.put(Egreso.INSTRUMENTO_FINANCIERO_ID, instrumentoId);
        values.put(Egreso.CATEGORIA_ID, categoriaId);

        return values;
    }

    @Override
    protected void initializeViews() {
        _selectedDate = DateTimeHelper.getNow();
        btn_egreso_fecha = ((Button) findViewById(R.id.btn_egreso_fecha));
        et_egreso_monto = ((EditText) findViewById(R.id.et_egreso_monto));
        et_egreso_descripcion = ((EditText) findViewById(R.id.et_egreso_descripcion));
        btn_egreso_fecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        btn_egreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));
        sp_egreso_instrumentofinanciero = ((Spinner) findViewById(R.id.sp_egreso_instrumentofinanciero));
        sp_egreso_instrumentofinanciero.setAdapter(getInstrumentoFinancieroAdapter());
    }

    private SpinnerAdapter getInstrumentoFinancieroAdapter() {
        return new ArrayAdapter<InstrumentoFinanciero>(EgresoActivity.this, R.layout.simple_spinner_item, R.id.simple_spinner_item_text, InstrumentoFinancieroContentProvider.getAll(EgresoActivity.this)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.simple_spinner_item, null);
                }
                TextView tvNombre = (TextView) view.findViewById(R.id.simple_spinner_item_text);
                InstrumentoFinanciero instrumento = getItem(position);
                if (instrumento != null) {
                    tvNombre.setText(instrumento.nombre);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.simple_dropdown_item, null, false);
                }
                TextView tvNombre = (TextView) view.findViewById(R.id.simple_dropdown_item_text);
                InstrumentoFinanciero instrumento = getItem(position);
                if (instrumento != null) {
                    tvNombre.setText(instrumento.nombre);
                }
                return view;
            }
        };
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        double monto = cursor.getFloat(cursor.getColumnIndexOrThrow(Egreso.MONTO));
        if (monto != 0) {
            et_egreso_monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        }
        et_egreso_descripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(Egreso.DESCRIPCION)));
        _selectedDate = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(Egreso.FECHA)));
        btn_egreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));

        String instrumento = InstrumentoFinancieroContentProvider.getNombreById(this, cursor.getLong(cursor.getColumnIndexOrThrow(Egreso.INSTRUMENTO_FINANCIERO_ID)));
        int position = spinnerIndexOf(sp_egreso_instrumentofinanciero, instrumento);
        sp_egreso_instrumentofinanciero.setSelection(position);
    }

    public static int spinnerIndexOf(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            InstrumentoFinanciero instrumento = (InstrumentoFinanciero) spinner.getItemAtPosition(i);
            if (instrumento != null && value.equals(instrumento.nombre)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void setViewDefaultValue() {
        btn_egreso_fecha.setText(DateTimeHelper.parseToString(DateTimeHelper.getNow()));
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedDate = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_egreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));
            }
        };
        int year = DateTimeHelper.getYear(_selectedDate);
        int month = DateTimeHelper.getMonth(_selectedDate);
        int day = DateTimeHelper.getDayOfMonth(_selectedDate);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }
}
