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
package org.openhab.binding.veluxklf200.internal.utility;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Certain commands that are sent to the KLF200 unit require a session
 * identifier so that responses can be attributed to that particular session.
 * This is a helper singleton that provides unique session identifiers.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class KLFSession {

    /** The instance. */
    @Nullable
    private static KLFSession instance;

    /** Mutex to ensure thread safety */
    private static Object mutex = new Object();

    /** The session. */
    private short session;

    /**
     * Instantiates a new KLF session.
     */
    private KLFSession() {
        this.session = 1;
    }

    /**
     * Gets the session identifier.
     *
     * @return the session identifier
     */
    public short getSessionIdentifier() {
        return this.session++;
    }

    /**
     * Gets the single instance of KLFSession.
     *
     * @return single instance of KLFSession
     */
    public static KLFSession getInstance() {
        KLFSession result = instance;
        if (null == result) {
            synchronized (mutex) {
                result = instance;
                if (null == result) {
                    instance = result = new KLFSession();
                }
            }
        }
        return result;
    }
}
