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
package org.openhab.binding.veluxklf200.internal.handler;

import static org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants.*;
import static org.openhab.binding.veluxklf200.internal.commands.KlfCmdSendCommand.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants;
import org.openhab.binding.veluxklf200.internal.commands.CommandStatus;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdGetNode;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdSendCommand;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdSetVelocity;
import org.openhab.binding.veluxklf200.internal.components.VeluxCommandInstruction;
import org.openhab.binding.veluxklf200.internal.components.VeluxNode;
import org.openhab.binding.veluxklf200.internal.components.VeluxPosition;
import org.openhab.binding.veluxklf200.internal.components.VeluxVelocity;
import org.openhab.binding.veluxklf200.internal.engine.KLFCommandProcessor;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles interactions relating to Vertical Interior Blinds
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
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
        final KLFCommandProcessor processor = getKLFCommandProcessor();
        if (processor == null) {
            return;
        }
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
                    processor.executeCommand(node);

                    final VeluxNode veluxNode = node.getNode();
                    if (veluxNode != null) {
                        if (node.getCommandStatus() == CommandStatus.COMPLETE) {
                            getThing().setLabel(veluxNode.getName());
                            if (VELUX_BLIND_POSITION.equals(channelUID.getId())
                                    || VELUX_SHUTTER_POSITION.equals(channelUID.getId())) {

                                final VeluxPosition position = veluxNode.getCurrentPosition();
                                if (position != null) {
                                    if (position.isUnknown()) {
                                        logger.debug(
                                                "Blind '{}' position is currentley unknown. Need to wait for an activation "
                                                        + "for KLF200 to learn its position.",
                                                veluxNode.getName());
                                        updateState(channelUID, UnDefType.UNDEF);
                                    } else {
                                        int pctClosed = position.getPercentageClosedAsInt();
                                        logger.debug("Blind '{}' is currentley {}% closed.", veluxNode.getName(),
                                                pctClosed);
                                        updateState(channelUID, new PercentType(pctClosed));
                                    }
                                }
                            } else if (VELUX_VELOCITY.equals(channelUID.getId())) {
                                final VeluxVelocity velocity = veluxNode.getVelocity();
                                if (velocity != null) {
                                    switch (velocity) {
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
                            }
                        } else {
                            logger.error("Failed to retrieve information about node {}, error detail: {}",
                                    node.getNodeId(), node.getCommandStatus().getErrorDetail());
                        }
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

                        processor.dispatchCommand(sendCmd);
                    } else if (command instanceof UpDownType) {
                        if (UpDownType.DOWN == command) {
                            logger.debug("Closing blind Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            List<VeluxCommandInstruction> instructions = new ArrayList<>();
                            instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER,
                                    VeluxPosition.setPercentOpen(0)));
                            // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                            KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                            processor.dispatchCommand(sendCmd);
                        } else {
                            logger.debug("Opening blind Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            List<VeluxCommandInstruction> instructions = new ArrayList<>();
                            instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER,
                                    VeluxPosition.setPercentOpen(100)));
                            // instructions.add(new VeluxCommandInstruction(nodeId, MAIN_PARAMETER, (short) 53456));
                            KlfCmdSendCommand sendCmd = new KlfCmdSendCommand(instructions);

                            processor.dispatchCommand(sendCmd);
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

                        processor.dispatchCommand(sendCmd);
                    }
                    break;
                case VeluxKLF200BindingConstants.VELUX_VELOCITY:
                    logger.debug("Trigger velocity set for Id:{} {} to {}.",
                            Integer.valueOf(channelUID.getThingUID().getId()), thing.getLabel(), command);

                    if (command instanceof OnOffType) {
                        if (OnOffType.ON == command) {
                            logger.debug("Set velocity Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            processor.dispatchCommand(new KlfCmdSetVelocity(nodeId, VeluxVelocity.SILENT));
                        } else {
                            logger.debug("Set velocity Id:{} {}.", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            processor.dispatchCommand(new KlfCmdSetVelocity(nodeId, VeluxVelocity.DEFAULT));
                        }
                    }
                    break;
            }
        }
    }
}
