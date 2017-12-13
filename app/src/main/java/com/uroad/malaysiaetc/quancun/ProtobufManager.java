package com.uroad.malaysiaetc.quancun;

import android.content.Context;
import android.content.Intent;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uroad.lib.util.log.LogUtil;
import com.uroad.malaysiaetc.quancun.BleProbuf.AuthResponse;
import com.uroad.malaysiaetc.quancun.BleProbuf.BasePush;
import com.uroad.malaysiaetc.quancun.BleProbuf.BaseResponse;
import com.uroad.malaysiaetc.quancun.BleProbuf.InitResponse;
import com.uroad.malaysiaetc.quancun.BleProbuf.RecvDataPush;
import com.uroad.malaysiaetc.quancun.BleProbuf.SendDataRequest;
import com.uroad.malaysiaetc.util.BlueToothUtil;

import java.util.ArrayList;
import java.util.List;

/** 微信协议数据处理 */
public class ProtobufManager implements Protobuf {

	/** 定长包头 */
	private static FixedPHead mFixedPHead;
	/** 计算包数据长度,用于判断是否是最后一帧 */
	private static int sumPackageLength = 0;
	/** 每帧最大数据长度 20字节 1字节(b)=8比特(bit) */
	private static final int MAX_FRAME_LENGTH = 20;

	private static byte[] sendData;

	private static Context mContext;

	public final static void init(Context context) {
		mContext = context;
	}

	/**
	 * 添加需要发送的数据
	 * 
	 * @param newData
	 * @return
	 */
	public static void addSendData(byte[] newData) {
		LogUtil.i("ProtobufManager","addSendData");
		if (sendData == null) {
			sendData = new byte[newData.length];
			System.arraycopy(newData, 0, sendData, 0, newData.length);
		} else {
			byte[] temp = new byte[sendData.length];
			System.arraycopy(sendData, 0, temp, 0, sendData.length);
			sendData = new byte[temp.length + newData.length];
			System.arraycopy(temp, 0, sendData, 0, temp.length);
			System.arraycopy(newData, 0, sendData, temp.length, newData.length);
		}
	}

	/** 初始化定长包头 */
	private static void initFixedPHead(byte[] data) {
		// TODO Auto-generated method stub
		// [bMagicNumber=-2, bVer=1, nLength=26, nCmdId=10001, nSeq=1]
		mFixedPHead = new FixedPHead();
		mFixedPHead.bMagicNumber = data[0];
		mFixedPHead.bVer = data[1];
		mFixedPHead.nLength = (data[2] << 8 | data[3]) & 0xFFFF;
		mFixedPHead.nCmdId = (data[4] << 8 | data[5]) & 0xFFFF;
		mFixedPHead.nSeq = (data[6] << 8 | data[7]) & 0xFFFF;
	}

