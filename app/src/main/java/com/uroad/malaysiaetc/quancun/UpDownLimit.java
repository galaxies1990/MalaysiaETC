package com.uroad.malaysiaetc.quancun;

import java.io.Serializable;

/**圈存上下限*/
public class UpDownLimit implements Serializable {

	private static final long serialVersionUID = 5434017643951341019L;
	
	/**状态码*/
	private String code;
	
	/**账户余额（单位：分）*/
	private String amount;
	
	/**下限（单位：分）*/
	private String rechargeDownLimit;
	
	/**上限（单位：分）*/
	private String rechargeUpperLimit;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRechargeDownLimit() {
		return rechargeDownLimit;
	}

	public void setRechargeDownLimit(String rechargeDownLimit) {
		this.rechargeDownLimit = rechargeDownLimit;
	}

	public String getRechargeUpperLimit() {
		return rechargeUpperLimit;
	}

	public void setRechargeUpperLimit(String rechargeUpperLimit) {
		this.rechargeUpperLimit = rechargeUpperLimit;
	}

	@Override
	public String toString() {
		return "UpDownLimit [code=" + code + ", amount=" + amount
				+ ", rechargeDownLimit=" + rechargeDownLimit
				+ ", rechargeUpperLimit=" + rechargeUpperLimit + "]";
	}
	
}