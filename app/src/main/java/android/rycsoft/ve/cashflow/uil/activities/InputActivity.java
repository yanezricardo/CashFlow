package android.rycsoft.ve.cashflow.uil.activities;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.R;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public abstract class InputActivity extends AppCompatActivity {
    private static final int REVERT_ID = Menu.FIRST;
    private static final int DISCARD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    private static final int SAVE_ID = Menu.FIRST + 3;

    private Cursor _cursor;
    private Uri _uri;
    private InputAction _action;
    private int _layoutResID;
    private Bundle _savedInstanceState;

    public InputActivity(int layoutResID) {
        _layoutResID = layoutResID;
    }

    protected Cursor getCursor() {
        return _cursor;
    }

    protected void setCursor(Cursor cursor) {
        this._cursor = cursor;
    }

    protected Uri getUri() {
        return _uri;
    }

    protected void setUri(Uri uri) {
        this._uri = uri;
    }

    protected InputAction getAction() {
        return _action;
    }

    protected void setAction(InputAction action) {
        this._action = action;
    }

    protected String getCursorSelection() {
        return null;
    }

    protected String[] getCursorSelectionArgs() {
        return null;
    }

    protected String getCursorOrderBy() {
        return "_id";
    }

    protected Bundle getSavedInstanceState() {
        return _savedInstanceState;
    }

    protected void setSavedInstanceState(Bundle _savedInstanceState) {
        this._savedInstanceState = _savedInstanceState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(_layoutResID);
        initializeToolbar();
        initializeComponents();
        initializeCursor();
        initializeViews();
        setViewDefaultValue();
        initializeLookAndFeel();
        setSavedInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCursor() != null) {
            if (getCursor().moveToFirst()) {
                setViewValuesFromCursor(getCursor());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && getAction() == InputAction.Create && !getIsValidModel(false)) {
            setResult(RESULT_CANCELED);
            deleteModel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                deleteModel();
                finish();
                break;
            case R.id.action_close_clear_cancel:
            case DISCARD_ID:
                cancelAction();
                break;
            case REVERT_ID:
                cancelAction();
                break;
            case R.id.action_save:
            case SAVE_ID:
                saveModel();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(getActionBarSubtitle());
        }
    }

    protected String getActionBarSubtitle() {
        return null;
    }

    protected void initializeComponents() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            setAction(InputAction.Update);
            setUri(intent.getData());
        } else if (Intent.ACTION_INSERT.equals(action)) {
            setAction(InputAction.Create);
        } else {
            Log.e(this.getClass().getSimpleName(), getResources().getString(R.string.error_action_unknown));
            finish();
        }
    }

    protected void initializeCursor() {
        if (getUri() != null) {
            Cursor cursor = getContentResolver().query(getUri(), getProjection(), getCursorSelection(), getCursorSelectionArgs(), getCursorOrderBy());
            setCursor(cursor);
        }
    }

    protected void deleteModel() {
        if (getCursor() != null) {
            getCursor().close();
            setCursor(null);
            getContentResolver().delete(getUri(), null, null);
        }
    }

    protected void saveModel() {
        if (getUri() == null && getAction() == InputAction.Create) {
            Uri uriIntent = getIntent().getData();
            Uri uri = getContentResolver().insert(uriIntent, getViewContentValues());
            String id = uri.getPathSegments().get(1);
            if (Long.valueOf(id) < 0) {
                Log.e(this.getClass().getSimpleName(), getResources().getString(R.string.error_failed_create) + getIntent().getData());
                finish();
                return;
            } else {
                setUri(uri);
            }
            setResult(RESULT_OK, (new Intent()).setAction(getUri().toString()));
            initializeCursor();
        }

        if (getCursor() != null && getIsValidModel(true)) {
            getContentResolver().update(getUri(), getViewContentValues(), null, null);
            finish();
        }
    }

    protected void cancelAction() {
        if (getCursor() != null) {
            if (getAction() == InputAction.Update) {
                getCursor().close();
                setCursor(null);
                ContentValues values = getOriginalContentValues(getSavedInstanceState());
                if (values != null) {
                    getContentResolver().update(getUri(), values, null, null);
                }
            } else if (getAction() == InputAction.Create) {
                deleteModel();
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    protected ContentValues getOriginalContentValues(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        for (int i = 0; i < getProjection().length; i++) {
            String columnNane = getProjection()[i];
            if (savedInstanceState.containsKey(columnNane)) {
                values.put(columnNane, savedInstanceState.getString(columnNane));
            } else {
                return null;
            }
        }
        return values;
    }

    protected abstract void initializeViews();

    protected abstract String[] getProjection();

    protected abstract boolean getIsValidModel(boolean showMessages);

    protected abstract ContentValues getViewContentValues();

    protected abstract void initializeLookAndFeel();

    protected abstract void setViewValuesFromCursor(Cursor cursor);

    protected abstract void setViewDefaultValue();

    public enum InputAction {
        Create, Update
    }
}