	/**
	 * 处理设备请求的特征值数据
	 * 
	 * @param data
	 */
	public static synchronized List<byte[]> processReqDtata(byte[] data) {
		List<byte[]> dataList = null;
		if (isFirstPackage(data)) {
			sumPackageLength = 0;
			initFixedPHead(data);
		}
		LogUtil.i("ProtobufManager",mFixedPHead.toString());
		LogUtil.i("ProtobufManager","data.length:" + data.length);
		LogUtil.i("ProtobufManager","处理设备请求的特征值数据:" + TopUpUtil.bytes2HexString(data));
		sumPackageLength += data.length;
		if (mFixedPHead.nCmdId == ECI_req_sendData) {
			addSendData(data);
		}
		if (isLastPackage(data)) {// 当数据接收完设备req数据时，才处理
			sumPackageLength = 0;
			BaseResponse.Builder baseRespBuilder = BaseResponse.newBuilder();
			// 登录 -> 初始化
			switch (mFixedPHead.nCmdId) {
			case ECI_req_auth: // 登录
				LogUtil.i("ProtobufManager","登录");
				AuthResponse.Builder authRespBuilder = AuthResponse
						.newBuilder();
				baseRespBuilder.setErrCode(0);// 这里随便填的
				baseRespBuilder.setErrMsg("OK");
				String aesSessionKey = "123124";
				ByteString aesSession = ByteString.copyFrom(aesSessionKey
						.getBytes());
				authRespBuilder.setAesSessionKey(aesSession);
				authRespBuilder.setBaseResponse(baseRespBuilder);
				AuthResponse authResponse = authRespBuilder.build();
				byte[] bufA = authResponse.toByteArray();
				mFixedPHead.nCmdId = ECI_resp_auth;// app响应设备登录
				dataList = buildPakeage(bufA);
				for (byte[] bs : dataList) {
					LogUtil.i("ProtobufManager","ECI_req_auth:" + TopUpUtil.bytes2HexString(bs));
				}

				break;

			case ECI_req_sendData: // 蓝牙设备发送数据给微信或厂商
				LogUtil.i("ProtobufManager","蓝牙设备发送数据给微信或厂商");
				LogUtil.i("ProtobufManager","蓝牙设备发送数据给微信或厂商数据1:"
						+ TopUpUtil.bytes2HexString(sendData));
				LogUtil.i("ProtobufManager","ECI_req_sendData:"
						+ TopUpUtil.bytes2HexString(sendData));
				byte[] sData = new byte[sendData.length - 8];
				System.arraycopy(sendData, 8, sData, 0, sendData.length - 8);
				try {
					SendDataRequest sendDataReq = SendDataRequest
							.parseFrom(sData);
					byte[] byteArray = sendDataReq.getData().toByteArray();
					CmdHelper.recvDataWrite(byteArray);
					// byteArray =
					// TopUpUtil.hexString2Bytes("fe010027271200030a00121933018014b20011c034343032313531313130303031313034f51800");
					// LogUtil.i("蓝牙设备发送数据给微信或厂商数据3:"+TopUpUtil.bytes2HexString(byteArray));
				} catch (InvalidProtocolBufferException e) {
					// TODO Auto-generated catch block
					Intent intent = new Intent(
							BlueToothUtil.ACTION_TRANSFER_EXCEPTIONS);
					mContext.sendBroadcast(intent);
					LogUtil.i("ProtobufManager","蓝牙设备发送数据给微信或厂商:" + e.getMessage());
					e.printStackTrace();
				} finally {
					sendData = null;
				}

				break;

			case ECI_req_init: // 初始化
				LogUtil.i("ProtobufManager","初始化");
				baseRespBuilder.setErrCode(0);
				baseRespBuilder.setErrMsg("OK");
				InitResponse.Builder initRespBuilder = InitResponse
						.newBuilder();
				initRespBuilder.setBaseResponse(baseRespBuilder);
				initRespBuilder.setUserIdHigh(0);
				initRespBuilder.setUserIdLow(0);
				initRespBuilder.setChalleangeAnswer(0);
				InitResponse initResponse = initRespBuilder.build();
				byte[] bufI = initResponse.toByteArray();
				mFixedPHead.nCmdId = ECI_resp_init; // 响应初始化
				dataList = buildPakeage(bufI);
				for (byte[] bs : dataList) {
					LogUtil.i("ProtobufManager","ECI_req_auth:" + TopUpUtil.bytes2HexString(bs));
				}
				break;

			default:
				break;
			}
		}
		return dataList;
	}

