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
package org.aoju.bus.crypto.provider;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.crypto.Builder;
import org.aoju.bus.crypto.Provider;
import org.aoju.bus.crypto.symmetric.DES;

/**
 * 数据加密标准,速度较快,适用于加密大量数据的场合
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class DESProvider implements Provider {

    /**
     * 加密
     *
     * @param key     密钥
     * @param content 需要加密的内容
     */
    @Override
    public byte[] encrypt(String key, byte[] content) {
        if (StringKit.isEmpty(key)) {
            throw new InstrumentException("key is null!");
        }
        DES des = Builder.des(key.getBytes());
        return des.encrypt(content);
    }


    /**
     * 解密
     *
     * @param key     密钥
     * @param content 需要解密的内容
     */
    @Override
    public byte[] decrypt(String key, byte[] content) {
        if (StringKit.isEmpty(key)) {
            throw new InstrumentException("key is null!");
        }
        DES des = Builder.des(key.getBytes());
        return des.decrypt(content);
    }

}
