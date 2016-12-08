package android.rycsoft.ve.cashflow;

import android.rycsoft.ve.cashflow.database.models.UserModel;
import android.rycsoft.ve.cashflow.utils.DateTimeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public final class GlobalValues {
    private static UserModel _currentPerson;
    private static List<IGlobalValueChanged<UserModel>> _currentPersonChangedListeners = new ArrayList<>();
    public static String CURRENT_MES;
    public static String CURRENT_QUINCENA;
    private static Date mDateFrom = Calendar.getInstance().getTime();
    private static Date mDateTo = Calendar.getInstance().getTime();

    static {
        mDateFrom = DateTimeHelper.getFirstDayOfCurrentFortnight();
        mDateTo = DateTimeHelper.getNow();
    }

    public static void setCurrentPerson(UserModel person) {
        _currentPerson = person;
        raiseCurrentPersonChanged();
    }

    public static UserModel getCurrentPerson() {
        return _currentPerson;
    }

    private static void raiseCurrentPersonChanged() {
        for (IGlobalValueChanged<UserModel> listener : _currentPersonChangedListeners) {
            listener.onGlobalValueChanged(_currentPerson);
        }
    }

    public static void registerCurrentPersonChangedListener(IGlobalValueChanged<UserModel> listener) {
        boolean existe = false;
        for (IGlobalValueChanged<UserModel> item : _currentPersonChangedListeners) {
            if (item.getClass() == listener.getClass()) {
                existe = true;
                break;
            }
        }
        if (!existe) {
            _currentPersonChangedListeners.add(listener);
        }
    }

    public static void setCurrentDateFrom(Date from) {
        mDateFrom = from;
    }

    public static void setCurrentDateTo(Date to) {
        mDateTo = to;
    }

    public static Date getDateFrom() {
        return mDateFrom;
    }

    public static Date getDateTo() {
        return mDateTo;
    }


    public static String getCurrentDateRange() {
        return getCurrentDateRange(" - ");
    }

    public static String getCurrentDateRange(String separador) {
        return DateTimeHelper.parseToString(GlobalValues.getDateFrom()) + separador + DateTimeHelper.parseToString(GlobalValues.getDateTo());
    }

    public static Locale getCurrentLocale() {
        return null;
    }

    public static String getCurrencySymbol() {
        return "Bs. ";
    }

    public interface IGlobalValueChanged<T> {
        void onGlobalValueChanged(T value);
    }
}
