package com.uroad.malaysiaetc.webservice;

import java.util.HashMap;

/**
 * Created by liwen on 2017/12/12.
 */

public class DeviceWS extends BaseWS {
    /**
     * 根据设备表面号获取设备信息
     **/
    public static String fetchDeviceInfoByDeviceNo = "/device/fetchDeviceInfoByDeviceNo";

    public static HashMap fetchDeviceInfoByDeviceNoParams(String useruuid, String deviceno, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("deviceno", deviceno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
    /**
     *  绑定设备
     **/
    public static String bind = "/device/bind";

    public static HashMap bindParams(String useruuid, String deviceno, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("deviceno", deviceno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
    /**
     * 解绑设备
     **/
    public static String unbind = "/device/unbind";

    public static HashMap unbindParams(String useruuid, String deviceno, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("deviceno", deviceno);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
    /**
     * 修改设备昵称
     **/
    public static String updateNickname = "/device/updateNickname";

    public static HashMap updateNicknameParams(String useruuid, String binddeviceid, String devicenickname,String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("binddeviceid", binddeviceid);
        baseParams.put("devicenickname", devicenickname);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

}
