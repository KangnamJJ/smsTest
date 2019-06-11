package com.spier.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Strings;
import com.google.common.io.Files;


public class HttpUtils {

    private static final int CONNECT_TIMEOUT = 6000;
    private static final int READ_TIMEOUT = 6000;

    /** Http InputStream 每次读入的字节数 */
    private static final int READ_TEMP_SIZE = 1024;

    /** http result */
    public static class HttpResult {
        /** http status code */
        private int code = -1;
        /** http response body */
        private String body;

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }

    /**
     * parameter url encode
     * @param str default using UTF-8
     * @return
     */
    public static String parameterUrlEncode(String str) {
        return parameterUrlEncode(str, "UTF-8");
    }

    /**
     * parameter url encode
     * @param str
     * @param charset
     * @return
     */
    public static String parameterUrlEncode(String str, String charset) {
        try {
            return URLEncoder.encode(str, charset);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String post(String url, Map<String, String> headers, Map<String, String> parameters) {
        URL postUrl = null;
        HttpURLConnection connection = null;
        DataOutputStream out = null;
        OutputStream outs = null;

        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader reader = null;
        try {
            postUrl = new URL(url);
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。

            if(null != parameters && 0 < parameters.size()) {
                StringBuilder buf = new StringBuilder();
                parameters.forEach((k, v) -> {
                    try {
                        buf.append(k).append('=').append(URLEncoder.encode(v, "UTF-8")).append('&');
                    } catch (Exception e) {
                    }
                });

                outs = connection.getOutputStream();
                out = new DataOutputStream(outs);
                out.writeBytes(buf.toString());
            }

            int code = connection.getResponseCode();
            if(200 == code) {
                in = connection.getInputStream();
                inr = new InputStreamReader(in);
                reader = new BufferedReader(inr);

                String line;
                StringBuilder buf = new StringBuilder();
                while ((line = reader.readLine()) != null){
                    buf.append(line);
                }

                return null != line && line.length()==buf.length()? line : buf.toString();
            }

        } catch (Exception e) {
        } finally {
            try {
                if(null != out) out.close();
                if(null != outs) outs.close();

                if(null != reader) reader.close();
                if(null != inr) inr.close();
                if(null != in) in.close();
                if(null != connection) connection.disconnect();
            } catch (Exception e2) {
            }
        }

        return null;
    }

    public static String post(String url, String body, boolean zip) {
        URL _url = null;
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if(! Strings.isNullOrEmpty(body)) {
                byte[] bytes = body.getBytes();
                if(zip) bytes = GzipUtils.compress(bytes);
                if(zip) conn.setRequestProperty("content-encoding", "gzip");
                out = conn.getOutputStream();
                out.write(bytes);
            }

            int code = conn.getResponseCode();
            if(200 == code) {
                in = conn.getInputStream();
                int len = conn.getHeaderFieldInt("Content-Length", 1024);
                byte[] resp = new byte[len];
                in.read(resp, 0, len);

                String encoding = conn.getHeaderField("Content-Encoding");
                if(null != encoding && "gzip".equalsIgnoreCase(encoding)) {
                    resp = GzipUtils.decompress(resp);
                }
                return new String(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != in) in.close();
                if(null != out) out.close();
                if(null != conn) conn.disconnect();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * <p>Http Get Method
     *
     * @param url
     * @return response body
     */
    public static String get(String url) {
        return getMethodWithResult(url).getBody();
    }

    public static String get(String url, Integer connectTimeout, Integer readTimeout) {
        return getMethodWithResult(url, null, null, connectTimeout, readTimeout).getBody();
    }

    public static String get(String url, Map<String, String> headers, Map<String, String> parameters) {
        return getMethodWithResult(url, headers, parameters, null, null).getBody();
    }

    public static HttpResult getMethodWithResult(String url) {
        return getMethodWithResult(url, null, null, null, null);
    }

    public static HttpResult getMethodWithResult(String url,Integer connectTimeout, Integer readTimeout) {
        return getMethodWithResult(url, null, null, connectTimeout, readTimeout);
    }

    public static HttpResult getMethodWithResult(String url, Map<String, String> headers, Map<String, String> parameters,
                                                 Integer connectTimeout, Integer readTimeout) {
        URL _url = null;
        HttpURLConnection conn = null;
        InputStream in = null;

        ByteArrayOutputStream byteStream = null;

        HttpResult result = new HttpResult();
        try {

            ///
            if(null != parameters && 0 < parameters.size()) {
                char ending = url.charAt(url.length() - 1);
                if(-1 == url.indexOf("?")) url += '?';
                else if('?' != ending && '&' != ending) url += '&';

                for(Map.Entry<String, String> entry : parameters.entrySet()) {
                    url += entry.getKey() + '=' + parameterUrlEncode(entry.getValue()) + '&';
                }

                url = url.substring(0, url.length() - 1); // remove the last char '&'
            }

            _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();

            ///
            if(null != headers && 0 < headers.size()) {
                for(Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if(url.indexOf("https")>=0) {
                //创建SSLContext
                SSLContext sslContext=SSLContext.getInstance("SSL");
                TrustManager[] tm={new MyX509TrustManager()};
                //初始化
                sslContext.init(null, tm, new java.security.SecureRandom());;
                //获取SSLSocketFactory对象
                SSLSocketFactory ssf=sslContext.getSocketFactory();
                HttpsURLConnection connection = (HttpsURLConnection) _url.openConnection();
                connection.setConnectTimeout((null==connectTimeout||1>connectTimeout)? CONNECT_TIMEOUT : connectTimeout);
                connection.setReadTimeout((null==readTimeout||1>readTimeout)? READ_TIMEOUT : readTimeout);
                connection.setDoOutput(Boolean.FALSE);
                connection.setDoInput(Boolean.TRUE);
                connection.setUseCaches(Boolean.FALSE);
                connection.setSSLSocketFactory(ssf);

                int code = connection.getResponseCode();
                result.setCode(code);
                result.setBody(connection.getResponseMessage());
                if(200 == code) {
                    in = connection.getInputStream();
                    byteStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[READ_TEMP_SIZE];
                    int len = 0;
                    while((len = in.read(bytes, 0, READ_TEMP_SIZE)) != -1) {
                        byteStream.write(bytes, 0, len);
                    }
                    result.setBody(new String(byteStream.toByteArray()));
                }
                return result;
            }

            conn.setConnectTimeout((null==connectTimeout||1>connectTimeout)? CONNECT_TIMEOUT : connectTimeout);
            conn.setReadTimeout((null==readTimeout||1>readTimeout)? READ_TIMEOUT : readTimeout);
            conn.setDoOutput(Boolean.FALSE);
            conn.setDoInput(Boolean.TRUE);
            conn.setUseCaches(Boolean.FALSE);

            int code = conn.getResponseCode();
            result.setCode(code);
            result.setBody(conn.getResponseMessage());
            if(200 == code) {
                in = conn.getInputStream();
                byteStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[READ_TEMP_SIZE];
                int len = 0;
                while((len = in.read(bytes, 0, READ_TEMP_SIZE)) != -1) {
                    byteStream.write(bytes, 0, len);
                }
                result.setBody(new String(byteStream.toByteArray()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != byteStream)
                try { byteStream.close();
                } catch (IOException e1) { }
            if(null != in)
                try { in.close();
                } catch (IOException e) { }
            if(null != conn) conn.disconnect();
        }
        return result;
    }

    public static byte[] getBytes(String url,Integer connectTimeout, Integer readTimeout) {
        URL _url = null;
        HttpURLConnection conn = null;
        InputStream in = null;
        ByteArrayOutputStream byteStream = null;
        try {
            _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();

            conn.setConnectTimeout((null==connectTimeout||1>connectTimeout)? CONNECT_TIMEOUT : connectTimeout);
            conn.setReadTimeout((null==readTimeout||1>readTimeout)? READ_TIMEOUT : readTimeout);
            conn.setDoOutput(Boolean.FALSE);
            conn.setDoInput(Boolean.TRUE);
            conn.setUseCaches(Boolean.FALSE);

            if(200 == conn.getResponseCode()) {
                in = conn.getInputStream();
                byteStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[READ_TEMP_SIZE];
                int len = 0;
                while((len = in.read(bytes, 0, READ_TEMP_SIZE)) != -1) {
                    byteStream.write(bytes, 0, len);
                }
                bytes = byteStream.toByteArray();
                return bytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != byteStream)
                try {
                    byteStream.close();
                } catch (IOException e) {}
            if(null != in)
                try {
                    in.close();
                } catch (IOException e) {}
            if(null != conn) conn.disconnect();
        }
        return null;
    }

    public static byte[] getBytes(String url) {
        URL _url = null;
        HttpURLConnection conn = null;
        InputStream in = null;
        ByteArrayOutputStream byteStream = null;
        try {
            _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            if(200 == conn.getResponseCode()) {
                in = conn.getInputStream();
                byteStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[READ_TEMP_SIZE];
                int len = 0;
                while((len = in.read(bytes, 0, READ_TEMP_SIZE)) != -1) {
                    byteStream.write(bytes, 0, len);
                }
                bytes = byteStream.toByteArray();
                return bytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != byteStream)
                try {
                    byteStream.close();
                } catch (IOException e) {}
            if(null != in)
                try {
                    in.close();
                } catch (IOException e) {}
            if(null != conn) conn.disconnect();
        }
        return null;
    }

    /**
     * file download
     * @param url
     * @return
     */
    public static File download(String url) {
        File target = null;
        byte[] bytes = getBytes(url);
        if(null != bytes) {
            int index = url.lastIndexOf("/");
            String filename = (0 < index)? url.substring(index + 1) : String.valueOf(System.nanoTime());
            target = new File("/tmp/" + filename);
            try {
                Files.write(bytes, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return target;
    }


    public static HttpResult getMethodWithResultByHTTPBasicAuth(String url, Map<String, String> headers, Map<String, String> parameters,
                                                                Integer connectTimeout, Integer readTimeout,String username,String password) {
        URL _url = null;
        HttpURLConnection conn = null;
        InputStream in = null;

        ByteArrayOutputStream byteStream = null;

        HttpResult result = new HttpResult();
        try {

            ///
            if(null != parameters && 0 < parameters.size()) {
                char ending = url.charAt(url.length() - 1);
                if(-1 == url.indexOf("?")) url += '?';
                else if('?' != ending && '&' != ending) url += '&';

                for(Map.Entry<String, String> entry : parameters.entrySet()) {
                    url += entry.getKey() + '=' + parameterUrlEncode(entry.getValue()) + '&';
                }

                url = url.substring(0, url.length() - 1); // remove the last char '&'
            }

            _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            String authString = username + ":" + password;

            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());

            String authStringEnc = new String(authEncBytes);
            conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

            ///
            if(null != headers && 0 < headers.size()) {
                for(Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            conn.setConnectTimeout((null==connectTimeout||1>connectTimeout)? CONNECT_TIMEOUT : connectTimeout);
            conn.setReadTimeout((null==readTimeout||1>readTimeout)? READ_TIMEOUT : readTimeout);
            conn.setDoOutput(Boolean.FALSE);
            conn.setDoInput(Boolean.TRUE);
            conn.setUseCaches(Boolean.FALSE);

            int code = conn.getResponseCode();
            result.setCode(code);
            result.setBody(conn.getResponseMessage());
            if(200 == code) {
                in = conn.getInputStream();
                byteStream = new ByteArrayOutputStream();

                byte[] bytes = new byte[READ_TEMP_SIZE];
                int len = 0;
                while((len = in.read(bytes, 0, READ_TEMP_SIZE)) != -1) {
                    byteStream.write(bytes, 0, len);
                }
                result.setBody(new String(byteStream.toByteArray()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != byteStream)
                try { byteStream.close();
                } catch (IOException e1) { }
            if(null != in)
                try { in.close();
                } catch (IOException e) { }
            if(null != conn) conn.disconnect();
        }
        return result;
    }
}
