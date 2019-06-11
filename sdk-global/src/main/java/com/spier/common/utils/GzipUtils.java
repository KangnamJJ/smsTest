package com.spier.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GzipUtils {

    private static final int BUFFER = 1024;

    /**
     * 数据压缩
     *
     * @param data
     * @return
     */
    public static byte[] compress(byte[] data) {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;

        byte[] output = null;
        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();
            // 压缩
            compress(bais, baos);
            output = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != baos)
                try { baos.flush();
                } catch (IOException e) {}
            if(null != baos)
                try { baos.close();
                } catch (IOException e) {}

            if(null != baos)
                try { bais.close();
                } catch (IOException e) {}
        }
        return output;
    }

    /**
     * 数据压缩
     *
     * @param data
     * @return
     */
    public static void compress(InputStream is, OutputStream os)
            throws Exception {

        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(os);

            int count;
            byte data[] = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }

            gos.finish();
        } catch (Exception e) {
            throw e;
        } finally {
            if(null != gos) gos.flush();
            if(null != gos) gos.close();
        }

    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();
            // 解压缩
            decompress(bais, baos);
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != baos)
                try {
                    baos.flush();
                } catch (IOException e) {}
            if(null != baos)
                try {
                    baos.close();
                } catch (IOException e) {}

            if(null != bais)
                try {
                    bais.close();
                } catch (IOException e) {}
        }
        return data;
    }

    /**
     * 数据解压缩
     *
     * @param is
     * @param os
     */
    public static void decompress(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
        } catch (Exception e) {
        } finally {
            if(null != gis) try {
                gis.close();
            } catch (Exception e2) {}
        }
    }


    public static void main(String[] args) {
        byte[] bytes = "{\"mob_id\":\"test\"}".getBytes();
        bytes = GzipUtils.compress(bytes);
        URL url = null;
        try {
//			url = new URL("http://test.ichestnut.net:8080/refer/v1/fetch");
            url = new URL("http://test.ichestnut.net:8080/refer/v1/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-encoding", "gzip");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);

            int code = conn.getResponseCode();
            System.out.println("code = " + code);

            InputStream in = conn.getInputStream();
            int len = conn.getHeaderFieldInt("Content-Length", 1024);
            byte[] resp = new byte[len];
            in.read(resp, 0, len);

            String encoding = conn.getHeaderField("Content-Encoding");
            if(null != encoding && "gzip".equalsIgnoreCase(encoding)) {
                resp = GzipUtils.decompress(resp);
            }
            System.out.println(new String(resp));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
