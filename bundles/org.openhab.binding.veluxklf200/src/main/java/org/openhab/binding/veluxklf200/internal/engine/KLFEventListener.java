/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.veluxklf200.internal.engine;

/**
 * Classes that which to be notified when events occur should implement this interface.
 *
 * @see KLFEventEvent
 * @author MKF - Initial Contribution
 */
public interface KLFEventListener {

    /**
     * Indicates that an individual event has occurred.
     *
     * @param nodeId          The Id of the node on the KLF200 unit.
     * @param currentPosition The current position of the node. Note that this should be interpreted in the context of
     *                            the nodeType
     */
    public void handleEvent(byte nodeId, short currentPosition);
}
