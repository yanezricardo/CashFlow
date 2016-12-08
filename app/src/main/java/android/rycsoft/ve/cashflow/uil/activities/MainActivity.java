package android.rycsoft.ve.cashflow.uil.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.rycsoft.ve.cashflow.App;
import android.rycsoft.ve.cashflow.GlobalValues;
import android.rycsoft.ve.cashflow.R;
import android.rycsoft.ve.cashflow.database.DefaultValues;
import android.rycsoft.ve.cashflow.database.contentproviders.PreferencesContentProvider;
import android.rycsoft.ve.cashflow.database.models.UserModel;
import android.rycsoft.ve.cashflow.uil.adapters.PersonaAdapter;
import android.rycsoft.ve.cashflow.uil.fragments.*;
import android.rycsoft.ve.cashflow.uil.widgets.AddFloatingActionButton;
import android.rycsoft.ve.cashflow.utils.*;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements BaseListFragment.OnFragmentInteractionListener {
    private static String STATE_SELECTED_POSITION = "state_selected_position";
    private int mCurrentSelectedPosition;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PersonaAdapter mPersonaAdapter;
    public NavigationView mNavigationView;
    private Spinner mNavigationHeaderSpinner;
    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setContext(this);
        setContentView(R.layout.activity_main);
        PreferencesContentProvider.loadPreferences();

        setupToolbar();
        setupNavDrawer();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = setupDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.nvView);
        if (mNavigationView != null) {
            setupDrawerContent();
            selectDrawerItem(mNavigationView.getMenu().getItem(mCurrentSelectedPosition));
            UIElementHelper.spinnerSetSelection(mNavigationHeaderSpinner, GlobalValues.getCurrentPerson().getName(), "_name");
        }
        AddFloatingActionButton floatingActionButton = (AddFloatingActionButton) findViewById(R.id.floating_action_button);
        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DefaultValues defaultValues = new DefaultValues(this);
        if(defaultValues.databaseIsEmpty()) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            defaultValues.insertDefaultValues();
                            selectDrawerItem(mNavigationView.getMenu().getItem(mCurrentSelectedPosition));
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Utils.getResourceAsString(R.string.default_values_install_dialog_message))
                    .setTitle(Utils.getResourceAsString(R.string.default_values_install_dialog_title))
                    .setPositiveButton("Si", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupNavDrawer() {
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
    }

    private void setupDrawerContent() {
        mPersonaAdapter = new PersonaAdapter(this);
        configNavigationHeader();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void configNavigationHeader() {
        mNavigationHeaderSpinner = (Spinner) mNavigationView.findViewById(R.id.header_person_list);
        if (mNavigationHeaderSpinner != null && mPersonaAdapter != null && mNavigationView != null) {
            mNavigationHeaderSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mPersonaAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
            mNavigationHeaderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    UserModel person = mPersonaAdapter.getItem(position);
                    ImageView image = (ImageView) mNavigationView.findViewById(R.id.header_image);
                    if (image != null) {
                        image.setImageDrawable(person.getImage());
                    }
                    GlobalValues.setCurrentPerson(person);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mNavigationHeaderSpinner.setAdapter(mPersonaAdapter);
            try {
                Field popup = Spinner.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(mNavigationHeaderSpinner);
                DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
                popupWindow.setHeight(displayMetrics.heightPixels - displayMetrics.densityDpi + 20);
            } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        if (menuItem == null) {
            return;
        }
        Fragment fragment = null;

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_movimiento_fragment:
                fragmentClass = FlujoDeEfectivoFragment.class;
                break;
            /*case R.id.nav_categoria_fragment:
                fragmentClass = CategoriaListFragment.class;
                break;*/
            case R.id.nav_instrumento_financiero_fragment:
                fragmentClass = InstrumentoFinancieroListFragment.class;
                break;
            case R.id.nav_presupuesto_fragment:
                fragmentClass = PresupuestoListFragment.class;
                break;
            case R.id.nav_cxp_fragment:
                fragmentClass = CuentaPorPagarListFragment.class;
                break;
            case R.id.nav_cxc_fragment:
                fragmentClass = CuentaPorCobrarListFragment.class;
                break;
            case R.id.nav_personas_fragment:
                fragmentClass = PersonaListFragment.class;
                break;
            default:
                fragmentClass = FlujoDeEfectivoFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        PreferencesContentProvider.savePreferences();
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        mNavigationView.getMenu().getItem(mCurrentSelectedPosition).setChecked(true);
    }

    @Override
    public void onFragmentInteraction(BaseListFragment fragment) {
        if (fragment instanceof PersonaListFragment) {
            if (mPersonaAdapter != null) {
                mPersonaAdapter.notifyDataSetChanged();
            }
        }
    }
}