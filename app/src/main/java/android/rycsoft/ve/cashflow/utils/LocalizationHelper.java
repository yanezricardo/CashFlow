package android.rycsoft.ve.cashflow.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import android.content.Context;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class LocalizationHelper {
    public static Locale getCurrentLocation() {
        return Locale.getDefault();
    }

    public static String parseToCurrencyString(double value, String c) {
        NumberFormat formater = NumberFormat.getCurrencyInstance();
        Currency currency = getCurrency(c);
        if (currency != null) {
            formater.setCurrency(currency);
        }
        return formater.format(value);
    }

    public static String parseToCurrencyString(double value) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        return formater.format(value);
    }

    public static String parseToPercentString(double value) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        return formater.format(value) + "%";
    }

    public static double parseToCurrency(String value) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        double result = 0;
        try {
            result = formater.parse(value).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Currency getCurrency(String c) {
        try {
            String currencyCode = TextUtils.substring(c, 0, 3);
            return Currency.getInstance(currencyCode);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
