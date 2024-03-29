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
package org.gecko.emf.osgi.extender.model;

/**
 * The state of a EMF model.
 *
 * The state represents the EMF model extenders view. It might not
 * reflect the current state of the system. For example if a
 * configuration is installed through the EMF model extender, it gets
 * the state "INSTALLED". However if an administrator now removes the service
 * through any other way like e.g. the web console,
 * the model still has the state "INSTALLED".
 *
 */
public enum ModelState {

    INSTALL,        // the configuration should be installed
    UNINSTALL,      // the configuration should be uninstalled
    INSTALLED,      // the configuration is installed
    UNINSTALLED,    // the configuration is uninstalled
    IGNORED         // the configuration is ignored
}
