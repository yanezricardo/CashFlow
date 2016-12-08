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
import android.rycsoft.ve.cashflow.database.models.Ingreso;
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

public class IngresoActivity extends InputActivity {
    private Button btn_ingreso_fecha;
    private EditText et_ingreso_monto;
    private EditText et_ingreso_descripcion;
    private Spinner sp_ingreso_instrumentofinanciero;
    private Date _selectedDate;
    private static long categoriaId;

    public IngresoActivity() {
        super(R.layout.fragment_ingreso);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra(Ingreso.CATEGORIA_ID)) {
            categoriaId = getIntent().getExtras().getLong(Ingreso.CATEGORIA_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActionBarSubtitle() {
        return CategoriaContentProvider.getNombreById(this, categoriaId);
    }

    @Override
    public String[] getProjection() {
        return new String[]{Ingreso._ID, Ingreso.FECHA, Ingreso.MONTO, Ingreso.DESCRIPCION, Ingreso.CATEGORIA_ID, Ingreso.INSTRUMENTO_FINANCIERO_ID};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_ingreso_monto.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.ingreso_monto_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        long instrumentoId = 0;
        InstrumentoFinanciero instrumento = (InstrumentoFinanciero) sp_ingreso_instrumentofinanciero.getSelectedItem();
        if (instrumento != null) {
            instrumentoId = instrumento.id;
        }
        ContentValues values = new ContentValues();
        values.put(Ingreso.FECHA, DateTimeHelper.getDate(_selectedDate));
        values.put(Ingreso.MONTO, LocalizationHelper.parseToCurrency(et_ingreso_monto.getText().toString()));
        values.put(Ingreso.DESCRIPCION, et_ingreso_descripcion.getText().toString());
        values.put(Ingreso.INSTRUMENTO_FINANCIERO_ID, instrumentoId);
        values.put(Ingreso.CATEGORIA_ID, categoriaId);

        return values;
    }

    @Override
    protected void initializeViews() {
        _selectedDate = DateTimeHelper.getNow();
        btn_ingreso_fecha = ((Button) findViewById(R.id.btn_ingreso_fecha));
        et_ingreso_monto = ((EditText) findViewById(R.id.et_ingreso_monto));
        et_ingreso_descripcion = ((EditText) findViewById(R.id.et_ingreso_descripcion));

        btn_ingreso_fecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        btn_ingreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));
        sp_ingreso_instrumentofinanciero = ((Spinner) findViewById(R.id.sp_ingreso_instrumentofinanciero));
        sp_ingreso_instrumentofinanciero.setAdapter(getInstrumentoFinancieroAdapter());
    }

    private SpinnerAdapter getInstrumentoFinancieroAdapter() {
        return new ArrayAdapter<InstrumentoFinanciero>(IngresoActivity.this, R.layout.simple_spinner_item, R.id.simple_spinner_item_text, InstrumentoFinancieroContentProvider.getAll(IngresoActivity.this)) {
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
                    view = inflater.inflate(R.layout.simple_dropdown_item, parent, false);
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
        double monto = cursor.getFloat(cursor.getColumnIndexOrThrow(Ingreso.MONTO));
        if (monto != 0) {
            et_ingreso_monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        }
        et_ingreso_descripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(Ingreso.DESCRIPCION)));
        _selectedDate = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(Ingreso.FECHA)));
        btn_ingreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));

        String instrumento = InstrumentoFinancieroContentProvider.getNombreById(this, cursor.getLong(cursor.getColumnIndexOrThrow(Ingreso.INSTRUMENTO_FINANCIERO_ID)));
        int position = spinnerIndexOf(sp_ingreso_instrumentofinanciero, instrumento);
        sp_ingreso_instrumentofinanciero.setSelection(position);
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
        btn_ingreso_fecha.setText(DateTimeHelper.parseToString(DateTimeHelper.getNow()));
    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedDate = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_ingreso_fecha.setText(DateTimeHelper.parseToString(_selectedDate));
            }
        };
        int year = DateTimeHelper.getYear(_selectedDate);
        int month = DateTimeHelper.getMonth(_selectedDate);
        int day = DateTimeHelper.getDayOfMonth(_selectedDate);
        Dialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.show();
    }
}