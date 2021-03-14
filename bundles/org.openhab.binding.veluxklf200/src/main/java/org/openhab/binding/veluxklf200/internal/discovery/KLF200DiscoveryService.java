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
package org.openhab.binding.veluxklf200.internal.discovery;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.veluxklf200.internal.VeluxKLF200BindingConstants;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdGetAllNodes;
import org.openhab.binding.veluxklf200.internal.commands.KlfCmdGetAllScenes;
import org.openhab.binding.veluxklf200.internal.components.VeluxNode;
import org.openhab.binding.veluxklf200.internal.components.VeluxNodeType;
import org.openhab.binding.veluxklf200.internal.components.VeluxScene;
import org.openhab.binding.veluxklf200.internal.engine.KLFCommandProcessor;
import org.openhab.binding.veluxklf200.internal.handler.KLF200BridgeHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for discovering all of the devices that are available on the KLF200 bridge.
 *
 * @author MFK - Initial Contribution
 */
@NonNullByDefault
public class KLF200DiscoveryService extends AbstractDiscoveryService {

    /** The logger. */
    private Logger logger = LoggerFactory.getLogger(KLF200DiscoveryService.class);

    /** How long to wait for discovery to complete. */
    private static final int DISCOVERY_TIMEOUT = 10;

    /** Reference to the parent bridge. */
    KLF200BridgeHandler bridge;

    /**
     * Constructor with reference to the parent bridge.
     *
     * @param bridge Parent bridge for the KLF200 {@link KLF200BridgeHandler}
     */
    public KLF200DiscoveryService(KLF200BridgeHandler bridge) {
        super(VeluxKLF200BindingConstants.SUPPORTED_VELUX_KLF200_THING_TYPES_UIDS, DISCOVERY_TIMEOUT, true);
        logger.debug("KLF200DiscoveryService {}", bridge);
        this.bridge = bridge;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.config.discovery.AbstractDiscoveryService#abortScan()
     */
    @Override
    public synchronized void abortScan() {
        super.abortScan();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.config.discovery.AbstractDiscoveryService#startScan()
     */
    @Override
    protected void startScan() {
        discoverKLF200Things(bridge.getKLFCommandProcessor(), bridge.getThing().getUID());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.config.discovery.AbstractDiscoveryService#startBackgroundDiscovery()
     */
    @Override
    protected void startBackgroundDiscovery() {
        discoverKLF200Things(bridge.getKLFCommandProcessor(), bridge.getThing().getUID());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.config.discovery.AbstractDiscoveryService#deactivate()
     */
    @Override
    public void deactivate() {
        super.deactivate();
        removeOlderResults(new Date().getTime());
    }

    /**
     * There are many different types of devices that can be controlled by the KLF200. Currentley only scenes and
     * 'Vertical Interior Blinds' are supported. Over time as additional are supported, they should be added here.
     *
     * @param klf200 CommandProcessor
     * @param bridgeUID bridge UID
     */
    private void discoverKLF200Things(@Nullable KLFCommandProcessor klf200, ThingUID bridgeUID) {
        if (klf200 != null) {
            discoverBlinds(klf200, bridgeUID);
            discoverRollerShutters(klf200, bridgeUID);
            discoverScenes(klf200, bridgeUID);
        }
    }

    /**
     * Executes a KLFCMD_GetAllNodes to get a list of all nodes on the KLF200. This is then filtered to retrieve only
     * the nodes that we are interested in. At this time, that is 'Vertical Interior Blinds'. Over time, it is expected
     * that this list will grow to include other types of devices.
     *
     * @param klf200 CommandProcessor
     * @param bridgeUID bridge UID
     */
    private void discoverBlinds(KLFCommandProcessor klf200, ThingUID bridgeUID) {
        KlfCmdGetAllNodes nodes = new KlfCmdGetAllNodes();
        klf200.executeCommand(nodes);
        for (Iterator<VeluxNode> it = nodes.getNodeByType(VeluxNodeType.VERTICAL_INTERIOR_BLINDS).iterator(); it
                .hasNext();) {
            VeluxNode n = it.next();
            logger.info("Found velux blind '{}', node: {}", n.getName(), n.getNodeId());
            ThingUID thingUID = new ThingUID(VeluxKLF200BindingConstants.THING_TYPE_VELUX_BLIND, bridgeUID,
                    String.valueOf(n.getNodeId()));

            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID)
                    .withProperties(getDiscoveredBlindProperties(n)).withBridge(bridgeUID).withLabel(n.getName())
                    .build();

            thingDiscovered(discoveryResult);
        }
    }

    /**
     * Executes a KLFCMD_GetAllNodes to get a list of all nodes on the KLF200. This is then filtered to retrieve only
     * the nodes that we are interested in. At this time, that is 'Vertical Interior Blinds'. Over time, it is expected
     * that this list will grow to include other types of devices.
     *
     * @param klf200 CommandProcessor
     * @param bridgeUID bridge UID
     */
    private void discoverRollerShutters(KLFCommandProcessor klf200, ThingUID bridgeUID) {
        KlfCmdGetAllNodes nodes = new KlfCmdGetAllNodes();
        klf200.executeCommand(nodes);
        for (Iterator<VeluxNode> it = nodes.getNodeByType(VeluxNodeType.ROLLER_SHUTTER).iterator(); it.hasNext();) {
            VeluxNode n = it.next();
            logger.info("Found velux roller shutter '{}', node: {}", n.getName(), n.getNodeId());
            ThingUID thingUID = new ThingUID(VeluxKLF200BindingConstants.THING_TYPE_VELUX_SHUTTER, bridgeUID,
                    String.valueOf(n.getNodeId()));

            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID)
                    .withProperties(getDiscoveredBlindProperties(n)).withBridge(bridgeUID).withLabel(n.getName())
                    .build();

            thingDiscovered(discoveryResult);
        }
    }

    /**
     * Executes a KLFCMD_GetAllScenes to get a list of all of the scenes on the KLF200.
     *
     * @param klf200 CommandProcessor
     * @param bridgeUID bridge UID
     */
    private void discoverScenes(KLFCommandProcessor klf200, ThingUID bridgeUID) {
        KlfCmdGetAllScenes scenes = new KlfCmdGetAllScenes();
        klf200.executeCommand(scenes);
        for (Iterator<VeluxScene> it = scenes.getScenes().iterator(); it.hasNext();) {
            VeluxScene s = it.next();
            logger.info("Found scene '{}', Called: {}", s.getSceneId(), s.getSceneName());
            ThingUID thingUID = new ThingUID(VeluxKLF200BindingConstants.THING_TYPE_VELUX_SCENE, bridgeUID,
                    String.valueOf(s.getSceneId()));

            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withBridge(bridgeUID)
                    .withLabel(s.getSceneName()).build();

            thingDiscovered(discoveryResult);
        }
    }

    /**
     * For each blind that is found, update the thing properties with additional information about that blind as
     * returned from the KLF200.
     *
     * @param node Individual discovered blind
     * @return Properties object containing all of the properties that were discovered.
     */
    private Map<String, Object> getDiscoveredBlindProperties(VeluxNode node) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("Power Mode", String.valueOf(node.getPowerMode()));
        properties.put("Node Variation", String.valueOf(node.getNodeVariation()));
        properties.put("Product Group", node.getProductGroup());
        properties.put("Serial Number", String.valueOf(node.getSerialNumber()));
        properties.put("Velocity", String.valueOf(node.getVelocity()));
        properties.put("Placement", node.getPlacement());
        properties.put("Order", node.getOrder());
        return properties;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.config.discovery.AbstractDiscoveryService#stopScan()
     */
    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }
}
