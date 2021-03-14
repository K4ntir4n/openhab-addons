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

/**
 * Used to indicate the velocity that a node is operating / configured for.
 *
 * @author MFK - Initial Contribution
 */
public enum VeluxVelocity {

    /** default. */
    DEFAULT(0, "Default"),

    /** silent. */
    SILENT(1, "Silent"),

    /** fast. */
    FAST(2, "Fast"),

    /** unknown. */
    UNKNOWN(254, "Unknown"),

    /** not available. */
    NOT_AVAILABLE(255, "Not Available");

    /** The velocity code. */
    private int velocityCode;

    /** The display velocity. */
    private String displayVelocity;

    /**
     * Instantiates a new velux velocity.
     *
     * @param code
     *            the code
     * @param displayVelocity
     *            the display velocity
     */
    private VeluxVelocity(int code, String displayVelocity) {
        this.velocityCode = code;
        this.displayVelocity = displayVelocity;
    }

    /**
     * Gets the velocity code.
     *
     * @return the velocity code
     */
    public int getVelocityCode() {
        return this.velocityCode;
    }

    /**
     * Gets the display velocity.
     *
     * @return the display velocity
     */
    public String getDisplayVelocity() {
        return this.displayVelocity;
    }

    /**
     * Creates the from code.
     *
     * @param code
     *            the code
     * @return the velux velocity
     */
    public static VeluxVelocity createFromCode(int code) {
        switch (code) {
            case 0:
                return DEFAULT;
            case 1:
                return SILENT;
            case 2:
                return FAST;
            case 254:
                return NOT_AVAILABLE;
            default:
                return UNKNOWN;
        }
    }
}
