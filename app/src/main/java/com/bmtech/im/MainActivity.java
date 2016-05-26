package com.bmtech.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bmtech.im.ndk.HelloJni;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
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

import net.java.sip.communicator.service.globaldisplaydetails.GlobalDisplayDetailsService;
import net.java.sip.communicator.service.globaldisplaydetails.event.GlobalAvatarChangeEvent;
import net.java.sip.communicator.service.globaldisplaydetails.event.GlobalDisplayDetailsListener;
import net.java.sip.communicator.service.globaldisplaydetails.event.GlobalDisplayNameChangeEvent;
import net.java.sip.communicator.service.protocol.AccountID;
import net.java.sip.communicator.service.protocol.PresenceStatus;
import net.java.sip.communicator.service.protocol.ProtocolProviderService;
import net.java.sip.communicator.service.protocol.globalstatus.GlobalStatusEnum;
import net.java.sip.communicator.service.protocol.globalstatus.GlobalStatusService;
import net.java.sip.communicator.service.update.UpdateService;
import net.java.sip.communicator.util.ServiceUtils;
import net.java.sip.communicator.util.account.AccountUtils;
import net.java.sip.communicator.util.account.LoginManager;

import org.jitsi.android.JitsiApplication;
import org.jitsi.android.gui.AndroidGUIActivator;
import org.jitsi.android.gui.account.AccountsListActivity;
import org.jitsi.android.gui.account.AndroidLoginRenderer;
import org.jitsi.android.gui.contactlist.AddContactActivity;
import org.jitsi.android.gui.contactlist.AddGroupDialog;
import org.jitsi.android.gui.contactlist.ContactListFragment;
import org.jitsi.android.gui.settings.SettingsActivity;
import org.jitsi.android.gui.util.event.EventListener;
import org.jitsi.service.osgi.OSGiActivity;
import org.jitsi.util.StringUtils;

import java.util.Collection;

public class MainActivity extends OSGiActivity implements EventListener<PresenceStatus>, GlobalDisplayDetailsListener {

    /**
     * The online status.
     */
    private static final int ONLINE = 1;

    /**
     * The offline status.
     */
    private static final int OFFLINE = 2;

    /**
     * The free for chat status.
     */
    private static final int FFC = 3;

    /**
     * The away status.
     */
    private static final int AWAY = 4;

    /**
     * The do not disturb status.
     */
    private static final int DND = 5;


    private static final int PROFILE_SETTING = 1;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;

    private AppCompatActivity mContext;

    //
    private FloatingActionButton fabBtn;

