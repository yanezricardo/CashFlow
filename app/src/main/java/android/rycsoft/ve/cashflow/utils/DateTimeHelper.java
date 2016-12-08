package android.rycsoft.ve.cashflow.utils;

import android.rycsoft.ve.cashflow.database.models.Categoria;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateTimeHelper {
    public static String parseToString(int year, int monthOfYear, int dayOfMonth) {
        return parseToString(year, monthOfYear, dayOfMonth, SimpleDateFormat.getDateInstance());
    }

    public static String parseToString(int year, int monthOfYear, int dayOfMonth, DateFormat dateFormat) {
        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        return dateFormat.format(date);
    }

    public static String parseToString(Date date) {
        return parseToString(date, SimpleDateFormat.getDateInstance());
    }

    public static String parseToString(Date date, DateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public static String parseToString(long milliseconds) {
        return parseToString(milliseconds, SimpleDateFormat.getDateInstance());
    }

    public static String parseToString(long milliseconds, DateFormat dateFormat) {
        return dateFormat.format(new Date(milliseconds));
    }

    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    public static Date getMinDate() {
        return createDate(1900, 1, 1);
    }

    public static Date addDayOfYear(long milliseconds, int days) {
        return addDayOfYear(createDate(milliseconds), days);
    }

    public static Date addDayOfYear(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public static Date createDate(int year, int monthOfYear, int dayOfMonth) {
        return new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getLastMonth(Date date) {
        int days = getDayOfMonth(date) * -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.get(Calendar.MONTH);
    }
/*
    public static int getLastMonth() {
        Date date = getNow();
        int days = getDayOfMonth(date) * -1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.get(Calendar.MONTH);
    }
*/

    public static int getMonthInt(String mothName) {
        if (StringHelper.isNullOrEmpty(mothName)) {
            return 0;
        }

        if (mothName.equalsIgnoreCase("Enero"))
            return 0;
        if (mothName.equalsIgnoreCase("Febrero"))
            return 1;
        if (mothName.equalsIgnoreCase("Marzo"))
            return 2;
        if (mothName.equalsIgnoreCase("Abril"))
            return 3;
        if (mothName.equalsIgnoreCase("Mayo"))
            return 4;
        if (mothName.equalsIgnoreCase("Junio"))
            return 5;
        if (mothName.equalsIgnoreCase("Julio"))
            return 6;
        if (mothName.equalsIgnoreCase("Agosto"))
            return 7;
        if (mothName.equalsIgnoreCase("Septiembre"))
            return 8;
        if (mothName.equalsIgnoreCase("Octubre"))
            return 9;
        if (mothName.equalsIgnoreCase("Noviembre"))
            return 10;
        if (mothName.equalsIgnoreCase("Diciembre"))
            return 11;
        return 0;
    }

    public static String getMonthName(Date date) {
        int month = getMonth(date);
        return getMonthName(month);
    }

    public static String getMonthName(int month) {
        switch (month) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
        }
        return "";
    }

    public static String getFortnight(Date date) {
        int day = getDayOfMonth(date);
        if (day <= 15) {
            return "1ra Quin.";
        } else {
            return "2da Quin.";
        }
    }

    public static long getStartDate(String monthName, String fortnight) {
        int month = getMonthInt(monthName);
        if (!StringHelper.isNullOrEmpty(fortnight)) {
            if (fortnight.equalsIgnoreCase("1ra Quin.")) {
                int year = getYear(getNow());
                Date date = createDate(year, month, 1);
                return date.getTime();
            } else {
                int year = getYear(getNow());
                Date date = createDate(year, month, 16);
                return date.getTime();
            }
        }
        return 0;
    }

    public static Date getStartDate(int month, String fortnight) {
        if (!StringHelper.isNullOrEmpty(fortnight)) {
            if (fortnight.equalsIgnoreCase("1ra Quin.")) {
                int year = getYear(getNow());
                Date date = createDate(year, month, 1);
                return date;
            } else {
                int year = getYear(getNow());
                Date date = createDate(year, month, 16);
                return date;
            }
        }
        return getNow();
    }

    public static long getEndDate(String monthName, String fortnight) {
        int month = getMonthInt(monthName);
        if (!StringHelper.isNullOrEmpty(fortnight)) {
            if (fortnight.equalsIgnoreCase("1ra Quin.")) {
                int year = getYear(getNow());
                Date date = createDate(year, month, 16);
                return date.getTime();
            } else {
                int year = getYear(getNow());
                Date date = createDate(year, month, 1);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                date = createDate(getYear(date), getMonth(date), maxDay);
                return date.getTime();
            }
        }
        return 0;
    }

    public static Date getEndDate(int month, String fortnight) {
        if (!StringHelper.isNullOrEmpty(fortnight)) {
            if (fortnight.equalsIgnoreCase("1ra Quin.")) {
                int year = getYear(getNow());
                Date date = createDate(year, month, 16);
                return date;
            } else {
                int year = getYear(getNow());
                Date date = createDate(year, month, 1);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                date = createDate(getYear(date), getMonth(date), maxDay);
                return date;
            }
        }
        return getNow();
    }

    public static Date createDate(long time) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time);
        return calendar.getTime();
    }

    public static Date getNextFortnight() {
        Date now = getNow();
        int day = getDayOfMonth(now);
        if (day <= 15) {
            return createDate(getYear(now), getMonth(now), 15);
        } else {
            return createDate(getYear(now), getMonth(now), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        }
    }

    public static Date getFirstDayOfCurrentFortnight() {
        Date now = getNow();
        int day = getDayOfMonth(now);
        if (day <= 15) {
            return createDate(getYear(now), getMonth(now), 1);
        } else {
            return createDate(getYear(now), getMonth(now), 15);
        }
    }

    public static Date getLastDayOfCurrentFortnight() {
        return getNextFortnight();
    }

    public static Date getFirstDayOfCurrentMonth() {
        Date now = getNow();
        return createDate(getYear(now), getMonth(now), 1);
    }

    public static Date getLastDayOfCurrentMonth() {
        Date now = getNow();
        return createDate(getYear(now), getMonth(now), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    public static Date getFirstDayOfMonth(Date date) {
        return createDate(getYear(date), getMonth(date), 1);
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return createDate(getYear(getNow()), getMonth(date), calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    public static Date getLastYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    public static Date getLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    public static Date getLastDayOfFortnight(Date date) {
        int day = getDayOfMonth(date);
        if (day <= 15) {
            return createDate(getYear(date), getMonth(date), 15);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return createDate(getYear(date), getMonth(date), calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
    }

    public static long getDate(Date date) {
        DateFormat formatter = SimpleDateFormat.getDateInstance();
        Date dateWithZeroTime = null;
        try {
            dateWithZeroTime = formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateWithZeroTime.getTime();
    }

    public static List<String> getMonthsOfYear(int year) {
        List<String> result = new ArrayList<>();
        int monthCount = 12;
        if(getYear(getNow()) == year) {
            monthCount = getMonth(getNow()) + 1;
        }
        for (int i = 0; i < monthCount; i++) {
            result.add(getMonthName(i));
        }
        return result;
    }
}
