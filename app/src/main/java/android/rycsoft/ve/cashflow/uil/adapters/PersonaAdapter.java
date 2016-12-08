package android.rycsoft.ve.cashflow.uil.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.contentproviders.PersonaContentProvider;
import android.rycsoft.ve.cashflow.database.models.Persona;
import android.rycsoft.ve.cashflow.database.models.UserModel;
import android.rycsoft.ve.cashflow.uil.widgets.TextDrawable;
import android.rycsoft.ve.cashflow.utils.OwnerInfo;
import android.rycsoft.ve.cashflow.utils.Utils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


public class PersonaAdapter extends BaseAdapter {
    private final Context _context;
    private ArrayList<UserModel> _values;

    public PersonaAdapter(Context context) {
        this._context = context;
        this._values = getPersonasFromDb();
        if(this._values != null && this._values.size() > 0 && GlobalValues.getCurrentPerson() == null) {
            GlobalValues.setCurrentPerson(_values.get(0));
        }
    }

    private ArrayList<UserModel> getPersonasFromDb() {
        ArrayList<UserModel> list = PersonaContentProvider.getAll(_context, new String[]{Persona._ID, Persona.NOMBRE, Persona.EMAIL, Persona.COLOR});
        if (list.size() == 0) {
            UserModel user = getSystemUser();
            ContentValues values = new ContentValues();
            values.put(Persona.NOMBRE, user.getName());
            values.put(Persona.EMAIL, user.getEmail());
            values.put(Persona.COLOR, Utils.colorToString(user.getColor()));
            PersonaContentProvider provider = new PersonaContentProvider();
            provider.setContext(_context);
            if (provider.onCreate()) {
                provider.insert(Persona.CONTENT_URI, values);
                list = PersonaContentProvider.getAll(_context, new String[]{Persona._ID, Persona.NOMBRE, Persona.EMAIL, Persona.COLOR});
            }
        }
        if (list.size() > 0) {
            for (UserModel user : list) {
                user.setImage(TextDrawable.builder()
                        .beginConfig()
                        .width(70)
                        .height(70)
                        .endConfig()
                        .buildRound(Utils.getFirstTwoLetersOfString(user.getName()), user.getColor()));
            }
        }
        return list;
    }

    private UserModel getSystemUser() {
        String name;
        String email = "";
        OwnerInfo info = new OwnerInfo(App.getContext());
        if (info.name == null) {
            name = Utils.getAccountName(_context);
            if (name == null) {
                name = "Usuario";
            }
        } else {
            name = info.name;
            email = info.email == null ? "" : info.email;
        }
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    public int getCount() {
        return _values.size();
    }

    public UserModel getItem(int position) {
        return _values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.drawer_header_spinner_selected_item, null);

            holder = new ViewHolder();
            holder._title = (TextView) row.findViewById(R.id.drawer_spinner_selected_item_title);
            holder._subtitle = (TextView) row.findViewById(R.id.drawer_spinner_selected_item_subtitle);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        UserModel person = getItem(position);
        holder.setTitle(person.getName());
        holder.setSubtitle(person.getEmail());
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AtomicReference<ViewHolder> holder = new AtomicReference<>();

        if (row == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.drawer_header_spinner_item, null);

            holder.set(new ViewHolder());
            holder.get()._image = (ImageView) row.findViewById(R.id.drawer_spinner_item_image);
            holder.get()._title = (TextView) row.findViewById(R.id.drawer_spinner_item_title);
            row.setTag(holder.get());
        } else {
            holder.set((ViewHolder) row.getTag());
        }

        UserModel person = getItem(position);
        holder.get().setTitle(person.getName());
        holder.get().setImageDrawable(person.getImage());

        return row;
    }

    @Override
    public void notifyDataSetChanged() {
        _values = getPersonasFromDb();
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView _image;
        TextView _title;
        TextView _subtitle;

        public void setImageDrawable(Drawable drawable) {
            if (_image != null && drawable != null) {
                _image.setImageDrawable(drawable);
            }
        }

        public void setTitle(String text) {
            if (_title != null) {
                _title.setText(text);
            }
        }

        public void setSubtitle(String text) {
            if (_subtitle != null) {
                _subtitle.setText(text);
            }
        }
    }
}