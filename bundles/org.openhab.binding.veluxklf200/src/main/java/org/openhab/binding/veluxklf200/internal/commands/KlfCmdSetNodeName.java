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
package org.openhab.binding.veluxklf200.internal.commands;

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.veluxklf200.internal.commands.structure.KLFCommandCodes;
import org.openhab.binding.veluxklf200.internal.commands.structure.KLFCommandStructure;
import org.openhab.binding.veluxklf200.internal.utility.KLFUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class KLFCMD_GetNode.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class KlfCmdSetNodeName extends BaseKLFCommand {

    /** Logging. */
    private final Logger logger = LoggerFactory.getLogger(KlfCmdSetNodeName.class);

    /** The node id. */
    private byte nodeId;

    /** Node Name */
    @Nullable
    private String name;

    /**
     * Instantiates a new KLFCM D get node.
     *
     * @param nodeId the node id
     * @param velocity velocity
     */
    public KlfCmdSetNodeName(byte nodeId, @Nullable String name) {
        super();
        this.nodeId = nodeId;
        this.name = name;
    }

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public byte getNodeId() {
        return this.nodeId;
    }

    /**
     * Gets the node name.
     *
     * @return the name of the node
     */
    @Nullable
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.veluxklf200.internal.commands.BaseKLFCommand#handleResponse(byte[])
     */
    @Override
    public void handleResponse(byte[] data) {
        logger.trace("Handling response: {}", KLFUtils.formatBytes(data));
        short responseCode = KLFUtils.decodeKLFCommand(data);
        switch (responseCode) {
            case KLFCommandCodes.GW_SET_NODE_NAME_CFM:
                switch (data[FIRSTBYTE]) {
                    case 0:
                        // Command has been accepted by the bridge
                        logger.trace("Command executing, expecting data for node Id: {}.", data[FIRSTBYTE + 1]);
                        this.commandStatus = CommandStatus.COMPLETE;
                        break;
                    case 2:
                        // Invalid system table index
                        logger.error("The KLF200 does not know the node id.");
                        this.commandStatus = CommandStatus.ERROR;
                        break;
                    case 1:
                    default:
                        // Command has been rejected by the bridge
                        logger.error("Command has been rejected by the KLF200 unit.");
                        this.commandStatus = CommandStatus.ERROR;
                        break;

                }
                break;
            default:
                // This should not happen. If it does, the most likely cause is that
                // the KLFCommandStructure has not been configured or implemented
                // correctly.
                this.commandStatus = CommandStatus.ERROR;
                logger.error("Processing requested for a KLF response code (command code) that is not supported: {}.",
                        responseCode);
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.veluxklf200.internal.commands.BaseKLFCommand#getKLFCommandStructure()
     */
    @Override
    public KLFCommandStructure getKLFCommandStructure() {
        return KLFCommandStructure.SET_NODE_NAME;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.veluxklf200.internal.commands.BaseKLFCommand#pack()
     */
    @Override
    protected byte[] pack() {
        String nonNullName = name;
        byte[] data = new byte[65];
        setMainNode(this.nodeId);
        data[0] = this.nodeId;
        if (nonNullName != null) {
            byte[] nameBytes = nonNullName.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(nameBytes, 0, data, 1, nameBytes.length);
        }
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.veluxklf200.internal.commands.BaseKLFCommand#extractSession(short, byte[])
     */
    @Override
    protected int extractSession(short responseCode, byte[] data) {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.veluxklf200.internal.commands.BaseKLFCommand#extractNode(byte[])
     */
    @Override
    protected byte extractNode(short responseCode, byte[] data) {
        switch (responseCode) {
            case KLFCommandCodes.GW_SET_NODE_NAME_CFM:
                return data[FIRSTBYTE + 1];
            default:
                return BaseKLFCommand.NOT_REQUIRED;
        }
    }

    public static boolean isNameLengthValid(@Nullable String name) {
        if (name == null) {
            return false;
        }
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        return nameBytes.length <= 64;
    }
}
