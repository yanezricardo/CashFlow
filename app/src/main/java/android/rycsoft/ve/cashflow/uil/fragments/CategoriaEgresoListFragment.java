package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.EgresoContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.PresupuestoContentProvider;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.database.models.Egreso;
import android.rycsoft.ve.cashflow.uil.activities.CategoriaActivity;
import android.rycsoft.ve.cashflow.uil.activities.EgresoListActivity;
import android.rycsoft.ve.cashflow.uil.widgets.TextDrawable;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

public class CategoriaEgresoListFragment extends BaseListFragment implements DateRangeDialogFragment.IOnSelectedRangeChangedListener {
    public CategoriaEgresoListFragment() {
        super(R.layout.fragment_categoria_egreso_list_item);
        _uniqueIndentifier = 2;
    }

    public static ListFragment newInstance() {
        return new CategoriaEgresoListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActionBarSubtitle(DateTimeHelper.parseToString(GlobalValues.getDateFrom()) + " - " + DateTimeHelper.parseToString(GlobalValues.getDateTo()));
        if (floatingActionButton != null)
            floatingActionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(_uniqueIndentifier, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingActionButton != null)
            floatingActionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(getSelectedItemFriendlyName(info.position));
        menu.add(this.getId() + _uniqueIndentifier, MENU_ITEM_DELETE, 0, getContextMenuDescription(MENU_ITEM_DELETE));
        menu.add(this.getId() + _uniqueIndentifier, MENU_ITEM_EDIT, 1, getContextMenuDescription(MENU_ITEM_EDIT));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != (this.getId() + _uniqueIndentifier)) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                Uri uri = ContentUris.withAppendedId(getContentUri(), info.id);
                getActivity().getContentResolver().delete(uri, null, null);
                return true;
            }
            case MENU_ITEM_EDIT: {
                Uri uri = ContentUris.withAppendedId(getContentUri(), info.id);
                Intent intent = new Intent(Intent.ACTION_EDIT, uri);
                intent.putExtra(CategoriaActivity.CATEGORIA_TYPE_NAME, CategoriaActivity.CATEGORIA_EGRESO);
                getActivity().startActivity(intent);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onFloatingActionButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_INSERT, getContentUri());
        intent.putExtra(CategoriaActivity.CATEGORIA_TYPE_NAME, CategoriaActivity.CATEGORIA_EGRESO);
        getActivity().startActivity(intent);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        initializeFloatingActionButton();
        //getLoaderManager().restartLoader(_uniqueIndentifier, null, this);
    }

    @Override
    protected Uri getContentUri() {
        return Categoria.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{Categoria._ID, Categoria.NOMBRE, Categoria.COLOR};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1, R.id.tv_categoria_egreso_nombre};
    }

    @Override
    protected String getCursorSelection() {
        return Categoria.TIPO + "=? AND " + Categoria.PERSONA_ID + "=?";
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        return new String[]{"Egreso", String.valueOf(GlobalValues.getCurrentPerson().getId())};
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), EgresoListActivity.class);
        i.putExtra(Egreso.CATEGORIA_ID, id);
        startActivity(i);
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
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void bindCursorAdapterView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view, cursor);
            view.setTag(holder);
        }

        int categoriaID = cursor.getInt(holder._IDColumnIndex);
        double monto = EgresoContentProvider.getSaldo(getActivity(), categoriaID,
                DateTimeHelper.getDate(GlobalValues.getDateFrom()), DateTimeHelper.getDate(GlobalValues.getDateTo()));
        double presupuesto = PresupuestoContentProvider.getTotalPresupuesto(getActivity(), categoriaID);
        int saldoActual = Utils.toPorcentaje(presupuesto, monto);
        String presupuestoStr = getResources().getString(R.string.flujo_de_efectivo_egreso_presupuesto_label) + ": " + LocalizationHelper.parseToCurrencyString(presupuesto);
        String diferenciaStr = getResources().getString(R.string.flujo_de_efectivo_egreso_diferencia_label) + ": " + LocalizationHelper.parseToCurrencyString(presupuesto - monto);
        String nombre = cursor.getString(holder.NombreColumnIndex);
        String color = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.COLOR));
        Drawable icono = TextDrawable.builder().buildRound(Utils.getFirstTwoLetersOfString(nombre), Color.parseColor(color));

        holder.Icon.setImageDrawable(icono);
        holder.Nombre.setText(nombre);
        holder.Monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        holder.Presupuesto.setText(presupuestoStr);
        holder.SaldoActual.setProgress(saldoActual);

        if ((presupuesto - monto) < 0) {
            holder.Diferencia.setTextColor(Color.RED);
            diferenciaStr = getResources().getString(R.string.flujo_de_efectivo_egreso_diferencia_negativa_label) + ": " + LocalizationHelper.parseToCurrencyString(presupuesto - monto);
        } else {
            holder.Diferencia.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        }
        holder.Diferencia.setText(diferenciaStr);
    }

    @Override
    protected CharSequence getSelectedItemFriendlyName(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor != null) {
            return cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE));
        }
        return getActivity().getTitle();
    }

    @Override
    public void onSelectedRangeChanged(Date from, Date to) {
        GlobalValues.setCurrentDateFrom(from);
        GlobalValues.setCurrentDateTo(to);
        String range = DateTimeHelper.parseToString(from) + " - " + DateTimeHelper.parseToString(to);
        setActionBarSubtitle(range);
        getLoaderManager().restartLoader(_uniqueIndentifier, null, this);
    }

    private class ViewHolder {
        TextView Nombre;
        TextView Presupuesto;
        TextView Monto;
        TextView Diferencia;
        ProgressBar SaldoActual;
        ImageView Icon;
        int _IDColumnIndex;
        int NombreColumnIndex;

        public ViewHolder(View view, Cursor cursor) {
            Nombre = (TextView) view.findViewById(R.id.tv_categoria_egreso_nombre);
            Monto = (TextView) view.findViewById(R.id.tv_categoria_egreso_monto);
            Presupuesto = (TextView) view.findViewById(R.id.tv_categoria_egreso_presupuesto);
            Diferencia = (TextView) view.findViewById(R.id.tv_categoria_egreso_diferencia);
            SaldoActual = (ProgressBar) view.findViewById(R.id.pb_categoria_egreso_saldo_actual);
            Icon = (ImageView) view.findViewById(R.id.iv_categoria_icon);
            _IDColumnIndex = cursor.getColumnIndexOrThrow(Categoria._ID);
            NombreColumnIndex = cursor.getColumnIndexOrThrow(Categoria.NOMBRE);
        }
    }

}
