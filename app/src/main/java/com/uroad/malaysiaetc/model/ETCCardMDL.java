package com.uroad.malaysiaetc.model;

import java.io.Serializable;

public class ETCCardMDL implements Serializable {
	// "status": "OK",
	// "data": [
	// {
	// "accountNo": "43031310161200000004",
	// "accountType": "10",
	// "balance": 1,
	// "status": "NORMAL",
	// "bankname": "长沙银行",
	// "bankicon":
	// "http://test.u-road.com/HuNanGSTAppAPIServer/usericon/20160428122326909.jpg",
	// "statusname": "正常"
	// }
	// ]
	public String accountName;
	public String accountNo;
	public String accountType;
	public String balance;
	public String status;
	public String statusname;
	public String bankname;
	public String bankicon;
	// 充值上下限
	public String uplimit;
	public String downlimit;
	// 修复金额
	public String amount;
	// 返回类型 REPAIR_AMOUNT：修复
	public String respType;

	// 是否选中
	public boolean isCheck;

	public String cardNo;
	public String vehiclePlate;
	public String vehicleColor;
	public String vehicleClass;

	private String cardid;
	private String carno;
	private String color;
	private String cardtype;
	private String fee;
	private boolean check;
	private String loadamount;
	private String cardaccountid;

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getCarno() {
		return carno;
	}

	public void setCarno(String carno) {
		this.carno = carno;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCardtype() {
		return cardtype;
	}

	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getLoadamount() {
		return loadamount;
	}

	public void setLoadamount(String loadamount) {
		this.loadamount = loadamount;
	}

	public String getCardaccountid() {
		return cardaccountid;
	}

	public void setCardaccountid(String cardaccountid) {
		this.cardaccountid = cardaccountid;
	}

}
