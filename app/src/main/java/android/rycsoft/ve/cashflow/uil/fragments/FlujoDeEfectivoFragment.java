package android.rycsoft.ve.cashflow.uil.fragments;

import android.os.Bundle;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.uil.adapters.FlujoDeEfectivoAdapter;
import android.rycsoft.ve.cashflow.uil.widgets.SlidingTabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FlujoDeEfectivoFragment extends Fragment {
    FlujoDeEfectivoAdapter _adapterViewPager;
    ViewPager _viewPager;
    SlidingTabLayout _tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flujo_de_efectivo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        _adapterViewPager = new FlujoDeEfectivoAdapter(getActivity().getSupportFragmentManager());
        _viewPager.setAdapter(_adapterViewPager);
        _viewPager.setCurrentItem(0);
        _tabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sliding_tabs);
        _tabLayout.setDistributeEvenly(true);
        _tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        _tabLayout.setViewPager(_viewPager);
    }
}
