package android.rycsoft.ve.cashflow.uil.adapters;

import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.uil.fragments.CategoriaEgresoListFragment;
import android.rycsoft.ve.cashflow.uil.fragments.CategoriaIngresoListFragment;
import android.rycsoft.ve.cashflow.uil.fragments.GraficosFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FlujoDeEfectivoAdapter extends FragmentStatePagerAdapter {
    private int tabTitlesResId[] = new int[]{R.string.flujo_de_efectivo_egresos, R.string.flujo_de_efectivo_ingresos, R.string.flujo_de_efectivo_graficos};

    public FlujoDeEfectivoAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CategoriaEgresoListFragment.newInstance();
            case 1:
                return CategoriaIngresoListFragment.newInstance();
            case 2:
                return GraficosFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return App.getContext().getResources().getString(tabTitlesResId[position]);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
