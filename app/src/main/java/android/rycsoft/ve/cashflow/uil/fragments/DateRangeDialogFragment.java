package android.rycsoft.ve.cashflow.uil.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;

public class DateRangeDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener, DatePicker.OnDateChangedListener {
    private static DateRangeDialogFragment mFragment;
    private Spinner sp_quick_pick;
    private DatePicker dp_date_from;
    private DatePicker dp_date_to;
    private IOnSelectedRangeChangedListener mListener;
    private static Date mDateFrom;
    private static Date mDateTo;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mDateFrom == null || mDateTo == null) {
            mDateFrom = DateTimeHelper.getFirstDayOfCurrentFortnight();
            mDateTo = DateTimeHelper.getNow();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_date_range_dialog, null);
        sp_quick_pick = (Spinner) view.findViewById(R.id.sp_data_range_quick_pick);
        sp_quick_pick.setOnItemSelectedListener(this);
        dp_date_from = (DatePicker) view.findViewById(R.id.dp_date_from);
        dp_date_from.init(DateTimeHelper.getYear(mDateFrom), DateTimeHelper.getMonth(mDateFrom), DateTimeHelper.getDayOfMonth(mDateFrom), this);
        dp_date_to = (DatePicker) view.findViewById(R.id.dp_date_to);
        dp_date_to.init(DateTimeHelper.getYear(mDateTo), DateTimeHelper.getMonth(mDateTo), DateTimeHelper.getDayOfMonth(mDateTo), this);
        builder.setView(view)
                .setTitle(R.string.date_range_dialog_title)
                .setPositiveButton(R.string.date_range_dialog_accept_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        raiseOnSelectionChanged();
                    }
                })
                .setNegativeButton(R.string.date_range_dialog_cancel_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    private void raiseOnSelectionChanged() {
        if (mListener != null) {
            Date from = DateTimeHelper.createDate(dp_date_from.getYear(), dp_date_from.getMonth(), dp_date_from.getDayOfMonth());
            Date to = DateTimeHelper.createDate(dp_date_to.getYear(), dp_date_to.getMonth(), dp_date_to.getDayOfMonth());

            String quickPickSelected = (String) sp_quick_pick.getSelectedItem();
            if (!quickPickSelected.equals("Selección rápida")) {

            }

            mListener.onSelectedRangeChanged(from, to);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) sp_quick_pick.getItemAtPosition(position);
        if (item.equals("Quincena actual")) {
            mDateFrom = DateTimeHelper.getFirstDayOfCurrentFortnight();
            mDateTo = DateTimeHelper.getNow();
        } else if (item.equals("Quincena anterior")) {
            Date now = DateTimeHelper.getNow();
            int day = DateTimeHelper.getDayOfMonth(now);
            if (day <= 15) {
                Date lastMonth = DateTimeHelper.getLastMonth();
                mDateFrom = DateTimeHelper.createDate(DateTimeHelper.getYear(lastMonth), DateTimeHelper.getMonth(lastMonth), 16);
                mDateTo = DateTimeHelper.getLastDayOfMonth(lastMonth);
            } else {
                mDateFrom = DateTimeHelper.createDate(DateTimeHelper.getYear(now), DateTimeHelper.getMonth(now), 1);
                mDateTo = DateTimeHelper.createDate(DateTimeHelper.getYear(now), DateTimeHelper.getMonth(now), 15);
            }
        } else if (item.equals("Mes actual")) {
            mDateFrom = DateTimeHelper.getFirstDayOfCurrentMonth();
            mDateTo = DateTimeHelper.getNow();
        } else if (item.equals("Mes anterior")) {
            Date lastMonth = DateTimeHelper.getLastMonth();
            mDateFrom = DateTimeHelper.getFirstDayOfMonth(lastMonth);
            mDateTo = DateTimeHelper.getLastDayOfMonth(lastMonth);
        } else if (item.equals("Año actual")) {
            Date now = DateTimeHelper.getNow();
            mDateFrom = DateTimeHelper.createDate(DateTimeHelper.getYear(now), 0, 1);
            mDateTo = now;
        } else if (item.equals("Año anterior")) {
            Date lastYear = DateTimeHelper.getLastYear();
            mDateFrom = DateTimeHelper.createDate(DateTimeHelper.getYear(lastYear), 0, 1);
            mDateTo = DateTimeHelper.createDate(DateTimeHelper.getYear(lastYear), 11, 31);
        } else if (item.equals("Hoy")) {
            Date now = DateTimeHelper.getNow();
            mDateFrom = now;
            mDateTo = now;
        }

        dp_date_to.updateDate(DateTimeHelper.getYear(mDateTo), DateTimeHelper.getMonth(mDateTo), DateTimeHelper.getDayOfMonth(mDateTo));
        dp_date_from.updateDate(DateTimeHelper.getYear(mDateFrom), DateTimeHelper.getMonth(mDateFrom), DateTimeHelper.getDayOfMonth(mDateFrom));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public DateRangeDialogFragment setOnSelectionChangedListener(IOnSelectedRangeChangedListener listener) {
        mListener = listener;
        return mFragment;
    }

    public static DateRangeDialogFragment builder() {
        mFragment = new DateRangeDialogFragment();
        return mFragment;
    }

    public DateRangeDialogFragment setDateFrom(Date dateFrom) {
        mDateFrom = dateFrom;
        return mFragment;
    }

    public DateRangeDialogFragment setDateTo(Date dateTo) {
        mDateTo = dateTo;
        return mFragment;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (view == dp_date_from) {
            Date date = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
            Date dateTo = DateTimeHelper.createDate(dp_date_to.getYear(), dp_date_to.getMonth(), dp_date_to.getDayOfMonth());
            if (date.getTime() > dateTo.getTime()) {
                dp_date_to.updateDate(year, monthOfYear, dayOfMonth);
            }
        } else if (view == dp_date_to) {
            Date date = DateTimeHelper.createDate(year, monthOfYear, dayOfMonth);
            Date dateFrom = DateTimeHelper.createDate(dp_date_from.getYear(), dp_date_from.getMonth(), dp_date_from.getDayOfMonth());
            if (date.getTime() < dateFrom.getTime()) {
                dp_date_from.updateDate(year, monthOfYear, dayOfMonth);
            }
        }
    }

    public interface IOnSelectedRangeChangedListener {
        void onSelectedRangeChanged(Date from, Date to);
    }
}
