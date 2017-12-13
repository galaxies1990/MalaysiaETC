package com.uroad.malaysiaetc.quancun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.uroad.lib.util.log.LogUtil;
import com.uroad.malaysiaetc.util.BlueToothUtil;

import java.util.ArrayList;
import java.util.List;

public class CmdHelper implements Cmd{

	/** 最大包长 */
	private static int MAX_PACKAGE_LEN = 1200;

	/** 最大帧长度 */
	private static int MAX_FRAME_LEN = 92;

	private static int mPackageSn = 0;

	public static byte[] frameData;

	static Context mContext;

	private static int currCmd = 0;
	
	public static byte[] recvData;

	/** 初始化帧长度 */
	public static void initFrameLen(int len, Context context) {
		MAX_FRAME_LEN = len;
		mContext = context;
		LogUtil.i("CmdHelper","MAX_FRAME_LEN:" + MAX_FRAME_LEN);
	}

	/** APP握手 A2 APP和读卡器建立握手 */
	public static final List<byte[]> getCmdA2() {// 33018001a222
		frameData = null;
		currCmd = CMD_A2;
		final byte[] data = new byte[MAX_PACKAGE_LEN];
		data[0] = (byte) CMD_A2;
		return buildFrame(data, 1);
	}
	
	/**获取卡余额*/
	public static final List<byte[]> getCardBalanceFrame(){
		frameData = null;
		currCmd = CMD_CARD_BALANCE;
		final byte[] cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_CARD_BALANCE);
		return doIccCommand(cmd); 
	}

	/**获取卡卡号*/
	public static final List<byte[]> getCardNumber(){
		frameData = null;
		currCmd = CMD_CARD_NUMBER;
		final byte[] cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_CARD_NUMBER);
		return doIccCommand(cmd); 
	}
	/**获取PIN为三字节的TLV指令*/
	public static final List<byte[]> getPin3(){
		frameData = null;
		currCmd = CMD_CARD_PIN3;
		final byte[] cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_CARD_PIN3);
		return doIccCommand(cmd); 
	}
	/**获取PIN为六字节的TLV指令*/
	public static final List<byte[]> getPin6(){
		frameData = null;
		currCmd = CMD_CARD_PIN6;
		final byte[] cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_CARD_PIN6);
		return doIccCommand(cmd); 
	}
	
	/**获取文件指令*/
	public static final List<byte[]> getFileCmd(int cmdFile){
		frameData = null;
		currCmd = cmdFile;
		byte[] cmd = null;
		switch (cmdFile) {
		case CMD_FILE_0019_1:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0019_1);
			break;

		case CMD_FILE_0019_2:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0019_2);
			break;
			
		case CMD_FILE_0008_1:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0008_1);
			break;
			
		case CMD_FILE_0008_2:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0008_2);
			break;
			
		case CMD_FILE_0009_1:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_1);
			break;
			
		case CMD_FILE_0009_2:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_2);
			break;
			
		case CMD_FILE_0009_3:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_3);
			break;
			
		case CMD_FILE_0009_4:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_4);
			break;
			
		case CMD_FILE_0009_5:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_5);
			break;
			
		case CMD_FILE_0009_6:
			cmd = TopUpUtil.hexString2Bytes(DIRECT_CMD_FILE_0009_6);
			break;
			
			
		default:
			break;
		}
		return doIccCommand(cmd); 
	}
	
	/**获取读卡器的电池电量*/
	public static List<byte[]> GetCmdC2() {
		frameData = null;
		currCmd = CMD_C2;
		byte[] data = new byte[2];
		data[0] = (byte) CMD_01;// 明文
		data[1] = (byte) CMD_C2;
		return doObuCommand(data);
	}
	
	/**强制读卡器断电*/
	public static List<byte[]> GetCmdC3() {
		frameData = null;
		currCmd = CMD_C3;
		byte[] data = new byte[2];
		data[0] = (byte) 0x1;// 明文
		data[1] = (byte) CMD_C3;// 断电指令
		return doObuCommand(data);
	}
	
	/**获取认证1*/
	public static List<byte[]> getVerify1C0(){
		frameData = null;
		currCmd = CMD_C0;
		byte[] apdu = new byte[1];
		apdu[0] = (byte) CMD_C0;
		return doAuthenticate(1, apdu);
	}
	
	/**获取国密认证*/
	public static List<byte[]> getVerify1C4(){
		frameData = null;
		currCmd = CMD_C4;
		byte[] apdu = new byte[1];
		apdu[0] = (byte) CMD_C4;
		return doAuthenticate(1, apdu);
	}

	/**获取认证2*/
	public static List<byte[]> getVerify2(String serverCertificate, String random2){
		frameData = null;
		currCmd = CMD_C1;
		byte[] certi = TopUpUtil.hexString2Bytes(serverCertificate);
		byte[] ran = TopUpUtil.hexString2Bytes(random2);
		byte[] apdu = new byte[1 + certi.length + ran.length];
		apdu[0] = (byte) CMD_C1;
		System.arraycopy(certi, 0, apdu, 1, certi.length);
		System.arraycopy(ran, 0, apdu, 1 + certi.length, ran.length);
		return doAuthenticate(1 + certi.length + ran.length, apdu);
	}

	/**获取认证5*/
	public static List<byte[]> getVerify5(String workkey, String workkeyMac, String mackey, String mackeyMac, String random2, String signdata){
		frameData = null;
		currCmd = CMD_C5;
		byte[] allData = TopUpUtil.hexString2Bytes(workkey + workkeyMac + mackey + mackeyMac +random2 + signdata);
		byte[] apdu = new byte[1 + allData.length];
		apdu[0] = (byte) CMD_C5;
		System.arraycopy(allData, 0, apdu, 1, allData.length);
		return doAuthenticate(1 + allData.length, apdu);
	}
	
	/**获取认证3*/
	public static List<byte[]> getVerify3(String serverHMAC){
		frameData = null;
		currCmd = CMD_VERITY_C2;
		byte[] f1 = TopUpUtil.hexString2Bytes(serverHMAC);
		byte[] apdu = new byte[1 + f1.length];
		apdu[0] = (byte) CMD_C2;
		System.arraycopy(f1, 0, apdu, 1, f1.length);
		return doAuthenticate(1 + f1.length, apdu);
	}
	
	/**圈存初始化*/
	public static List<byte[]> doDepositInit(String instructions){
		frameData = null;
		currCmd = CMD_DEPOSIT_INIT;
		byte[] f1 = TopUpUtil.hexString2Bytes(instructions);
		return doIccCommandEnc(f1); 
	}
	
	/**圈存写卡*/
	public static List<byte[]> doDepositWrite(String instructions){
		frameData = null;
		currCmd = CMD_DEPOSIT_WRITE;
		byte[] f1 = TopUpUtil.hexString2Bytes(instructions);
		return doIccCommandEnc(f1); 
	}
	
	/**圈存写卡2*/
	public static List<byte[]> doDepositWrite2(String instructions){
		frameData = null;
		currCmd = CMD_DEPOSIT_WRITE2;
		byte[] f1 = TopUpUtil.hexString2Bytes(instructions);
		return doIccCommandEnc(f1); 
	}
	
	/**半条流水*/
	public static List<byte[]> doDepositHalf(String instructions){
		frameData = null;
		currCmd = CMD_DEPOSIT_HALF_1;
		byte[] f1 = TopUpUtil.hexString2Bytes(instructions);
		return doIccCommandEnc(f1); 
	}
	
	// 密文
	public static List<byte[]> doIccCommandEnc(byte[] tlv) // apdu: data1_len, data1,
	{
		byte[] data = new byte[MAX_PACKAGE_LEN];
		int len;
		data[0] = (byte) 0xA3;
		data[1] = (byte) 0x01;
		data[2] = (byte) (tlv.length & 0xFF);
		data[3] = (byte) ((tlv.length >> 8) & 0xFF);
		System.arraycopy(tlv, 0, data, 4, tlv.length);
		len = 4 + tlv.length;
		return buildFrame(data, len);
	}
	
	// 明文
	public static List<byte[]> doIccCommand(byte[] src) {
		byte[] data = new byte[MAX_PACKAGE_LEN];
		data[0] = (byte) 0xA3;
		data[1] = (byte) 0x0;
		data[2] = (byte) (src.length & 0xFF);
		data[3] = (byte) ((src.length >> 8) & 0xFF); //和文档有出入
		System.arraycopy(src, 0, data, 4, src.length);// source, startindex,
		int len = 4 + src.length;
		return buildFrame(data, len);
	}
	
	public static List<byte[]> doAuthenticate(int len, byte[] apdu) {
		LogUtil.e("CmdHelper","doAuthenticate apdu:"+TopUpUtil.bytes2HexString(apdu));
		byte[] data = new byte[MAX_PACKAGE_LEN];
		data[0] = (byte) 0xA6;
		data[1] = (byte) (len & 0xFF);
		data[2] = (byte) ((len >> 8) & 0xFF);
		System.arraycopy(apdu, 0, data, 3, len);// source, startindex, dest,
		len += 3;
		return buildFrame(data, len);
	}
	
	
	/**
	 * 打包数据
	 * 
	 * @param data
	 *            数据指令
	 * @param len
	 *            数据长度
	 * @return
	 */
	public static List<byte[]> buildFrame(byte[] data, int len) {
		LogUtil.e("CmdHelper","buildFrame data:"+TopUpUtil.bytes2HexString(data));
		LogUtil.e("CmdHelper","buildFrame len:"+len);
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] frameBuf = null;
		int pgklen = 0, pgknum = 0, pgkindex = 1;
		int dataPointer = 0;

		if (len == 0 || len > MAX_PACKAGE_LEN)
			return null;
		
		pgknum = len / MAX_FRAME_LEN;
		if ((len % MAX_FRAME_LEN) != 0)
			pgknum++;

		mPackageSn++;
		if (mPackageSn > 0xF)
			mPackageSn = 0;

		do { // 开始分帧组数据
			if (pgkindex < pgknum) {
				pgklen = MAX_FRAME_LEN;
			} else {
				pgklen = len % MAX_FRAME_LEN;

				if (pgklen == 0)//写法未知,可能有错
					pgklen = MAX_FRAME_LEN;

			}

			frameBuf = new byte[pgklen + 5]; // 初始化帧长度

			frameBuf[0] = (byte) 0x33; // st
			frameBuf[1] = (byte) mPackageSn; // sn
			if (pgkindex == 1) {
				frameBuf[2] = (byte) (((pgknum - pgkindex) & 0x7F) | 0x80); // ctl
			} else {
				frameBuf[2] = (byte) ((pgknum - pgkindex) & 0x7F); // ctl
			}
			frameBuf[3] = (byte) (pgklen & 0xFF); // len
			System.arraycopy(data, dataPointer, frameBuf, 4, pgklen);
			dataPointer += pgklen;
			byte[] snToData = new byte[frameBuf.length - 1];
			System.arraycopy(frameBuf, 1, snToData, 0, snToData.length);
			frameBuf[pgklen + 4] = GetBcc(snToData, pgklen + 3); // bcc
			list.add(frameBuf); // send包创建,一个frameBuf为一帧数据
			pgkindex++;

		} while (pgkindex <= pgknum);
		for (byte[] bs : list) {
			String s = TopUpUtil.bytes2HexString(bs);
			LogUtil.i("CmdHelper","s---"+s);
		}
		return list;
	}
	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
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

	private static byte GetBcc(byte[] data, int len) {
		byte uCRC = 0x0;
		int i;

		for (i = 0; i < len; i++)
			uCRC ^= data[i];

		return uCRC;
	}

	/**接收到设备发给app的数据*/
	public static void recvDataWrite(byte[] data) {
		// 收到的数据处理（protobuf已经解析）
		if (data == null)
			return;
		LogUtil.i("CmdHelper","ringWrite  data = " + TopUpUtil.bytes2HexString(data));
		if (recvData == null) {
			recvData = new byte[data.length];
			System.arraycopy(data, 0, recvData, 0, data.length);
		} else {
			byte[] temp = new byte[recvData.length];
			System.arraycopy(recvData, 0, temp, 0, recvData.length);
			recvData = new byte[temp.length + data.length];
			System.arraycopy(temp, 0, recvData, 0, temp.length);
			System.arraycopy(data, 0, recvData, temp.length, data.length);
		}
		// 判断收到的数据是否为最后一帧数据
		boolean isLastFrame = isLastFrame(recvData);
		if (recvData == null) {
			return;
		}
		if (frameData == null) {
			frameData = new byte[recvData.length];
			System.arraycopy(recvData, 0, frameData, 0, recvData.length);
		} else {
			byte[] temp = new byte[frameData.length];
			System.arraycopy(frameData, 0, temp, 0, frameData.length);
			frameData = new byte[temp.length + recvData.length];
			System.arraycopy(temp, 0, frameData, 0, temp.length);
			System.arraycopy(recvData, 0, frameData, temp.length,
					recvData.length);
		}
		LogUtil.i("CmdHelper","framData:" + TopUpUtil.bytes2HexString(frameData));
		recvData = null;
		if (isLastFrame) {
			LogUtil.i("CmdHelper","收到完整数据，通知接收  data = "
					+ TopUpUtil.bytes2HexString(frameData));
			// 如果数据收满。通知activity处理数据
			CmdReceiveData receiveData = new CmdReceiveData(currCmd, frameData);
			Bundle bundle = new Bundle();
			bundle.putSerializable(
					BlueToothUtil.EXTRA_CMD_RECEIVCE_DATA,
					receiveData);
			broadcastUpdate(
					BlueToothUtil.ACTION_RECEIVCE_DATA_COMPLETE,
					bundle);
		} else {
			LogUtil.i("CmdHelper","非最后帧数据");
		}
	}

	/**
	 * 判断收到的数据是否为最后一帧数据
	 * 
	 * @param indata
	 * @return
	 */
	public static boolean isLastFrame(byte[] indata) {
		byte[] data = indata;
		// TODO Auto-generated method stub
		boolean success = false;
		LogUtil.i("CmdHelper","待处理帧数据 data = " + TopUpUtil.bytes2HexString(data));
		if (byte2Integer255(data[0]) != 0x33) {
			data = null;
			return false;
		}
		int recvlen = data[3];
		if (recvlen + 5 != data.length) {
			data = null;
			return false;
		}
		byte[] snToData = new byte[data.length - 2];
		System.arraycopy(data, 1, snToData, 0, data.length - 2);
		if (data[data.length - 1] != GetBcc(snToData, snToData.length)) {
			data = null;
			return false;
		}
		byte[] datas = new byte[data.length - 5];
		System.arraycopy(data, 4, datas, 0, data.length - 5);
		if (byte2Integer127(data[2]) != 0) {
			success = false;
		} else {
			success = true;
		}
		recvData = datas;
		return success;
	}

	/**读卡器通道指令*/
	public static List<byte[]> doObuCommand(byte[] apdu) {
		LogUtil.e("CmdHelper","doObuCommand apdu:"+TopUpUtil.bytes2HexString(apdu));
		byte[] data = new byte[MAX_PACKAGE_LEN];
		int len = 2, pointer = 0, apdulen = 0;
		data[0] = (byte) 0xA5;
		apdulen = apdu[0] & 0xFF; // 传入数据的长度
		pointer = apdulen + 1;

		System.arraycopy(apdu, 0, data, 1, pointer);// not mine
		len = 1 + pointer; // not mine
		return buildFrame(data, len);
	}

	private static int byte2Integer127(byte data) {
		return data & 0x7F;
	}

	public static int byte2Integer255(byte data) {
		return Integer.parseInt((data & 0xFF) + "", 10);
	}

	private static void broadcastUpdate(String action, Bundle bundle) {
		final Intent intent = new Intent(action);
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
	}
	
	
	/**
	 * 创建ble通道
	 * 
	 * @param data
	 *            数据指令
	 * @param len
	 *            数据长度
	 * @return
	 */
