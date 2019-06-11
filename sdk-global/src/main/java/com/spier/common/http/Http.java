package com.spier.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.utils.GZIPUtil;

/**
 * 实现http请求
 * @author GHB
 * @date 2018.3.10
 * @version 1.0
 */
public class Http implements IHttp,Serializable{
	private static final long serialVersionUID = 4768252373953324457L;

	public Http(String url, String para) {
		this.mUrl = url;
		this.mPara = para;
		
		setHttpErrorType(HttpErrorType.HTTP_ERR_NONE);
	}

	public Http(String url) {
		this(url, null);
	}

	public final static Charset DEFAUTL_CHARSET = Charset.forName("UTF-8");

	String mUrl;

	@Override
	public String getUrl() {
		return mUrl;
	}

	@Override
	public void setUrl(String v) {
		mUrl = v;
	}

	String mPara;

	@Override
	public String getPara() {
		return mPara;
	}

	@Override
	public void setPara(String v) {
		mPara = v;
	}

	@Override
	public void access(HttpCtx ctx) {
		accessInternet(ctx);
	}

	@Override
	public void access() {
		access(HttpCtx.getDefault());
	}

	@Override
	public boolean isNetworkError() {
		return HttpAccessStatus.Done != getHttpAccessStatus();
	}

	@Override
	public boolean isHttpStatusError() {
		if(getHttpStatus() == HttpStatus.OK) {
			return false;
		}
		
		boolean res = true;
		
		switch(getHttpStatus().getCode()) {
			case HttpURLConnection.HTTP_MOVED_TEMP:
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_SEE_OTHER:
				res = false;
			break;
		}
		
		return res;
	}

	@Override
	public boolean isSucceed() {
		return !isNetworkError() && !isHttpStatusError();
	}

	HttpAccessStatus mHttpAccessStatus;

	@Override
	public HttpAccessStatus getHttpAccessStatus() {
		return mHttpAccessStatus;
	}

	@Override
	public void setHttpAccessStatus(HttpAccessStatus v) {
		mHttpAccessStatus = v;
	}

	long mElapsedTime;

	/*
	 * http消耗的时间，毫秒数
	 */
	@Override
	public long getElapsedTime() {
		return mElapsedTime;
	}

	@Override
	public void setElapsedTime(long v) {
		mElapsedTime = v;
	}

	HttpStatus mHttpStatus;

	@Override
	public HttpStatus getHttpStatus() {
		return mHttpStatus;
	}

	@Override
	public void setHttpStatus(HttpStatus v) {
		mHttpStatus = v;
	}

	byte[] mResult;

	@Override
	public byte[] getResult() {
		return mResult;
	}

	@Override
	public void setResult(byte[] v) {
		mResult = v;
	}

	@Override
	public String getHtml(Charset charset) {
		if (null == getResult()) {
			return null;
		}
		return new String(getResult(), charset);
	}

	@Override
	public String getHtml() {
		return getHtml(DEFAUTL_CHARSET);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("url [");
		sb.append(getUrl());
		sb.append("]\r\n");

		sb.append("para [");
		sb.append(getPara());
		sb.append("]\r\n");

		sb.append("HttpAccessStatus [");
		sb.append(getHttpAccessStatus());
		sb.append("]\r\n");

		sb.append("ElapsedTime [");
		sb.append(String.valueOf(getElapsedTime()));
		sb.append("ms]\r\n");

		sb.append("HttpStatus [");
		sb.append(getHttpStatus());
		sb.append("]\r\n");

		sb.append("Html [");
		sb.append(getHtml());
		sb.append("]\r\n");

		sb.append("HttpAccessErrorMsg [");
		sb.append((null == getHttpAccessErrMsg() ? "null" : new String(
				getHttpAccessErrMsg())));
		sb.append("]\r\n");
		
		sb.append("Headers [");
		if(null != mHeaders) {
			for(Entry<String, String> entry : mHeaders.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				
				sb.append(MessageFormat.format("{0}={1},", key, value));
			}
		}
		sb.append("]\r\n");

		return sb.toString();
	}

