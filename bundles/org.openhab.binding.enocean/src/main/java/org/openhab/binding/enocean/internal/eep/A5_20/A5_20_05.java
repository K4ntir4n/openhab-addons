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
package org.openhab.binding.enocean.internal.eep.A5_20;

import static org.openhab.binding.enocean.internal.EnOceanBindingConstants.*;

import java.util.function.Function;

import org.openhab.binding.enocean.internal.messages.ERP1Message;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * Vebtilation Unit
 *
 * @author Stefan Neis - Initial contribution
 */
public class A5_20_05 extends A5_20 {

    private enum SPEED_STATUS {
        MIN,
        LOW,
        MID,
        HIGH,
        MAX,
        AUTO;
    }

    public A5_20_05() {
        super();
    }

    public A5_20_05(ERP1Message packet) {
        super(packet);
    }

    private byte getNewSpeedSetting(Function<String, State> getCurrentStateFunc) {
        State current = getCurrentStateFunc.apply(CHANNEL_SPEEDSETTING);

        if ((current != null) && (current instanceof StringType)) {
            StringType state = current.as(StringType.class);

            if (state != null) {
                int value = SPEED_STATUS.valueOf(state.toFullString()).ordinal();
                return (byte) (value << 5);
            }
        }

        return (byte) (SPEED_STATUS.AUTO.ordinal() << 5);
    }

    private byte getNewSpeedTimerSetting(Function<String, State> getCurrentStateFunc) {
        State current = getCurrentStateFunc.apply(CHANNEL_NEWSPEEDTIMERSETTING);

        if ((current != null) && (current instanceof DecimalType)) {
            DecimalType state = current.as(DecimalType.class);

            if (state != null) {
                int value = state.intValue();
                if (value > 29) {
                    return 0;
                }
                return state.byteValue();
            }
        }

        return 0;
    }

    private byte getResetError(Function<String, State> getCurrentStateFunc) {
        State current = getCurrentStateFunc.apply(CHANNEL_RESETERROR);

        if ((current != null) && (current instanceof OnOffType)) {
            OnOffType state = current.as(OnOffType.class);

            if (state != null) {
                return (byte) (state.equals(OnOffType.ON) ? 0x80 : 0x00);
            }
        }

        return 0x00; // off
    }

    private byte getResetFilterTimer(Function<String, State> getCurrentStateFunc) {
        State current = getCurrentStateFunc.apply(CHANNEL_RESETFILTERTIMER);

        if ((current != null) && (current instanceof OnOffType)) {
            OnOffType state = current.as(OnOffType.class);

            if (state != null) {
                return (byte) (state.equals(OnOffType.ON) ? 0x40 : 0x00);
            }
        }

        return 0x00; // off
    }

    @Override
    protected void convertFromCommandImpl(String channelId, String channelTypeId, Command command,
            Function<String, State> getCurrentStateFunc, Configuration config) {
        if (CHANNEL_SEND_COMMAND.equals(channelId) && (command.equals(OnOffType.ON))) {
            byte db3 = (byte) (getNewSpeedSetting(getCurrentStateFunc) | getNewSpeedTimerSetting(getCurrentStateFunc));
            byte db2 = 0x00;
            byte db1 = 0x00;
            byte db0 = (byte) (0x00 | getResetFilterTimer(getCurrentStateFunc) | 0x08
                    | getResetError(getCurrentStateFunc));

            setData(db3, db2, db1, db0);

            return;
        }
    }

    @Override
    protected State convertToStateImpl(String channelId, String channelTypeId,
            Function<String, State> getCurrentStateFunc, Configuration config) {

        switch (channelId) {
            case CHANNEL_SPEEDSETTING:
                return getActualSpeedSetting();
            case CHANNEL_ACTUALSPEEDTIMERSETTING:
                return getActualSpeedTimerSetting();
            case CHANNEL_NODELOWBATTERY:
                return getNodeLowBattery();
            case CHANNEL_NODECOMMUNICATIONERROR:
                return getNodeCommunicationError();
            case CHANNEL_SENSORERROR:
                return getSensorError();
            case CHANNEL_FANSPEEDERROR:
                return getFanSpeedError();
            case CHANNEL_ERROR:
                return getError();
            case CHANNEL_FILTERCONDITION:
                return getFilterCondition();
            case CHANNEL_AUTOSPEEDSUPPORT:
                return getAutoSpeedSupport();
            case CHANNEL_BYPASSACTIVE:
                return getByPassActive();
            case CHANNEL_FROSTPROTECTION:
                return getFrostProtection();
        }

        switch (channelId) {
            case CHANNEL_NEWSPEEDTIMERSETTING:
            case CHANNEL_RESETERROR:
            case CHANNEL_RESETFILTERTIMER:
                return null;
        }

        return UnDefType.UNDEF;
    }

    private State getFrostProtection() {
        return getBit(getDB_0Value(), 5) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getByPassActive() {
        return getBit(getDB_0Value(), 6) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getAutoSpeedSupport() {
        return getBit(getDB_0Value(), 7) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getFilterCondition() {
        return new QuantityType<>(getDB_1Value(), Units.PERCENT);
    }

    private State getError() {
        return getBit(getDB_2Value(), 0) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getFanSpeedError() {
        return getBit(getDB_2Value(), 1) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getSensorError() {
        return getBit(getDB_2Value(), 2) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getNodeCommunicationError() {
        return getBit(getDB_2Value(), 3) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getNodeLowBattery() {
        return getBit(getDB_2Value(), 4) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getActualSpeedTimerSetting() {
        // DB3.4 .. DB3.2
        int mask = 28;
        int value = (getDB_3Value() & mask) >>> 2;
        value *= 10;
        return new QuantityType<>(value, Units.MINUTE);
    }

    private State getActualSpeedSetting() {
        // DB3.7 .. DB3.5
        int mask = 224;
        int value = (getDB_3Value() & mask) >>> 5;

        if (value >= 6) {
            return UnDefType.UNDEF;
        }

        return new StringType(SPEED_STATUS.values()[value].toString());
    }
}
