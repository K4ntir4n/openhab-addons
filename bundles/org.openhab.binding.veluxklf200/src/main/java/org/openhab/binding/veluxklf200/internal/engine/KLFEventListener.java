/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.veluxklf200.internal.engine;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Classes that which to be notified when events occur should implement this interface.
 *
 * @see KLFEventEvent
 * @author MKF - Initial Contribution
 */
@NonNullByDefault
public interface KLFEventListener {

    /**
     * Indicates that an individual event has occurred.
     *
     * @param nodeId The Id of the node on the KLF200 unit.
     * @param currentPosition The current position of the node. Note that this should be interpreted in the context of
     *            the nodeType
     */
    public void handleEvent(byte nodeId, short currentPosition);
}
