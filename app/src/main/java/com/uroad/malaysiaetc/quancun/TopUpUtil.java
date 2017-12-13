package com.uroad.malaysiaetc.quancun;


import com.uroad.lib.util.log.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;


public class TopUpUtil {

	/** 艾特斯设备表面号，第7位标识为1 */
	public static final char ATS_CHARACTERISTIC = '1';
	/** 金益设备表面号，第7位标识为4 */
	public static final char JY_CHARACTERISTIC = '4';
	/** 成谷设备表面号，第7位标识为3 */
	public static final char CG_CHARACTERISTIC = '3';
	/** 华工设备表面号，第7位标识为6 */
	public static final char HG_CHARACTERISTIC = '6';

	/** 1 OBU */
	public static final char OBU_CHARACTERISTIC = '1';
	/** 2 CZY */
	public static final char CZY_CHARACTERISTIC = '2';

	public static final int DEVICE_OBU_ATS = 11;
	public static final int DEVICE_OBU_JY = 14;

	public static final int DEVICE_CZY_ATS = 21;
	public static final int DEVICE_CZY_JY = 24;
	public static final int DEVICE_CZY_HG = 26;
 
	/** 交易状态 */
	public static class Deal {
		// 交易状态 0，未付款，1，已付款，2，已冲正，3，已作废
		/** 0，未付款 */
		public static final String NO_PAY = "0";
		/** 1，已付款,可能需要半条流水 */
		public static final String ALREADY_PAY = "1";
		/** 2，已冲正, 交易异常退款 */
		public static final String ALREADY_REFUND = "2";
		/** 3，已作废 */
		public static final String ALREADY_CANCEL = "3";
	}

	/** 写卡状态 */
	public static class WriteCard {
		// 写卡状态 0，待写卡，1，写卡成功，2，写卡失败，3，人工处理，4，已写卡
		/** 待写卡,需要处理半条流水 */
		public static final String NO_WRITE = "0";

		/** 写卡成功 */
		public static final String WRITE_SUCCESS = "1";

		/** 写卡失败,需要处理半条流水 */
		public static final String WRITE_FAIL = "2";

		/** 人工处理 */
		public static final String PERSON_HANDLE = "3";

		/** 已写卡,需要处理半条流水 */
		public static final String ALREADY_WRITE = "4";
	}

