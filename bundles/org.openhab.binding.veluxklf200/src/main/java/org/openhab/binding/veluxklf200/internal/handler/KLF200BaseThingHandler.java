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
package org.openhab.binding.veluxklf200.internal.handler;

import static org.openhab.binding.veluxklf200.internal.commands.KlfCmdSetNodeName.isNameLengthValid;

import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdSetNodeName;
import org.openhab.binding.veluxklf200.internal.engine.KLFCommandProcessor;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstraction from the main handlers to include some helper functionality such as methods for finding the bridge and
 * finding things
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public abstract class KLF200BaseThingHandler extends BaseThingHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(KLF200BaseThingHandler.class);

    /**
     * Constructor
     *
     * @param thing The thing
     */
    public KLF200BaseThingHandler(Thing thing) {
        super(thing);
    }

    /**
     * Gets a channel by channel UID.
     *
     * @param channelUID the channel UID
     * @return the channel from the channel UID or null if not found
     */
    @Nullable
    protected Channel getChannelByChannelUID(ChannelUID channelUID) {
        if (thing.getChannel(channelUID.getId()) != null) {
            return thing.getChannel(channelUID.getId());
        }
        logger.debug("Cannot find channel for UID: {}", channelUID.getId());
        return null;
    }

    /**
     * Gets the parent bridge handler.
     *
     * @return the parent bridge handler.
     */
    @Nullable
    protected KLF200BridgeHandler getBridgeHandler() {
        Bridge b = getBridge();
        if (null != b) {
            return (KLF200BridgeHandler) b.getHandler();
        }
        return null;
    }

    /**
     * Gets the KLFCommandProcessor from the bridge.
     *
     * @return the KLF CommandProcessor
     */
    @Nullable
    protected KLFCommandProcessor getKLFCommandProcessor() {
        final KLF200BridgeHandler bridgeHandler = getBridgeHandler();
        if (bridgeHandler != null) {
            return bridgeHandler.getKLFCommandProcessor();
        }
        return null;
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
    }

    @Override
    public void thingUpdated(Thing thing) {
        Thing oldThing = getThing();

        final KLFCommandProcessor processor = getKLFCommandProcessor();
        if (processor != null && !Objects.equals(oldThing.getLabel(), thing.getLabel())
                && isNameLengthValid(thing.getLabel())) {
            KlfCmdSetNodeName node = new KlfCmdSetNodeName(
                    (byte) Integer.valueOf(getThing().getUID().getId()).intValue(), thing.getLabel());
            processor.executeCommand(node);
        }

        super.thingUpdated(thing);
    }
}
