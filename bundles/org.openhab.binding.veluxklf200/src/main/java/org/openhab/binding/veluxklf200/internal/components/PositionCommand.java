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
import org.eclipse.jdt.annotation.Nullable;

/**
 * Provides the information / parameters required to execute a command.
 *
 * @author K4ntir4n - Initial Contribution
 */
@NonNullByDefault
public class PositionCommand extends VeluxCommandInstruction {

    /** Indicates the command / position that should be sent to the function. */
    private short position;

    @Nullable
    private Short speed;

    public PositionCommand(byte nodeId, byte function, short position) {
        this(nodeId, function, position, null);
    }

    public PositionCommand(byte nodeId, byte function, short position, @Nullable Short speed) {
        super(nodeId, function);
        this.position = position;
        this.speed = speed;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public short getPosition() {
        return position;
    }

    @Nullable
    public Short getSpeed() {
        return speed;
    }

}
