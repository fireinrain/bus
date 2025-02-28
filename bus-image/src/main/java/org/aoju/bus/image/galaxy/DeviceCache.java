/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
package org.aoju.bus.image.galaxy;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.image.Device;

/**
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class DeviceCache extends ConfigurationCache<Configuration, Device>
        implements IDeviceCache {

    public DeviceCache(Configuration conf) {
        super(conf);
    }

    @Override
    protected Device find(Configuration conf, String key) throws InstrumentException {
        return conf.findDevice(key);
    }

    @Override
    public Device findDevice(String deviceName) throws InstrumentException {
        Device device = get(deviceName);
        if (device == null)
            throw new InstrumentException("Unknown Device: " + deviceName);
        if (!device.isInstalled())
            throw new InstrumentException("Device: " + deviceName + " not installed");
        return device;
    }

}
