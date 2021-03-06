/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.service.osgi;

import android.app.*;
import android.os.*;
import android.support.v4.app.DialogFragment;

import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * Class can be used to build {@link DialogFragment}s that require OSGI services
 * access.
 *
 * @author Pawel Domas
 */
public class OSGiDialogFragment extends DialogFragment implements OSGiUiPart {
    /**
     * The logger
     */
    private final static Logger logger = Logger.getLogger(OSGiDialogFragment.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        OSGiActivity osGiActivity = (OSGiActivity) activity;
        osGiActivity.registerOSGiFragment(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach() {
        ((OSGiActivity) getActivity()).unregisterOSGiFragment(this); super.onDetach();
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bundleContext) throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext bundleContext) throws Exception {
    }

    /**
     * Convenience method for running code on UI thread looper(instead of
     * getActivity().runOnUIThread()). It is never guaranteed that
     * <tt>getActivity()</tt> will return not <tt>null</tt> value, hence it must
     * be checked in the <tt>action</tt>.
     *
     * @param action <tt>Runnable</tt> action to execute on UI thread.
     */
    protected void runOnUiThread(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run(); return;
        }
        // Post action to the ui looper
        OSGiActivity.uiHandler.post(action);
    }
}
