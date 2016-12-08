package android.rycsoft.ve.cashflow.database.models;

import android.graphics.drawable.Drawable;
import android.rycsoft.ve.cashflow.utils.RandomColor;


public class UserModel {
    private int _id;
    private String _name;
    private String _email;
    private int _color;
    private Drawable _image;
    public boolean AddNew;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public int getColor() {
        if(_color == 0) {
            _color = RandomColor.MATERIAL.getRandomColor();
        }
        return _color;
    }

    public void setColor(int color) {
        this._color = color;
    }

    public Drawable getImage() {
        return this._image;
    }

    public void setImage(Drawable image) {
        this._image = image;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }


}
