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
 * Blablub
 *
 * @author s.neis - Initial Contribution
 *
 */
@NonNullByDefault
public class VeluxModeInstruction extends AbstractNodeInstruction {

    private byte modeNumber;
    private byte modeParameter;

    public VeluxModeInstruction(byte nodeId, byte modeNumber, byte modeParameter) {
        super(nodeId);
        this.modeNumber = modeNumber;
        this.modeParameter = modeParameter;
    }

    public byte getModeNumber() {
        return modeNumber;
    }

    public byte getModeParameter() {
        return modeParameter;
    }
}
