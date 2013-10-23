
/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.statisticsmanager.internal;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.felix.dm.Component;
import org.opendaylight.controller.clustering.services.ICacheUpdateAware;
import org.opendaylight.controller.clustering.services.IClusterContainerServices;
import org.opendaylight.controller.connectionmanager.IConnectionManager;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.controller.sal.core.IContainer;
import org.opendaylight.controller.sal.inventory.IListenInventoryUpdates;
import org.opendaylight.controller.sal.reader.IReadService;
import org.opendaylight.controller.sal.reader.IReadServiceListener;
import org.opendaylight.controller.statisticsmanager.IStatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator extends ComponentActivatorAbstractBase {
    protected static final Logger logger = LoggerFactory.getLogger(Activator.class);


    /**
     * Function that is used to communicate to dependency manager the list of
     * known implementations for services inside a container
     *
     *
     * @return An array containing all the CLASS objects that will be
     *         instantiated in order to get an fully working implementation
     *         Object
     */
    public Object[] getImplementations() {
        Object[] res = { StatisticsManager.class };
        return res;
    }

    /**
     * Function that is called when configuration of the dependencies is
     * required.
     *
     * @param c
     *            dependency manager Component object, used for configuring the
     *            dependencies exported and imported
     * @param imp
     *            Implementation class that is being configured, needed as long
     *            as the same routine can configure multiple implementations
     * @param containerName
     *            The containerName being configured, this allow also optional
     *            per-container different behavior if needed, usually should not
     *            be the case though.
     */
    public void configureInstance(Component c, Object imp, String containerName) {
        if (imp.equals(StatisticsManager.class)) {
            // export the service
            Dictionary<String, Object> props = new Hashtable<String, Object>();
            Set<String> propSet = new HashSet<String>();
            // trigger cache
            propSet.add(StatisticsManager.TRIGGERS_CACHE);
            // flow statistics cache
            propSet.add(StatisticsManager.FLOW_STATISTICS_CACHE);
            props.put("cachenames", propSet);

            String interfaces[] = new String[] {
                    IStatisticsManager.class.getName(),
                    IReadServiceListener.class.getName(),
                    IListenInventoryUpdates.class.getName(),
                    ICacheUpdateAware.class.getName() };
            c.setInterface(interfaces, props);

            c.add(createContainerServiceDependency(containerName).setService(IReadService.class)
                    .setCallbacks("setReaderService", "unsetReaderService").setRequired(true));
            c.add(createContainerServiceDependency(containerName).setService(IClusterContainerServices.class)
                    .setCallbacks("setClusterContainerService", "unsetClusterContainerService").setRequired(true));
            c.add(createContainerServiceDependency(containerName).setService(IContainer.class)
                    .setCallbacks("setIContainer", "unsetIContainer").setRequired(true));
            c.add(createServiceDependency().setService(IConnectionManager.class)
                    .setCallbacks("setIConnectionManager", "unsetIConnectionManager").setRequired(false));

        }
    }
}
