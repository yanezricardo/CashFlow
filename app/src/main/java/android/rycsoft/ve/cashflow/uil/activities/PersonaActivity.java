package android.rycsoft.ve.cashflow.uil.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.Persona;
import android.rycsoft.ve.cashflow.utils.RandomColor;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class PersonaActivity extends InputActivity {
    private EditText et_persona_nombre;
    private EditText et_persona_email;
    private String mColor;
    private TextWatcher mEmailValidator = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (et_persona_email.getText().toString().equals("")) {
                et_persona_email.setError(getResources().getString(R.string.persona_email_invalid_message));
            } else if (!isEmailValid(et_persona_email.getText().toString())) {
                et_persona_email.setError(getResources().getString(R.string.persona_email_invalid_message));
            }
        }

        boolean isEmailValid(CharSequence email) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    };

    public PersonaActivity() {
        super(R.layout.fragment_persona);
    }

    @Override
    public String[] getProjection() {
        return new String[]{Persona._ID, Persona.NOMBRE, Persona.EMAIL, Persona.COLOR};
    }

    @Override
    protected boolean getIsValidModel(boolean showMessages) {
        boolean result = true;
        if (TextUtils.isEmpty(et_persona_nombre.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.persona_nombre_required_message), Toast.LENGTH_LONG).show();
            }
        } else if (TextUtils.isEmpty(et_persona_email.getText().toString())) {
            result = false;
            if (showMessages) {
                Toast.makeText(this, getResources().getString(R.string.persona_email_required_message), Toast.LENGTH_LONG).show();
            }
        }

        return result;
    }

    @Override
    protected ContentValues getViewContentValues() {
        ContentValues values = new ContentValues();
        values.put(Persona.NOMBRE, et_persona_nombre.getText().toString());
        values.put(Persona.EMAIL, et_persona_email.getText().toString());
        values.put(Persona.COLOR, mColor);

        return values;
    }

    @Override
    protected void initializeViews() {
        et_persona_nombre = ((EditText) findViewById(R.id.et_persona_nombre));
        et_persona_email = ((EditText) findViewById(R.id.et_persona_email));
        et_persona_email.addTextChangedListener(mEmailValidator);
    }

    @Override
    protected void initializeLookAndFeel() {
    }

    @Override
    protected void setViewValuesFromCursor(Cursor cursor) {
        et_persona_nombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(Persona.NOMBRE)));
        et_persona_email.setText(cursor.getString(cursor.getColumnIndexOrThrow(Persona.EMAIL)));
        mColor = cursor.getString(cursor.getColumnIndexOrThrow(Persona.COLOR));
    }

    @Override
    protected void setViewDefaultValue() {
        mColor = Utils.colorToString(RandomColor.MATERIAL.getRandomColor());
    }
}
