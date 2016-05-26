/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.service.osgi;

import org.osgi.framework.*;

/**
 * @author Lyubomir Marinov
 */
public interface BundleContextHolder {
    void addBundleActivator(BundleActivator bundleActivator);

    BundleContext getBundleContext();

    void removeBundleActivator(BundleActivator bundleActivator);
}
