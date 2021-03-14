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
package org.openhab.binding.veluxklf200.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link VeluxKLF200Configuration} class contains fields mapping thing configuration parameters.
 *
 * @author mkf - Initial contribution
 */
@NonNullByDefault
public class VeluxKLF200Configuration {

    /** The hostname or IP address of the KLF200 unit. */
    @Nullable
    public String hostname;

    /** The port that the KLF200 is listening on, by default 51200. */
    @Nullable
    public Integer port;

    /** The password to access the KLF200. */
    @Nullable
    public String password;

    /**
     * The refresh interval by which the binding will automatically query the KLF200 and update the states of all of the
     * items that the bridge is aware of.
     */
    @Nullable
    public Integer refresh;
}
