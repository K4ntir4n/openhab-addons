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
package org.openhab.binding.veluxklf200.internal.components;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Provides the information / parameters required to execute a command.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class VeluxCommandInstruction extends AbstractNodeInstruction {

    /**
     * Indicates the functional parameter of the device that the command is to
     * be executed on. The 'main parameter' is parameter 0 (zero)
     */
    private byte function;

    /**
     * Instantiates a new velux command instruction.
     *
     * @param nodeId
     *            the node id
     * @param function
     *            the function
     */
    public VeluxCommandInstruction(byte nodeId, byte function) {
        super(nodeId);
        this.function = function;
    }

    /**
     * Gets the function.
     *
     * @return the function
     */
    public byte getFunction() {
        return function;
    }

}
