package com.uroad.malaysiaetc.quancun;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 命令回传数据
 * @author Administrator
 *
 */
public class CmdReceiveData implements Serializable {

	private static final long serialVersionUID = -3682286427764881166L;

	private int cmdType;

	private byte[] data;

	public CmdReceiveData() {
		super();
	}
	
	public CmdReceiveData(int cmdType, byte[] data) {
		super();
		this.cmdType = cmdType;
		this.data = data;
	}

	public int getCmdType() {
		return cmdType;
	}

	public void setCmdType(int cmdType) {
		this.cmdType = cmdType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CmdGetData [cmdType=" + cmdType + ", data="
				+ Arrays.toString(data) + "]";
	}

}