    //FrameLayout rootLayout;
    CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_header_background)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && ((IDrawerItem) profile).getIdentifier() == PROFILE_SETTING) {
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman").withEmail("batman@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile));
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            }
                            else {
                                headerResult.addProfiles(newProfile);
                            }
                        }
                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_menu1).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSelectable(false),
                        //new PrimaryDrawerItem().withName(R.string.drawer_item_menu2).withIcon(GoogleMaterial.Icon.gmd_wb_sunny).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_menu3).withIcon(GoogleMaterial.Icon.gmd_group).withIdentifier(3).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_menu4).withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(4).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_update).withIcon(GoogleMaterial.Icon.gmd_cached).withIdentifier(5).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_sendlog).withIcon(GoogleMaterial.Icon.gmd_send).withIdentifier(6).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_setting).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(7).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("更多").withIcon(GoogleMaterial.Icon.gmd_person).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_menu5).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(8).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_menu7).withIcon(GoogleMaterial.Icon.gmd_power_settings_new).withIdentifier(9).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {//主页，好友列表
                                //intent = new Intent(MainActivity.this, SimpleCompactHeaderDrawerActivity.class);
                            }
                            else if (drawerItem.getIdentifier() == 2) {// 添加新账号
                                startActivity(AddContactActivity.class);
                            }
                            else if (drawerItem.getIdentifier() == 3) {// 创建用户组
                                AddGroupDialog.showCreateGroupDialog(mContext, null);
                            }
                            else if (drawerItem.getIdentifier() == 4) {// 账户信息
                                startActivity(AccountsListActivity.class);
                            }
                            else if (drawerItem.getIdentifier() == 5) {// 检查更新
                                new Thread() {
                                    @Override
                                    public void run() {
                                        UpdateService updateService = ServiceUtils.getService(AndroidGUIActivator.bundleContext, UpdateService.class);
                                        updateService.checkForUpdates(true);
                                    }
                                }.start();
                            }
                            else if (drawerItem.getIdentifier() == 6) {// 发送日志
                                JitsiApplication.showSendLogsDialog();
                            }
                            else if (drawerItem.getIdentifier() == 7) {// 系统设置
                                startActivity(SettingsActivity.class);
                            }
                            else if (drawerItem.getIdentifier() == 8) {// 注销账户
                                Collection<AccountID> accounts = AccountUtils.getStoredAccounts();
                                System.err.println("Do sign out!");
                                for (AccountID account : accounts) {
                                    ProtocolProviderService protocol = AccountUtils.getRegisteredProviderForAccount(account);
                                    if (protocol != null) {
                                        System.err.println("Loggin off: " + protocol.getAccountID().getDisplayName());
                                        LoginManager.logoff(protocol);
                                    }
                                }
                            }
                            else if (drawerItem.getIdentifier() == 9) {//退出系统
                                // Shutdown application
                                JitsiApplication.shutdownApplication();
                            }

                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 10
            // 设置选择标识符为10的项
            result.setSelection(10, false);

            //set the active profile
            //headerResult.setActiveProfile(profile3);
        }

        //
        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = new ContactListFragment();
        mFragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit();

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rootLayout, new HelloJni().stringFromJNI(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "undo", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();

        // 获取登录状态
        AndroidLoginRenderer loginRenderer = AndroidGUIActivator.getLoginRenderer();

        if(loginRenderer == null){
            return;
        }

        loginRenderer.addGlobalStatusListener(this);

        onChangeEvent(loginRenderer.getGlobalStatus());

        GlobalDisplayDetailsService displayDetailsService = AndroidGUIActivator.getGlobalDisplayDetailsService();

        displayDetailsService.addGlobalDisplayDetailsListener(this);

        setGlobalAvatar(displayDetailsService.getGlobalDisplayAvatar());
        setGlobalDisplayName(displayDetailsService.getGlobalDisplayName());

    }

    @Override
    public void onPause() {
        AndroidGUIActivator.getLoginRenderer().removeGlobalStatusListener(this);

        GlobalDisplayDetailsService displayDetailsService = AndroidGUIActivator.getGlobalDisplayDetailsService();

        displayDetailsService.removeGlobalDisplayDetailsListener(this);

        super.onPause();
    }

    /**
     * Sets the global avatar in the action bar.
     *
     * @param avatar the byte array representing the avatar to set
     */
    private void setGlobalAvatar(final byte[] avatar) {
        if (avatar != null && avatar.length > 0) {
            //ActionBarUtil.setAvatar(this, avatar);
        }
    }

    /**
     * Publishes global status on separate thread to prevent
     * <tt>NetworkOnMainThreadException</tt>.
     *
     * @param newStatus new global status to set.
     */
    private void publishGlobalStatus(final int newStatus) {
        /**
         * Runs publish status on separate thread to prevent
         * NetworkOnMainThreadException
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlobalStatusService globalStatusService = AndroidGUIActivator.getGlobalStatusService();

                if(globalStatusService == null){
                    return;
                }

                switch (newStatus) {
                    case ONLINE:
                        globalStatusService.publishStatus(GlobalStatusEnum.ONLINE);
                        break;
                    case OFFLINE:
                        globalStatusService.publishStatus(GlobalStatusEnum.OFFLINE);
                        break;
                    case FFC:
                        globalStatusService.publishStatus(GlobalStatusEnum.FREE_FOR_CHAT);
                        break;
                    case AWAY:
                        globalStatusService.publishStatus(GlobalStatusEnum.AWAY);
                        break;
                    case DND:
                        globalStatusService.publishStatus(GlobalStatusEnum.DO_NOT_DISTURB);
                        break;
                }
            }
        }).start();
    }

    /**
     * Method fired when change occurs on the <tt>eventObject</tt>
     *
     * @param eventObject the instance that has been changed
     */
    @Override
    public void onChangeEvent(PresenceStatus eventObject) {

    }

    @Override
    public void globalDisplayNameChanged(final GlobalDisplayNameChangeEvent globalDisplayNameChangeEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setGlobalDisplayName(globalDisplayNameChangeEvent.getNewDisplayName());
            }
        });
    }

    @Override
    public void globalDisplayAvatarChanged(GlobalAvatarChangeEvent globalAvatarChangeEvent) {

    }

    /**
     * Sets the global display name in the action bar.
     *
     * @param name the display name to set
     */
    private void setGlobalDisplayName(final String name) {
        String displayName = name;

        if (StringUtils.isNullOrEmpty(displayName)) {
            Collection<ProtocolProviderService> pProviders = AccountUtils.getRegisteredProviders();

            if (pProviders.size() > 0)
                displayName = pProviders.iterator().next().getAccountID().getUserID();
        }

        if(headerResult.getProfiles().size() == 0){

            final IProfile profile = new ProfileDrawerItem().withName(displayName).withEmail(displayName).withIcon(getResources().getDrawable(R.drawable.profile));
            headerResult.addProfile(profile,0);

            // 设置当前活动用户
            headerResult.setActiveProfile(profile);
            //ActionBarUtil.setTitle(getActivity(), displayName);
        }
        //设置在线状态
        publishGlobalStatus(ONLINE);
    }
}
