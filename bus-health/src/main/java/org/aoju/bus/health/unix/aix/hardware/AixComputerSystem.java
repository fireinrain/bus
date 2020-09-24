/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org OSHI and other contributors.                 *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.health.unix.aix.hardware;

import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractComputerSystem;
import org.aoju.bus.health.builtin.hardware.Baseboard;
import org.aoju.bus.health.builtin.hardware.Firmware;

import java.util.List;
import java.util.function.Supplier;

/**
 * Hardware data obtained from lsattr
 *
 * @author Kimi Liu
 * @version 6.1.0
 * @since JDK 1.8+
 */
@Immutable
final class AixComputerSystem extends AbstractComputerSystem {

    private final Supplier<LsattrStrings> lsattrStrings = Memoize.memoize(AixComputerSystem::readLsattr);
    private final Supplier<List<String>> lscfg;

    AixComputerSystem(Supplier<List<String>> lscfg) {
        this.lscfg = lscfg;
    }

    private static LsattrStrings readLsattr() {
        String fwVendor = "IBM";
        String fwVersion = null;
        String fwPlatformVersion = null;

        String manufacturer = fwVendor;
        String model = null;
        String serialNumber = null;

        /*-
        fwversion       IBM,RG080425_d79e22_r                Firmware version and revision levels                False
        modelname       IBM,9114-275                         Machine name                                        False
        os_uuid         789f930f-b15c-4639-b842-b42603862704 N/A                                                 True
        rtasversion     1                                    Open Firmware RTAS version                          False
        systemid        IBM,0110ACFDE                        Hardware system identifier                          False
        */

        final String fwVersionMarker = "fwversion";
        final String modelMarker = "modelname";
        final String systemIdMarker = "systemid";
        final String fwPlatformVersionMarker = "Platform Firmware level is";

        for (final String checkLine : Executor.runNative("lsattr -El sys0")) {
            if (checkLine.startsWith(fwVersionMarker)) {
                fwVersion = checkLine.split(fwVersionMarker)[1].trim();
                int comma = fwVersion.indexOf(Symbol.C_COMMA);
                if (comma > 0 && fwVersion.length() > comma) {
                    fwVendor = fwVersion.substring(0, comma);
                    fwVersion = fwVersion.substring(comma + 1);
                }
                fwVersion = RegEx.SPACES.split(fwVersion)[0];
            } else if (checkLine.startsWith(modelMarker)) {
                model = checkLine.split(modelMarker)[1].trim();
                int comma = model.indexOf(Symbol.C_COMMA);
                if (comma > 0 && model.length() > comma) {
                    manufacturer = model.substring(0, comma);
                    model = model.substring(comma + 1);
                }
                model = RegEx.SPACES.split(model)[0];
            } else if (checkLine.startsWith(systemIdMarker)) {
                serialNumber = checkLine.split(systemIdMarker)[1].trim();
                serialNumber = RegEx.SPACES.split(serialNumber)[0];
            }
        }
        for (final String checkLine : Executor.runNative("lsmcode -c")) {
            /*-
             Platform Firmware level is 3F080425
             System Firmware level is RG080425_d79e22_regatta
             */
            if (checkLine.startsWith(fwPlatformVersionMarker)) {
                fwPlatformVersion = checkLine.split(fwPlatformVersionMarker)[1].trim();
                break;
            }
        }
        return new LsattrStrings(fwVendor, fwPlatformVersion, fwVersion, manufacturer, model, serialNumber);
    }

    @Override
    public String getManufacturer() {
        return lsattrStrings.get().manufacturer;
    }

    @Override
    public String getModel() {
        return lsattrStrings.get().model;
    }

    @Override
    public String getSerialNumber() {
        return lsattrStrings.get().serialNumber;
    }

    @Override
    public Firmware createFirmware() {
        return new AixFirmware(lsattrStrings.get().biosVendor, lsattrStrings.get().biosPlatformVersion,
                lsattrStrings.get().biosVersion);
    }

    @Override
    public Baseboard createBaseboard() {
        return new AixBaseboard(lscfg);
    }

    private static final class LsattrStrings {

        private final String biosVendor;
        private final String biosPlatformVersion;
        private final String biosVersion;

        private final String manufacturer;
        private final String model;
        private final String serialNumber;

        private LsattrStrings(String biosVendor, String biosPlatformVersion, String biosVersion, String manufacturer,
                              String model, String serialNumber) {
            this.biosVendor = StringKit.isBlank(biosVendor) ? Normal.UNKNOWN : biosVendor;
            this.biosPlatformVersion = StringKit.isBlank(biosPlatformVersion) ? Normal.UNKNOWN : biosPlatformVersion;
            this.biosVersion = StringKit.isBlank(biosVersion) ? Normal.UNKNOWN : biosVersion;

            this.manufacturer = StringKit.isBlank(manufacturer) ? Normal.UNKNOWN : manufacturer;
            this.model = StringKit.isBlank(model) ? Normal.UNKNOWN : model;
            this.serialNumber = StringKit.isBlank(serialNumber) ? Normal.UNKNOWN : serialNumber;
        }

    }

}
