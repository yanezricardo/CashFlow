package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.CuentaPorPagar;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class CuentaPorPagarListFragment extends BaseListFragment {

    public CuentaPorPagarListFragment() {
        super(R.layout.fragment_cuenta_por_pagar_list_item);
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
        return CuentaPorPagar.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                CuentaPorPagar._ID,
                CuentaPorPagar.FECHA,
                CuentaPorPagar.MONTO,
                CuentaPorPagar.DESCRIPCION,
                CuentaPorPagar.ACREEDOR,
                CuentaPorPagar.FECHA_DE_PAGO
        };
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1,
                R.id.tv_cuenta_por_pagar_desde_hasta,
                R.id.tv_cuenta_por_pagar_monto,
                R.id.tv_cuenta_por_pagar_acreedor
        };
    }

    @Override
    protected String getCursorSelection() {
        if (_curFilter == null) {
            return CuentaPorPagar.PERSONA_ID + "=?";
        } else {
            return CuentaPorPagar.PERSONA_ID + "=? AND (" + CuentaPorPagar.DESCRIPCION + " LIKE ?) OR (" + CuentaPorPagar.MONTO + " LIKE ?)";
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
            Date fecha = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA)));
            Date fechaDePago = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA_DE_PAGO)));
            String acreedor = cursor.getString(cursor.getColumnIndexOrThrow(CuentaPorPagar.ACREEDOR));
            return acreedor + " (" + DateTimeHelper.parseToString(fecha) + " - " + DateTimeHelper.parseToString(fechaDePago) + ")";
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
        String acreedor = cursor.getString(holder.AcreedorColumnIndex);
        holder.DesdeHasta.setText(DateTimeHelper.parseToString(fecha) + " - " + DateTimeHelper.parseToString(fechaDePago));
        holder.Monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        holder.Acreedor.setText(acreedor);
    }

    private class ViewHolder {
        TextView DesdeHasta;
        TextView Monto;
        TextView Acreedor;
        int FechaColumnIndex;
        int MontoColumnIndex;
        int AcreedorColumnIndex;
        int FechaDePagoColumnIndex;

        public ViewHolder(View view, Cursor cursor) {
            DesdeHasta = (TextView) view.findViewById(R.id.tv_cuenta_por_pagar_desde_hasta);
            Monto = (TextView) view.findViewById(R.id.tv_cuenta_por_pagar_monto);
            Acreedor = (TextView) view.findViewById(R.id.tv_cuenta_por_pagar_acreedor);
            FechaColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA);
            MontoColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorPagar.MONTO);
            AcreedorColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorPagar.ACREEDOR);
            FechaDePagoColumnIndex = cursor.getColumnIndexOrThrow(CuentaPorPagar.FECHA_DE_PAGO);
        }
    }
}
