package com.uroad.malaysiaetc.quancun;

public interface Code {
	
	/** 圈存正常 */
	public static final int STATE_NORMAL = 0;
	/** 圈存半条流水 */
	public static final int STATE_EXCEPTION = 1;
	/** 圈存写卡失败 */
	public static final int STATE_WRITE_FAILE = 2;
	
	
	/**00为正常*/
	public static final String CODE_00 = "00";
	
	/**51 未半条流水 上一次圈存异常*/
	public static final String CODE_51 = "51";
	
	/**61 是否存在写卡失败记录，如果是，返回该单据  上一次写卡失败*/
	public static final String CODE_61 = "61";
	
	/**73 是否存在写卡失败记录，如果是，返回该单据 上一次写卡失败*/
	public static final String CODE_73 = "73";
	
	/**00代表没电，非00代表有电*/
	public static final String NO_POWER = "00";
	
	/**0x00表示正常返回；其他表示错误*/
	public static final int RESP_00 = 0x00;

	/**ICC复位响应握手响应   返回读卡器中的ICC卡复位操作结果 返回握手信息*/
	public static final int RESP_B2 = 0xB2;
	
	/**ICC通道指令响应   返回读卡器中的ICC卡通道操作结果*/
	public static final int RESP_B3 = 0xB3;

	/**读卡器通道指令响应 返回读卡器通道操作结果*/
	public static final int RESP_B5 = 0xB5;
	
	/**读卡器通道指令响应 返回读卡器认证结果*/
	public static final int RESP_B6 = 0xB6;
	
	
	
}
