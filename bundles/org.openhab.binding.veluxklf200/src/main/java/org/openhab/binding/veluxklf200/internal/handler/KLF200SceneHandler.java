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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdExecuteScene;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdStopScene;
import org.openhab.binding.veluxklf200.internal.engine.KLFCommandProcessor;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles interactions with a Scene configured on the KLF200.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class KLF200SceneHandler extends KLF200BaseThingHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(KLF200SceneHandler.class);

    /**
     * Constructor
     *
     * @param thing the thing
     */
    public KLF200SceneHandler(Thing thing) {
        super(thing);
    }

    /*
     * By default the 'trigger_scene' switch is set to be OFF. When a user interacts and turns it on, the scene is
     * executed. The handler blocks until the scene has executed fully and then updates the state of the item to OFF
     * again.
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.ThingHandler#handleCommand(org.eclipse.smarthome.core.thing.ChannelUID,
     * org.eclipse.smarthome.core.types.Command)
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handling scene state refresh command.");
        if (command == RefreshType.REFRESH) {
            switch (channelUID.getId()) {
                case VeluxKLF200BindingConstants.KLF200_TRIGGER_SCENE: {
                    updateState(channelUID, OnOffType.OFF);
                    break;
                }
            }
        } else {
            final KLFCommandProcessor processor = getKLFCommandProcessor();
            if (processor != null) {
                switch (channelUID.getId()) {
                    case VeluxKLF200BindingConstants.KLF200_TRIGGER_SCENE:
                        Thing thing = getThing();
                        if (command.equals(OnOffType.ON)) {
                            logger.debug("Trigger Scene ID:{} {}", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            KlfCmdExecuteScene execScene = new KlfCmdExecuteScene(
                                    (byte) Integer.valueOf(channelUID.getThingUID().getId()).intValue());
                            processor.dispatchCommand(execScene);
                            scheduler.execute(new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (execScene) {
                                        try {
                                            execScene.wait();
                                        } catch (InterruptedException e) {
                                            logger.warn("Expected execption while waiting for scene to execute: ", e);
                                        }
                                        updateState(channelUID, OnOffType.OFF);
                                    }
                                }
                            });

                        } else if (command.equals(OnOffType.OFF)) {
                            logger.debug("Stop Scene ID:{} {}", Integer.valueOf(channelUID.getThingUID().getId()),
                                    thing.getLabel());
                            processor.executeCommand(new KlfCmdStopScene(
                                    (byte) Integer.valueOf(channelUID.getThingUID().getId()).intValue()));
                        }
                }
            }
        }
    }
}
