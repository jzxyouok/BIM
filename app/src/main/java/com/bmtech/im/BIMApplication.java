package com.bmtech.im;

import android.widget.Toast;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import net.java.sip.communicator.service.protocol.AccountManager;
import net.java.sip.communicator.util.ServiceUtils;

import org.jitsi.android.JitsiApplication;
import org.jitsi.android.gui.AndroidGUIActivator;
import org.jitsi.android.gui.account.AccountLoginActivity;
import org.osgi.framework.BundleContext;

/**
 *
 *
 * Created by lycoris on 2015/5/26.
 */
public class BIMApplication extends JitsiApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.init("BIM")               // default PRETTYLOGGER or use just init()
                .setMethodCount(3)            // default 2
                .hideThreadInfo()             // default shown
                .setLogLevel(LogLevel.NONE)  // default LogLevel.FULL
                .setMethodOffset(2);           // default 0
    }

    /**
     * Returns home <tt>Activity</tt> class.
     *
     * @return Returns home <tt>Activity</tt> class.
     */
    public static Class<?> getHomeScreenActivityClass() {
        BundleContext osgiContext = AndroidGUIActivator.bundleContext;
        if (osgiContext == null) {
            // If OSGI has not started show splash screen as home
            return android.app.LauncherActivity.class;
        }

        AccountManager accountManager = ServiceUtils.getService(osgiContext, AccountManager.class);

        // If account manager is null it means that OSGI has not started yet
        if (accountManager == null) return android.app.LauncherActivity.class;

        final int accountCount = accountManager.getStoredAccounts().size();

        if (accountCount == 0) {
            // Start new account Activity
            return AccountLoginActivity.class;
        }
        else {
            // Start main view
            return MainActivity.class;
        }
    }

}
