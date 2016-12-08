package android.rycsoft.ve.cashflow.utils;

import android.content.Context;
import android.os.Environment;
import android.rycsoft.ve.cashflow.database.contentproviders.DatabaseHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseBackupHelper {
    private File _databasePath;
    private Context _context;

    public DatabaseBackupHelper(Context context) {
        _context = context;
        _databasePath = _context.getDatabasePath(DatabaseHelper.DATABASE_NAME);
    }

    public boolean doRestore() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            DatabaseHelper.createDatabaseIfNecesary(_context);
            File backupFile = getDatabaseBackupFile();
            return copyFile(backupFile, _databasePath);
        }
        return false;
    }

    public boolean doBackup() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File backupFile = getDatabaseBackupFile();
            return copyFile(_databasePath, backupFile);
        }
        return false;
    }

    private boolean copyFile(File source, File target) {
        boolean result = true;
        if (source == null || target == null) {
            return false;
        }

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(source));
            OutputStream os = new FileOutputStream(target);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            result = false;
            Log.w("Storage", "Error reading " + source, e);
        }
        return result;
    }

    private File getDatabaseBackupFile() {
        File backupPath = new File(Environment.getExternalStorageDirectory(), "Contabilidad/Backups");
        File backupFile = new File(backupPath, DatabaseHelper.DATABASE_NAME);
        if (!backupPath.exists()) {
            backupPath.mkdirs();
        }
        return backupFile;
    }

}