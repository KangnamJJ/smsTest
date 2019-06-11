package com.spier.common.http;

/** 
 * HTTP请求模式
* @ClassName: HttpMode 
* @author GHB
* @date 2015年5月23日 下午12:09:32 
*/
public enum HttpMode {
	Get(1), Post(2);

	private int Code;

	private HttpMode(int code) {
		this.Code = code;
	}

	public int getCode() {
		return this.Code;
	}

	@Override
	public String toString() {
		if(this.getCode() == Get.getCode()) {
			return "GET";
		} else {
			return "POST";
		}
	}
	
	
}