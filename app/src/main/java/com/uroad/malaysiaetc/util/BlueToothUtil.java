package com.uroad.malaysiaetc.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.uroad.malaysiaetc.model.ETCCardMDL;
import com.uroad.malaysiaetc.quancun.GattAttributes;

import java.util.UUID;

/** 操作蓝牙圈存的工具类 */
@SuppressLint("NewApi")
public class BlueToothUtil {

	/** 蓝牙搜索的uuid过滤 */
	private final UUID[] uuid = { GattAttributes.UUID_SERVICE };
	/** 设备物理地址 */
	public static final String EXTRA_DEVICE_MAC_ADDRESS = "EXTRA_DEVICE_MAC_ADDRESS";
	/** 设备表面号 */
	public static final String EXTRA_DEVICE_NUM = "EXTRA_DEVICE_NUM";
	/** 设备信息类 */
	public static final String EXTRA_SCAN_DEVICE_INFO_MDL = "EXTRA_SCAN_DEVICE_INFO_MDL";
	/** 设备发送个手机数据接收完成类 */
	public static final String EXTRA_CMD_RECEIVCE_DATA = "EXTRA_CMD_RECEIVCE_DATA";
	/** 国密 */
	public static final String EXTRA_DOMESTIC_CRYPTOGRAPHIC_ALGORITHMS = "EXTRA_DOMESTIC_CRYPTOGRAPHIC_ALGORITHMS";

	/** 发现服务广播 */
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";

	/** 响应数据初始化成功 */
	public final static String ACTION_RESP_INIT_SUCCESS = "ACTION_ACTIVITY_RESP_INIT_SUCCESS";

	/** 设备发送个手机数据接收完成 */
	public final static String ACTION_RECEIVCE_DATA_COMPLETE = "ACTION_ACTIVITY_RECEIVCE_DATA_COMPLETE";

	/** 设备与手机断开了蓝牙连接 */
	public final static String ACTION_STATE_DISCONNECTED = "ACTION_ACTIVITY_STATE_DISCONNECTED";

	/** 圈存错误，提醒退出 */
	public final static String ACTION_TRANSFER_EXCEPTIONS = "ACTION_ACTIVITY_TRANSFER_EXCEPTIONS";

	/** 根据API可判断是否为蓝牙4.0，非4.0不执行蓝牙操作，代码未添加 */

	/** 蓝牙搜索设备的时间,10s. */
	public static final long SCAN_PERIOD = 10 * 1000;

	/* 蓝牙状态 */
	/** 蓝牙是否处于扫描设备的状态 */
	public boolean isScanning = false;
	/** 蓝牙是否和设备处于连接状态 */
	public boolean isConnect = false;

	/** Activity是否绑定了服务 */
	boolean isBindService = false;

	/** 我的设备名称 */
	private String mMyDeviceName = "";
	/** 连接的设备的信息 */
	public BluetoothDevice mDevice;

	/** 插入蓝牙设备的卡片信息 */
	private ETCCardMDL mCard;
	/** 要圈存的金额 */
	private String money;

	private Context mContext;
	private BluetoothAdapter mBluetoothAdapter;
	// private QuanCunServiceHelper mQuanCunServiceHelper;
	private BleStateCallback bleStateCallback;
	
	BluetoothAdapter.LeScanCallback mLeScanCallback;

	public BlueToothUtil(Context context) {
		mContext = context;
		BluetoothManager bluetoothmanager = (BluetoothManager) mContext
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothmanager.getAdapter();
		mMyDeviceName = mBluetoothAdapter.getName();
		// mQuanCunServiceHelper = new QuanCunServiceHelper(mContext);
		if (mBluetoothAdapter == null) {
			// ToastUtil.show(mContext, "当前设备不支持蓝牙4.0");
			// ((Activity)mContext).finish();
			return;
		}

	}

	public String getMyDeviceName() {
		return mMyDeviceName;
	}

	/** 扫描设备 */
	public void scanDevice(final BluetoothAdapter.LeScanCallback leScanCallback) {
		mLeScanCallback = leScanCallback;
		if (!mBluetoothAdapter.isEnabled()) {
			// 蓝牙没打开
			Toast.makeText(mContext, "您没有打开蓝牙",Toast.LENGTH_SHORT).show();
			return;
		}
		// 这段代码导致某个手机弹出warn---handler 没有 looper.parpare()
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
				isScanning = false;
				if(bleStateCallback!=null){
					bleStateCallback.stopScan();
				}
			}
		}, SCAN_PERIOD);
		Log.i("BlueToothHelper", "开始扫描设备");
		//安卓4.4 会过滤掉蓝牙的特征值 所以当安卓版本为4.4时 取消过滤
		if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
			mBluetoothAdapter.startLeScan(uuid, mLeScanCallback);
		} else {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}
		isScanning = true;
		if(bleStateCallback!=null){
			bleStateCallback.startScan();
		}
	}

	public void stopScanDevice() {
		if (mLeScanCallback != null) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			Log.i("BlueToothHelper", "已停止扫描设备");
			isScanning = false;
			if(bleStateCallback!=null){
				bleStateCallback.startScan();
			}
		}
	}

	// /**连接设备*/
	// public void connectDevice( BluetoothDevice device){
	// mDevice = device;
	// mQuanCunServiceHelper.BindBlueToothService(conn);
	// }
	//

	// /**通过蓝牙设备读取卡片信息*/
	// public void readCardInfo(){
	// mQuanCunServiceHelper.bluetoothService.sendDirectCmd(Cmd.CMD_CARD_NUMBER);
	// }
	//
	// public void getBalance(){
	// mQuanCunServiceHelper.bluetoothService.sendDirectCmd(Cmd.CMD_CARD_BALANCE);
	// }
	//
	/** 获取已读到的卡片信息 */
	public ETCCardMDL getCard() {
		return mCard;
	}

	/** 圈存 */
	public void quancun() {

	}

	/** 检查蓝牙连接状态 */
	public boolean checkConnectState() {
		// 从蓝牙service中获取吧
		return false;
	}

	// public void unbindService(){
	// if(isBindService){
	// ((Activity)mContext).unbindService(conn);
	// isBindService = false;
	// }
	//
	// }

	// ServiceConnection conn = new ServiceConnection() {
	//
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// // TODO Auto-generated method stub
	// LogUtils.i("onServiceConnected");
	// mQuanCunServiceHelper.bluetoothService =
	// ((BlueToothService.LocalBinder)service).getService();
	// if(mQuanCunServiceHelper.initBle()){
	// isConnect = mQuanCunServiceHelper.connect(mDevice.getAddress());
	// if(isConnect){
	// //若连接成功，则开始握手
	// LogUtils.i("连接成功,下一步是握手-------------");
	// // mQuanCunServiceHelper.auth();
	// }
	// }
	// }
	// };
	//蓝牙扫描状态
	public void setBleStateCallback(BleStateCallback bleStateCallback){
		this.bleStateCallback = bleStateCallback;
	}
	
	
	public interface BleStateCallback{
		void startScan();
		void stopScan();
	}
}
