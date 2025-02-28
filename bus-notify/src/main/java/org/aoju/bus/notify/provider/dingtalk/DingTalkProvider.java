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
package org.aoju.bus.notify.provider.dingtalk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.notify.Builder;
import org.aoju.bus.notify.Context;
import org.aoju.bus.notify.magic.Message;
import org.aoju.bus.notify.provider.AbstractProvider;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 钉钉通知
 *
 * @author Justubborn
 * @version 6.1.1
 * @since JDK1.8+
 */
@Setter
public class DingTalkProvider extends AbstractProvider<DingTalkProperty, Context> {

    private static final String SUCCESS_RESULT = "200";
    private static final String DINGTALK_TOKEN_API = "https://oapi.dingtalk.com/gettoken";
    private static final String DingTalk_NOTIFY_API = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    private AtomicReference<String> accessToken = new AtomicReference<>();
    private long refreshTokenTime;
    private long tokenTimeOut = Duration.ofSeconds(7000).toMillis();

    public DingTalkProvider(Context properties) {
        super(properties);
    }

    @Override
    public Message send(DingTalkProperty entity) {
        Map<String, Object> param = new HashMap<>();
        param.put("access_token", entity.getToken());
        param.put("agent_id", entity.getAgentId());
        param.put("msg", entity.getMsg());
        if (StringKit.isNotBlank(entity.getUserIdList())) {
            param.put("userid_list", entity.getUserIdList());
        }
        if (StringKit.isNotBlank(entity.getDeptIdList())) {
            param.put("dept_id_list", entity.getDeptIdList());
        }
        param.put("to_all_user", entity.isToAllUser());
        String response = Httpx.post(DingTalk_NOTIFY_API, param);
        JSONObject object = JSON.parseObject(response);
        return Message.builder()
                .errcode(SUCCESS_RESULT.equals(object.getString("errcode")) ? Builder.ErrorCode.SUCCESS.getCode() : object.getString("errcode"))
                .errmsg(object.getString("errmsg"))
                .build();
    }

    private String getToken() {
        if (System.currentTimeMillis() - refreshTokenTime > tokenTimeOut || accessToken.get() == null) {
            return requestToken();
        }
        return accessToken.get();
    }

    /**
     * 获取钉钉token
     *
     * @return 结果
     */
    private String requestToken() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("corpid", properties.getAppKey());
        paramMap.put("corpsecret", properties.getAppSecret());
        String response = Httpx.get(DINGTALK_TOKEN_API, paramMap);
        JSONObject object = JSON.parseObject(response);
        if (SUCCESS_RESULT.equals(object.getString("errcode"))) {
            String token = object.getString("access_token");
            refreshTokenTime = System.currentTimeMillis();
            accessToken.set(token);
            return token;
        }

        Logger.error("获取钉钉token失败：{}", object.getString("errmsg"));
        return null;
    }

}