	@Override
	public String getErrorStr() {
		if (isSucceed()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Url [");
		sb.append(getUrl());
		sb.append("] HttpAccessStatus [");
		if (null != getHttpAccessStatus()) {
			sb.append(getHttpAccessStatus().getCode());
			sb.append(":");
		}
		sb.append(getHttpAccessStatus());

		sb.append("] HttpStatus [");
		if (null != getHttpStatus()) {
			sb.append(getHttpStatus().getCode());
			sb.append(":");
		}
		sb.append(getHttpStatus());
		sb.append("] HttpAccessErrorMsg [");

		if (null == getHttpAccessErrMsg()) {
			sb.append("null]");
		} else {
			sb.append(new String(getHttpAccessErrMsg()));
			sb.append("]");
		}

		return sb.toString();
	}

	private String mErrMsg;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.dxy.websafe.IHttp#setHttpAccessErrMsg(byte[])
	 */
  @Override
  public void setHttpAccessErrMsg(byte[] msg) {
  	mErrMsg = (null == msg) ? null : new String(msg);
  }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.dxy.websafe.IHttp#getHttpAccessErrMsg()
	 */
	@Override
	public byte[] getHttpAccessErrMsg() {
		return (null == mErrMsg) ? null : mErrMsg.getBytes();
	}

	// 使用java的方式做http请求
	@Override
	public void accessInternet(HttpCtx ctx) {
		if (getUrl() == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "url is null, failed to accessInternet");
			return;
		}

		// charset
		String charset = HttpCtx.DEFAULT_CHARSET;
		if (null != ctx) {
			charset = ctx.getCharset();
		}

		// timeout
		int connectTimeout = (ctx == null) ? HttpCtx.DEFAULT_TIMEOUT : ctx
				.getTimeout();
		int readTimeout = (ctx == null) ? HttpCtx.DEFAULT_TIMEOUT : ctx
				.getReadTimeout();

		// 请求方式
		HttpMode requestMode = (ctx == null) ? HttpMode.Post : ctx
				.getHttpMode();

		// 是否自动重定向
		boolean autoRedirect = (ctx == null) ? false : ctx.willFollowRedirect();
		
		// url
		String surl = getUrl();
		if (getPara() != null && requestMode == HttpMode.Get) {
			surl += "?" + getPara();
		}

		// 请求前的时间记录
		long timeBeforeRequest = System.currentTimeMillis();

		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		OutputStream os = null;
		ByteArrayOutputStream baos = null;

		try {
			// url
			url = new URL(surl);
			conn = (HttpURLConnection) url.openConnection();

			conn.setAllowUserInteraction(true);
			
			// 设置自动跳转
			HttpURLConnection.setFollowRedirects(autoRedirect);
			conn.setInstanceFollowRedirects(autoRedirect);
			
			// 字符集
			if (null != charset) {
				conn.setRequestProperty("Accept-Charset", charset);
				conn.setRequestProperty("contentType", charset);
			} else {
				conn.setRequestProperty("Accept-Charset", "UTF-8");
				conn.setRequestProperty("contentType", "UTF-8");
			}
			
			// 设置headers
			setHeadersToConnection(conn, ctx);

			// 设置超时
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);

			// 设置请求方式
			conn.setRequestMethod(requestMode.toString());

			// 如果是post，则需要写参数
			if (requestMode == HttpMode.Post) {
				conn.setDoOutput(true);
			} else if (requestMode == HttpMode.Get) {
				conn.setDoOutput(false);
			}
			
			// 写参数
			if (requestMode == HttpMode.Post && !StringUtils.isEmpty(getPara())) {
				os = conn.getOutputStream();
				os.write(getPara().getBytes());
				os.flush();
			}
						
			// 获取响应码并处理重定向
//			conn.connect();
			int respCode = conn.getResponseCode();
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("测试连接的状态码：{0}", respCode));
			
			if (respCode == HttpURLConnection.HTTP_MOVED_TEMP
		            || respCode == HttpURLConnection.HTTP_MOVED_PERM
		                || respCode == HttpURLConnection.HTTP_SEE_OTHER) {
				// 不做后续处理，只在finally段保存头信息和cookies信息
				setHttpStatus(HttpStatus.valueOf(respCode));
				setHttpAccessStatus(HttpAccessStatus.Done);
				return;
			}
			
			// 获取响应码
//			respCode = conn.getResponseCode();
			setHttpStatus(HttpStatus.valueOf(respCode));
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("连接url【{0}】的状态码：【{1}】", surl, respCode));
			if (respCode != HttpURLConnection.HTTP_OK) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"连接url【{0}】返回状态码【{1}】，请求失败！", surl, respCode));
				return;
			}

			// 接收，改为ByteArrayOutputStream更好！
			baos = new ByteArrayOutputStream();
			is = conn.getInputStream();
			
			int count = 0;
			byte[] buf = new byte[1024];
			while((count = is.read(buf)) >= 0) {
				baos.write(buf, 0, count);
			}
			
			baos.flush();
			
			byte[] data = baos.toByteArray();
			
			if(GZIPUtil.isGZIPFormat(data)) {
				Logger.getAnonymousLogger().log(Level.INFO, "服务端响应数据为gzip格式，解压缩");
				byte[] unzippedData = GZIPUtil.gunzipData(data);
				if(null == unzippedData) {
					Logger.getAnonymousLogger().log(Level.SEVERE, "gzip解压缩失败，使用原有数据");
					setResult(data);
				} else {
					Logger.getAnonymousLogger().log(Level.INFO, "gzip解压缩成功，设置解压后的数据");
					setResult(unzippedData);
				}
			} else {
				Logger.getAnonymousLogger().log(Level.INFO, "服务端响应数据不是gzip格式，无需解压缩");
				setResult(data);
			}
			
			setHttpAccessStatus(HttpAccessStatus.Done);
			
