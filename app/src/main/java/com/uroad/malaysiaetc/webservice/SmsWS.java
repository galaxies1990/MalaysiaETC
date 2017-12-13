package com.uroad.malaysiaetc.webservice;

import java.util.HashMap;

/**
 * Created by liwen on 2017/12/12.
 */

public class SmsWS extends BaseWS{
    /**
     * 发送短信验证码
     **/
    public static String sendCode = "/sms/sendCode";

    public static HashMap sendCodeParams(String cmd, String phone, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("cmd", cmd);
        baseParams.put("phone", phone);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
}
