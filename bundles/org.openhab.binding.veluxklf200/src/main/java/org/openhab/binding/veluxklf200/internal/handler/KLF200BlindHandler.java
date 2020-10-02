/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.veluxklf200.internal.handler;

import static org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants.*;
import static org.openhab.binding.veluxklf200.internal.commands.KlfCmdSendCommand.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants;
import org.openhab.binding.veluxklf200.internal.commands.CommandStatus;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdGetNode;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdSendCommand;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdSetVelocity;
import org.openhab.binding.veluxklf200.internal.components.VeluxCommandInstruction;
import org.openhab.binding.veluxklf200.internal.components.VeluxPosition;
import org.openhab.binding.veluxklf200.internal.components.VeluxVelocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles interactions relating to Vertical Interior Blinds
 *
 * @author MFK - Initial Contribution
 */
public class KLF200BlindHandler extends KLF200BaseThingHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(KLF200BlindHandler.class);

    /**
     * Constructor
     *
     * @param thing thing
     */
    public KLF200BlindHandler(Thing thing) {
        super(thing);
    }

    /*
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.ThingHandler#handleCommand(org.eclipse.smarthome.core.thing.ChannelUID,
     * org.eclipse.smarthome.core.types.Command)
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {
            logger.debug("Handling blind state refresh command.");
            switch (channelUID.getId()) {
                case VeluxKLF200BindingConstants.VELUX_BLIND_POSITION:
                case VeluxKLF200BindingConstants.VELUX_SHUTTER_POSITION:
                case VeluxKLF200BindingConstants.VELUX_VELOCITY: {
                    logger.debug("Updating state for Velux blind [{}] Id: {}", getThing().getLabel(),
                            getThing().getUID().getId());
                    KlfCmdGetNode node = new KlfCmdGetNode(
                            (byte) Integer.valueOf(getThing().getUID().getId()).intValue());
                    getKLFCommandProcessor().executeCommand(node);
                    if (node.getCommandStatus() == CommandStatus.COMPLETE) {
                        getThing().setLabel(node.getNode().getName());
                        if (VELUX_BLIND_POSITION.equals(channelUID.getId())
                                || VELUX_SHUTTER_POSITION.equals(channelUID.getId())) {
                            if (node.getNode().getCurrentPosition().isUnknown()) {
                                logger.debug(
                                        "Blind '{}' position is currentley unknown. Need to wait for an activation "
                                                + "for KLF200 to learn its position.",
                                        node.getNode().getName());
                                updateState(channelUID, UnDefType.UNDEF);
                            } else {
                                int pctClosed = node.getNode().getCurrentPosition().getPercentageClosedAsInt();
                                logger.debug("Blind '{}' is currentley {}% closed.", node.getNode().getName(),
                                        pctClosed);
                                updateState(channelUID, new PercentType(pctClosed));
                            }
                        } else if (VELUX_VELOCITY.equals(channelUID.getId())) {
                            switch (node.getNode().getVelocity()) {
                                case SILENT:
                                    updateState(channelUID, OnOffType.ON);
                                    break;
                                case FAST:
                                case DEFAULT:
                                    updateState(channelUID, OnOffType.OFF);
                                    break;
                                case NOT_AVAILABLE:
                                default:
                                    updateState(channelUID, UnDefType.UNDEF);
                                    break;
                            }
                        }
                    } else {
                        logger.error("Failed to retrieve information about node {}, error detail: {}", node.getNodeId(),
                                node.getCommandStatus().getErrorDetail());
                    }
                    break;
                }
            }
        } else {
            logger.debug("Handling blind state change command.");
            byte nodeId = (byte) Integer.valueOf(getThing().getUID().getId()).intValue();
            switch (channelUID.getId()) {
                case VeluxKLF200BindingConstants.VELUX_BLIND_POSITION:
                case VeluxKLF200BindingConstants.VELUX_SHUTTER_POSITION:
                    logger.debug("Trigger blind movement for blind Id:{} {} to {}.",
                            Integer.valueOf(channelUID.getThingUID().getId()), thing.getLabel(), command);

                    if ((command instanceof StopMoveType) && (StopMoveType.STOP == command)) {
                        logger.debug("Attempting to stop actuation of blind Id:{} {}.",
                                Integer.valueOf(channelUID.getThingUID().getId()), thing.getLabel());

                        List<VeluxCommandInstruction> instructions = new ArrayList<>();
                        instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, STOP_PARAMETER));
                        // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                        KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                        getKLFCommandProcessor().dispatchCommand(sendCmd);
                    } else if (command instanceof UpDownType) {
                        if (UpDownType.DOWN == command) {
                            logger.debug("Closing blind Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            List<VeluxCommandInstruction> instructions = new ArrayList<>();
                            instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER,
                                    VeluxPosition.setPercentOpen(0)));
                            // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                            KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                            getKLFCommandProcessor().dispatchCommand(sendCmd);
                        } else {
                            logger.debug("Opening blind Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            List<VeluxCommandInstruction> instructions = new ArrayList<>();
                            instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER,
                                    VeluxPosition.setPercentOpen(100)));
                            // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                            KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                            getKLFCommandProcessor().dispatchCommand(sendCmd);
                        }
                    } else if (command instanceof PercentType) {
                        PercentType percentType = (PercentType) command;
                        percentType.doubleValue();
                        logger.debug("Moving blind Id:{} {} to {}% closed.",
                                Integer.valueOf(channelUID.getThingUID().getId()), thing.getLabel(),
                                percentType.doubleValue());

                        List<VeluxCommandInstruction> instructions = new ArrayList<>();
                        instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER,
                                VeluxPosition.setPercentClosed((int) percentType.doubleValue())));
                        // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                        KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                        getKLFCommandProcessor().dispatchCommand(sendCmd);
                    }
                    break;
                case VeluxKLF200BindingConstants.VELUX_VELOCITY:
                    logger.debug("Trigger velocity set for Id:{} {} to {}.",
                            Integer.valueOf(channelUID.getThingUID().getId()), thing.getLabel(), command);

                    if (command instanceof OnOffType) {
                        if (OnOffType.ON == command) {
                            logger.debug("Set velocity Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            getKLFCommandProcessor()
                                    .dispatchCommand(new KlfCmdSetVelocity(nodeId, VeluxVelocity.SILENT));
                        } else {
                            logger.debug("Set velocity Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            getKLFCommandProcessor()
                                    .dispatchCommand(new KlfCmdSetVelocity(nodeId, VeluxVelocity.DEFAULT));
                        }
                    }
                    break;
            }
        }
    }
}
