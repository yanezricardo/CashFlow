package android.rycsoft.ve.cashflow.uil.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.utils.RandomColor;
import android.rycsoft.ve.cashflow.utils.UIElementHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

public class CategoriaActivity extends InputActivity {
    public static final String CATEGORIA_TYPE_NAME = "categoria_tipo";
    public static final String CATEGORIA_INGRESO = "Ingreso";
    public static final String CATEGORIA_EGRESO = "Egreso";
    private EditText et_categoria_nombre;
    private Spinner sp_categoria_tipo;
    private String mColor;

    public CategoriaActivity() {
        super(R.layout.fragment_categoria);
    }

    @Override
    public String[] getProjection() {
        return new String[]{Categoria._ID, Categoria.NOMBRE, Categoria.TIPO, Categoria.COLOR};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_categoria_nombre.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.categoria_nombre_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(Categoria.NOMBRE, et_categoria_nombre.getText().toString());
        values.put(Categoria.TIPO, UIElementHelper.spinnerGetSelectedItemOrDefault(sp_categoria_tipo));
        values.put(Categoria.PERSONA_ID, GlobalValues.getCurrentPerson().getId());
        values.put(Categoria.COLOR, mColor);
        return values;
    }

    @Override
    protected void initializeViews() {
        et_categoria_nombre = ((EditText) findViewById(R.id.et_categoria_nombre));
        sp_categoria_tipo = ((Spinner) findViewById(R.id.sp_categoria_tipo));
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CATEGORIA_TYPE_NAME)) {
            TableRow tr_categoria_tipo = (TableRow) findViewById(R.id.tr_categoria_tipo);
            if (tr_categoria_tipo != null) {
                tr_categoria_tipo.setVisibility(View.INVISIBLE);
            }
            String tipo = intent.getStringExtra(CATEGORIA_TYPE_NAME);
            UIElementHelper.spinnerSetSelection(sp_categoria_tipo, tipo);
            getSupportActionBar().setSubtitle(tipo);

            ImageView iv_categoria_icon = (ImageView) findViewById(R.id.iv_categoria_icon);
            if(iv_categoria_icon != null) {
                if(CATEGORIA_EGRESO.equals(tipo)) {
                    iv_categoria_icon.setBackgroundResource(R.drawable.ic_egreso);
                } else if(CATEGORIA_INGRESO.equals(tipo)) {
                    iv_categoria_icon.setBackgroundResource(R.drawable.ic_ingreso);
                }
            }
        }
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        et_categoria_nombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE)));
        UIElementHelper.spinnerSetSelection(sp_categoria_tipo, cursor.getString(cursor.getColumnIndexOrThrow(Categoria.TIPO)));
        mColor = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.COLOR));
    }

    @Override
    protected void setViewDefaultValue() {
        mColor = Utils.colorToString(RandomColor.MATERIAL.getRandomColor());
    }
}
