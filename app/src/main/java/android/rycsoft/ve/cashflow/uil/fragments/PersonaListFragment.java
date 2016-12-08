package android.rycsoft.ve.cashflow.uil.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.models.Persona;
import android.rycsoft.ve.cashflow.uil.widgets.TextDrawable;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class PersonaListFragment extends BaseListFragment {

    public PersonaListFragment() {
        super(R.layout.fragment_persona_list_item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        floatingActionButton.setVisibility(View.VISIBLE);
        setActionBarSubtitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initializeFloatingActionButton();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        floatingActionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Uri getContentUri() {
        return Persona.CONTENT_URI;
    }

    @Override
    protected String[] getProjection() {
        return new String[]{Persona._ID, Persona.NOMBRE, Persona.EMAIL, Persona.COLOR};
    }

    @Override
    protected int[] getItemLayoutViewsResID() {
        return new int[]{-1, R.id.tv_persona_nombre, R.id.tv_persona_email,};
    }

    @Override
    protected String getCursorSelection() {
        if (_curFilter == null) {
            return null;
        }
        return "(" + Persona.NOMBRE + " LIKE ?) OR (" + Persona.EMAIL + " LIKE ?)";
    }

    @Override
    protected String[] getCursorSelectionArgs() {
        if (_curFilter == null) {
            return null;
        }
        return new String[]{"%" + _curFilter + "%", "%" + _curFilter + "%"};
    }

    @Override
    protected void bindCursorAdapterView(View view, Context context, Cursor cursor) {
        ImageView ivImagen = (ImageView) view.findViewById(R.id.iv_persona_imagen);
        TextView tvNombre = (TextView) view.findViewById(R.id.tv_persona_nombre);
        TextView tvEmail = (TextView) view.findViewById(R.id.tv_persona_email);

        String nombre = cursor.getString(cursor.getColumnIndexOrThrow(Persona.NOMBRE));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(Persona.EMAIL));
        String color = cursor.getString(cursor.getColumnIndexOrThrow(Persona.COLOR));
        Drawable imagen = TextDrawable.builder().beginConfig().width(110).height(110).endConfig()
                .buildRound(Utils.getFirstTwoLetersOfString(nombre), Color.parseColor(color));
        ivImagen.setImageDrawable(imagen);
        tvNombre.setText(nombre);
        tvEmail.setText(email);
    }

    @Override
    protected CharSequence getSelectedItemFriendlyName(int position) {
        Cursor cursor = (Cursor)getListAdapter().getItem(position);
        if(cursor != null) {
            return cursor.getString(cursor.getColumnIndexOrThrow(Persona.NOMBRE));
        }
        return getActivity().getTitle();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        super.onLoadFinished(loader, data);
        raiseFragmentInteraction();
    }
}
