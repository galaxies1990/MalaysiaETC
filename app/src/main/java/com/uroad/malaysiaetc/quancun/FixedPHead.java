package com.uroad.malaysiaetc.quancun;

/**
 * 定长包头
 * 
 * @author Administrator
 * @category http://iot.weixin.qq.com/document-6_2.html
 *
 */
public class FixedPHead {

	/** bMagicNumber unsigned char 填0xFE */
	public byte bMagicNumber;

	/** bVer unsigned char 包格式版本号，填1 */
	public byte bVer;

	/** nLength unsigned short 为包头+包体的长度 */
	public int nLength;

	/**
	 * nCmdId unsigned short
	 * 命令号，如ECI_req_auth，ECI_resp_sendDataToManufacturerSvr等
	 */
	public int nCmdId;

	/** nSeq unsigned short 递增。一个Req对应一个Resp，并且它们的nSeq相同，并且永不为0。 Push的nSeq永远为0； */
	public int nSeq;

	@Override
	public String toString() {
		return "FixedPHead [bMagicNumber=" + bMagicNumber + ", bVer=" + bVer
				+ ", nLength=" + nLength + ", nCmdId=" + nCmdId + ", nSeq="
				+ nSeq + "]";
	}

}