	/**
	 * 判断是否是第一帧数据 fe01001a271100010a0018808004200128023a06
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isFirstPackage(byte[] data) {
		if (data[0] == (byte) 0xfe && data[1] == (byte) 0x01) {
			return true;
		}
		return false;
	}

	/**
	 * 判断收到的数据是否为一包数据的最后一帧
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isLastPackage(byte[] data) {
		if (sumPackageLength == mFixedPHead.nLength) {
			return true;
		}
		return false;
	}

	public static List<byte[]> buildPakeage(byte[] data) { // 创建待传数据

		LogUtil.i("ProtobufManager","buildPakeage:" + TopUpUtil.bytes2HexString(data));
		if (mFixedPHead == null) {
			mFixedPHead = new FixedPHead();
		}
		int frameLen = 0, frameNum = 0;
		int dataPointer = 0;
		mFixedPHead.nLength = 8 + data.length;// 包头长度为 8
		byte[] sendBuf = new byte[mFixedPHead.nLength];
		sendBuf[0] = mFixedPHead.bMagicNumber;
		sendBuf[1] = mFixedPHead.bVer;
		sendBuf[2] = (byte) ((mFixedPHead.nLength >> 8) & 0xFF);
		sendBuf[3] = (byte) (mFixedPHead.nLength & 0xFF);
		sendBuf[4] = (byte) ((mFixedPHead.nCmdId >> 8) & 0xFF);
		sendBuf[5] = (byte) (mFixedPHead.nCmdId & 0xFF);
		sendBuf[6] = (byte) ((mFixedPHead.nSeq >> 8) & 0xFF);
		sendBuf[7] = (byte) (mFixedPHead.nSeq & 0xFF);
		System.arraycopy(data, 0, sendBuf, 8, data.length);
		LogUtil.i("ProtobufManager","mFixedPHead:" + mFixedPHead.toString());
		LogUtil.i("ProtobufManager","待发数据：" + TopUpUtil.bytes2HexString(sendBuf));
		// 将待发的数据保存在本地txt文件
		// DataUtil.save("123.txt", TopUpUtil.bytes2HexString(sendBuf));
		// 计算帧个数
		// 分帧：假设蓝牙手环上有1k数据，要发给手机微信。由于一个特征值长度有限（如20个字节），显然需要分多次才能传输完成。
		// 1k数据，要分成1024字节/ 20字节=51 个帧。
		// 剩下的4个字节，不足一帧（20个字节），需补齐为一帧并对剩下的16个字节赋0。总共是52帧。
		frameNum = mFixedPHead.nLength / MAX_FRAME_LENGTH;
		if ((mFixedPHead.nLength % MAX_FRAME_LENGTH) != 0)
			frameNum += 1;

		List<byte[]> dataList = new ArrayList<byte[]>();
		for (int i = 1; i <= frameNum; i++) {
			if (i != frameNum) {
				frameLen = MAX_FRAME_LENGTH;
			} else {// 最后一帧
				frameLen = mFixedPHead.nLength % MAX_FRAME_LENGTH;
				if (frameLen == 0) {
					frameLen = MAX_FRAME_LENGTH;
				}
			}
			byte[] frameBuf = new byte[frameLen];
			System.arraycopy(sendBuf, dataPointer, frameBuf, 0, frameLen);
			dataPointer += frameLen;
			dataList.add(frameBuf);
			// DataUtil.save("123.txt",
			// frameNum+TopUpUtil.bytes2HexString(frameBuf));
		}
		return dataList;
	}

	public static List<byte[]> buildDataFrame(List<byte[]> data) {
		// 创建protobuf包（下发数据到设备）
		for (byte[] bs : data) {
			LogUtil.i("ProtobufManager","帧指令  = " + TopUpUtil.bytes2HexString(bs));
		}
		RecvDataPush.Builder recvDataPushBuilder = RecvDataPush.newBuilder();
		BasePush.Builder baseBuilder = BasePush.newBuilder();
		recvDataPushBuilder.setBasePush(baseBuilder);
		// RecvDataBuilder.setData(ByteString.copyFrom(data));
		List<ByteString> list = new ArrayList<ByteString>();
		for (byte[] b : data) {
			list.add(ByteString.copyFrom(b));
		}
		recvDataPushBuilder.setData(ByteString.copyFrom(list));
		recvDataPushBuilder.setType(BleProbuf.EmDeviceDataType.valueOf(0));
		RecvDataPush recvDataPush = recvDataPushBuilder.build();
		byte[] buf = recvDataPush.toByteArray();
		mFixedPHead.nCmdId = ECI_push_recvData;
		return buildPakeage(buf);
	}

	public static List<byte[]> buildDataFrame(byte[] data) {
		// 创建protobuf包（下发数据到设备）
		LogUtil.i("ProtobufManager","帧指令  = " + TopUpUtil.bytes2HexString(data));
		RecvDataPush.Builder RecvDataBuilder = RecvDataPush.newBuilder();
		BasePush.Builder BaseBuilder = BasePush.newBuilder();
		RecvDataBuilder.setBasePush(BaseBuilder);
		RecvDataBuilder.setData(ByteString.copyFrom(data));
		RecvDataBuilder.setType(BleProbuf.EmDeviceDataType.valueOf(0));
		RecvDataPush RecvData = RecvDataBuilder.build();
		byte[] buf = RecvData.toByteArray();
		if (mFixedPHead != null) {
			mFixedPHead.nCmdId = ECI_push_recvData;
		} else {
			LogUtil.i("ProtobufManager","mFixedPHead == null");
		}
		return buildPakeage(buf);
	}

}
