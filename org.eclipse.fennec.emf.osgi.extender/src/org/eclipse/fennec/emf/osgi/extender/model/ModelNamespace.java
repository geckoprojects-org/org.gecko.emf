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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The model list holds all models for a single namespace.
 * Usually only one model exists for a namespace, but there might be use-case that make the need of many models per name-space necessary
 */
public class ModelNamespace implements Serializable, Iterable<Model> {

    private static final long serialVersionUID = 1L;

    /** Serialization version. */
    private static final int VERSION = 1;

    private List<Model> models = new ArrayList<>();

    /** The change count. */
    private volatile long changeCount = -1;

    /** Flag to indicate whether this list needs to be processed. */
    private volatile boolean hasChanges;

    /** Last registered model. */
    private Model lastInstalled;

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
        out.writeObject(models);
        out.writeObject(lastInstalled);
        out.writeLong(changeCount);
        out.writeBoolean(hasChanges);
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
        this.models = (List<Model>) in.readObject();
        lastInstalled = (Model) in.readObject();
        this.changeCount = in.readLong();
        this.hasChanges = in.readBoolean();
    }

    /**
     * Does this list need to be processed
     * @return {@code true} if it needs processing.
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    /**
     * Set the has changes flag.
     * @param value New value.
     */
    public void setHasChanges(final boolean value) {
        this.hasChanges = hasChanges;
    }

    /**
     * Add a model to the list.
     * @param m The model.
     */
    public void add(final Model m) {
        this.hasChanges = true;
        this.models.add(m);
    }

    /**
     * Add all models from another model list
     * @param models The current model list
     */
    public void addAll(final List<Model> models) {
        for(final Model model : models) {
            // search if we already have this configuration

            for(final Model current : this.models) {
                if ( current.getBundleId() == model.getBundleId()
                  && current.getProperties().equals(model.getProperties()) ) {
                    if ( current.getState() == ModelState.UNINSTALL ) {
                        model.setState(ModelState.INSTALLED);
                        current.setState(ModelState.UNINSTALLED);
                    }
                    break;
                }
            }

            this.hasChanges = true;
            this.models.add(model);
        }
    }

    /**
     * Get the size of the list of models
     * @return
     */
    public int size() {
        return this.models.size();
    }

    @Override
    public Iterator<Model> iterator() {
        return this.models.iterator();
    }

    /**
     * Get the change count.
     * @return The change count
     */
    public long getChangeCount() {
        return this.changeCount;
    }

    /**
     * Set the change count
     * @param value The new change count
     */
    public void setChangeCount(final long value) {
        this.changeCount = value;
    }

    public Model getLastInstalled() {
        return lastInstalled;
    }

    public void setLastInstalled(Model lastInstalled) {
        this.lastInstalled = lastInstalled;
    }

    /**
     * Mark models for a bundle uninstall
     * @param bundleId The bundle id of the uninstalled bundle
     */
    public void uninstall(final long bundleId) {
        for(final Model model : this.models) {
            if ( model.getBundleId() == bundleId ) {
                this.hasChanges = true;
                if ( model.getState() == ModelState.INSTALLED ) {
                    model.setState(ModelState.UNINSTALL);
                } else {
                    model.setState(ModelState.UNINSTALLED);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ModelList [models=" + models + ", changeCount=" + changeCount + ", hasChanges="
                + hasChanges + ", lastInstalled=" + lastInstalled + "]";
    }
}
