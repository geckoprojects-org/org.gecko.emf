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
package org.eclipse.fennec.emf.osgi.extender.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class State extends AbstractState implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Serialization version. */
    private static final int VERSION = 1;

    public static final String FILE_NAME = "modelstate.ser";

    private Map<Long, Long> bundlesLastModified = new HashMap<>();

    /**
     * Serialize the object
     * - write version id
     * - serialize fields
     * @param out Object output stream
     * @throws IOException
     */
    private void writeObject(final java.io.ObjectOutputStream out)
    throws IOException {
        out.writeInt(VERSION);
        out.writeObject(bundlesLastModified);
    }

    /**
     * Deserialize the object
     * - read version id
     * - deserialize fields
     */
    @SuppressWarnings("unchecked")
    private void readObject(final java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        final int version = in.readInt();
        if ( version < 1 || version > VERSION ) {
            throw new ClassNotFoundException(this.getClass().getName());
        }
        this.bundlesLastModified =(Map<Long, Long>) in.readObject();
    }

    public static State createOrReadState(final File f)
    throws ClassNotFoundException, IOException {
        if ( f == null || !f.exists() ) {
            return new State();
        }
        try ( final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)) ) {

            return (State) ois.readObject();
        }
    }

    public static void writeState(final File f, final State state)
    throws IOException {
        if ( f == null ) {
            // do nothing, no file system support
            return;
        }
        try ( final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f)) ) {
            oos.writeObject(state);
        }
    }

    public Long getLastModified(final long bundleId) {
        return this.bundlesLastModified.get(bundleId);
    }

    public void setLastModified(final long bundleId, final long lastModified) {
        this.bundlesLastModified.put(bundleId, lastModified);
    }

    public void removeLastModified(final long bundleId) {
        this.bundlesLastModified.remove(bundleId);
    }

    public Set<Long> getKnownBundleIds() {
        return this.bundlesLastModified.keySet();
    }

    /**
     * Mark all configurations from that bundle as changed to reprocess them
     * @param bundleId The bundle id
     */
    public void checkEnvironments(final long bundleId) {
        for(final String pid : this.getNamespaces()) {
            final ModelNamespace configList = this.getModels(pid);
            for(final Model cfg : configList) {
                if ( cfg.getBundleId() == bundleId ) {
                    configList.setHasChanges(true);
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "State [bundlesLastModified=" + bundlesLastModified+ "]";
    }
}
