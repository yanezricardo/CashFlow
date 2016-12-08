package android.rycsoft.ve.cashflow.uil.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.CategoriaContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.PresupuestoContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.PresupuestoDetalleContentProvider;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.Presupuesto;
import android.rycsoft.ve.cashflow.database.models.PresupuestoDetalle;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class PresupuestoActivity extends InputActivity {
    private Button btn_presupuesto_fecha_desde;
    private Button btn_presupuesto_fecha_hasta;
    private Date _selectedFechaDesde;
    private Date _selectedFechaHasta;
    private TableLayout tl_presupuesto;
    private TextView tv_presupuesto_total_egresos;
    private TextView tv_presupuesto_total_ingresos;
    private boolean mCreandoDetalle;

    public PresupuestoActivity() {
        super(R.layout.fragment_presupuesto);
    }

    @Override
    public String[] getProjection() {
        return new String[]{Presupuesto._ID, Presupuesto.FECHA_DESDE, Presupuesto.FECHA_HASTA,};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        return true;
    }

    @Override
    protected void saveModel() {
        if (getUri() == null && getAction() == InputAction.Create) {
            setUri(getContentResolver().insert(getIntent().getData(), getViewContentValues()));
            if (getUri() == null) {
                Log.e(this.getClass().getSimpleName(), getResources().getString(R.string.error_failed_create) + getIntent().getData());
                finish();
                return;
            }
            saveDetail();
            setResult(RESULT_OK, (new Intent()).setAction(getUri().toString()));
            initializeCursor();
        }

        if (getCursor() != null && getIsValidModel(true)) {
            getContentResolver().update(getUri(), getViewContentValues(), null, null);
            saveDetail();
            finish();
        }
    }

    private void saveDetail() {
        if (getUri() == null) {
            return;
        }
        String id = getUri().getPathSegments().get(1);
        getContentResolver().delete(PresupuestoDetalle.CONTENT_URI, PresupuestoDetalle.PRESUPUESTO_ID + "=?", new String[]{id});
        getContentResolver().bulkInsert(PresupuestoDetalle.CONTENT_URI, getViewDetailContentValues(id));
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(Presupuesto.FECHA_DESDE, DateTimeHelper.getDate(_selectedFechaDesde));
        values.put(Presupuesto.FECHA_HASTA, DateTimeHelper.getDate(_selectedFechaHasta));
        values.put(Presupuesto.PERSONA_ID, GlobalValues.getCurrentPerson().getId());

        return values;
    }

    private ContentValues[] getViewDetailContentValues(String presupuestoId) {
        int detailCount = tl_presupuesto.getChildCount() - 2;
        ContentValues[] result = new ContentValues[detailCount];

        int index = 0;
        for (int i = 0; i < tl_presupuesto.getChildCount(); i++) {
            View view = tl_presupuesto.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                View et = row.getChildAt(1);
                if (et instanceof EditText) {
                    Categoria categoria = (Categoria) et.getTag();
                    double monto = LocalizationHelper.parseToCurrency(((EditText) et).getText().toString());
                    ContentValues values = new ContentValues();
                    values.put(PresupuestoDetalle.MONTO, monto);
                    values.put(PresupuestoDetalle.CATEGORIA_ID, categoria.ID);
                    values.put(PresupuestoDetalle.PRESUPUESTO_ID, presupuestoId);
                    result[index++] = values;
                }
            }
        }
        return result;
    }

    @Override
    protected void initializeViews() {
        _selectedFechaDesde = getFechaDesde();
        _selectedFechaHasta = DateTimeHelper.getLastDayOfFortnight(_selectedFechaDesde);
        btn_presupuesto_fecha_desde = ((Button) findViewById(R.id.btn_presupuesto_fecha_desde));
        btn_presupuesto_fecha_hasta = ((Button) findViewById(R.id.btn_presupuesto_fecha_hasta));
        tv_presupuesto_total_egresos = (TextView) findViewById(R.id.tv_presupuesto_total_egresos);
        tv_presupuesto_total_ingresos = (TextView) findViewById(R.id.tv_presupuesto_total_ingresos);

        btn_presupuesto_fecha_desde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFechaDesde();
            }
        });
        btn_presupuesto_fecha_desde.setText(DateTimeHelper.parseToString(_selectedFechaDesde));

        btn_presupuesto_fecha_hasta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseFechaHasta();
            }
        });
        btn_presupuesto_fecha_hasta.setText(DateTimeHelper.parseToString(_selectedFechaHasta));

        tl_presupuesto = (TableLayout) findViewById(R.id.tl_presupuesto);
        addDetail();
    }

    private Date getFechaDesde() {
        Date result = DateTimeHelper.getFirstDayOfCurrentFortnight();
        if (getAction() == InputAction.Create) {
            Presupuesto presupuesto = PresupuestoContentProvider.getUltimoPresupuesto(this);
            if (presupuesto != null) {
                result = DateTimeHelper.addDayOfYear(presupuesto.FechaHasta, 1);
            }
        }
        return result;
    }

    private void addDetail() {
        String id = "";
        if (getUri() != null) {
            id = getUri().getPathSegments().get(1);
        }

        mCreandoDetalle = true;
        List<Categoria> categoriasEgreso = CategoriaContentProvider.getAll(this, Categoria.TIPO + "=?", new String[]{"Egreso"});
        tl_presupuesto.addView(createRowHeader("Egresos"));
        for (Categoria categoria : categoriasEgreso) {
            tl_presupuesto.addView(createCategoriaRow(id, categoria));
        }

        List<Categoria> categoriasIngresos = CategoriaContentProvider.getAll(this, Categoria.TIPO + "=?", new String[]{"Ingreso"});
        tl_presupuesto.addView(createRowHeader("Ingresos"));
        for (Categoria categoria : categoriasIngresos) {
            tl_presupuesto.addView(createCategoriaRow(id, categoria));
        }
        mCreandoDetalle = false;
    }

    private TableRow createRowHeader(String header) {
        TableRow row = new TableRow(this);
        TextView tv = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(params);
        tv.setPadding(3, 16, 3, 3);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(14);
        //tv.setBackgroundColor(Color.GRAY);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        tv.setText(header);
        row.addView(tv);

        return row;
    }

    private TableRow createCategoriaRow(String presupuestoId, Categoria categoria) {
        TableRow row = new TableRow(this);

        TextView tv = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(params);
        tv.setPadding(16, 16, 3, 3);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        tv.setText(categoria.Nombre);
        row.addView(tv);

        EditText et = new EditText(this);
        et.setId(categoria.ID);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et.setGravity(Gravity.END | Gravity.TOP);
        et.setMinWidth(200);
        et.setTag(categoria);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mCreandoDetalle) {
                    calculaTotales();
                }
            }
        });

        double monto;
        if (!TextUtils.isEmpty(presupuestoId)) {
            monto = PresupuestoDetalleContentProvider.getMonto(this, presupuestoId, String.valueOf(categoria.ID));
            if (monto > 0) {
                et.setText(LocalizationHelper.parseToCurrencyString(monto));
            }
        }
        row.addView(et);

        return row;
    }

    private void calculaTotales() {
        double totalIngresos = 0;
        double totalEgresos = 0;
        for (int i = 0; i < tl_presupuesto.getChildCount(); i++) {
            View view = tl_presupuesto.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                View et = row.getChildAt(1);
                if (et instanceof EditText) {
                    Categoria categoria = (Categoria) et.getTag();
                    double monto = LocalizationHelper.parseToCurrency(((EditText) et).getText().toString());
                    if ("Ingreso".equals(categoria.Tipo)) {
                        totalIngresos += monto;
                    } else {
                        totalEgresos += monto;
                    }
                }
            }
        }

        tv_presupuesto_total_egresos.setText(LocalizationHelper.parseToCurrencyString(totalEgresos));
        tv_presupuesto_total_ingresos.setText(LocalizationHelper.parseToCurrencyString(totalIngresos));
    }

    @Override
    protected void initializeLookAndFeel() {
        calculaTotales();
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {

    }

    @Override
    protected void setViewDefaultValue() {
    }

    public void chooseFechaDesde() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFechaDesde = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_presupuesto_fecha_desde.setText(DateTimeHelper.parseToString(_selectedFechaDesde));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFechaDesde);
        int month = DateTimeHelper.getMonth(_selectedFechaDesde);
        int day = DateTimeHelper.getDayOfMonth(_selectedFechaDesde);
        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.getDatePicker().setMinDate(_selectedFechaDesde.getTime());
        dialog.show();
    }

    public void chooseFechaHasta() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _selectedFechaHasta = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
                btn_presupuesto_fecha_hasta.setText(DateTimeHelper.parseToString(_selectedFechaHasta));
            }
        };
        int year = DateTimeHelper.getYear(_selectedFechaHasta);
        int month = DateTimeHelper.getMonth(_selectedFechaHasta);
        int day = DateTimeHelper.getDayOfMonth(_selectedFechaHasta);
        DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
        dialog.getDatePicker().setMinDate(_selectedFechaDesde.getTime());
        dialog.show();
    }
}
