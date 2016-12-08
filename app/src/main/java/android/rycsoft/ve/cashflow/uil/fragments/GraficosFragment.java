package android.rycsoft.ve.cashflow.uil.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.CategoriaContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.EgresoContentProvider;
import android.rycsoft.ve.cashflow.database.contentproviders.IngresoContentProvider;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.rycsoft.ve.cashflow.utils.LocalizationHelper;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraficosFragment extends Fragment implements DateRangeDialogFragment.IOnSelectedRangeChangedListener {
    PieChart egresos_pie_chart;
    PieChart ingresos_pie_chart;
    HorizontalBarChart ingresos_egresos_bar_chart;
    TextView egresos_pie_chart_title;
    TextView ingresos_pie_chart_title;
    TextView ingresos_egresos_bar_chart_title;

    public GraficosFragment() {
    }

    public static Fragment newInstance() {
        return new GraficosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graficos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initializeEgresosPieChart();
        initializeIngresosPieChart();
        initializeIngresosEgresosBarChart();
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

    @Override
    public void onSelectedRangeChanged(Date from, Date to) {
        GlobalValues.setCurrentDateFrom(from);
        GlobalValues.setCurrentDateTo(to);
        String range = DateTimeHelper.parseToString(from) + " - " + DateTimeHelper.parseToString(to);
        setActionBarSubtitle(range);
        initializeEgresosPieChart();
        initializeIngresosPieChart();
    }

    public void showDialog() {
        DateRangeDialogFragment newFragment = DateRangeDialogFragment
                .builder()
                .setDateFrom(GlobalValues.getDateFrom())
                .setDateTo(GlobalValues.getDateTo())
                .setOnSelectionChangedListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void initializeEgresosPieChart() {
        egresos_pie_chart_title = (TextView) getActivity().findViewById(R.id.egresos_pie_chart_title);
        egresos_pie_chart_title.setText(Utils.getResourceAsString(R.string.flujo_de_efectivo_egresos) + " entre " + GlobalValues.getCurrentDateRange(" y "));

        egresos_pie_chart = (PieChart) getActivity().findViewById(R.id.egresos_pie_chart);
        egresos_pie_chart.setDescription("");
        egresos_pie_chart.setNoDataText(Utils.getResourceAsString(R.string.no_char_data_available_message));
        egresos_pie_chart.setCenterText(Utils.getResourceAsString(R.string.flujo_de_efectivo_egresos));
        egresos_pie_chart.setCenterTextSize(18f);
        egresos_pie_chart.setHoleRadius(35f);
        egresos_pie_chart.setTransparentCircleRadius(40f);
        egresos_pie_chart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_INSIDE);
        egresos_pie_chart.getLegend().setEnabled(false);
        egresos_pie_chart.animateXY(2000, 2000);
        PieData data = generateEgresosPieData();
        if (data != null && data.getXValCount() > 0) {
            egresos_pie_chart.setData(generateEgresosPieData());
        }
        egresos_pie_chart.invalidate();
    }

    private void initializeIngresosPieChart() {
        ingresos_pie_chart_title = (TextView) getActivity().findViewById(R.id.ingresos_pie_chart_title);
        ingresos_pie_chart_title.setText(Utils.getResourceAsString(R.string.flujo_de_efectivo_ingresos) + " entre " + GlobalValues.getCurrentDateRange(" y "));

        ingresos_pie_chart = (PieChart) getActivity().findViewById(R.id.ingresos_pie_chart);
        ingresos_pie_chart.setDescription("");
        ingresos_pie_chart.setNoDataText(Utils.getResourceAsString(R.string.no_char_data_available_message));
        ingresos_pie_chart.setCenterText(Utils.getResourceAsString(R.string.flujo_de_efectivo_ingresos));
        ingresos_pie_chart.setCenterTextSize(18f);
        ingresos_pie_chart.setHoleRadius(35f);
        ingresos_pie_chart.setTransparentCircleRadius(40f);
        ingresos_pie_chart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART_INSIDE);
        ingresos_pie_chart.getLegend().setEnabled(false);
        ingresos_pie_chart.animateXY(2000, 2000);
        PieData data = generateIngresosPieData();
        if (data != null && data.getXValCount() > 0) {
            ingresos_pie_chart.setData(generateIngresosPieData());
        }
        ingresos_pie_chart.invalidate();
    }

    private void initializeIngresosEgresosBarChart() {
        ingresos_egresos_bar_chart_title = (TextView) getActivity().findViewById(R.id.ingresos_egresos_bar_chart_title);
        ingresos_egresos_bar_chart_title.setText(Utils.getResourceAsString(R.string.flujo_de_efectivo_ingresos) + " vs " + Utils.getResourceAsString(R.string.flujo_de_efectivo_egresos));
        ingresos_egresos_bar_chart = (HorizontalBarChart) getActivity().findViewById(R.id.ingresos_egresos_bar_chart);
        ingresos_egresos_bar_chart.setDescription("");
        ingresos_egresos_bar_chart.setNoDataText(Utils.getResourceAsString(R.string.no_char_data_available_message));
        ingresos_egresos_bar_chart.getLegend().setEnabled(false);
        ingresos_egresos_bar_chart.getAxisLeft().setEnabled(false);
        ingresos_egresos_bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        ingresos_egresos_bar_chart.animateXY(2000, 2000);
        BarData data = generateIngresosEgresosBarData();
        if(data != null && data.getXValCount() > 0) {
            ingresos_egresos_bar_chart.setData(generateIngresosEgresosBarData());
        }
        ingresos_egresos_bar_chart.invalidate();
    }

    private PieData generateEgresosPieData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        int index = 0;
        long fechaInicio = DateTimeHelper.getDate(GlobalValues.getDateFrom());
        long fechaFin = DateTimeHelper.getDate(GlobalValues.getDateTo());
        double totalEgresos = EgresoContentProvider.getTotalEgreso(getActivity(), fechaInicio, fechaFin);
        List<Categoria> categorias = CategoriaContentProvider.getAll(getActivity(), Categoria.TIPO + "=?", new String[]{"Egreso"});
        int[] colores = new int[categorias.size()];
        for (Categoria categoria : categorias) {
            double monto = EgresoContentProvider.getSaldo(getActivity(), categoria.ID, fechaInicio, fechaFin);
            if (monto > 0) {
                colores[index] = categoria.Color;
                xVals.add(categoria.Nombre);
                yVals.add(new Entry((float) Utils.toPorcentaje(totalEgresos, monto), index++));
            }
        }
        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setColors(colores);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return LocalizationHelper.parseToPercentString(v);
            }
        });
        return data;
    }

    private PieData generateIngresosPieData() {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        int index = 0;
        long fechaInicio = DateTimeHelper.getDate(GlobalValues.getDateFrom());
        long fechaFin = DateTimeHelper.getDate(GlobalValues.getDateTo());
        double totalIngresos = IngresoContentProvider.getTotalIngreso(getActivity(), fechaInicio, fechaFin);
        List<Categoria> categorias = CategoriaContentProvider.getAll(getActivity(), Categoria.TIPO + "=?", new String[]{"Ingreso"});
        int[] colores = new int[categorias.size()];
        for (Categoria categoria : categorias) {
            double monto = IngresoContentProvider.getSaldo(getActivity(), categoria.ID, fechaInicio, fechaFin);
            if (monto > 0) {
                colores[index] = categoria.Color;
                xVals.add(categoria.Nombre);
                yVals.add(new Entry((float) Utils.toPorcentaje(totalIngresos, monto), index++));
            }
        }

        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setColors(colores);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return LocalizationHelper.parseToPercentString(v);
            }
        });
        return data;
    }

    private BarData generateIngresosEgresosBarData() {
        ArrayList<BarDataSet> dataSetList = new ArrayList<>();
        ArrayList<BarEntry> entriesIngresos = new ArrayList<>();
        ArrayList<BarEntry> entriesEgresos = new ArrayList<>();
        int index = 0;
        int year = DateTimeHelper.getYear(DateTimeHelper.getNow());
        List<String> meses = DateTimeHelper.getMonthsOfYear(year);
        List<String> xLabels = new ArrayList<>();
        int[] coloresIngreso = new int[meses.size()];
        int[] coloresEgreso = new int[meses.size()];
        for (String mes : meses) {
            Date date = DateTimeHelper.createDate(year, DateTimeHelper.getMonthInt(mes), 1);
            long fechaInicio = DateTimeHelper.getDate(DateTimeHelper.getFirstDayOfMonth(date));
            long fechaFin = DateTimeHelper.getDate(DateTimeHelper.getLastDayOfMonth(date));

            double totalEgresos = EgresoContentProvider.getTotalEgreso(getActivity(), fechaInicio, fechaFin);
            double totalIngresos = IngresoContentProvider.getTotalIngreso(getActivity(), fechaInicio, fechaFin);

            if (totalEgresos > 0 || totalIngresos > 0) {
                xLabels.add(mes);
                entriesEgresos.add(new BarEntry((float) totalEgresos, index));
                coloresEgreso[index] = Color.RED;

                entriesIngresos.add(new BarEntry((float) totalIngresos, index));
                coloresIngreso[index] = getActivity().getResources().getColor(R.color.ColorPrimaryDark);

                index++;
            }
        }


        BarDataSet dataSetIngreso = new BarDataSet(entriesIngresos, Utils.getResourceAsString(R.string.flujo_de_efectivo_ingresos));
        dataSetIngreso.setColors(coloresIngreso);
        dataSetList.add(dataSetIngreso);

        BarDataSet dataSetEgreso = new BarDataSet(entriesEgresos, Utils.getResourceAsString(R.string.flujo_de_efectivo_egresos));
        dataSetEgreso.setColors(coloresEgreso);
        dataSetList.add(dataSetEgreso);

        return new BarData(xLabels, dataSetList);
    }

    protected void setActionBarSubtitle(String subtitle) {
        AppCompatActivity main = (AppCompatActivity) getActivity();
        if (main != null) {
            ActionBar actionBar = main.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(subtitle);
            }
        }
    }
}
