package android.rycsoft.ve.cashflow.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public final class Utils {
    private static Context _context;

    public static Context getContext() {
        return _context;
    }

    public static void setContext(Context _context) {
        Utils._context = _context;
    }

    public static String getFirstTwoLetersOfString(int textResId) {
        if (_context == null) {
            return "";
        }
        String text = _context.getResources().getString(textResId);
        return getFirstTwoLetersOfString(text);
    }

    public static String getFirstTwoLetersOfString(String text) {
        if (_context == null) {
            return "";
        }
        if (text != null && text.length() > 0) {
            String letras = "";
            String[] palabras = text.split(" ");
            for (String palabra : palabras) {
                letras += palabra.substring(0, 1);
            }
            if (letras.length() > 2) {
                return letras.substring(0, 2);
            } else {
                return letras;
            }
        }
        return "";
    }

    public static String getResourceAsString(int resId) {
        if (_context == null) {
            return "";
        }
        return _context.getResources().getString(resId);
    }

    public static String getAccountEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    public static String getAccountName(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccounts(); //accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    public static boolean SNToBoolean(String value) {
        boolean result = false;
        if(value != null) {
            if("S".equals(value.toUpperCase())) {
                result = true;
            }
        }
        return  result;
    }

    public static String booleanToSN(boolean value) {
        String result = "N";
        if(value) {
            result = "S";
        }
        return result;
    }

    public static String colorToString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int toPorcentaje(double mayor, double menor) {
        if(menor <= 0) {
            return 0;
        }
        return (int) ((menor / mayor) * 100);
    }
}
