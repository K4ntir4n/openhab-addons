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
 * Used to indicate the status of a command that is executing.
 *
 * @author MFK - Initial Contribution
 */
public enum VeluxRunStatus {

    /** The execution completed. */
    EXECUTION_COMPLETED(0),
    /** The execution failed. */
    EXECUTION_FAILED(1),
    /** The execution active. */
    EXECUTION_ACTIVE(2),
    /** The unknown. */
    UNKNOWN(99);

    /** The status code. */
    private int statusCode;

    /**
     * Instantiates a new velux run status.
     *
     * @param statusCode
     *            the status code
     */
    private VeluxRunStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Creates the.
     *
     * @param code
     *            the code
     * @return the velux run status
     */
    public static VeluxRunStatus create(byte code) {
        switch (code) {
            case 0:
                return EXECUTION_COMPLETED;
            case 1:
                return EXECUTION_FAILED;
            case 2:
                return EXECUTION_ACTIVE;
            default:
                return UNKNOWN;
        }
    }
}
