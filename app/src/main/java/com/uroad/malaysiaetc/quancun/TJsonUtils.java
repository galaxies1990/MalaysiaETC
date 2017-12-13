package com.uroad.malaysiaetc.quancun;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.uroad.lib.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 空中JSON  数据解析， 另外一种解析格式
 * @author Administrator
 *
 */
/*{
    "code": "78",
    "msg": "卡余额格式不对"
}
{
    "amount": "10",
    "code": "00",
    "rechargeDownLimit": 1,
    "rechargeUpperLimit": 2999999
}*/
public class TJsonUtils {

	/**
	 * 判断数据状态是否ok
	 * 
	 * @param result
	 *            json数据
	 * @return
	 */
	public static boolean isOk(String result) {
		try {
			JSONObject json = new JSONObject(result);
			if (Code.CODE_00.equalsIgnoreCase(json.getString("code"))) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.e("TJsonUtil","isOk 解析异常" + e.getMessage());
		}
		return false;
	}
	
	
	/**
	 * 判断数据状态是否ok，如果状态为 error 直接toast 提示
	 * 
	 * @param context
	 * @param result
	 * @return
	 */
	public static boolean isOk(Context context, String result) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);
			if ("00".equalsIgnoreCase(json.getString("code"))) {
				return true;
			} else {
				Toast.makeText(context, json.getString("msg"),Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.e("TJsonUtil","isOk 解析异常" + e.getMessage());
			e.printStackTrace();
			Toast.makeText(context,
					"isOK 解析异常",Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	
	/**
	 * 获取code
	 * @param json
	 * @return
	 */
	public static String getCodeString(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.get("code").toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LogUtil.e("TJsonUtil","getError 解析异常" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取返回的错误信息
	 * 
	 * @param json
	 * @return
	 */
	public static String getErrorString(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.get("msg").toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			LogUtil.e("TJsonUtil","getError 解析异常" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 获取data 里面的 某个key 对应value值
	 * @param json
	 * @param key 
	 * @return
	 */
	public static String getDataInVlaue(String json, String key) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.getString(key);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.e("TJsonUtil","getData 解析异常" + e.getMessage());
		}
		return null;
	}
	
	
	/** to list */
	@SuppressWarnings("unchecked")
	public static <T> T parseArrayJSON(String jsonText, Class<T> clazz) {
		return (T) JSON.parseArray(jsonText, clazz);
	}

	/** to object */
	public static <T> T parseObjectJSON(String jsonText, Class<T> clazz) {
		return (T) JSON.parseObject(jsonText, clazz);
	}
}
