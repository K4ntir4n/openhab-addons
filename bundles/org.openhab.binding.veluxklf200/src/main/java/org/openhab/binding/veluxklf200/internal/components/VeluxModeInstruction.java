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
