package com.snake.salarycounter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.snake.salarycounter.R;
import com.snake.salarycounter.fragments.CalendarFragment;
import com.snake.salarycounter.fragments.FinanceCondition.ListFinanceConditionFragment;
import com.snake.salarycounter.fragments.MainCalcFragment;
import com.snake.salarycounter.fragments.ShiftType.ListShiftTypeFragment;
import com.snake.salarycounter.fragments.Statistic.StatisticFragment;
import com.snake.salarycounter.fragments.Tabel.ListTabelFragment;

import io.fabric.sdk.android.Fabric;
import pub.devrel.easygoogle.Google;
import pub.devrel.easygoogle.gac.SignIn;


public class MainActivity extends AppCompatActivity implements
        SignIn.SignInListener,
        BillingProcessor.IBillingHandler {

    private static final int PROFILE_SETTING = 1;
    private static final int REQUEST_CODE_ENABLE = 11;
    private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";
    private SharedPreferences mSharedPreferences;

    private AccountHeader headerResult = null;
    private Drawer drawerResult = null;
    private PrimaryDrawerItem authDrawerItem = null;

    private Google mGoogle;
    private BillingProcessor bp;

    Activity that;

    final static int II_SHIFT_TYPES = 10;
    final static int II_FINANCE_CONDITIONS = 20;
    final static int II_TABLE = 30;
    final static int II_CALENDAR = 40;
    final static int II_MAIN_CALC = 50;
    final static int II_STATISTIC = 60;

    final static int II_SETTINGS = 101;
    final static int II_ABOUT = 102;
    final static int II_ACCOUNT = 103;
    final static int II_DONATE = 104;

    //final static String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgm1rO1sNVQhKm0Ra7SceSzjS4Emj3dDpx+lTcT8b0WjwNifNV2AXve+4tqXq7luH/ZBzgBpQt938jSm61xpQhUGF18Hl+cz3hJ+TaAEPxHDwKiEsRk+JKwFNbrWrQ8xirW7vDHc6bpsTs3KM1ZzGnu+TtrhioIzV9j/Fm2dzlGQMrU1x42fGJCChex49VQbgTFfIoMMgZsuN7rMcq8wF5OXb+r4QoLZhbcG8v9DDmEmE+XjS+EUm0ajSqaSJVdxWwZ617V/JbOmEymf0irU+wErQf+IDRNUz8Mp4rrwhrJ/K+PKQd7/1Zu3ATAB7WiMjgwK0Epg7xulFR6tDdfqwnQIDAQAB";
    final static String LICENSE_KEY = null;

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

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

        //initializeGoogle();
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
        if(null != bp){
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
            default:
                return false;
        }

        return true;
    }

    public void showAbout(Context context) {
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
                    //startActivity(new Intent(v.getContext(), MyIntro.class));
                    return true;
                }
                if (specialButton.name().compareToIgnoreCase("special3") == 0) {
                    //startActivity(new Intent(v.getContext(), ChangelogActivity.class));
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
                .withAboutDescription("{faw-android} This is a small sample which can be set in the about my app description file.<br /><b>You can style this with html markup :D</b>")
                //start the activity
                .start(context);
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

    //region Drawer
    private void buildDrawer(final Context context, Bundle savedInstanceState, Toolbar toolbar) {
        // Create the AccountHeader
        buildHeader(false, savedInstanceState);

        /*authDrawerItem = new PrimaryDrawerItem()
                .withSelectable(false)
                .withName(R.string.account_title)
                .withIcon(CommunityMaterial.Icon.cmd_account)
                .withIdentifier(II_ACCOUNT);*/

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
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                        //if the back arrow is shown. close the activity
                        MainActivity.this.finish();
                        //return true if we have consumed the event
                        return true;
                    }
                })
                .addStickyDrawerItems(
                        //authDrawerItem,
                        /*new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName(R.string.drawer_item_settings)
                                .withIcon(CommunityMaterial.Icon.cmd_settings)
                                .withIdentifier(II_SETTINGS),*/
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
                            case 1000:
                                intent.setClass(context, CustomPinActivity.class);
                                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                                startActivity(intent);
                                break;
                            case 2000:
                                /*intent.setClass(context, ListShiftTypeActivity.class);
                                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                                startActivity(intent);*/
                                break;
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
                            case II_ACCOUNT:
                                if (mGoogle.getSignIn().isSignedIn()) {
                                    mGoogle.getSignIn().signOut();
                                } else {
                                    mGoogle.getSignIn().signIn();
                                }
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
                /*.addProfiles(
                        profile,
                        profile2,
                        profile3,
                        profile4,
                        profile5,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus_one).actionBar().paddingDp(5).colorRes(R.color.material_drawer_dark_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings)
                )*/
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withEmail("batman@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile5));
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    public Drawer getDrawer(){
        return drawerResult;
    }
    //endregion

    //region Google SingIn
    private void initializeGoogle() {
        mGoogle = new Google.Builder(this)
                //.enableSignIn(this, serverClientId)
                .enableSignIn(this)
                .build();
    }

    @Override
    public void onSignedIn(GoogleSignInAccount googleSignInAccount) {
        authDrawerItem
                .withName(R.string.account_signout)
                .withIcon(CommunityMaterial.Icon.cmd_account_off);
        drawerResult.updateStickyFooterItem(authDrawerItem);

        headerResult.clear();
        headerResult.addProfile(
                new ProfileDrawerItem()
                        .withName(googleSignInAccount.getDisplayName())
                        .withEmail(googleSignInAccount.getEmail())
                        .withIcon(googleSignInAccount.getPhotoUrl())
                , 0);

    }

    @Override
    public void onSignInFailed() {
        SuperToast.create(MainActivity.this, getString(R.string.error_signing_in), SuperToast.Duration.MEDIUM).show();
    }

    @Override
    public void onSignedOut() {
        headerResult.clear();

        authDrawerItem
                .withName(R.string.account_title)
                .withIcon(CommunityMaterial.Icon.cmd_account);

        drawerResult.updateStickyFooterItem(authDrawerItem);

        SuperToast.create(MainActivity.this, getString(R.string.signed_out), SuperToast.Duration.MEDIUM).show();
    }

    //endregion

    //region Google billing
    private void showDonateDialog(){
        new MaterialDialog.Builder(this)
                .title(R.string.donate)
                .items(R.array.donate)
                .itemsCallback(new MaterialDialog.ListCallback(){
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        //We make choice
                        if(BillingProcessor.isIabServiceAvailable(getApplicationContext())){
                            String[] donateStrings = getResources().getStringArray(R.array.donate_strings);
                            bp.purchase(that, donateStrings[position]);
                            //SuperToast.create(that, donateStrings[position] + " " + String.valueOf(position), SuperToast.Duration.LONG).show();
                        }
                        else{
                            SuperToast.create(that, getString(R.string.market_unavailable), SuperToast.Duration.LONG).show();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!bp.handleActivityResult(requestCode, resultCode, data)) {
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
