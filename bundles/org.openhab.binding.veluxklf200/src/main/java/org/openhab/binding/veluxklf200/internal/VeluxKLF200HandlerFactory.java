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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.veluxklf200.internal.discovery.KLF200DiscoveryService;
import org.openhab.binding.veluxklf200.internal.handler.KLF200BlindHandler;
import org.openhab.binding.veluxklf200.internal.handler.KLF200BridgeHandler;
import org.openhab.binding.veluxklf200.internal.handler.KLF200SceneHandler;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VeluxKLF200HandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author mkf - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.veluxklf200", service = ThingHandlerFactory.class)
public class VeluxKLF200HandlerFactory extends BaseThingHandlerFactory {

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger(VeluxKLF200HandlerFactory.class);

    /** A registry of things we have discovered. */
    private final Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory#supportsThingType(org.eclipse.smarthome.core.thing.
     * ThingTypeUID)
     */
    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return VeluxKLF200BindingConstants.SUPPORTED_VELUX_KLF200_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory#createHandler(org.eclipse.smarthome.core.thing.
     * Thing)
     */
    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(VeluxKLF200BindingConstants.THING_TYPE_VELUX_KLF200)) {
            KLF200BridgeHandler handler = new KLF200BridgeHandler((Bridge) thing);
            registerDiscoveryService(handler);
            logger.debug("Creating the bridge handler.");
            return handler;
        }
        if (thingTypeUID.equals(VeluxKLF200BindingConstants.THING_TYPE_VELUX_BLIND)
                || thingTypeUID.equals(VeluxKLF200BindingConstants.THING_TYPE_VELUX_SHUTTER)) {
            logger.debug("Creating the blind handler.");
            return new KLF200BlindHandler(thing);
        }
        if (thingTypeUID.equals(VeluxKLF200BindingConstants.THING_TYPE_VELUX_SCENE)) {
            logger.debug("Creating the scene handler.");
            return new KLF200SceneHandler(thing);
        }
        return null;
    }

    /**
     * Registers a discovery service for the bridge handler.
     *
     * @param bridgeHandler handler to register service for
     */
    private synchronized void registerDiscoveryService(KLF200BridgeHandler bridgeHandler) {
        logger.debug("Registering discovery service for the KLF200 bridgeHandler");
        KLF200DiscoveryService discoveryService = new KLF200DiscoveryService(bridgeHandler);
        this.discoveryServiceRegs.put(bridgeHandler.getThing().getUID(),
                bundleContext.registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<>()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory#removeHandler(org.eclipse.smarthome.core.thing.
     * binding.ThingHandler)
     */
    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        logger.debug("Removing handler: {}.", thingHandler);
        if (thingHandler instanceof KLF200BridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                serviceReg.unregister();
            }
        }
    }
}
