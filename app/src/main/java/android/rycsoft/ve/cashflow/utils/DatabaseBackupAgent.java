package android.rycsoft.ve.cashflow.utils;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;
import android.rycsoft.ve.cashflow.database.contentproviders.DatabaseHelper;

import java.io.File;
import java.io.IOException;

public class DatabaseBackupAgent extends BackupAgentHelper {
    @Override
    public void onCreate() {
        File file = getDatabasePath(DatabaseHelper.DATABASE_NAME);
        FileBackupHelper database = new FileBackupHelper(this, file.getName());
        addHelper(DatabaseHelper.DATABASE_NAME, database);
    }

    @Override
    public File getFilesDir() {
        File path = getDatabasePath(DatabaseHelper.DATABASE_NAME);
        return path.getParentFile();
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);
    }

}
