package android.rycsoft.ve.cashflow.uil.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.UserModel;
import android.rycsoft.ve.cashflow.uil.widgets.AddFloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public abstract class BaseListFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, LoaderManager.LoaderCallbacks<Cursor>, GlobalValues.IGlobalValueChanged<UserModel>, AbsListView.OnScrollListener, View.OnTouchListener {
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    //public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
    public static final int MENU_ITEM_EDIT = Menu.FIRST + 2;
    private android.support.v4.widget.SimpleCursorAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    SearchView mSearchView;
    protected String _curFilter;
    private int _itemLayoutResID;
    AddFloatingActionButton floatingActionButton;
    int _uniqueIndentifier = 0;

    public BaseListFragment(int itemLayoutResID) {
        _itemLayoutResID = itemLayoutResID;
        GlobalValues.registerCurrentPersonChangedListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initializeAdapter();
        setListShown(false);
        getListView().setOnScrollListener(this);
        getLoaderManager().initLoader(_uniqueIndentifier, null, this);
        initializeFloatingActionButton();
        registerForContextMenu(getListView());
    }

    protected void initializeFloatingActionButton() {
        //if (floatingActionButton == null) {
            floatingActionButton = (AddFloatingActionButton) getActivity().findViewById(R.id.floating_action_button);
        //}
        if (floatingActionButton != null) {
            floatingActionButton.setOnTouchListener(this);
        }
    }

    protected boolean onFloatingActionButtonClick(View view) {
        getActivity().startActivity(new Intent(Intent.ACTION_INSERT, getContentUri()));
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        MenuItem item = menu.add(R.string.list_search_label);
        item.setIcon(R.drawable.ic_action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new CustomSearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }

    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        if (_curFilter == null && newFilter == null) {
            return true;
        }
        if (_curFilter != null && _curFilter.equals(newFilter)) {
            return true;
        }
        _curFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getContentUri(), id);

        String action = getActivity().getIntent().getAction();
        if (!Intent.ACTION_PICK.equals(action) && !Intent.ACTION_GET_CONTENT.equals(action)) {
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(getSelectedItemFriendlyName(info.position));
        menu.add(this.getId(), MENU_ITEM_DELETE, 0, getContextMenuDescription(MENU_ITEM_DELETE));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != this.getId()) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                Uri noteUri = ContentUris.withAppendedId(getContentUri(), info.id);
                getActivity().getContentResolver().delete(noteUri, null, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException ignored) {

        }
    }

    protected CharSequence getSelectedItemFriendlyName(int position) {
        return getActivity().getTitle();
    }

    protected CharSequence getContextMenuDescription(int menuItem) {
        if (menuItem == MENU_ITEM_EDIT) {
            return getResources().getString(R.string.menu_edit);
        } else if (menuItem == MENU_ITEM_DELETE) {
            return getResources().getString(R.string.menu_delete);
        } else {
            return "";
        }
    }

    protected void initializeAdapter() {
        mAdapter = new CustomSimpleCursorAdapter(getActivity(), _itemLayoutResID, null, getProjection(), getItemLayoutViewsResID());
        setListAdapter(mAdapter);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), getContentUri(), getProjection(),
                getCursorSelection(), getCursorSelectionArgs(), getCursorOrderBy());
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (isResumed()) {
            setListShown(true);
        } else if (getView() != null) {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    protected abstract Uri getContentUri();

    protected abstract String[] getProjection();

    protected abstract String getCursorSelection();

    protected abstract String[] getCursorSelectionArgs();

    protected String getCursorOrderBy() {
        return "_id";
    }

    protected abstract int[] getItemLayoutViewsResID();

    protected abstract void bindCursorAdapterView(View view, Context context, Cursor cursor);

    protected void raiseFragmentInteraction() {
        if (mListener != null) {
            mListener.onFragmentInteraction(this);
        }
    }

    @Override
    public void onGlobalValueChanged(UserModel value) {
        if (this.isAdded()) {
            getLoaderManager().restartLoader(_uniqueIndentifier, null, this);
        }
    }

    protected void setActionBarTitle(String title) {
        AppCompatActivity main = (AppCompatActivity) getActivity();
        if (main != null) {
            ActionBar actionBar = main.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }
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

    int mLastFirstVisibleItem;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /*if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            final ListView lw = getListView();

            if (lw != null && view.getId() == lw.getId()) {
                final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    floatingActionButton.animate().cancel();
                    floatingActionButton.animate().translationYBy(350);
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    floatingActionButton.animate().cancel();
                    floatingActionButton.animate().translationYBy(-350);
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        }*/
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            return onFloatingActionButtonClick(v);
        }
        return true;
    }

    private class CustomSimpleCursorAdapter extends android.support.v4.widget.SimpleCursorAdapter {
        public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            bindCursorAdapterView(view, context, cursor);
        }
    }

    public static class CustomSearchView extends SearchView {
        public CustomSearchView(Context context) {
            super(context);
        }

        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);
            super.onActionViewCollapsed();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(BaseListFragment fragment);
    }
}
