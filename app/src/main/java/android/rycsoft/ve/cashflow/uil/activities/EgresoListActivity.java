package android.rycsoft.ve.cashflow.uil.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.CategoriaContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.EgresoContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.PresupuestoContentProvider;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.Egreso;
import android.rycsoft.ve.cashflow.uil.adapters.EgresoListAdapter;
import android.rycsoft.ve.cashflow.uil.fragments.DateRangeDialogFragment;
import android.rycsoft.ve.cashflow.uil.widgets.DividerItemDecoration;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class EgresoListActivity extends AppCompatActivity implements EgresoListAdapter.OnItemClickListener, DateRangeDialogFragment.IOnSelectedRangeChangedListener {
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_EDIT = Menu.FIRST + 2;
    public static Toolbar mToolbar;
    private static long mCategoriaId;
    private EgresoListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_egreso_list);
        if (getIntent().hasExtra(Egreso.CATEGORIA_ID)) {
            mCategoriaId = getIntent().getExtras().getLong(Egreso.CATEGORIA_ID);
        }
        setupToolbar();
        setupRecyclerView();
        setupFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter_list) {
            showDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        DateRangeDialogFragment newFragment = DateRangeDialogFragment
                .builder()
                .setDateFrom(GlobalValues.getDateFrom())
                .setDateTo(GlobalValues.getDateTo())
                .setOnSelectionChangedListener(this);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        mAdapter = new EgresoListAdapter(this, getSupportLoaderManager(), (int) mCategoriaId);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        registerForContextMenu(recyclerView);
    }

    private void setupToolbar() {
        Categoria categoria = CategoriaContentProvider.getCategoriaById(this, mCategoriaId);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                getSupportActionBar().setTitle(categoria.Nombre);
                getSupportActionBar().setSubtitle(GlobalValues.getCurrentDateRange());
            }
        }
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(categoria.Nombre);
        collapsingToolbar.setBackgroundColor(categoria.Color);
        collapsingToolbar.setStatusBarScrimColor(categoria.Color);

        updateHeaderInfo();
    }

    private void updateHeaderInfo() {
        double monto = EgresoContentProvider.getSaldo(this, (int) mCategoriaId, DateTimeHelper.getDate(GlobalValues.getDateFrom()), DateTimeHelper.getDate(GlobalValues.getDateTo()));
        double presupuesto = PresupuestoContentProvider.getTotalPresupuesto(this, (int) mCategoriaId);

        TextView tv_egreso_list_periodo = (TextView) findViewById(R.id.tv_egreso_list_periodo);
        if(tv_egreso_list_periodo != null) {
            tv_egreso_list_periodo.setText(GlobalValues.getCurrentDateRange());
        }
        TextView tv_egreso_list_total = (TextView) findViewById(R.id.tv_egreso_list_total);
        if(tv_egreso_list_total != null) {
            String montoStr = getResources().getString(R.string.egreso_list_total_label) + ": " + LocalizationHelper.parseToCurrencyString(monto);
            tv_egreso_list_total.setText(montoStr);
        }
        TextView tv_egreso_list_presupuesto = (TextView) findViewById(R.id.tv_egreso_list_presupuesto);
        if(tv_egreso_list_presupuesto != null) {
            String presupuestoStr = getResources().getString(R.string.flujo_de_efectivo_egreso_presupuesto_label) + ": " + LocalizationHelper.parseToCurrencyString(presupuesto);
            tv_egreso_list_presupuesto.setText(presupuestoStr);
        }
        TextView tv_egreso_list_disponible = (TextView) findViewById(R.id.tv_egreso_list_disponible);
        if(tv_egreso_list_disponible != null) {
            String diferenciaStr = getResources().getString(R.string.flujo_de_efectivo_egreso_diferencia_label) + ": " + LocalizationHelper.parseToCurrencyString(presupuesto - monto);
            tv_egreso_list_disponible.setText(diferenciaStr);
        }
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab_actions_menu = (FloatingActionButton) findViewById(R.id.fab_actions_menu);
        fab_actions_menu.setElevation(4f);
        fab_actions_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_INSERT, Egreso.CONTENT_URI);
                i.putExtra(Egreso.CATEGORIA_ID, mCategoriaId);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemClick(View itemView, int position) {
        long id = mAdapter.getItemId(position);
        Uri uri = ContentUris.withAppendedId(mAdapter.getContentUri(), id);
        startActivity(new Intent(Intent.ACTION_EDIT, uri));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View itemView, int position, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, itemView, menuInfo);
        menu.setHeaderTitle(getSelectedItemFriendlyName(position));
        menu.add(hashCode(), MENU_ITEM_DELETE, 0, getContextMenuDescription(MENU_ITEM_DELETE));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        if (item.getGroupId() != hashCode()) {
            return false;
        }
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                Uri noteUri = ContentUris.withAppendedId(mAdapter.getContentUri(), mAdapter.getCurrentItemId());
                getContentResolver().delete(noteUri, null, null);
                return true;
            }
        }
        return false;
    }

    protected CharSequence getSelectedItemFriendlyName(int position) {
        if (mAdapter != null) {
            return DateTimeHelper.parseToString(mAdapter.getItemColumnValueAsLong(position, Egreso.FECHA));
        }
        return getTitle();
    }

    protected CharSequence getContextMenuDescription(int menuItem) {
        if (menuItem == MENU_ITEM_EDIT) {
            return getResources().getString(R.string.menu_edit);
        } else if (menuItem == MENU_ITEM_DELETE) {
            return getResources().getString(R.string.menu_delete);
        } else {
            return "";
        }
    }

    @Override
    public void onSelectedRangeChanged(Date from, Date to) {
        GlobalValues.setCurrentDateFrom(from);
        GlobalValues.setCurrentDateTo(to);
        updateHeaderInfo();
        mAdapter.restarLoader();
    }

}
