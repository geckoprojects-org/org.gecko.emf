/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.gecko.emf.osgi.extender;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

/**
 * Utility class for coordinations
 */
public class CoordinatorUtil {
	
	private static Logger logger = Logger.getLogger(CoordinatorUtil.class.getName());
	
	private CoordinatorUtil() {
	}

    public static Object getCoordination(final Object object) {
        final Coordinator coordinator = (Coordinator) object;
        final Coordination threadCoordination = coordinator.peek();
        if ( threadCoordination == null ) {
            try {
                return coordinator.create("org.apache.felix.configurator", 0L);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, e, ()->"Unable to create new coordination with coordinator " + coordinator);
            }
        }
        return null;
    }

    public static void endCoordination(final Object object) {
        final Coordination coordination = (Coordination) object;
        try {
            coordination.end();
        } catch (final Exception e) {
        	logger.log(Level.SEVERE, e, ()->"Error ending coordination " + coordination);
        }
    }
}
