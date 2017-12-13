package com.uroad.malaysiaetc.quancun;

import java.util.UUID;

public interface GattAttributes {

	public static UUID UUID_HEART_RATE_MEASUREMENT = UUID
			.fromString("00002a37-0000-1000-8000-00805f9b34fb");
	public static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	/** SERVICE */
	public static UUID UUID_SERVICE = UUID
			.fromString("0000fee7-0000-1000-8000-00805f9b34fb");

	/************************ 微信协议相关 start ********************/
	/** Write Characteristics UUID 0xFEC7 */
	public static UUID UUID_WRITE_CHARACTERISTICS = UUID
			.fromString("0000fec7-0000-1000-8000-00805f9b34fb");

	/** Indicate Characteristics UUID */
	public static UUID UUID_INDICATE_CHARACTERISTICS = UUID
			.fromString("0000fec8-0000-1000-8000-00805f9b34fb");

	/** Read Characteristics UUID */
	public static UUID UUID_READ_CHARACTERISTICS = UUID
			.fromString("0000fec9-0000-1000-8000-00805f9b34fb");

	/** Andriod RFCOMM UUID */
	public static UUID UUID_ANDRIOD_RFCOMM = UUID
			.fromString("e5b152ed-6b46-09e9-4678-665e9a972cbc");
	/************************ 微信协议相关 end ********************/

}