//	public static boolean buildBleFrame2(byte[] data, int len) {
//		
//		List<byte[]> frameList = new ArrayList<byte[]>();
//		byte[] frameBuf = null;
//		int pgklen = 0, pgknum = 0, pgkindex = 1;
//		int dataPointer = 0;
//
//		Log.e("boosan", "buildBleFrame data:"+TopUpUtil.bytes2HexString(data));
//		Log.e("boosan", "buildBleFrame len:"+len);
//		
//		if (len == 0 || len > MAX_PACKAGE_LEN)
//			return false;
//		
//		Log.e("boosan", "data[0]:"+data[0]);
//		Log.e("boosan", "data[1]:"+data[1]);
//
//		pgknum = len / MAX_FRAME_LEN;
//		if ((len % MAX_FRAME_LEN) != 0)
//			pgknum++;
//
//		mPackageSn++;
//		if (mPackageSn > 0xF)
//			mPackageSn = 0;
//
//		do { // 开始分帧组数据
//			if (pgkindex < pgknum) {
//				pgklen = MAX_FRAME_LEN;
//			} else {
//				pgklen = len % MAX_FRAME_LEN;
//
//				if (pgklen == 0)
//					pgklen = MAX_FRAME_LEN;
//
//			}
//
//			frameBuf = new byte[pgklen + 5]; // 初始化帧长度
//
//			frameBuf[0] = (byte) 0x33; // st
//			frameBuf[1] = (byte) mPackageSn; // sn
//			if (pgkindex == 1) {
//				frameBuf[2] = (byte) (((pgknum - pgkindex) & 0x7F) | 0x80); // ctl
//			} else {
//				frameBuf[2] = (byte) ((pgknum - pgkindex) & 0x7F); // ctl
//			}
//			frameBuf[3] = (byte) (pgklen & 0xFF); // len
//			System.arraycopy(data, dataPointer, frameBuf, 4, pgklen);
//			dataPointer += pgklen;
//			byte[] snToData = new byte[frameBuf.length - 1];
//			System.arraycopy(frameBuf, 1, snToData, 0, snToData.length);
//			frameBuf[pgklen + 4] = GetBcc(snToData, pgklen + 3); // bcc
////			addDataToFrameList(frameBuf); // send包创建,一个frameBuf为一帧数据
//			frameList.add(frameBuf);
//			pgkindex++;
//			
//			Log.e("boosan", "pgklen:"+pgklen);
//			Log.e("boosan", "pgknum:"+pgknum);
//			Log.e("boosan", "pgkindex:"+pgkindex);
//		} while (pgkindex <= pgknum);
//		for (byte[] bs : frameList) {
//			String s = bytesToHexString(bs);
//			Log.i("boosan", "s---"+s);
//		}
//		return true;
//	}

