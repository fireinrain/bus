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
package org.aoju.bus.extra.captcha.strategy;

import org.aoju.bus.core.toolkit.RandomKit;
import org.aoju.bus.core.toolkit.StringKit;

/**
 * 随机字符验证码生成器
 * 可以通过传入的基础集合和长度随机生成验证码字符
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class RandomStrategy extends AbstractStrategy {

    /**
     * 构造，使用字母+数字做为基础
     *
     * @param count 生成验证码长度
     */
    public RandomStrategy(int count) {
        super(count);
    }

    /**
     * 构造
     *
     * @param baseStr 基础字符集合，用于随机获取字符串的字符集合
     * @param length  生成验证码长度
     */
    public RandomStrategy(String baseStr, int length) {
        super(baseStr, length);
    }

    @Override
    public String generate() {
        return RandomKit.randomString(this.baseStr, this.length);
    }

    @Override
    public boolean verify(String code, String userInputCode) {
        if (StringKit.isNotBlank(userInputCode)) {
            return StringKit.equalsIgnoreCase(code, userInputCode);
        }
        return false;
    }

}
