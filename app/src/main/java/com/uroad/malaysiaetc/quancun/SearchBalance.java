package com.uroad.malaysiaetc.quancun;

import java.io.Serializable;

/**余额查询*/
public class SearchBalance implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5949692971400143916L;
	
	public String deviceNum;
	public String cardNum;
	public String balance;
	public String plateNum;
	public String validity;
}
