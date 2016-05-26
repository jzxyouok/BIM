/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.android.gui;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;

import org.jitsi.android.gui.util.*;
import org.jitsi.service.osgi.*;

/**
 * Activity displayed when shutdown procedure is in progress.
 *
 * @author Pawel Domas
 */
public class ShutdownActivity extends AppCompatActivity {
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        if (!OSGiService.hasStarted()) {
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
            return;
        }

        if (AndroidUtils.hasAPI(11)) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {

                // Disable up arrow
                actionBar.setDisplayHomeAsUpEnabled(false);
                if (AndroidUtils.hasAPI(14)) {
                    actionBar.setHomeButtonEnabled(false);
                }
                ActionBarUtil.setTitle(this, getTitle());
            }
        }

        // lycoris
        finish();

        //setProgressBarIndeterminateVisibility(true);

        //setContentView(R.layout.activity_launcher);

        //((TextView) findViewById(R.id.restoring)).setText(R.string.service_gui_SHUTDOWN_IN_PROGRESS);
    }
}
