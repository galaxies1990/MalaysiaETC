package com.uroad.malaysiaetc.quancun;

import java.io.Serializable;

/**
 * 通过蓝牙模块获取卡数据
 * 
 * @author Administrator
 *
 */
public class TopUpCardInfo implements Serializable {

	private static final long serialVersionUID = 478844449070225568L;

	/**发卡方标识*/
	private String cardFrom;
	/** 卡区域，在空中请求某个请求接口时，需要用到 */
	private String cardArea;

	private String cardNumber;//不包含卡片网络编号

	/** 卡日期 */
	private String validity;

	/** 车牌号 可能为空 */
	private String plateNum;
	
	private String startDate;
	private String endDate;
	
	private String cardType;
	private String cardVersion;
	/**用户类型*/
	private String userType;
	/**车牌颜色*/
	private String plateColor;
	/**车型*/
	private String carType;
	
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardVersion() {
		return cardVersion;
	}

	public void setCardVersion(String cardVersion) {
		this.cardVersion = cardVersion;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	

	public TopUpCardInfo() {
		super();
	}

	public String getCardArea() {
		return cardArea;
	}

	public void setCardArea(String cardArea) {
		this.cardArea = cardArea;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	@Override
	public String toString() {
		return "TopUpCardInfo [cardArea=" + cardArea + ", cardNumber="
				+ cardNumber + ", validity=" + validity + ", plateNum="
				+ plateNum + "]";
	}

	public String getCardFrom() {
		return cardFrom;
	}

	public void setCardFrom(String cardFrom) {
		this.cardFrom = cardFrom;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

}
