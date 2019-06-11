package com.spier.common.http;

import java.nio.charset.Charset;

/** 
 * HTTP接口<br>
 * 修改记录：<br>
 * 修改人：GHB<br>
 * 修改时间：2015.12.30<br>
 * 修改内容：增加http底层错误信息的设置和获取方法<br>
 * 版本号：1.1<br>
 * 
 * 修改人：GHB<br>
 * 修改时间：2016.7.27<br>
 * 修改内容：增加http头信息的添加、获取接口<br>
 * 版本号：1.2<br>
 * 
 * 修改人：GHB<br>
 * 修改时间：2016.9.1<br>
 * 修改内容：增加http访问错误类型<br>
 * 版本号：1.3<br>
 * 
 * 修改人：GHB<br>
 * 修改时间：2018.3.10<br>
 * 修改内容：从websafe包下转移到http包下。为了保证兼容，原有的代码予以保留，并打上了废弃标识<br>
 * 版本号：1.4
 * 
* @author GHB
* @date 2015年11月20日 下午3:53:19 
*  @version 1.4
*/
public interface IHttp {

	/*request para*/
	String getUrl();

	void setUrl(String v);

	String getPara();

	void setPara(String v);

	/*提交*/
	void access(HttpCtx ctx);

	void access();
	
	public void accessInternet(HttpCtx ctx);

	/*response para*/

	boolean isNetworkError();

	boolean isHttpStatusError();

	boolean isSucceed();

	/*
	 * http访问完成情况
	 */
	HttpAccessStatus getHttpAccessStatus();

	void setHttpAccessStatus(HttpAccessStatus v);

	/*
	 * http消耗的时间，毫秒数
	 */
	long getElapsedTime();

	void setElapsedTime(long v);

	HttpStatus getHttpStatus();

	void setHttpStatus(HttpStatus v);

	byte[] getResult();

	void setResult(byte[] v);

	String getHtml(Charset charset);

	String getHtml();

	String getErrorStr();
	
	/**
	 * 设置http请求过程中，由底层返回的错误信息，在jni层调用
	 * @param msg
	 * @since 1.1
	 */
	void setHttpAccessErrMsg(byte[] msg);
	
	/**
	 * 获取http请求过程中，底层返回的错误信息
	 * @return 错误信息或null
	 * @since 1.1
	 */
	byte[] getHttpAccessErrMsg();
	
	/**
	 * 设置头信息
	 * @param headerName 头信息名称
	 * @param headerValue 头信息值
	 */
	public void addHeader(String headerName, String headerValue);
	
	/**
	 * 获取头信息
	 * @param headerName 头信息名称
	 * @return 可能为null
	 */
	public String getHeader(String headerName);
	
	/**
	 * 设置http错误类型
	 * @param type
	 * @since 1.3
	 */
	public void setHttpErrorType(HttpErrorType type);
	
	/**
	 * 获取http错误类型
	 * @return 不为null
	 */
	public HttpErrorType getHttpErrorType();
}
