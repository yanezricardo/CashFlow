package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.Categoria;
import android.rycsoft.ve.cashflow.uil.activities.CategoriaActivity;
import android.rycsoft.ve.cashflow.uil.widgets.FloatingActionButton;
import android.rycsoft.ve.cashflow.uil.widgets.FloatingActionsMenu;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;

public class CategoriaListFragment extends BaseListFragment {

    private FloatingActionsMenu mFabActionMenu;
    private ExpandableListAdapter mAdapter;
    private ExpandableListView mListView;
    private FloatingActionButton mAddEgreso;
    private FloatingActionButton mAddIngreso;

    public CategoriaListFragment() {
        super(R.layout.simple_list_item);
        _uniqueIndentifier = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);
        mListView = (ExpandableListView) inflater.inflate(R.layout.expandable_list_view, null);
        layout.addView(mListView);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Uri uri = ContentUris.withAppendedId(getContentUri(), id);
                String action = getActivity().getIntent().getAction();
                if (!Intent.ACTION_PICK.equals(action) && !Intent.ACTION_GET_CONTENT.equals(action)) {
                    Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
                    if (cursor != null) {
                        String tipo = cursor.getString(cursor.getColumnIndexOrThrow(Categoria.TIPO));
                        Intent intent = new Intent(Intent.ACTION_EDIT, uri);
                        intent.putExtra(CategoriaActivity.CATEGORIA_TYPE_NAME, tipo);
                        startActivity(intent);
                    }
                }
                return true;
            }
        });
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFloatingActionMenu();
        registerForContextMenu(mListView);
        setActionBarSubtitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        if (ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
            menu.setHeaderTitle(getSelectedItemFriendlyName(groupPosition, childPosition));
            menu.add(this.getId(), MENU_ITEM_DELETE, 0, getContextMenuDescription(MENU_ITEM_DELETE));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != this.getId()) {
            return false;
        }
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        if (ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
            Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
            if (cursor != null) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(Categoria._ID));
                switch (item.getItemId()) {
                    case MENU_ITEM_DELETE: {
                        Uri noteUri = ContentUris.withAppendedId(getContentUri(), id);
                        getActivity().getContentResolver().delete(noteUri, null, null);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initializeFloatingActionMenu() {
        mFabActionMenu = (FloatingActionsMenu) getActivity().findViewById(R.id.fab_actions_menu);
        if (mFabActionMenu != null) {
            mFabActionMenu.setVisibility(View.VISIBLE);
            mFabActionMenu.addButton(getAddEgreso());
            mFabActionMenu.addButton(getAddIngreso());
        }
    }

    public FloatingActionButton getAddEgreso() {
        if (mAddEgreso == null) {
            mAddEgreso = createAddEgresoButton();
        }
        return mAddEgreso;
    }

    public FloatingActionButton getAddIngreso() {
        if (mAddIngreso == null) {
            mAddIngreso = createAddIngresoButton();
        }
        return mAddIngreso;
    }

    private FloatingActionButton createAddEgresoButton() {
        FloatingActionButton fabButton = new FloatingActionButton(getActivity());
        fabButton.setSize(FloatingActionButton.SIZE_MINI);
        fabButton.setTitle(getString(R.string.categoria_tipo_egreso_label));
        fabButton.setIcon(R.drawable.ic_egreso);
        fabButton.setColorNormalResId(R.color.white);
        fabButton.setColorPressedResId(R.color.white_pressed);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT, getContentUri());
                intent.putExtra(CategoriaActivity.CATEGORIA_TYPE_NAME, CategoriaActivity.CATEGORIA_EGRESO);
                getActivity().startActivity(intent);
                mFabActionMenu.collapse();
            }
        });
        return fabButton;
    }

    private FloatingActionButton createAddIngresoButton() {
        FloatingActionButton fabButton = new FloatingActionButton(getActivity());
        fabButton.setSize(FloatingActionButton.SIZE_MINI);
        fabButton.setTitle(getString(R.string.categoria_tipo_ingreso_label));
        fabButton.setIcon(R.drawable.ic_ingreso);
        fabButton.setColorNormalResId(R.color.white);
        fabButton.setColorPressedResId(R.color.white_pressed);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT, getContentUri());
                intent.putExtra(CategoriaActivity.CATEGORIA_TYPE_NAME, CategoriaActivity.CATEGORIA_INGRESO);
                getActivity().startActivity(intent);
                mFabActionMenu.collapse();
            }
        });
        return fabButton;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFabActionMenu.setVisibility(View.INVISIBLE);
        mFabActionMenu.collapse();
        mFabActionMenu.removeButton(getAddEgreso());
        mFabActionMenu.removeButton(getAddIngreso());
    }

    @Override
    protected Uri getContentUri() {
        return Categoria.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{Categoria._ID, Categoria.TIPO,};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1, R.id.simple_list_group_text,};
    }

    @Override
    protected String getCursorSelection() {
        return null;
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        return null;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), getContentUri(), getProjection(),
                getCursorSelection(), getCursorSelectionArgs(), getCursorOrderBy()) {
            private final ForceLoadContentObserver mObserver = new ForceLoadContentObserver();

            @Override
            public Cursor loadInBackground() {
                MatrixCursor cursor = new MatrixCursor(new String[]{Categoria._ID, Categoria.TIPO});
                String[] tipos = getActivity().getResources().getStringArray(R.array.categoria_tipo_entries);
                int index = 0;
                for (String tipo : tipos) {
                    cursor.addRow(new Object[]{index++, tipo});
                }
                if (cursor != null) {
                    cursor.getCount();
                    cursor.registerContentObserver(mObserver);
                }

                return cursor;
            }
        };
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setGroupCursor(data);
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            mListView.expandGroup(i);
        }
        if (isResumed()) {
            setListShown(true);
        } else if (getView() != null) {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setGroupCursor(null);
    }

    @Override
    protected void initializeAdapter() {
        mAdapter = new ExpandableListAdapter(getActivity(),
                getLoaderManager(), null,
                R.layout.simple_list_group,
                new String[]{Categoria._ID, Categoria.TIPO},
                new int[]{-1, R.id.simple_list_group_text},
                R.layout.simple_list_item,
                new String[]{Categoria._ID, Categoria.NOMBRE},
                new int[]{-1, R.id.simple_list_item_text}
        );
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void bindCursorAdapterView(View view, Context context, Cursor cursor) {

    }

    protected CharSequence getSelectedItemFriendlyName(int groupPosition, int childPosition) {
        Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
        if (cursor != null) {
            return cursor.getString(cursor.getColumnIndexOrThrow(Categoria.NOMBRE));
        }
        return getActivity().getTitle();
    }

    class ExpandableListAdapter extends SimpleCursorTreeAdapter implements LoaderManager.LoaderCallbacks<Cursor> {
        private Context mContext;
        private LoaderManager mManager;

        public ExpandableListAdapter(Context context, LoaderManager manager, Cursor groupCursor,
                                     int groupLayout, String[] groupFrom, int[] groupTo,
                                     int childLayout, String[] childFrom, int[] childTo) {
            super(context, groupCursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
            mContext = context;
            mManager = manager;
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            final String tipo = groupCursor.getString(groupCursor.getColumnIndexOrThrow(Categoria.TIPO));
            Bundle bundle = new Bundle();
            bundle.putString(Categoria.TIPO, tipo);
            int groupPos = groupCursor.getPosition();
            if (mManager.getLoader(groupPos) != null && !mManager.getLoader(groupPos).isReset()) {
                mManager.restartLoader(groupPos, bundle, this);
            } else {
                mManager.initLoader(groupPos, bundle, this);
            }
            return null;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int groupPos, Bundle bundle) {
            String tipo = bundle.getString(Categoria.TIPO);
            return new CursorLoader(
                    mContext,
                    getContentUri(),
                    new String[]{Categoria._ID, Categoria.NOMBRE, Categoria.TIPO},
                    Categoria.PERSONA_ID + "=? AND " + Categoria.TIPO + "=?",
                    new String[]{String.valueOf(GlobalValues.getCurrentPerson().getId()), String.valueOf(tipo)},
                    getCursorOrderBy()
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            setChildrenCursor(loader.getId(), cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
