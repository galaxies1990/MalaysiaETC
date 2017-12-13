package com.uroad.malaysiaetc.webservice;

import java.util.HashMap;

/**
 * Created by liwen on 2017/12/12.
 */

public class UserWS extends BaseWS {

    /**
     * 登陆
     **/
    public static String login = "/user/login";

    public static HashMap loginParams(String username,String password,String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("username", username);
        baseParams.put("password", password);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

    /**
     * 注册
     **/
    public static String register = "/user/register";

    public static HashMap registerParams(String phone, String password,
                                         String smscode, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("phone", phone);
        baseParams.put("password", password);
        baseParams.put("smscode", smscode);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

    /**
     * 修改昵称
     **/
    public static String updateNickname   = "/user/updateNickname";

    public static HashMap updateNicknameParams(String useruuid, String nickname,
                                               String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("nickname", nickname);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

    /**
     * 修改密码
     **/
    public static String changePassword = "/user/changePassword";

    public static HashMap changePasswordParams(String username, String oldpassword,
                                               String newpassword, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("username", username);
        baseParams.put("oldpassword", oldpassword);
        baseParams.put("newpassword", newpassword);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

    /**
     * 修改头像
     **/
    public static String updateAvatar   = "/user/updateAvatar";

    public static HashMap updateAvatarParams(String useruuid, String avatarbase64,String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("useruuid", useruuid);
        baseParams.put("avatarbase64", avatarbase64);
        baseParams.put("langversion", langversion);
        return baseParams;
    }

    /**
     * 获取我的设备与卡片
     **/
    public static String fetchMyDevices   = "/user/fetchMyDevices";

    public static HashMap fetchMyDevicesParams(String phone, String langversion) {
        HashMap baseParams = getBaseParams();
        baseParams.put("phone", phone);
        baseParams.put("langversion", langversion);
        return baseParams;
    }
}
