package com.spier.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 请求最外层的数据类型
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 */
public class SafeRequestOutlayerObj implements Serializable{
	private static final long serialVersionUID = 6776815731615849301L;

	/**
	 * 渠道号，明文
	 */
	@Expose
	@SerializedName("c")
	public String mChannelId;
	
	/**
	 * AES秘钥被加密后的密文
	 */
	@Expose
	@SerializedName("k")
	public String mAesKeyCipher;
	
	/**
	 * 业务数据，被AES明文的秘钥加密过，加密前是json
	 */
	@Expose
	@SerializedName("d")
	public String mBussinessData;
	
}