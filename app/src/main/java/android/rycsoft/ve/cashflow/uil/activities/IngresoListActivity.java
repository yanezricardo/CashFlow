package android.rycsoft.ve.cashflow.uil.activities;

import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.CategoriaContentProvider;
import android.rycsoft.ve.cashflow.database.models.Ingreso;
import android.rycsoft.ve.cashflow.uil.fragments.IngresoListFragment;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class IngresoListActivity extends AppCompatActivity {
    public static Toolbar toolbar;
    private static long categoriaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ingreso_list);
        if(getIntent().hasExtra(Ingreso.CATEGORIA_ID)) {
            categoriaId = getIntent().getExtras().getLong(Ingreso.CATEGORIA_ID);
        }
        setupToolbar();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(android.R.id.content) == null) {
            IngresoListFragment list = new IngresoListFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(Ingreso.CATEGORIA_ID, categoriaId);
            list.setArguments(bundle);
            fm.beginTransaction().add(R.id.ingreso_fl_content, list).commit();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (toolbar != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                getSupportActionBar().setTitle(getCategoriaNombre());
                String range = DateTimeHelper.parseToString(GlobalValues.getDateFrom()) + " - " + DateTimeHelper.parseToString(GlobalValues.getDateTo());
                getSupportActionBar().setSubtitle(range);
            }
        }
    }

    private String getCategoriaNombre() {
        String result;
        result = CategoriaContentProvider.getNombreById(this, categoriaId);
        return result;
    }
}
