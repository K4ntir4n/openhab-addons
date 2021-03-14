/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.veluxklf200.internal.components;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Representation of a Scene (Program) as defined on the KLF 200.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class VeluxScene {

    /** The scene id. */
    private int sceneId;

    /** The scene name. */
    private String sceneName;

    /**
     * Instantiates a new velux scene.
     *
     * @param sceneId
     *            the scene id
     * @param sceneName
     *            the scene name
     */
    public VeluxScene(int sceneId, String sceneName) {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
    }

    /**
     * Gets the scene id.
     *
     * @return the scene id
     */
    public int getSceneId() {
        return sceneId;
    }

    /**
     * Gets the scene name.
     *
     * @return the scene name
     */
    public String getSceneName() {
        return sceneName;
    }
}
