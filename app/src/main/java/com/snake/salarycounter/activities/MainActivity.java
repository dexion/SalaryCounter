package com.snake.salarycounter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.johnpersano.supertoasts.SuperToast;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.snake.salarycounter.R;
import com.snake.salarycounter.fragments.Calendar.CalendarFragment;
import com.snake.salarycounter.fragments.FinanceCondition.ListFinanceConditionFragment;
import com.snake.salarycounter.fragments.MainCalcFragment;
import com.snake.salarycounter.fragments.ShiftType.ListShiftTypeFragment;
import com.snake.salarycounter.fragments.Statistic.StatisticFragment;
import com.snake.salarycounter.fragments.Tabel.ListTabelFragment;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements
        BillingProcessor.IBillingHandler {

    //private static final int PROFILE_SETTING = 1;
    //private static final int REQUEST_CODE_ENABLE = 11;
    //private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";
    //private SharedPreferences mSharedPreferences;

    private AccountHeader headerResult = null;
    private Drawer drawerResult = null;

    private BillingProcessor bp;

    Activity that;

    static final int II_SHIFT_TYPES = 10;
    static final int II_FINANCE_CONDITIONS = 20;
    static final int II_TABLE = 30;
    static final int II_CALENDAR = 40;
    static final int II_MAIN_CALC = 50;
    static final int II_STATISTIC = 60;

    static final int II_SETTINGS = 101;
    static final int II_ABOUT = 102;
    static final int II_DONATE = 103;

    //static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgm1rO1sNVQhKm0Ra7SceSzjS4Emj3dDpx+lTcT8b0WjwNifNV2AXve+4tqXq7luH/ZBzgBpQt938jSm61xpQhUGF18Hl+cz3hJ+TaAEPxHDwKiEsRk+JKwFNbrWrQ8xirW7vDHc6bpsTs3KM1ZzGnu+TtrhioIzV9j/Fm2dzlGQMrU1x42fGJCChex49VQbgTFfIoMMgZsuN7rMcq8wF5OXb+r4QoLZhbcG8v9DDmEmE+XjS+EUm0ajSqaSJVdxWwZ617V/JbOmEymf0irU+wErQf+IDRNUz8Mp4rrwhrJ/K+PKQd7/1Zu3ATAB7WiMjgwK0Epg7xulFR6tDdfqwnQIDAQAB";
    static final String LICENSE_KEY = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        Fabric.with(this, new Crashlytics());

        bp = new BillingProcessor(this, LICENSE_KEY, this);
        that = this;

        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*if (!mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
            Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, REQUEST_CODE_ENABLE);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            startActivity(intent);
        }*/

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frame_container, new MainCalcFragment());
        t.commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buildDrawer(this, savedInstanceState, toolbar);
    }

    @Override
    protected void onDestroy() {
        if (null != bp) {
            bp.release();
        }

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent();
        Context thisContext = this;

        switch (item.getItemId()) {
            case R.id.action_settings:
                intent.setClass(thisContext, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                showAbout(thisContext);
                break;
            case R.id.action_crash:
                // TODO: Use your own string attributes to track common values over time
                // TODO: Use your own number attributes to track median value over time
                Answers.getInstance().logCustom(new CustomEvent("Video Played")
                        .putCustomAttribute("Category", "Comedy")
                        .putCustomAttribute("Length", 350));

                Crashlytics.getInstance().crash();
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawerResult.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    //region Internal functions
    private void showAbout(Context context) {
        LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
            @Override
            public void onIconClicked(View v) {
                //Toast.makeText(v.getContext(), "We are able to track this now ;)", Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean onLibraryAuthorClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onExtraClicked(View v, Libs.SpecialButton specialButton) {
                if (specialButton.name().compareToIgnoreCase("special1") == 0) {
                    showIntro(v.getContext());
                    return true;
                }
                if (specialButton.name().compareToIgnoreCase("special2") == 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://4pda.ru/forum/index.php?showtopic=514934"));
                    startActivity(browserIntent);
                    return true;
                }
                if (specialButton.name().compareToIgnoreCase("special3") == 0) {
                    showChangelog(v.getContext());
                    return true;
                }
                return false;
            }

            @Override
            public boolean onIconLongClicked(View v) {
                return false;
            }

            @Override
            public boolean onLibraryAuthorLongClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentLongClicked(View v, Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomLongClicked(View v, Library library) {
                return false;
            }
        };

        new LibsBuilder()
                .withAutoDetect(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withActivityTitle(getString(R.string.about))
                .withListener(libsListener)
                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(getString(R.string.about_description))
                //start the activity
                .start(context);
    }

    private void showChangelog(Context context){
        new MaterialDialog.Builder(context)
                .title(R.string.title_activity_changelog)
                .customView(R.layout.fragment_changelog, false)
                .positiveText(R.string.btn_ok)
                .show();
    }

    private void showIntro(Context context){
        startActivity(new Intent(context, MyIntro.class));
    }
    //endregion

    //region Drawer
    private void buildDrawer(final Context context, Bundle savedInstanceState, Toolbar toolbar) {
        // Create the AccountHeader
        buildHeader(false, savedInstanceState);

        //Create the drawer
        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_shift_types)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calendar_multiple)
                                .withIdentifier(II_SHIFT_TYPES),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_tabel)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calendar_clock)
                                .withIdentifier(II_TABLE),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_finance_conditions)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_diamond)
                                .withIdentifier(II_FINANCE_CONDITIONS),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_calendar)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calendar_text)
                                .withIdentifier(II_CALENDAR),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_calculator)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calculator)
                                .withIdentifier(II_MAIN_CALC),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_statistic)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_chart_line)
                                .withIdentifier(II_STATISTIC)
                ) // add the items we want to use with our Drawer
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName(R.string.drawer_item_donate)
                                .withIcon(CommunityMaterial.Icon.cmd_cake_variant)
                                .withIdentifier(II_DONATE),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName(R.string.drawer_item_about)
                                .withIcon(CommunityMaterial.Icon.cmd_help)
                                .withIdentifier(II_ABOUT)
                )
                .withSelectedItem(-1)
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = new Intent();
                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        //t.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);

                        switch ((int) drawerItem.getIdentifier()) {
                            case II_SHIFT_TYPES:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new ListShiftTypeFragment());
                                t.commit();
                                break;
                            case II_FINANCE_CONDITIONS:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new ListFinanceConditionFragment());
                                t.commit();
                                break;
                            case II_TABLE:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new ListTabelFragment());
                                t.commit();
                                break;
                            case II_CALENDAR:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new CalendarFragment());
                                t.commit();
                                break;
                            case II_MAIN_CALC:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new MainCalcFragment());
                                t.commit();
                                break;
                            case II_STATISTIC:
                                drawerResult.closeDrawer();
                                t.replace(R.id.frame_container, new StatisticFragment());
                                t.commit();
                                break;
                            case II_DONATE:
                                showDonateDialog();
                                break;
                            case II_SETTINGS:
                                intent.setClass(context, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case II_ABOUT:
                                showAbout(context);
                                break;
                            default:
                                Snackbar.make(view, "Clicked " + String.valueOf(drawerItem.getIdentifier()), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                return false;
                        }

                        return true;
                    }
                })
                .build();
    }

    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .withCompactStyle(compact)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    public Drawer getDrawer() {
        return drawerResult;
    }
    //endregion

    //region Google billing
    private void showDonateDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.donate)
                .items(R.array.donate)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        //We make choice
                        if (BillingProcessor.isIabServiceAvailable(getApplicationContext())) {
                            String[] donateStrings = getResources().getStringArray(R.array.donate_strings);
                            bp.purchase(that, donateStrings[position]);
                            //SuperToast.create(that, donateStrings[position] + " " + String.valueOf(position), SuperToast.Duration.LONG).show();
                        } else {
                            SuperToast.create(that, getString(R.string.market_unavailable), SuperToast.Duration.LONG).show();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        //SuperToast.create(MainActivity.this, productId, SuperToast.Duration.MEDIUM).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        //SuperToast.create(MainActivity.this, "onPurchaseHistoryRestored", SuperToast.Duration.MEDIUM).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
    //endregion
}
