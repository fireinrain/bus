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
package org.aoju.bus.notify.provider.aliyun;

import org.aoju.bus.core.lang.Fields;
import org.aoju.bus.core.lang.Http;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.notify.Context;
import org.aoju.bus.notify.magic.Message;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云短信
 *
 * @author Justubborn
 * @version 6.1.1
 * @since JDK1.8+
 */
public class AliyunSmsProvider extends AliyunProvider<AliyunSmsProperty, Context> {

    /**
     * 阿里云短信产品域名
     */
    private static final String ALIYUN_SMS_API = "dysmsapi.aliyuncs.com";

    public AliyunSmsProvider(Context properties) {
        super(properties);
    }

    @Override
    public Message send(AliyunSmsProperty entity) {
        SimpleDateFormat df = new SimpleDateFormat(Fields.UTC_PATTERN);
        // 这里一定要设置GMT时区
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Map<String, String> params = new HashMap<>();
        // 1. 系统参数
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("AccessKeyId", properties.getAppKey());
        params.put("SignatureVersion", "1.0");
        params.put("Timestamp", df.format(new Date()));
        params.put("Format", "JSON");
        // 2. 业务API参数
        params.put("Action", "SendSms");
        params.put("Version", "2017-05-25");
        params.put("RegionId", "cn-hangzhou");
        params.put("PhoneNumbers", entity.getReceive());
        params.put("SignName", properties.getSignName());
        params.put("TemplateParam", entity.getTemplateParam());
        params.put("TemplateCode", entity.getTempCode());

        params.put("Signature", getSign(params));

        Map<String, Object> map = new HashMap<>();
        for (String str : params.keySet()) {
            map.put(specialUrlEncode(str), specialUrlEncode(params.get(str)));
        }
        return checkResponse(Httpx.get(Http.HTTPS_PREFIX + ALIYUN_SMS_API, map));
    }

}
