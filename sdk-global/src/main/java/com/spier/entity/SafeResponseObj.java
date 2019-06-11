package com.spier.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.common.config.ErrorCodes;

/**
 * 响应最外层的数据类型
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 */
public class SafeResponseObj implements Serializable{
	private static final long serialVersionUID = -8691246316780594085L;

	/**
	 * 错误码，见{@link ErrorCodes}
	 */
	@Expose
	@SerializedName("cd")
	public int mErrorCode;
	
	/**
	 * 响应数据
	 */
	@Expose
	@SerializedName("c")
	public String mResponseContent;
}