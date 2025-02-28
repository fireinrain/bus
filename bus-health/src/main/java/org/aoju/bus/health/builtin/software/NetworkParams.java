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
package org.aoju.bus.health.builtin.software;

import org.aoju.bus.core.annotation.ThreadSafe;

/**
 * NetworkParams presents network parameters of running OS, such as DNS, host
 * name etc.
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
public interface NetworkParams {

    /**
     * <p>
     * getHostName.
     * </p>
     *
     * @return Gets host name
     */
    String getHostName();

    /**
     * <p>
     * getDomainName.
     * </p>
     *
     * @return Gets domain name
     */
    String getDomainName();

    /**
     * <p>
     * getDnsServers.
     * </p>
     *
     * @return Gets DNS servers
     */
    String[] getDnsServers();

    /**
     * <p>
     * getIpv4DefaultGateway.
     * </p>
     *
     * @return Gets default gateway(routing destination for 0.0.0.0/0) for IPv4,
     * empty string if not defined.
     */
    String getIpv4DefaultGateway();

    /**
     * <p>
     * getIpv6DefaultGateway.
     * </p>
     *
     * @return Gets default gateway(routing destination for ::/0) for IPv6, empty
     * string if not defined.
     */
    String getIpv6DefaultGateway();

}
