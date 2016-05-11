package com.snake.salarycounter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.orangegangsters.lollipin.lib.PinActivity;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.snake.salarycounter.R;
import com.snake.salarycounter.activities.ShiftType.ListShiftTypeActivity;
import com.snake.salarycounter.models.ShiftType;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends PinActivity {

    private static final int PROFILE_SETTING = 1;
    private static final int REQUEST_CODE_ENABLE = 11;
    private static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private PrimaryDrawerItem authDrawerItem = null;

    final static int II_SETTINGS = 101;
    final static int II_ABOUT = 102;
    final static int II_ACCOUNT = 103;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        Fabric.with(this, new Crashlytics());

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSharedPreferences.contains(PASSWORD_PREFERENCE_KEY)) {
            Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, REQUEST_CODE_ENABLE);
        }
        /*else
        {
            Intent intent = new Intent(MainActivity.this, CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
            startActivity(intent);
        }*/

        // TODO: Move this to where you establish a user session
        //logUser();

        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Setted title");

        // Create the AccountHeader
        buildHeader(false, savedInstanceState);

        final Context that = this;

        authDrawerItem = new PrimaryDrawerItem()
                .withSelectable(false)
                .withName(R.string.account_title)
                .withIcon(CommunityMaterial.Icon.cmd_plus)
                .withIdentifier(II_ACCOUNT);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_home)
                                .withIcon(CommunityMaterial.Icon.cmd_home)
                                .withIdentifier(10)
                                .withBadge("100")
                                .withDescription("Description"),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_shift_types)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calendar_multiple)
                                .withIdentifier(20),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_calendar)
                                .withSelectable(false)
                                .withIcon(CommunityMaterial.Icon.cmd_calendar_clock)
                                .withIdentifier(40)
                        //here we use a customPrimaryDrawerItem we defined in our sample app
                        //this custom DrawerItem extends the PrimaryDrawerItem so it just overwrites some methods
                       /* new OverflowMenuDrawerItem()
                                .withName(R.string.drawer_item_menu_drawer_item)
                                .withDescription(R.string.drawer_item_menu_drawer_item_desc)
                                .withMenu(R.menu.menu_main)
                                .withOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                })
                                .withIcon(GoogleMaterial.Icon.gmd_filter_center_focus)
                                .withIdentifier(20),
                        new CustomPrimaryDrawerItem()
                                .withBackgroundRes(R.color.accent)
                                .withName(R.string.drawer_item_free_play)
                                .withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem()
                                .withName(R.string.drawer_item_custom)
                                .withDescription("This is a description")
                                .withIcon(FontAwesome.Icon.faw_eye)
                                .withIdentifier(30),
                        new CustomUrlPrimaryDrawerItem()
                                .withName(R.string.drawer_item_fragment_drawer)
                                .withDescription(R.string.drawer_item_fragment_drawer_desc)
                                .withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460")
                                .withIdentifier(40),
                        new SectionDrawerItem()
                                .withName(R.string.drawer_item_section_header),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_settings)
                                .withIcon(FontAwesome.Icon.faw_cart_plus)
                                .withIdentifier(50),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_help)
                                .withIcon(FontAwesome.Icon.faw_database)
                                .withEnabled(false)
                                .withIdentifier(60),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_open_source)
                                .withIcon(FontAwesome.Icon.faw_github)
                                .withIdentifier(70),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_contact)
                                .withSelectedIconColor(Color.RED)
                                .withIconTintingEnabled(true)
                                .withIcon(
                                        new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus_one)
                                                .actionBar()
                                                .paddingDp(5)
                                                .colorRes(R.color.material_drawer_dark_primary_text))
                                .withTag("Bullhorn")
                                .withIdentifier(80),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_help)
                                .withIcon(FontAwesome.Icon.faw_question)
                                .withEnabled(false)
                                .withIdentifier(90)*/
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
                        new SecondaryDrawerItem()
                                .withSelectable(false)
                                .withName(R.string.drawer_item_settings)
                                .withIcon(CommunityMaterial.Icon.cmd_settings)
                                .withIdentifier(II_SETTINGS),
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

                        switch((int)drawerItem.getIdentifier()) {
                            case 10:
                                intent.setClass(that, CustomPinActivity.class);
                                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                                startActivity(intent);
                                break;
                            case 20:
                                intent.setClass(that, ListShiftTypeActivity.class);
                                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                                startActivity(intent);
                                break;
                            case 40:
                                intent.setClass(that, CalendarActivity.class);
                                startActivity(intent);
                                break;
                            case 50:
                                intent.setClass(that, ListShiftTypeActivity.class);
                                startActivity(intent);
                                break;
                            case II_SETTINGS:
                                intent.setClass(that, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case II_ABOUT:
                                showAbout(that);
                                break;
                            case II_ACCOUNT:

                                break;
                            default:
                                Snackbar.make(view, "Clicked " + String.valueOf(drawerItem.getIdentifier()), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                return false;
                        }

                        return true;
                    }} )
                .build();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
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
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
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

    protected void showAbout(Context context)
    {
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
                if(specialButton.name().compareToIgnoreCase("special1") == 0) {
                    //startActivity(new Intent(v.getContext(), MyIntro.class));
                    return true;
                }
                if(specialButton.name().compareToIgnoreCase("special3") == 0) {
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
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
