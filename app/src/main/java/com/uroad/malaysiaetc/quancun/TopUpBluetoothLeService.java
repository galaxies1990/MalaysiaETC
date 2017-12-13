package com.uroad.malaysiaetc.quancun;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.uroad.lib.util.log.LogUtil;
import com.uroad.malaysiaetc.util.BlueToothUtil;
import com.uroad.malaysiaetc.util.Util;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("NewApi")
public class TopUpBluetoothLeService extends Service implements GattAttributes,
		Protobuf, Cmd {

	private final IBinder mBinder = new LocalBinder();

	/** 蓝牙设备的mac地址 */
	private String mBluetoothDeviceAddress;

	/**
	 * 蓝牙设备的连接状态 未连接、连接中、已连接
	 */
	private int mConnectionState = STATE_DISCONNECTED;
	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;

	/**
	 * 该变量用来表示 当前蓝牙所处的步骤 在发送数据的时候，蓝牙会根据该值 而执行另外的操作 为了蓝牙设备顺利初始化而创建的变量
	 * ECI_resp_auth 响应登录 ECI_resp_init 响应初始化 ECI_resp_sendData 微信或厂商响应蓝牙设备
	 */
	private int STATE_ECI = ECI_resp_auth;

	// 身份特征值
	BluetoothGattCharacteristic mWriteCharacteristic;

	List<byte[]> dataList = new ArrayList<byte[]>();

	/** 需要批量发送的数据 */
	private List<byte[]> batchDatas;

	// List<byte[]> cmdList = new ArrayList<byte[]>();

	/*
	 * public enum SendMode{ single //单条发送 ,batch //批量发送 }
	 */

	public class LocalBinder extends Binder {
		/** 获取Service */
		public TopUpBluetoothLeService getService() {
			return TopUpBluetoothLeService.this;
		}
	}

	private boolean isInited = false;// 是否初始化完成

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.i("TopUpBluetoothLeService","TopUpBluetoothLeService onCreate");
		/*
		 * 注册receiver，该接收器主要用来发送数据 通过利用android的消息队列，receiver能将发送数据从异步整理成同步
		 */
		registerReceiver(mReceiver, getIntentFilter());

		ProtobufManager.init(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.i("TopUpBluetoothLeService","TopUpBluetoothLeService onBind");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.i("TopUpBluetoothLeService","TopUpBluetoothLeService onUnbind");
		close();
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
		LogUtil.i("TopUpBluetoothLeService","TopUpBluetoothLeService onDestroy");
	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	// 实现本机与蓝牙设备交互的类
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		/** 连接蓝牙或者断开蓝牙 */
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
											int newState) {
			LogUtil.i("TopUpBluetoothLeService","onConnectionStateChange");
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mConnectionState = STATE_CONNECTED;
				LogUtil.i("TopUpBluetoothLeService","onConnectionStateChange --> Connected to GATT server.");
				// Attempts to discover services after successful connection.
				LogUtil.i("TopUpBluetoothLeService","onConnectionStateChange --> Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				mConnectionState = STATE_DISCONNECTED;
				mBluetoothGatt.close();
				mBluetoothGatt = null;
				broadcastUpdate(BlueToothUtil.ACTION_STATE_DISCONNECTED);
				LogUtil.i("TopUpBluetoothLeService","Disconnected from GATT server.");
				LogUtil.e("TopUpBluetoothLeService","设备断开了连接");
			}

		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {// conn方法之后，回调
			LogUtil.i("TopUpBluetoothLeService","onServicesDiscovered");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				LogUtil.i("TopUpBluetoothLeService","onServicesDiscovered BluetoothGatt.GATT_SUCCESS");
				broadcastUpdate(BlueToothUtil.ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				LogUtil.w("TopUpBluetoothLeService","onServicesDiscovered received: " + status);
			}

		};

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic,
				int status) {
			LogUtil.i("TopUpBluetoothLeService","onCharacteristicRead");
		};

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
										  BluetoothGattCharacteristic characteristic, int status) {// mBluetoothGatt.writeCharacteristic
																			// 写入后回调
			// Message msg = new Message();
			// msg.what = MSG_WHITE_DATA;
			// Bundle bundle = new Bundle();
			// bundle.putInt("status", status);
			// Intent intent = new Intent(ACTION_DATA_WHITE);
			// intent.putExtra("status", status);
			// sendBroadcast(intent);
			// msg.setData(bundle);
			// mHandler.handleMessage(msg);

			if (isInited) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					LogUtil.i("TopUpBluetoothLeService","onCharacteristicWrite : BluetoothGatt.GATT_SUCCESS");
					if (dataList != null && dataList.size() > 0) {// 继续发送为发送的数据
						broadcastUpdate(ACTION_WRITE_CHAR);
					} else {// 广播数据已发送完成
						LogUtil.i("TopUpBluetoothLeService","ACTION_SEND_SUCCESSS-----------------OK---"
								+ STATE_ECI);
						if (batchDatas != null && batchDatas.size() > 0) {// 继续发送为发送的数据
							// mHandler.sendEmptyMessage(MSG_BATCH_SEND);
							broadcastUpdate(ACTION_BATCH_SEND);
						}

						if (STATE_ECI == ECI_resp_auth) {// 响应登录成功，进入初始化
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_auth");
							STATE_ECI = ECI_resp_init;
						} else if (STATE_ECI == ECI_resp_init) {
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_init");
							broadcastUpdate(BlueToothUtil.ACTION_RESP_INIT_SUCCESS);
							STATE_ECI = ECI_resp_sendData;
						} else if (STATE_ECI == ECI_resp_sendData) {// 微信或厂商响应蓝牙设备
						}
						//
						// // if (!isBatchSend) {
						// // }else{
						// // }
						//
					}
				}
			}

		};

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {// 收数据
			// Message msg = new Message();
			// msg.what = MSG_READ_DATA;
			// Bundle bundle = new Bundle();
			// byte[] b = characteristic.getValue();
			// bundle.putByteArray("b", b);
			// msg.setData(bundle);
			// mHandler.handleMessage(msg);
			Bundle bundle = new Bundle();
			byte[] b = characteristic.getValue();
			bundle.putByteArray("b", b);
			Intent intent = new Intent(ACTION_DATA_READ);
			intent.putExtras(bundle);
			sendBroadcast(intent);

			// LogUtil.i("onCharacteristicChanged");
			// byte[] b = characteristic.getValue();
			// LogUtil.i("onCharacteristicChanged data:"
			// + TopUpUtil.bytes2HexString(b));
			// if(GlobalData.Is_Device_Debug){
			// Bundle bundle = new Bundle();
			// bundle.putString("log","onCharacteristicChanged data:"
			// + TopUpUtil.bytes2HexString(b));
			// Intent _intent = new Intent(BlueToothUtil.ACTION_STATE_LOG);
			// _intent.putExtras(bundle);
			// TopUpBluetoothLeService.this.sendBroadcast(_intent);
			// }
			// dataList = ProtobufManager.processReqDtata(b);
			// if (dataList != null && dataList.size() > 0) {
			// broadcastUpdate(ACTION_WRITE_CHAR);
			// }
		}

	};

	/**
	 * 设置通知方式
	 * 
	 * @return 成功 true
	 */
	public boolean setNotificationWay() {
		/*
		 * 根据uuid返回一个bluetoothGattService
		 * bluetoothGattService作为周边来提供数据，可当作他是充值易等蓝牙设备的GATTService
		 */
		BluetoothGattService bluetoothGattService = mBluetoothGatt
				.getService(UUID_SERVICE);

		BluetoothGattCharacteristic indicateCharacteristic = bluetoothGattService
				.getCharacteristic(UUID_INDICATE_CHARACTERISTICS);
		// 身份特征值
		mWriteCharacteristic = bluetoothGattService
				.getCharacteristic(UUID_WRITE_CHARACTERISTICS);
		// bluetoothGattService.getCharacteristic(UUID_READ_CHARACTERISTICS)
		if (indicateCharacteristic == null) {
			LogUtil.e("TopUpBluetoothLeService","setNotificationWay() indicateCharacteristic == null");
			return false;
		}
		if (mWriteCharacteristic == null) {
			LogUtil.e("TopUpBluetoothLeService","setNotificationWay() writeCharacteristic == null");
			return false;
		}
		// Indication方式，即带响应的通知。当通知完成的时候，可以认为手机已经读完数据
		mBluetoothGatt.setCharacteristicNotification(indicateCharacteristic,
				true);
		BluetoothGattDescriptor descriptor = indicateCharacteristic
				.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
		if (descriptor == null) {
			LogUtil.e("TopUpBluetoothLeService","setNotificationWay() descriptor == null");
			return false;
		}
		// ENABLE_INDICATION_VALUE
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		return mBluetoothGatt.writeDescriptor(descriptor);
	}

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initializeBle() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) this
					.getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				LogUtil.e("TopUpBluetoothLeService","Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			LogUtil.e("TopUpBluetoothLeService","Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address
	 *            The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			LogUtil.w("TopUpBluetoothLeService","BluetoothAdapter not initialized or unspecified address.");
		}
		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			LogUtil.i("TopUpBluetoothLeService","Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			LogUtil.w("TopUpBluetoothLeService","Device not found.  Unable to connect.");
			return false;
		}

		// We want to directly connect to the device, so we are setting the
		// autoConnect parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
		// mBluetoothGatt.close();
		// mBluetoothGatt = null;
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	/** 写入数据 */
	private synchronized boolean mWriteCharacteristic(byte[] bytes) {
		boolean bool = mWriteCharacteristic.setValue(bytes);
		LogUtil.i("TopUpBluetoothLeService","mWriteCharacteristic.setValue(bytes):" + bool);
		LogUtil.i("TopUpBluetoothLeService","data:" + TopUpUtil.bytes2HexString(bytes));
		return mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
	};

	long delayTime = 400;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			if (ACTION_WRITE_CHAR.equals(action)) {
				if (dataList != null && dataList.size() > 0) {
					if (mWriteCharacteristic != null) {
						LogUtil.e("TopUpBluetoothLeService","onReceive mWriteCharacteristic != null");
						if (mWriteCharacteristic(dataList.get(0))) {
							try {
								LogUtil.i("TopUpBluetoothLeService","确定已发送："
										+ Util.ByteArrayToHexString(dataList
												.get(0)));
								dataList.remove(0);
								if (!isInited) {
									// 在这里增加判断代码，判断数据是否发送完成。
									mHandler.postDelayed(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											if (dataList != null
													&& dataList.size() > 0) {// 继续发送为发送的数据
												broadcastUpdate(ACTION_WRITE_CHAR);
											} else {// 广播数据已发送完成
												LogUtil.i("TopUpBluetoothLeService","ACTION_SEND_SUCCESSS-----------------OK---"
														+ STATE_ECI);
												if (batchDatas != null
														&& batchDatas.size() > 0) {// 继续发送为发送的数据
													// mHandler.sendEmptyMessage(MSG_BATCH_SEND);
													broadcastUpdate(ACTION_BATCH_SEND);
												}
												if (STATE_ECI == ECI_resp_auth) {// 响应登录成功，进入初始化
													LogUtil.i("TopUpBluetoothLeService","ECI_resp_auth");
													STATE_ECI = ECI_resp_init;
												} else if (STATE_ECI == ECI_resp_init) {
													LogUtil.i("TopUpBluetoothLeService","ECI_resp_init");
													isInited = true;
													STATE_ECI = ECI_resp_sendData;
													broadcastUpdate(BlueToothUtil.ACTION_RESP_INIT_SUCCESS);
												} else if (STATE_ECI == ECI_resp_sendData) {// 微信或厂商响应蓝牙设备
													// delayTime = 0;
												}
											}
										}
									}, delayTime);
								}

							} catch (Exception e) {
								// TODO: handle exception
								LogUtil.e("TopUpBluetoothLeService","WriteCharacteristic:"
										+ e.getMessage());
								e.printStackTrace();
							}
						}
					}
				}
			} else if (ACTION_SEND_SUCCESSS.equals(action)) {
				LogUtil.i("TopUpBluetoothLeService","ACTION_SEND_SUCCESSS-----------------OK");
				if (STATE_ECI == ECI_resp_auth) {// 响应登录成功，进入初始化
					STATE_ECI = ECI_resp_init;
				} else if (STATE_ECI == ECI_resp_init) {

				}
			} else if (ACTION_BATCH_SEND.equals(action)) {
				if (batchDatas != null && batchDatas.size() > 0) {
					dataList = ProtobufManager
							.buildDataFrame(batchDatas.get(0));
					batchDatas.remove(0);
					if (dataList != null && dataList.size() > 0) {
						broadcastUpdate(ACTION_WRITE_CHAR);
					}
				}
			} else if (ACTION_DATA_WHITE.equals(action)) {
				int status = intent.getIntExtra("status", 99);
				// Bundle bundle1 = intent.getExtras();
				// int status = bundle1.getInt("status");
				if (status == BluetoothGatt.GATT_SUCCESS) {
					LogUtil.i("TopUpBluetoothLeService","onCharacteristicWrite : BluetoothGatt.GATT_SUCCESS");
					if (dataList != null && dataList.size() > 0) {// 继续发送为发送的数据
						broadcastUpdate(ACTION_WRITE_CHAR);
					} else {// 广播数据已发送完成
						LogUtil.i("TopUpBluetoothLeService","ACTION_SEND_SUCCESSS-----------------OK---"
								+ STATE_ECI);
						if (batchDatas != null && batchDatas.size() > 0) {// 继续发送为发送的数据
							// mHandler.sendEmptyMessage(MSG_BATCH_SEND);
							broadcastUpdate(ACTION_BATCH_SEND);
						}

						if (STATE_ECI == ECI_resp_auth) {// 响应登录成功，进入初始化
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_auth");
							STATE_ECI = ECI_resp_init;
						} else if (STATE_ECI == ECI_resp_init) {
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_init");
							broadcastUpdate(BlueToothUtil.ACTION_RESP_INIT_SUCCESS);
							STATE_ECI = ECI_resp_sendData;
						} else if (STATE_ECI == ECI_resp_sendData) {// 微信或厂商响应蓝牙设备

						}

						// if (!isBatchSend) {
						// }else{
						// }

					}
				}
			} else if (ACTION_DATA_READ.equals(action)) {
				Bundle bundle2 = intent.getExtras();
				if (bundle2 == null) {
					return;
				}
				LogUtil.i("TopUpBluetoothLeService","onCharacteristicChanged");
				byte[] b = bundle2.getByteArray("b");
				// byte[] b = characteristic2.getValue();
				LogUtil.i("TopUpBluetoothLeService","onCharacteristicChanged data:"
						+ TopUpUtil.bytes2HexString(b));
				dataList = ProtobufManager.processReqDtata(b);
				if (dataList != null && dataList.size() > 0) {
					broadcastUpdate(ACTION_WRITE_CHAR);
				}
			}
		}
	};

	public synchronized void sendCmd(int cmdType) {
		if (mConnectionState == STATE_DISCONNECTED) {
			LogUtil.e("TopUpBluetoothLeService","蓝牙未连接，无法发送");
			return;
		}
		List<byte[]> list = null;
		switch (cmdType) {
		case CMD_A2:
			list = CmdHelper.getCmdA2();
			break;

		case CMD_C3:
			list = CmdHelper.GetCmdC3();
			break;

		case CMD_C2:
			list = CmdHelper.GetCmdC2();
			break;

		case CMD_C0:
			list = CmdHelper.getVerify1C0();
			break;

		case CMD_C4:
			list = CmdHelper.getVerify1C4();
			break;

		default:
			break;
		}
		// dataList = ProtobufManager.buildDataFrame(list);
		batchDatas = list;
		for (int i=0;i<list.size();i++){
			LogUtil.e("TopUpBluetoothLeService","圈存日志：发送指令==>"+list.get(i).toString());
		}
		// LogUtil.i("dataList.size():"+dataList.size());
		broadcastUpdate(ACTION_BATCH_SEND);
		// broadcastUpdate(ACTION_WRITE_CHAR);
	}

	// ICC 通道
	public synchronized void sendDirectCmd(int cmdType) {
		if (mConnectionState == STATE_DISCONNECTED) {
			LogUtil.e("TopUpBluetoothLeService","蓝牙未连接，无法发送");
			return;
		}
		List<byte[]> list = null;
		switch (cmdType) {
		case CMD_CARD_BALANCE:
			list = CmdHelper.getCardBalanceFrame();
			break;

		case CMD_CARD_NUMBER:
			list = CmdHelper.getCardNumber();
			break;

		case CMD_CARD_PIN3:
			list = CmdHelper.getPin3();
			break;

		case CMD_CARD_PIN6:
			list = CmdHelper.getPin6();
			break;
		case CMD_FILE_0019_1:

			break;

		default:
			break;
		}
		// dataList = ProtobufManager.buildDataFrame(list);

		if (CMD_FILE_0008_1 == cmdType || CMD_FILE_0008_2 == cmdType
				|| CMD_FILE_0019_1 == cmdType || CMD_FILE_0019_2 == cmdType
				|| CMD_FILE_0009_1 == cmdType || CMD_FILE_0009_2 == cmdType
				|| CMD_FILE_0009_3 == cmdType || CMD_FILE_0009_4 == cmdType
				|| CMD_FILE_0009_5 == cmdType || CMD_FILE_0009_6 == cmdType) {

			LogUtil.i("TopUpBluetoothLeService","sendDirectCmd cmdType:" + cmdType);
			list = CmdHelper.getFileCmd(cmdType);
		}

		LogUtil.i("TopUpBluetoothLeService","sendDirectCmd");
		batchDatas = list;
		broadcastUpdate(ACTION_BATCH_SEND);
		// broadcastUpdate(ACTION_WRITE_CHAR);
	}

	/** 验证命令 */
	public synchronized void sendVerifyCmd(int cmdType, String... params) {
		if (mConnectionState == STATE_DISCONNECTED) {
			LogUtil.e("TopUpBluetoothLeService","蓝牙未连接，无法发送");
			return;
		}
		// final List<byte[]> list = null;
		switch (cmdType) {
		case CMD_C1:
			// serverCertificate, random2
			batchDatas = CmdHelper.getVerify2(params[0], params[1]);
			// mHandler.sendEmptyMessage(MSG_BATCH_SEND);
			break;

		case CMD_C5:
			// String workkey, String workkeyMac, String mackey, String
			// mackeyMac, String random2,String signdata
			batchDatas = CmdHelper.getVerify5(params[0], params[1], params[2],
					params[3], params[4], params[5]);
			break;

		case CMD_VERITY_C2:
			batchDatas = CmdHelper.getVerify3(params[0]);
			break;

		case CMD_DEPOSIT_INIT:
			batchDatas = CmdHelper.doDepositInit(params[0]);
			break;

		case CMD_DEPOSIT_WRITE:
			batchDatas = CmdHelper.doDepositWrite(params[0]);
			break;

		case CMD_DEPOSIT_WRITE2:
			batchDatas = CmdHelper.doDepositWrite2(params[0]);
			break;

		case CMD_DEPOSIT_HALF_1:
			batchDatas = CmdHelper.doDepositHalf(params[0]);
			break;
		default:	break;
		}
for (int i=0;i<batchDatas.size();i++) {
	LogUtil.e("TopUpBluetoothLeService","圈存日志：发送指令==>"+batchDatas.get(i).toString());
}
		broadcastUpdate(ACTION_BATCH_SEND);
		/*
		 * dataList = ProtobufManager.buildDataFrame(list);
		 * broadcastUpdate(ACTION_WRITE_CHAR);
		 */
	}

	/** 批量发送数据 */
	private static final int MSG_BATCH_SEND = 0x01;
	private static final int MSG_WHITE_DATA = 0x02;
	private static final int MSG_READ_DATA = 0x03;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_BATCH_SEND:
				if (batchDatas != null && batchDatas.size() > 0) {
					dataList = ProtobufManager
							.buildDataFrame(batchDatas.get(0));
					batchDatas.remove(0);
					broadcastUpdate(ACTION_WRITE_CHAR);
				}
				break;
			case MSG_WHITE_DATA:
				Bundle bundle1 = msg.getData();
				int status = bundle1.getInt("status");
				if (status == BluetoothGatt.GATT_SUCCESS) {
					LogUtil.i("TopUpBluetoothLeService","onCharacteristicWrite : BluetoothGatt.GATT_SUCCESS");
					if (dataList != null && dataList.size() > 0) {// 继续发送为发送的数据
						broadcastUpdate(ACTION_WRITE_CHAR);
					} else {// 广播数据已发送完成
						LogUtil.i("TopUpBluetoothLeService","ACTION_SEND_SUCCESSS-----------------OK---"
								+ STATE_ECI);
						if (batchDatas != null && batchDatas.size() > 0) {// 继续发送为发送的数据
							// mHandler.sendEmptyMessage(MSG_BATCH_SEND);
							broadcastUpdate(ACTION_BATCH_SEND);
						}

						if (STATE_ECI == ECI_resp_auth) {// 响应登录成功，进入初始化
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_auth");
							STATE_ECI = ECI_resp_init;
						} else if (STATE_ECI == ECI_resp_init) {
							LogUtil.i("TopUpBluetoothLeService","ECI_resp_init");
							broadcastUpdate(BlueToothUtil.ACTION_RESP_INIT_SUCCESS);
							STATE_ECI = ECI_resp_sendData;
						} else if (STATE_ECI == ECI_resp_sendData) {// 微信或厂商响应蓝牙设备

						}

						// if (!isBatchSend) {
						// }else{
						// }

					}
				}

			case MSG_READ_DATA:
				Bundle bundle2 = msg.getData();
				if (bundle2 == null) {
					return;
				}
				LogUtil.i("TopUpBluetoothLeService","onCharacteristicChanged");
				byte[] b = bundle2.getByteArray("b");
				// byte[] b = characteristic2.getValue();
				LogUtil.i("TopUpBluetoothLeService","onCharacteristicChanged data:"
						+ TopUpUtil.bytes2HexString(b));
				dataList = ProtobufManager.processReqDtata(b);
				if (dataList != null && dataList.size() > 0) {
					broadcastUpdate(ACTION_WRITE_CHAR);
				}

				break;

			default:
				break;
			}
		}
	};

	public final static String ACTION_WRITE_CHAR = "ACTION_SERVICE_WRITE_CHAR";
	public final static String ACTION_SEND_SUCCESSS = "ACTION_SERVICE_SEND_SUCCESSS";
	public final static String ACTION_BATCH_SEND = "ACTION_SERVICE_BATCH_SEND";
	public final static String ACTION_DATA_WHITE = "ACTION_DATA_WHITE_AAA";
	public final static String ACTION_DATA_READ = "ACTION_DATA_READ_AAA";

	private final IntentFilter getIntentFilter() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(ACTION_WRITE_CHAR);
		mIntentFilter.addAction(ACTION_SEND_SUCCESSS);
		mIntentFilter.addAction(ACTION_BATCH_SEND);
		mIntentFilter.addAction(ACTION_DATA_WHITE);
		mIntentFilter.addAction(ACTION_DATA_READ);
		return mIntentFilter;
	}

	// public synchronized void sendCmd(int cmdType){
	// isBatchSend = false;
	// List<byte[]> list = null;
	// switch (cmdType) {
	// case CMD_A2:
	// list = CmdHelper.getCmdA2();
	// break;
	//
	// case CMD_C3:
	// list = CmdHelper.GetCmdC3();
	// break;
	//
	// case CMD_C2:
	// list = CmdHelper.GetCmdC2();
	// break;
	//
	// case CMD_C0:
	// list = CmdHelper.getVerify1C0();
	// break;
	//
	// default:
	// break;
	// }
	// dataList = ProtobufManager.buildDataFrame(list);
	// LogUtil.i("dataList.size():"+dataList.size());
	//
	// broadcastUpdate(ACTION_WRITE_CHAR);
	// }
	//
	// public synchronized void sendDirectCmd(int cmdType){
	// isBatchSend = false;
	// List<byte[]> list = null;
	// switch (cmdType) {
	// case CMD_CARD_BALANCE:
	// list = CmdHelper.getCardBalanceFrame();
	// break;
	//
	// case CMD_CARD_NUMBER:
	// list = CmdHelper.getCardNumber();
	// break;
	//
	// default:
	// break;
	// }
	// dataList = ProtobufManager.buildDataFrame(list);
	// LogUtil.i("sendDirectCmd");
	// for (byte[] bs : dataList) {
	// LogUtil.i("dataList:"+TopUpUtil.bytes2HexString(bs));
	// }
	// broadcastUpdate(ACTION_WRITE_CHAR);
	// }
}
