package com.uroad.malaysiaetc.webservice;

import java.util.HashMap;

/**
 * Created by liwen on 2017/12/12.
 */

public class CardWS extends BaseWS {
    /**
     * 根据卡号查询卡片信息
     **/
    public static String fetchCardInfoByCardNo = "/card/fetchCardInfoByCardNo";

    public static HashMap fetchCardInfoByCardNoParams(String useruuid, String cardno, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("cardno", cardno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
    /**
     * 根据卡号查询卡片信息
     **/
    public static String bind = "/card/bind";

    public static HashMap bindParams(String useruuid, String deviceno, String cardno,String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("deviceno", deviceno);
        baseParams.put("cardno", cardno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
    /**
     * 根据卡号查询卡片信息
     **/
    public static String unbind = "/card/unbind";

    public static HashMap unbindParams(String useruuid, String deviceno, String cardno,String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("deviceno", deviceno);
        baseParams.put("cardno", cardno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

}
