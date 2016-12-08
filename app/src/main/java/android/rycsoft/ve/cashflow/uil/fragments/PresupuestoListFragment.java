package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.PresupuestoContentProvider;
import android.rycsoft.ve.cashflow.database.models.Presupuesto;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class PresupuestoListFragment extends BaseListFragment {

    public PresupuestoListFragment() {
        super(R.layout.fragment_presupuesto_list_item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFloatingActionButton();
        floatingActionButton.setVisibility(View.VISIBLE);
        setActionBarSubtitle(null);
    }

    @Override
    protected boolean onFloatingActionButtonClick(View view) {
        Intent i = new Intent(Intent.ACTION_INSERT, getContentUri());
        getActivity().startActivity(i);
        return true;
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
        return Presupuesto.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{
                Presupuesto._ID,
                Presupuesto.FECHA_DESDE,
                Presupuesto.FECHA_HASTA,};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1,
                R.id.tv_presupuesto_list_desde_hasta,
                R.id.tv_presupuesto_list_total_egresos,
                R.id.tv_presupuesto_list_total_ingresos,};
    }

    @Override
    protected String getCursorSelection() {
        return Presupuesto.PERSONA_ID + "=?";
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        return new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId())};
    }

    @Override
    protected CharSequence getSelectedItemFriendlyName(int position) {
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        if (cursor != null) {
            Date fecha = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_DESDE)));
            Date fechaDeCobro = DateTimeHelper.createDate(cursor.getLong(cursor.getColumnIndexOrThrow(Presupuesto.FECHA_HASTA)));
            return DateTimeHelper.parseToString(fecha) + " - " + DateTimeHelper.parseToString(fechaDeCobro);
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

        int presupuestoId = cursor.getInt(cursor.getColumnIndexOrThrow(Presupuesto._ID));
        Date fechaDesde = DateTimeHelper.createDate(cursor.getLong(holder.FechaDesdeColumnIndex));
        Date fechaHasta = DateTimeHelper.createDate(cursor.getLong(holder.FechaHastaColumnIndex));
        double totalEgresos = PresupuestoContentProvider.getTotalEgresos(getActivity(), presupuestoId);
        double totalIngresos = PresupuestoContentProvider.getTotalIngresos(getActivity(), presupuestoId);
        String totalEgresosStr = getActivity().getResources().getString(R.string.presupuesto_total_egresos) + ": " + LocalizationHelper.parseToCurrencyString(totalEgresos);
        String totalIngresosStr = getActivity().getResources().getString(R.string.presupuesto_total_ingresos) + ": " + LocalizationHelper.parseToCurrencyString(totalIngresos);
        holder.DesdeHasta.setText(DateTimeHelper.parseToString(fechaDesde) + " - " + DateTimeHelper.parseToString(fechaHasta));
        holder.TotalEgresos.setText(totalEgresosStr);
        holder.TotalIngresos.setText(totalIngresosStr);
    }

    private class ViewHolder {
        TextView DesdeHasta;
        TextView TotalEgresos;
        TextView TotalIngresos;
        int FechaDesdeColumnIndex;
        int FechaHastaColumnIndex;

        public ViewHolder(View view, Cursor cursor) {
            DesdeHasta = (TextView) view.findViewById(R.id.tv_presupuesto_list_desde_hasta);
            TotalEgresos = (TextView) view.findViewById(R.id.tv_presupuesto_list_total_egresos);
            TotalIngresos = (TextView) view.findViewById(R.id.tv_presupuesto_list_total_ingresos);
            FechaDesdeColumnIndex = cursor.getColumnIndexOrThrow(Presupuesto.FECHA_DESDE);
            FechaHastaColumnIndex = cursor.getColumnIndexOrThrow(Presupuesto.FECHA_HASTA);
        }
    }
}