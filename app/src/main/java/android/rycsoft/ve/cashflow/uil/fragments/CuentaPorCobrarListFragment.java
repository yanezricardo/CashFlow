package android.rycsoft.ve.cashflow.uil.fragments;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.CuentaPorCobrar;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.TextView;

import java.util.Date;

public class CuentaPorCobrarListFragment extends BaseListFragment {

    public CuentaPorCobrarListFragment() {
        super(R.layout.fragment_cuenta_por_cobrar_list_item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        floatingActionButton.setVisibility(View.VISIBLE);
        setActionBarSubtitle(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatingActionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        initializeFloatingActionButton();
    }

    @Override
    protected Uri getContentUri() {
        return CuentaPorCobrar.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                CuentaPorCobrar._ID,
                CuentaPorCobrar.FECHA,
                CuentaPorCobrar.MONTO,
                CuentaPorCobrar.DEUDOR,
                CuentaPorCobrar.FECHA_DE_COBRO,};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1,
                R.id.tv_cuenta_por_cobrar_desde_hasta,
                R.id.tv_cuenta_por_cobrar_monto,
                R.id.tv_cuenta_por_cobrar_deudor};
    }

    @Override
    protected String getCursorSelection() {
        if (_curFilter == null) {
            return CuentaPorCobrar.PERSONA_ID + "=?";
        } else {
            return CuentaPorCobrar.PERSONA_ID + "=? AND (" + CuentaPorCobrar.DEUDOR + " LIKE ?) OR (" + CuentaPorCobrar.MONTO + " LIKE ?)";
        }
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        if (_curFilter == null) {
            return new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId())};
        } else {
            return new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId()), "%" + _curFilter + "%", "%" + _curFilter + "%"};
        }
    }

    @Override
    protected CharSequence getSelectedItemFriendlyName(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor != null) {
            Date fecha = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA)));
            Date fechaDeCobro = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA_DE_COBRO)));
            String deudor = cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorCobrar.DEUDOR));
            return deudor + " (" + DateTimeHelper.parseToString(fecha) + " - " + DateTimeHelper.parseToString(fechaDeCobro) + ")";
        }
        return getActivity().getTitle();
    }

    @Override
    protected void bindCursorAdapterView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view, cursor);
            view.setTag(holder);
        }

        Date fecha = DateTimeHelper.createDate(cursor.getLong(holder.FechaColumnIndex));
        Date fechaDePago = DateTimeHelper.createDate(cursor.getLong(holder.FechaDePagoColumnIndex));
        double monto = cursor.getDouble(holder.MontoColumnIndex);
        String deudor = cursor.getString(holder.DeudorColumnIndex);
        holder.DesdeHasta.setText(DateTimeHelper.parseToString(fecha) + " - " + DateTimeHelper.parseToString(fechaDePago));
        holder.Monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        holder.Deudor.setText(deudor);
    }

    private class ViewHolder {
        TextView DesdeHasta;
        TextView Monto;
        TextView Deudor;
        int FechaColumnIndex;
        int MontoColumnIndex;
        int DeudorColumnIndex;
        int FechaDePagoColumnIndex;

        public ViewHolder(View view, Cursor cursor) {
            DesdeHasta = (TextView) view.findViewById(R.id.tv_cuenta_por_cobrar_desde_hasta);
            Monto = (TextView) view.findViewById(R.id.tv_cuenta_por_cobrar_monto);
            Deudor = (TextView) view.findViewById(R.id.tv_cuenta_por_cobrar_deudor);
            FechaColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA);
            MontoColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorCobrar.MONTO);
            DeudorColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorCobrar.DEUDOR);
            FechaDePagoColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorCobrar.FECHA_DE_COBRO);
        }
    }
}