	/***
	 * 根据JSON 数据判断圈存是否半条流水
	 * 
	 * @param result
	 * @return
	 * @throws JSONException
	 */
	public static boolean isTopupUnfinished(String result) {
		// 获取圈存是否半条流水，当用户没有消费记录时，{"status":"OK","data":[]} 会出现异常
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jArray = jsonObject.getJSONArray("data");
			if (jArray.length() == 0) {
				LogUtil.i("TopUpUtil","isTopupUnfinished jArray length:" + jArray.length());
				return false;
			}
			JSONArray jsonArray = jArray.getJSONArray(0);
			// 交易状态 0，未付款，1，已付款，2，已冲正，3，已作废
			String dealState = jsonArray.get(8) + "";// 交易状态
			// 写卡状态0，待写卡，1，写卡成功，2，写卡失败，3，人工处理，4，已写卡
			String writeCardState = jsonArray.get(7) + "";// 写卡状态
			LogUtil.i("TopUpUtil","dealState:" + dealState);
			LogUtil.i("TopUpUtil","writeCardState:" + writeCardState);
			if (Deal.ALREADY_PAY.equals(dealState)) {// 已支付
				if (WriteCard.NO_WRITE.equals(writeCardState)
						|| WriteCard.WRITE_FAIL.equals(writeCardState)
						|| WriteCard.ALREADY_WRITE.equals(writeCardState)) {
					// 半条流水情况
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.i("TopUpUtil","数据解析错误" + e.getMessage());
		}
		return false;
	}

	/** 字符串转MAC 地址 */
	public static String Str2Mac(String str) {
		String mac = "";
		if (str != null && str.length() > 0) {
			for (int i = 0; i < str.length(); i++) {
				mac += str.charAt(i);
				if (i % 2 == 1 && i != str.length() - 1) {
					mac += ":";
				}
			}
		}
		return mac;
	}

	/**
	 * 将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytes2HexString(byte[] src) {
		if (src == null) {
			return null;
		}
		if (src.length == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexString2Bytes(String hexString) {
		if (hexString != null && hexString.length() > 0) {
			hexString = hexString.toLowerCase();
			byte[] byteArray = new byte[hexString.length() / 2];
			int k = 0;
			for (int i = 0; i < byteArray.length; i++) {
				byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
				byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
				byteArray[i] = (byte) (high << 4 | low);
				k += 2;
			}
			return byteArray;
		}
		return null;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String getCardBalance(byte[] data) {
		String temp = bytes2HexString(data);

		int length = Integer.parseInt(temp.substring(6, 8), 16)
				+ Integer.parseInt(temp.substring(8, 10), 16) * 256;
		String datastring1 = byte2Hex(data, 5, length);
		datastring1 = datastring1.substring(8, 16);
		String balance = Integer.parseInt(datastring1, 16) + "";// 余额(分)

		return divideHundred(balance);
	}

	public static String byte2Hex(byte[] bin, int index, int len) {// bytes-->hexString:
		// B20101410100070000
		int b;
		byte[] hex_str = new byte[len * 2];
		int index_str = 0;
		byte[] bin2hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		String str = null;

		if (len <= 0)
			return null;

		for (int i = 0; i < len; i++) {
			if (bin[index] >= 0)
				b = bin[index];// *bin ++;
			else
				b = 256 + bin[index];

			index++;

			hex_str[index_str++] = bin2hex[b >> 4];
			hex_str[index_str++] = bin2hex[b & 0xf];

		}

		try {
			str = new String(hex_str, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LogUtil.i("TopUpUtil","binToHex:" + str);

		return str;

	}

	/**
	 * 根据蓝牙发送过来的数据,获取指令集响应
	 * 
	 * @param data
	 * @return
	 */
	public final static String getInstructionResp(byte[] data) {
		String temp = TopUpUtil.bytes2HexString(data);
		int length = Integer.parseInt(temp.substring(6, 8), 16)
				+ Integer.parseInt(temp.substring(8, 10), 16) * 256;
		String instructionResps = TopUpUtil.byte2Hex(data, 5, length);
		return instructionResps;
	}

	/**
	 * 获取文件数据
	 * 
	 * @param data
	 * @return
	 */
	public final static String getFileData(byte[] data) {
		String rData = getInstructionResp(data);
		LogUtil.i("TopUpUtil","getFileData rData:" + rData);
		String fileData = rData.substring(8, rData.length() - 4);
		LogUtil.i("TopUpUtil","getFileData fileData:" + fileData);
		return fileData;
	}

	/**
	 * 除以100
	 * 
	 * @param str
	 * @return
	 */
	public static String divideHundred(String str) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(Double.parseDouble(str) / 100.00);
	}

	/** 乘以100 */
	public static String multiplyHundred(String str) {
		String value = (long) (Double.parseDouble(str) * 100) + "";
		return value;
	}

	/***
	 * 获取卡区域、卡号
	 * 
	 * @param data
	 * @return
	 */
	public static TopUpCardInfo getCardNumber(byte[] data) {
		// TODO Auto-generated method stub
		String temp = bytes2HexString(data);
		int length = Integer.parseInt(temp.substring(6, 8), 16)
				+ Integer.parseInt(temp.substring(8, 10), 16) * 256;
		String content = byte2Hex(data, 5, length);
		String cardType = content.substring(24, 26);
		String cardVersion = content.substring(26, 28);

		String cardArea = content.substring(28, 32);
		String cardNum = content.substring(32, 48); // 卡号
		// String validity = content.substring(48, 56);
		String validity = content.substring(56, 64);

		String plateNum = "";
		try {
			plateNum = new String(hexString2Bytes(content.substring(64, 88)),
					"GBK");
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.i("TopUpUtil","TopUpCardInfo getCardNumber 方法获取车牌号错误" + e.getMessage());
			e.printStackTrace();
		}

		String plateColor = content.substring(90, 92);

		TopUpCardInfo cardInfo = new TopUpCardInfo();
		cardInfo.setCardArea(cardArea);
		cardInfo.setCardNumber(cardNum);
		cardInfo.setValidity(validity);
		cardInfo.setPlateNum(plateNum);
		cardInfo.setPlateColor(plateColor);
		cardInfo.setCardVersion(cardVersion);
		cardInfo.setCardType(cardType);

		return cardInfo;
	}

	/***
	 * 判断是否是成谷obu 必须是已经绑定了设备
	 * 
	 * @param activity
	 * @return
	 */
	// public static final boolean isCgObu(Activity activity){
	// MyApplication application = (MyApplication) activity.getApplication();
	// String deviceNo = application.getUserCard().getSedeviceList().get(0)
	// .get(1);
	// return isCgObu(deviceNo);
	// }