//			mHeaderFields = conn.getHeaderFields();
		} catch (MalformedURLException e) {
			String errMsg = MessageFormat.format(
					"the url [{0}] is in wrong format ", url);
			
			e.printStackTrace();
			Logger.getAnonymousLogger().log(Level.SEVERE, errMsg);
			
			setHttpAccessStatus(HttpAccessStatus.NotDone);
			setHttpStatus(HttpStatus.NotAccess);
			setHttpAccessErrMsg(errMsg.getBytes());
			setHttpErrorType(HttpErrorType.HTTP_ERR_URL);
		} catch (SocketTimeoutException e) {
			String errMsg = MessageFormat.format(
					"failed to connect to the url [{0}], the reasons are follows: {1}", 
					url, e.getMessage());
			
			e.printStackTrace();
			Logger.getAnonymousLogger().log(Level.SEVERE, errMsg);
			
			setHttpAccessStatus(HttpAccessStatus.NotDone);
			setHttpStatus(HttpStatus.NotAccess);
			setHttpAccessErrMsg(errMsg.getBytes());
			setHttpErrorType(HttpErrorType.HTTP_ERR_TIMEOUT);
		} catch (IOException e) {
			String errMsg = MessageFormat.format(
					"failed to connect to or read from the url [{0}], the reasons are follows: {1}", 
					url, e.getMessage());
			
			e.printStackTrace();
			Logger.getAnonymousLogger().log(Level.SEVERE, errMsg);
			
			setHttpAccessStatus(HttpAccessStatus.NotDone);
			setHttpStatus(HttpStatus.NotAccess);
			setHttpAccessErrMsg(errMsg.getBytes());
			setHttpErrorType(HttpErrorType.HTTP_ERR_IO);
		} catch(Exception e) {
			String errMsg = MessageFormat.format(
					"failed to connect to the url [{0}], the reasons are follows: {1}", 
					url, e.getMessage());
			
			e.printStackTrace();
			Logger.getAnonymousLogger().log(Level.SEVERE, errMsg);
			
			setHttpAccessStatus(HttpAccessStatus.NotDone);
			setHttpStatus(HttpStatus.NotAccess);
			setHttpAccessErrMsg(errMsg.getBytes());
			setHttpErrorType(HttpErrorType.HTTP_ERR_OTHERS);
		} finally {
			setElapsedTime(System.currentTimeMillis() - timeBeforeRequest);
			// 保存头信息
			mHeaderFields = conn.getHeaderFields();
			
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != conn) {
				conn.disconnect();
			}
		}
	}
	
	private Map<String, List<String>> mHeaderFields;
	
	/**
	 * 获取头信息，请求前后可能不一样
	 * @return 可能为null
	 */
	public Map<String, List<String>> getHeaderFields() {
		return mHeaderFields;
	}

	private void setHeadersToConnection(HttpURLConnection conn, HttpCtx ctx) {
		if(null == conn) {
			return;
		}
		
		// 设置本类的头
		if(null != getHeaderMap()) {
			for(Entry<String, String> entry : getHeaderMap().entrySet()) {
				String headerName = entry.getKey();
				String headerValue = entry.getValue();
				
				if(StringUtils.isEmpty(headerName) || StringUtils.isEmpty(headerValue)) {
					continue;
				}
				
				conn.setRequestProperty(headerName, headerValue);
			}
		}
		
		// 设置ctx中的头：user-agent
		if(null != ctx) {
			if(!StringUtils.isEmpty(ctx.mAgent)) {
				getHeaderMap().put("User-Agent", ctx.mAgent);
				conn.setRequestProperty("User-Agent", ctx.mAgent);
			}
		}
	}
	
	private Map<String, String> mHeaders = new HashMap<String, String>();

	private Map<String, String> getHeaderMap() {
		return mHeaders;
	}
	
	/* (non-Javadoc)
	 * @see net.dxy.websafe.IHttp#addHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHeader(String headerName, String headerValue) {
		if(StringUtils.isEmpty(headerName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "header name is empty, failed to add http header.");
			return;
		}
		
		if(StringUtils.isEmpty(headerValue)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "header value is empty, failed to add http header.");
			return;
		}
		
		if(null == getHeaderMap()) {
			mHeaders = new HashMap<String, String>();
		}
		
		getHeaderMap().put(headerName, headerValue);
	}

	/* (non-Javadoc)
	 * @see net.dxy.websafe.IHttp#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String headerName) {
		if(StringUtils.isEmpty(headerName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "header name is empty, failed to get http header.");
			return null;
		}
		
		if(null == getHeaderMap()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no http header found, null returned.");
			return null;
		}
		
		return getHeaderMap().get(headerName);
	}

	private HttpErrorType mHttpErrType;
	
	@Override
	public void setHttpErrorType(HttpErrorType type) {
		mHttpErrType = type;
	}

	@Override
	public HttpErrorType getHttpErrorType() {
		return mHttpErrType;
	}
	
	
}
