package com.uroad.malaysiaetc.quancun;

/***
 * 指令 
 * 0x100001-  自己编造的
 * @author Administrator
 *
 */
public interface Cmd {
	
	/**0x01表示渠道证书1（001C文件）*/
	public static final int CMD_01 = 0x01;
	
	
	/**ICC复位响应握手响应   返回读卡器中的ICC卡复位操作结果*/
	public static final int CMD_B2 = 0xB2;
	
	/** APP握手 A2 APP和读卡器建立握手 */
	public final static int CMD_A2 = 0xA2;
	
	/**ICC通道指令响应   返回读卡器中的ICC卡通道操作结果*/
	public static final int CMD_B3 = 0xB3;
	
	/**获取读卡器的电池电量*/
	/**验证2 C1+渠道证书号+渠道证书+Rnd2*/
	public static final int CMD_C2 = 0xC2;
	
	/**强制读卡器断电*/
	public static final int CMD_C3 = 0xC3;

	/**认证指令1*/
	public static final int CMD_C0 = 0xC0;
	
	/**验证2 C1+渠道证书号+渠道证书+Rnd2*/
	public final static int CMD_C1 = 0xC1;
	
	/**国密验证 C4+渠道证书号+渠道证书+Rnd2*/
	public final static int CMD_C4 = 0xC4;
	
	public final static int CMD_C5 = 0xC5;
	
	/**认证通道指令 A6 对读卡器进行认证*/
	public final static int CMD_A6 = 0xA6;
	
	/**PIN为三字节的TLV指令*/
//	public final static String DIRECT_CMD_CARD_PIN3 = "800A01080020000003123456";
	/**PIN为三字节的TLV指令 并进行圈存初始化 获取圈存金额为0的圈存初始化明文返回*/
	public final static String DIRECT_CMD_CARD_PIN3 = "801C010800200000031234560210805000020B0100000000000440101001";
	public final static int CMD_CARD_PIN3 = 0x1000018;
	
	/**PIN为六字节的TLV指令*/
//	public final static String DIRECT_CMD_CARD_PIN6 = "800D010B0020000006313233343536";
	/**PIN为六字节的TLV指令 并进行圈存初始化 获取圈存金额为0的圈存初始化明文返回*/
	public final static String DIRECT_CMD_CARD_PIN6 = "801F010B00200000063132333435360210805000020B0100000000000440101001";
	public final static int CMD_CARD_PIN6 = 0x1000019;
	

	/**卡余额*/
	public final static String DIRECT_CMD_CARD_BALANCE = "8019810700A40000023F00830700A400000210010405805C000204";
	public final static int CMD_CARD_BALANCE = 0x100001;

	/**卡号*/
	public final static String DIRECT_CMD_CARD_NUMBER = "8010810700A40000021001020500B095002B";
	public final static int CMD_CARD_NUMBER = 0x100002;

	/**验证第三步*/
	public final static int CMD_VERITY_C2 = 0x100003;
	
	/**圈存 初始化*/
	public final static int CMD_DEPOSIT_INIT = 0x100004;
	
	/**圈存 写卡*/
	public final static int CMD_DEPOSIT_WRITE = 0x100005;
	
	/**圈存 写卡2*/
	public final static int CMD_DEPOSIT_WRITE2 = 0x100006;
	
	/**圈存 半条流水3001*/
	public final static int CMD_DEPOSIT_HALF_1 = 0x100007;
	
	/**file0019文件*/
	public final static String DIRECT_CMD_FILE_0019_1 = "8019810700A40000021001820700A40000020019030500B201CC00";
	public final static int CMD_FILE_0019_1 = 0x100008;

	public final static String DIRECT_CMD_FILE_0019_2 = "8019810700A40000021001820700A40000020019030500B202CC00";
	public final static int CMD_FILE_0019_2 = 0x100009;

	/**file0008文件*/
	public final static String DIRECT_CMD_FILE_0008_1 = "8019810700A40000021001820700A40000020008030500B0000064";
	public final static int CMD_FILE_0008_1 = 0x100010;

	public final static String DIRECT_CMD_FILE_0008_2 = "8019810700A40000021001820700A40000020008030500B000641C";
	public final static int CMD_FILE_0008_2 = 0x100011;
	
	
//	8019810700A40000021001820700A40000020009030500B0000064
//	8019810700A40000021001820700A40000020009030500B0006464
//	8019810700A40000021001820700A40000020009030500B000C864
//	8019810700A40000021001820700A40000020009030500B0012C64
//	8019810700A40000021001820700A40000020009030500B0019064
//	8019810700A40000021001820700A40000020009030500B001F40C
	
	/**file0009文件*/
	public final static String DIRECT_CMD_FILE_0009_1 = "8019810700A40000021001820700A40000020009030500B0000064";
	public final static int CMD_FILE_0009_1 = 0x100012;
	public final static String DIRECT_CMD_FILE_0009_2 = "8019810700A40000021001820700A40000020009030500B0006464";
	public final static int CMD_FILE_0009_2 = 0x100013;
	public final static String DIRECT_CMD_FILE_0009_3 = "8019810700A40000021001820700A40000020009030500B000C864";
	public final static int CMD_FILE_0009_3 = 0x100014;
	public final static String DIRECT_CMD_FILE_0009_4 = "8019810700A40000021001820700A40000020009030500B0012C64";
	public final static int CMD_FILE_0009_4 = 0x100015;
	public final static String DIRECT_CMD_FILE_0009_5 = "8019810700A40000021001820700A40000020009030500B0019064";
	public final static int CMD_FILE_0009_5 = 0x100016;
	public final static String DIRECT_CMD_FILE_0009_6 = "8019810700A40000021001820700A40000020009030500B001F40C";
	public final static int CMD_FILE_0009_6 = 0x100017;
	
	
	
	
	

}