	/**
	 * 判断是否是成谷obu 必须是已经绑定了设备
	 * 
	 * @param deviceNo
	 * @return
	 */
	// public static final boolean isCgObu(String deviceNo){
	// char c = deviceNo.charAt(6);//第七位为3
	// return c == CG_CHARACTERISTIC;
	// }

	/**
	 * 获取图片类型id，无此设备则返回0
	 * 
	 * @param activity
	 * @return
	 */
	// public static final int getDeviceTypeImgRes(Activity activity){
	// int idRes = 0;
	// int type = getDeviceType(activity);
	// switch (type) {
	// case DEVICE_CZY_ATS:
	// idRes = R.drawable.icon_device_czy_ats;
	// break;
	// case DEVICE_CZY_JY:
	// idRes = R.drawable.icon_device_czy_jy;
	// break;
	//
	// case DEVICE_CZY_HG:
	// idRes = R.drawable.icon_device_czy_hg;
	// break;
	//
	// case DEVICE_OBU_ATS:
	// idRes = R.drawable.icon_device_obu_ats;
	// break;
	//
	// case DEVICE_OBU_JY:
	// idRes = R.drawable.icon_device_obu_jy;
	// break;
	//
	// default:
	// break;
	// }
	// return idRes;
	// }
	//
	// public static final int getReadCardTipImgRes(Activity activity){
	// int idRes = 0;
	// int type = getDeviceType(activity);
	// switch (type) {
	// case DEVICE_CZY_ATS:
	// idRes = R.drawable.img_readcard_czy_ats;
	// break;
	// case DEVICE_CZY_JY:
	// idRes = R.drawable.img_readcard_czy_jy;
	// break;
	//
	// case DEVICE_CZY_HG:
	// idRes = R.drawable.img_readcard_czy_hg;
	// break;
	//
	// case DEVICE_OBU_ATS:
	// idRes = R.drawable.img_readcard_obu_ats;
	// break;
	//
	// case DEVICE_OBU_JY:
	// idRes = R.drawable.img_readcard_obu_jy;
	// break;
	//
	// default:
	// break;
	// }
	// return idRes;
	// }
	//
	// /**获取设备型号*/
	// public static final int getDeviceType(Activity activity){
	// int dChar = 0;
	// MyApplication application = (MyApplication) activity.getApplication();
	// String deviceNo = application.getUserCard().getSedeviceList().get(0)
	// .get(1);
	// char type = deviceNo.charAt(3);//第4位
	// char device = deviceNo.charAt(6);//第7位
	// if(type == OBU_CHARACTERISTIC){
	// if(device == ATS_CHARACTERISTIC){
	// dChar = DEVICE_OBU_ATS;
	// }else if(device == JY_CHARACTERISTIC){
	// dChar = DEVICE_OBU_JY;
	// }
	// }else if(type == CZY_CHARACTERISTIC){
	// if(device == ATS_CHARACTERISTIC){
	// dChar = DEVICE_CZY_ATS;
	// }else if(device == JY_CHARACTERISTIC){
	// dChar = DEVICE_CZY_JY;
	// }else if(device == HG_CHARACTERISTIC){
	// dChar = DEVICE_CZY_HG;
	// }
	// }
	// return dChar;
	// }

	/**
	 * 圈存方式，2充值易 3蓝牙obu
	 * 
	 * @param activity
	 */
	// public static final String get1005LoadType(Activity activity){
	// String loadtype = "";
	// MyApplication application = (MyApplication) activity.getApplication();
	// String deviceNo = application.getUserCard().getSedeviceList().get(0)
	// .get(1);
	// char type = deviceNo.charAt(3);//第4位
	// if(type == OBU_CHARACTERISTIC){
	// loadtype = "3";
	// }else if(type == CZY_CHARACTERISTIC){
	// loadtype = "2";
	// }
	// return loadtype;
}

/***
 * 判断是否是艾特斯obu 必须是已经绑定了设备
 * 
 * @param activity
 * @return
 */
// public static final boolean isAtsObu(Activity activity){
// MyApplication application = (MyApplication) activity.getApplication();
// String deviceNo = application.getUserCard().getSedeviceList().get(0)
// .get(1);
// return isAtsObu(deviceNo);
// }
//
// /**
// * 判断是否是艾特斯obu 必须是已经绑定了设备
// * @param deviceNo
// * @return
// */
// public static final boolean isAtsObu(String deviceNo){
// char c = deviceNo.charAt(6);//第七位为3
// return c == ATS_CHARACTERISTIC;
// }
// }
