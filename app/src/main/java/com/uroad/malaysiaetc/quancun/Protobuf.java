package com.uroad.malaysiaetc.quancun;

/**
 * 微信ProtoBuf协议 http://iot.weixin.qq.com/wiki/document-6_4.html
 * */
public interface Protobuf {

	// EmCmdId
	public final static int ECI_none = 0;

	/********* req： 蓝牙设备 -> 微信/厂商服务器 statr ***************/
	/** 登录 */
	public final static int ECI_req_auth = 10001;
	/** 蓝牙设备发送数据给微信或厂商 */
	public final static int ECI_req_sendData = 10002;
	/** 初始化 */
	public final static int ECI_req_init = 10003;
	/********* req： 蓝牙设备 -> 微信/厂商服务器 end ***************/

	/********* resp：微信/厂商服务器 -> 蓝牙设备 statr ***************/
	/** 响应登录 */
	public final static int ECI_resp_auth = 20001;
	/** 微信或厂商响应蓝牙设备 */
	public final static int ECI_resp_sendData = 20002;
	/** 响应初始化 */
	public final static int ECI_resp_init = 20003;
	/********* resp：微信/厂商服务器 -> 蓝牙设备 end ***************/

	/********* push：微信/厂商服务器 -> 蓝牙设备 statr ***************/
	/** 微信或厂商发送数据给蓝牙设备 */
	public final static int ECI_push_recvData = 30001;
	/** 微信或厂商发送数据给蓝牙设备 */
	public final static int ECI_push_switchView = 30002;
	/** 切换后台 */
	public final static int ECI_push_switchBackgroud = 30003;
	/********* push：微信/厂商服务器 -> 蓝牙设备 end ***************/
	// EmCmdId

}
