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

    /** Indicates the command / possition that should be sent to the function. */
    private short position;

    /**
     * Instantiates a new velux command instruction.
     *
     * @param nodeId
     *            the node id
     * @param function
     *            the function
     * @param position
     *            the position
     */
    public VeluxCommandInstruction(byte nodeId, byte function, short position) {
        super(nodeId);
        this.function = function;
        this.position = position;
    }

    /**
     * Gets the function.
     *
     * @return the function
     */
    public byte getFunction() {
        return function;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public short getPosition() {
        return position;
    }
}
