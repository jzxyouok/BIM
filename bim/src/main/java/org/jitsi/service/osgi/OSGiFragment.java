package org.jitsi.service.osgi;

import android.content.Context;
import android.os.*;
import android.support.v4.app.Fragment;

import net.java.sip.communicator.util.*;

import org.osgi.framework.*;

/**
 * Class can be used to build {@link Fragment}s that require OSGI services
 * access.
 *
 * @author Pawel Domas
 */
public class OSGiFragment extends Fragment implements OSGiUiPart {
    /**
     * The logger
     */
    private static final Logger logger = Logger.getLogger(OSGiFragment.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        OSGiActivity osGiActivity = (OSGiActivity) context;
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
        logger.error("OSGiFragment start...");
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext bundleContext) throws Exception {
        logger.error("OSGiFragment stop...");
    }

    /**
     * Convenience method for running code on UI thread looper(instead of
     * getActivity().runOnUIThread()). It is never guaranteed that
     * <tt>getActivity()</tt> will return not <tt>null</tt> value, hence it must
     * be checked in the <tt>action</tt>.
     *
     * @param action <tt>Runnable</tt> action to execute on UI thread.
     */
    public void runOnUiThread(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run(); return;
        }
        // Post action to the ui looper
        OSGiActivity.uiHandler.post(action);
    }
}
