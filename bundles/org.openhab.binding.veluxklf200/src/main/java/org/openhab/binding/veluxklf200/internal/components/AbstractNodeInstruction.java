/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.veluxklf200.internal.components;

/**
 * Blablub
 *
 * @author s.neis - Initial Contribution
 *
 */
public abstract class AbstractNodeInstruction {

    /** Indicates the node on which the command is to be executed. */
    protected byte nodeId;

    public AbstractNodeInstruction(byte nodeId) {
        super();
        this.nodeId = nodeId;
    }

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public byte getNodeId() {
        return nodeId;
    }

}