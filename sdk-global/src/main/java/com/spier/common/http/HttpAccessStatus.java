package com.spier.common.http;

/** 
* @ClassName: HttpAccessStatus 
* @author GHB
* @date 2015年6月12日 上午2:56:01 
*  
*/
public enum HttpAccessStatus {
	Timeout(-2),Error( -1), NotDone(0), Done(1);

	private int Code;

	private HttpAccessStatus(int code) {
		this.Code = code;
	}

	public int getCode() {
		return this.Code;
	}

	public static HttpAccessStatus valueOf(int value) {
		switch (value) {
		case -1:
			return Error;
		case 0:
			return NotDone;
		case 1:
			return Done;
		default:
			return null;
		}
	}

}
