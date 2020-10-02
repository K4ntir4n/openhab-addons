/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.veluxklf200.internal.commands;

import java.nio.charset.StandardCharsets;

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
public class KlfCmdSetNodeName extends BaseKLFCommand {

    /** Logging. */
    private final Logger logger = LoggerFactory.getLogger(KlfCmdSetNodeName.class);

    /** The node id. */
    private byte nodeId;

    /** Node Name */
    private String name;

    /**
     * Instantiates a new KLFCM D get node.
     *
     * @param nodeId   the node id
     * @param velocity velocity
     */
    public KlfCmdSetNodeName(byte nodeId, String name) {
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
        setMainNode(this.nodeId);
        byte[] data = new byte[65];
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        data[0] = this.nodeId;
        System.arraycopy(nameBytes, 0, data, 1, nameBytes.length);
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

    public static boolean isNameLengthValid(String name) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        return nameBytes.length <= 64;
    }

}
