package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.IngresoContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.InstrumentoFinancieroContentProvider;
import android.rycsoft.ve.cashflow.database.models.Ingreso;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class IngresoListFragment extends BaseListFragment implements DateRangeDialogFragment.IOnSelectedRangeChangedListener {
    private TextView tv_ingreso_list_total;

    public IngresoListFragment() {
        super(R.layout.fragment_ingreso_list_item);
        _uniqueIndentifier = 4;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (floatingActionButton != null)
            floatingActionButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected boolean onFloatingActionButtonClick(View view) {
        Intent i = new Intent(Intent.ACTION_INSERT, getContentUri());
        i.putExtra(Ingreso.CATEGORIA_ID, getArguments().getLong(Ingreso.CATEGORIA_ID));
        getActivity().startActivity(i);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingActionButton != null)
            floatingActionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    protected Uri getContentUri() {
        return Ingreso.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{Ingreso._ID, Ingreso.FECHA, Ingreso.MONTO, Ingreso.DESCRIPCION, Ingreso.CATEGORIA_ID, Ingreso.INSTRUMENTO_FINANCIERO_ID};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1, R.id.tv_ingreso_fecha, R.id.tv_ingreso_monto, R.id.tv_ingreso_descripcion, -1};
    }

    @Override
    protected String getCursorSelection() {
        return "(" + Ingreso.CATEGORIA_ID + "=?) AND (" + Ingreso.FECHA + " BETWEEN ? AND ?)";
    }

    @Override
    protected String getCursorOrderBy() {
        return Ingreso.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        long id = getArguments().getLong(Ingreso.CATEGORIA_ID);
        long fechaInicio = DateTimeHelper.getDate(GlobalValues.getDateFrom());
        long fechaFin = DateTimeHelper.getDate(DateTimeHelper.addDayOfYear(GlobalValues.getDateTo(), 1));
        return new String[]{Long.toString(id), String.valueOf(fechaInicio), String.valueOf(fechaFin)};
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        calculaTotal();
    }

    private void calculaTotal() {
        long categoriaId = getArguments().getLong(Ingreso.CATEGORIA_ID);
        double monto = IngresoContentProvider.getSaldo(getActivity(), (int) categoriaId,
                DateTimeHelper.getDate(GlobalValues.getDateFrom()), DateTimeHelper.getDate(GlobalValues.getDateTo()));
        if (tv_ingreso_list_total == null) {
            tv_ingreso_list_total = (TextView) getActivity().findViewById(R.id.tv_ingreso_list_total);
        }
        tv_ingreso_list_total.setText(LocalizationHelper.parseToCurrencyString(monto));
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

        Date date = DateTimeHelper.createDate(cursor.getLong(holder.FechaColumnIndex));
        String instrumentoFinanciero = InstrumentoFinancieroContentProvider.getNombreById(getActivity(), cursor.getLong(holder.InstrumentoFinancieroColumnIndex));
        double monto = cursor.getDouble(holder.MontoColumnIndex);
        String descripcion = cursor.getString(holder.DescripcionColumnIndex);
        holder.Fecha.setText(DateTimeHelper.parseToString(date));
        holder.Monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        holder.Descripcion.setText(descripcion);
        holder.InstrumentoFinanciero.setText(instrumentoFinanciero);
    }

    @Override
    protected CharSequence getSelectedItemFriendlyName(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor != null) {
            return cursor.getString(cursor.getColumnIndexOrThrow(Ingreso.DESCRIPCION));
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
        TextView Fecha;
        TextView Monto;
        TextView Descripcion;
        TextView InstrumentoFinanciero;
        int FechaColumnIndex;
        int MontoColumnIndex;
        int DescripcionColumnIndex;
        int InstrumentoFinancieroColumnIndex;

        public ViewHolder(View view, Cursor cursor) {
            Fecha = (TextView) view.findViewById(R.id.tv_ingreso_fecha);
            Monto = (TextView) view.findViewById(R.id.tv_ingreso_monto);
            Descripcion = (TextView) view.findViewById(R.id.tv_ingreso_descripcion);
            InstrumentoFinanciero = (TextView) view.findViewById(R.id.tv_ingreso_instrumento_financiero);
            FechaColumnIndex = cursor.getColumnIndexOrThrow(Ingreso.FECHA);
            MontoColumnIndex = cursor.getColumnIndexOrThrow(Ingreso.MONTO);
            DescripcionColumnIndex = cursor.getColumnIndexOrThrow(Ingreso.DESCRIPCION);
            InstrumentoFinancieroColumnIndex = cursor.getColumnIndexOrThrow(Ingreso.INSTRUMENTO_FINANCIERO_ID);
        }
    }
}
