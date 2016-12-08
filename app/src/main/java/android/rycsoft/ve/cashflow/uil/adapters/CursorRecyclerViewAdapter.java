package android.rycsoft.ve.cashflow.uil.adapters;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private LoaderManager mLoadManager;
    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
    protected int mUniqueIndentifier;
    private int mPosition;

    public CursorRecyclerViewAdapter(Context context, LoaderManager loadManager, int uniqueIndentifier) {
        mContext = context;
        mLoadManager = loadManager;
        mDataSetObserver = new NotifyingDataSetObserver();
        mUniqueIndentifier = uniqueIndentifier;
        //getLoaderManager().initLoader(mUniqueIndentifier, null, this);
    }

    public LoaderManager getLoaderManager() {
        return mLoadManager;
    }

    public Context getContext() {
        return mContext;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCurrentPosition(int position) {
        mPosition = position;
    }

    public int getCurrentPosition() {
        return mPosition;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    public long getCurrentItemId() {
        return getItemId(getCurrentPosition());
    }

    public String getItemColumnValueAsString(int position, String columnName) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getString(mCursor.getColumnIndexOrThrow(columnName));
        }
        return null;
    }

    public long getItemColumnValueAsLong(int position, String columnName) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(columnName));
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    public void restarLoader() {
        getLoaderManager().restartLoader(mUniqueIndentifier, null, this);
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), getContentUri(), getProjection(),
                getCursorSelection(), getCursorSelectionArgs(), getCursorOrderBy());
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        swapCursor(null);
    }

    public abstract Uri getContentUri();

    protected abstract String[] getProjection();

    protected abstract String getCursorSelection();

    protected abstract String[] getCursorSelectionArgs();

    protected String getCursorOrderBy() {
        return "_id";
    }

    protected abstract int[] getItemLayoutViewsResID();
}