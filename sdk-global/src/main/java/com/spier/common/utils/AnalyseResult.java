package com.spier.common.utils;

import java.io.Serializable;

import com.spier.common.utils.RequestAnalyseUtil.CodecResult;

/**
 * 安全接口解析结果
 * @author GHB
 * @version 1.0
 * @date 2018.12.18
 * @param <RequestType> 请求数据结构类型
 * @param <ResponseType> 响应数据结构类型
 */

public class AnalyseResult<RequestType, ResponseType> extends CodecResult implements Serializable{
	private static final long serialVersionUID = 3634686544072236770L;
	/**
	 * 渠道号
	 */
	public String mChanId;
	/**
	 * 请求中解析得出的对象
	 */
	public RequestType mRequestObj;
	/**
	 * 响应数据的对象
	 */
	public ResponseType mResponseObj;
	
	public static <RequestType, ResponseType> AnalyseResult<RequestType, ResponseType> getDefaultObj() {
		return new AnalyseResult<RequestType, ResponseType>();
	}
}