//public static List<byte[]> buildBleFrame3(byte[] data, int len) {
//		
//		List<byte[]> frameList = new ArrayList<byte[]>();
//		byte[] frameBuf = null;
//		int pgklen = 0, pgknum = 0, pgkindex = 1;
//		int dataPointer = 0;
//
//		Log.e("boosan", "buildBleFrame data:"+TopUpUtil.bytes2HexString(data));
//		Log.e("boosan", "buildBleFrame len:"+len);
//		
//		if (len == 0 || len > MAX_PACKAGE_LEN)
//			return null;
//		
//		Log.e("boosan", "data[0]:"+data[0]);
//		Log.e("boosan", "data[1]:"+data[1]);
//
//		pgknum = len / MAX_FRAME_LEN;
//		if ((len % MAX_FRAME_LEN) != 0)
//			pgknum++;
//
//		mPackageSn++;
//		if (mPackageSn > 0xF)
//			mPackageSn = 0;
//
//		do { // 开始分帧组数据
//			if (pgkindex < pgknum) {
//				pgklen = MAX_FRAME_LEN;
//			} else {
//				pgklen = len % MAX_FRAME_LEN;
//
//				if (pgklen == 0)
//					pgklen = MAX_FRAME_LEN;
//
//			}
//
//			frameBuf = new byte[pgklen + 5]; // 初始化帧长度
//
//			frameBuf[0] = (byte) 0x33; // st
//			frameBuf[1] = (byte) mPackageSn; // sn
//			if (pgkindex == 1) {
//				frameBuf[2] = (byte) (((pgknum - pgkindex) & 0x7F) | 0x80); // ctl
//			} else {
//				frameBuf[2] = (byte) ((pgknum - pgkindex) & 0x7F); // ctl
//			}
//			frameBuf[3] = (byte) (pgklen & 0xFF); // len
//			System.arraycopy(data, dataPointer, frameBuf, 4, pgklen);
//			dataPointer += pgklen;
//			byte[] snToData = new byte[frameBuf.length - 1];
//			System.arraycopy(frameBuf, 1, snToData, 0, snToData.length);
//			frameBuf[pgklen + 4] = GetBcc(snToData, pgklen + 3); // bcc
////			addDataToFrameList(frameBuf); // send包创建,一个frameBuf为一帧数据
//			frameList.add(frameBuf);
//			pgkindex++;
//			
//			Log.e("boosan", "pgklen:"+pgklen);
//			Log.e("boosan", "pgknum:"+pgknum);
//			Log.e("boosan", "pgkindex:"+pgkindex);
//		} while (pgkindex <= pgknum);
//		for (byte[] bs : frameList) {
//			String s = bytesToHexString(bs);
//			Log.i("boosan", "s---"+s);
//		}
//		return frameList;
//	}
}
