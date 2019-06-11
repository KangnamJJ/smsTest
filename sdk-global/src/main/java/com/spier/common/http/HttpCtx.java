package com.spier.common.http;

import java.io.Serializable;
import java.text.MessageFormat;


/** 
 * HTTP请求参数类<br>
 * 修改记录：<br>
 * 修改人：GHB<br>
 * 修改时间：2016.1.15<br>
 * 修改内容：添加默认请求超时时间，产生对象时，如果没有设定请求超时时间，则默认为20s<br>
 * 版本号：1.1<br>
 * 修改人：GHB<br>
 * 修改时间：2016.2.15<br>
 * 修改内容：<br>
 * 修改原有的timeout为connecttimeout，以及其getters和setters方法；<br>
 * 增加readtimeout属性及其getters和setters方法。<br>
 * 版本号：1.2<br>
 * 修改人：GHB<br>
 * 修改时间：2016.3.2<br>
 * 修改内容：增加4个参数的构造方法，添加读超时参数。并将其他构造方法调用到此方法，默认设置20s的读超时时间。<br>
 * 版本号：1.3<br>
* @ClassName: HttpCtx 
* @author GHB
* @date 2015年7月8日 上午12:53:31 
*  @version 1.3
*/
public class HttpCtx implements Serializable{
	private static final long serialVersionUID = -6203601941529781761L;

	/**
	 * 默认超时时间，5s
	 * @since 1.1
	 */
	public static final int DEFAULT_TIMEOUT = 5 * 1000;
	
	/**
	 * 
	 * @param connectTimeout 连接超时
	 * @param readTimeout 读超时
	 * @param agent
	 * @param mode
	 */
	public HttpCtx(int connectTimeout, int readTimeout, String agent, HttpMode mode) {
		mTimeout = connectTimeout;
		mReadTimeout = readTimeout;
		mAgent = agent;
		mHttpMode = mode;
	}
	/**
	 * 
	 * @param timeout 连接超时
	 * @param agent
	 * @param mode get post
	 */
	public HttpCtx(int timeout, String agent, HttpMode mode) {
		this(timeout, DEFAULT_TIMEOUT, agent, mode);
	}

	/**
	 * 
	 * @param timeout 连接超时
	 * @param agent
	 */
	public HttpCtx(int timeout, String agent) {
		this(timeout, agent, HttpMode.Get);
	}

	public HttpCtx() {
		this(DEFAULT_TIMEOUT, null, HttpMode.Get);
	}

	/** 连接超时 */
	int mTimeout;

	/**
	 * 获得连接超时时间，以豪秒为单位，最小为100，小于100等同于无超时
	 * @return
	 */
	public int getTimeout() {
		return mTimeout;
	}

	/**
	 * 设置连接超时时间
	 * @param v
	 */
	public void setTimeout(int v) {
		mTimeout = v;
	}
	
	/** 读取超时 */
	int mReadTimeout;
	
	public int getReadTimeout() {
		return mReadTimeout;
	}
	
	public void setReadTimeout(int value) {
		mReadTimeout = value;
	}

	String mAgent;

	public String getAgent() {
		return mAgent;
	}

	public void setAgent(String v) {
		mAgent = v;
	}

	HttpMode mHttpMode;

	public HttpMode getHttpMode() {
		return mHttpMode;
	}

	public void setHttpMode(HttpMode v) {
		mHttpMode = v;
	}
	
	String mCharset = DEFAULT_CHARSET;
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public String getCharset() {
		return mCharset;
	}
	
	public void setCharset(String charset) {
		mCharset = charset;
	}

	private boolean mFollowRedirect;
	
	/**
	 * 设置是否自动跟踪重定向
	 * @param follow
	 */
	public void setFollowRedirect(boolean follow) {
		mFollowRedirect = follow;
	}
	
	/**
	 * 是否自动跟踪重定向
	 * @return
	 */
	public boolean willFollowRedirect() {
		return mFollowRedirect;
	}
	
	private static HttpCtx _Default;

	public static HttpCtx getDefault() {
		if (null == _Default) {
			synchronized (HttpCtx.class) {
				if (null == _Default) {
					_Default = new HttpCtx();
				}
			}
		}
		return _Default;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(MessageFormat.format("connect timeout: {0}", getTimeout()));
		
		buffer.append("\n");
		buffer.append(MessageFormat.format("read timeout: {0}", getReadTimeout()));
		
		buffer.append("\n");
		buffer.append(MessageFormat.format("agent: {0}", getAgent()));
		
		buffer.append("\n");
		buffer.append(MessageFormat.format("http mode: {0}", getHttpMode()));
		
		buffer.append("\n");
		buffer.append(MessageFormat.format("charset: {0}", getCharset()));
		
		return buffer.toString();
	}
	
	
}
