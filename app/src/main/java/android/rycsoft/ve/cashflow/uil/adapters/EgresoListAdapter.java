package android.rycsoft.ve.cashflow.uil.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.InstrumentoFinancieroContentProvider;
import android.rycsoft.ve.cashflow.database.models.Egreso;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class EgresoListAdapter extends CursorRecyclerViewAdapter<EgresoListAdapter.ViewHolder> {
    private long mCategoriaId;
    private OnItemClickListener listener;

    public EgresoListAdapter(Context context, LoaderManager loadManager, long categoriaId) {
        super(context, loadManager, 2);
        mCategoriaId = categoriaId;
        getLoaderManager().initLoader(mUniqueIndentifier, null, this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_egreso_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        int fechaColumnIndex = cursor.getColumnIndexOrThrow(Egreso.FECHA);
        int montoColumnIndex = cursor.getColumnIndexOrThrow(Egreso.MONTO);
        int descripcionColumnIndex = cursor.getColumnIndexOrThrow(Egreso.DESCRIPCION);
        int instrumentoFinancieroColumnIndex = cursor.getColumnIndexOrThrow(Egreso.INSTRUMENTO_FINANCIERO_ID);

        Date date = DateTimeHelper.createDate(cursor.getLong(fechaColumnIndex));
        String instrumentoFinanciero = InstrumentoFinancieroContentProvider.getNombreById(getContext(), cursor.getLong(instrumentoFinancieroColumnIndex));
        double monto = cursor.getDouble(montoColumnIndex);
        String descripcion = cursor.getString(descripcionColumnIndex);
        holder.Fecha.setText(DateTimeHelper.parseToString(date));
        holder.Monto.setText(LocalizationHelper.parseToCurrencyString(monto));
        holder.Descripcion.setText(descripcion);
        holder.InstrumentoFinanciero.setText(instrumentoFinanciero);
    }

    @Override
    public Uri getContentUri() {
        return Egreso.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{Egreso._ID, Egreso.FECHA, Egreso.MONTO, Egreso.DESCRIPCION, Egreso.CATEGORIA_ID, Egreso.INSTRUMENTO_FINANCIERO_ID};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1, R.id.tv_egreso_fecha, R.id.tv_egreso_monto, R.id.tv_egreso_descripcion, -1};
    }

    @Override
    protected String getCursorSelection() {
        return "(" + Egreso.CATEGORIA_ID + "=?) AND (" + Egreso.FECHA + " BETWEEN ? AND ?)";
    }

    @Override
    protected String getCursorOrderBy() {
        return Egreso.DEFAULT_SORT_ORDER;
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        long fechaInicio = DateTimeHelper.getDate(GlobalValues.getDateFrom());
        long fechaFin = DateTimeHelper.getDate(DateTimeHelper.addDayOfYear(GlobalValues.getDateTo(), 1));
        return new String[]{Long.toString(mCategoriaId), String.valueOf(fechaInicio), String.valueOf(fechaFin)};
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {
        TextView Fecha;
        TextView Monto;
        TextView InstrumentoFinanciero;
        TextView Descripcion;

        public ViewHolder(View view) {
            super(view);
            Fecha = (TextView) view.findViewById(R.id.tv_egreso_fecha);
            Monto = (TextView) view.findViewById(R.id.tv_egreso_monto);
            Descripcion = (TextView) view.findViewById(R.id.tv_egreso_descripcion);
            InstrumentoFinanciero = (TextView) view.findViewById(R.id.tv_egreso_instrumento_financiero);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            setCurrentPosition(getLayoutPosition());
            if (listener != null) {
                listener.onItemClick(itemView, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            setCurrentPosition(getLayoutPosition());
            itemView.showContextMenu();
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            setCurrentPosition(getLayoutPosition());
            if (listener != null) {
                listener.onCreateContextMenu(menu, itemView, getLayoutPosition(), menuInfo);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);

        void onCreateContextMenu(ContextMenu menu, View itemView, int position, ContextMenu.ContextMenuInfo menuInfo);
    }
}