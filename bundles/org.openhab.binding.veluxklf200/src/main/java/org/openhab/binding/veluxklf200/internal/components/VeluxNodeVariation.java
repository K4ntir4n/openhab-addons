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
 * Indicates a node variation.
 *
 * @author MFK - Initial Contribution
 */
public enum VeluxNodeVariation {

    /** The not set. */
    NOT_SET(0),

    /** The tophung. */
    TOPHUNG(1),

    /** The kip. */
    KIP(2),

    /** The flat roof. */
    FLAT_ROOF(3),

    /** The sky light. */
    SKY_LIGHT(4),

    /** The unknown. */
    UNKNOWN(-1);

    /** The variation code. */
    private int variationCode;

    /**
     * Instantiates a new velux node variation.
     *
     * @param code
     *            the code
     */
    private VeluxNodeVariation(int code) {
        this.variationCode = code;
    }

    /**
     * Gets the variation code.
     *
     * @return the variation code
     */
    public int getVariationCode() {
        return this.variationCode;
    }

    /**
     * Creates the.
     *
     * @param c
     *            the c
     * @return the velux node variation
     */
    public static VeluxNodeVariation create(int c) {
        switch (c) {
            case 0:
                return NOT_SET;
            case 1:
                return TOPHUNG;
            case 2:
                return KIP;
            case 3:
                return FLAT_ROOF;
            case 4:
                return SKY_LIGHT;
            default:
                return UNKNOWN;

        }
    }
}
