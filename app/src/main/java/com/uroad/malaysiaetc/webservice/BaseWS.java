package com.uroad.malaysiaetc.webservice;

import java.util.HashMap;

public class BaseWS {
    public static String Base_Url="http://malaysia.u-road.com/MYLoadAPIServer/index.php?/";
    public static String Base_Url_Debug="http://malaysia.u-road.com/MYLoadAPIServer/index.php?/";

    public static HashMap<String, Object> getBaseParams() {
        HashMap<String, Object> params = new HashMap();
        return params;
    }
}
