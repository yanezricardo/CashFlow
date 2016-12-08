package android.rycsoft.ve.cashflow.utils;

import android.database.Cursor;
import android.text.TextUtils;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIElementHelper {
    public static List<Field> getTypeFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        if (type != null) {
            for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                Collections.addAll(result, fields);
            }
        }
        return result;
    }

    public static Field getTypeField(Class<?> type, String fieldName) {
        List<Field> fields = getTypeFields(type);
        Field result = null;
        if (type != null && !TextUtils.isEmpty(fieldName)) {
            for (Field f : fields) {
                if (f.getName().equals(fieldName)) {
                    result = f;
                    break;
                }
            }
        }
        return result;
    }

    public static Object getTypeFieldValue(Object instance, String fieldName) {
        Object result = null;
        if (instance != null && !TextUtils.isEmpty(fieldName)) {
            Field field = getTypeField(instance.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                try {
                    result = field.get(instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static int spinnerIndexOf(Spinner spinner, String value, String fieldName) {
        if (spinner != null) {
            for (int i = 0; i < spinner.getCount(); i++) {
                Object item = spinner.getItemAtPosition(i);
                String fieldValue = String.valueOf(getTypeFieldValue(item, fieldName));
                if (value.equals(fieldValue)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int spinnerIndexOf(Spinner spinner, String value) {
        if (spinner != null) {
            for (int i = 0; i < spinner.getCount(); i++) {
                Object item = spinner.getItemAtPosition(i);
                if (value.equals(item)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void spinnerSetSelection(Spinner spinner, String value) {
        int position = UIElementHelper.spinnerIndexOf(spinner, value);
        spinner.setSelection(position);
    }

    public static void spinnerSetSelection(Spinner spinner, String value, String fieldName) {
        int position = UIElementHelper.spinnerIndexOf(spinner, value, fieldName);
        spinner.setSelection(position);
    }

    public static String spinnerGetSelectedItemOrDefault(Spinner spinner) {
        String result = "";
        if (spinner != null) {
            Object selected = spinner.getSelectedItem();
            if (selected != null) {
                result = selected.toString();
            }
        }
        return result;
    }

    public static String spinnerGetSelectedItemFromCursorOrDefault(Spinner spinner, String columnName) {
        String result = "";
        if (spinner != null) {
            Cursor cursor = (Cursor) spinner.getSelectedItem();
            if (cursor != null) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
            }
        }
        return result;
    }

    public static void spinnerSetSelectionFromCursor(Spinner spinner, String value, String columnName) {
        int position = UIElementHelper.spinnerIndexOfFromCursor(spinner, value, columnName);
        spinner.setSelection(position);
    }

    private static int spinnerIndexOfFromCursor(Spinner spinner, String value, String columnName) {
        for (int i = 0; i < spinner.getCount(); i++) {
            Cursor cursor = (Cursor) spinner.getItemAtPosition(i);
            if (cursor != null) {
                String columnValue = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
                if (TextUtils.equals(value, columnValue)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
