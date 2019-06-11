package com.spier.common.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;


/**
 * 消息摘要工具
 * @author GHB
 * @version 1.0
 * @date 2018.10.19
 */
public class MessageDigestUtil {

	/**
	 * MD5算法：{@value}
	 */
	public static final String M_DIGEST_ALGORITHM_MD5 = "MD5";
	/**
	 * sha1算法：{@value}
	 */
	public static final String M_DIGEST_ALGORITHM_SHA1 = "SHA1";
	
	/**
	 * 获取数据的消息摘要
	 * @param data 原始数据
	 * @param algorithm 摘要算法，@see {@link M_DIGEST_ALGORITHM_MD5}
	 * @return 数据的消息摘要，可能为null
	 */
	public static byte[] getDigest(byte[] data, String algorithm) {
		if(null == data) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "原始数据为null，无法计算消息摘要");
			return null;
		}
		
		if(StringUtils.isEmpty(algorithm)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "算法为空，无法计算消息摘要");
			return null;
		}
		
		byte[] res = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(data);
			res = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 获取MD5的消息摘要
	 * @param data
	 * @return 可能为null
	 */
	public static byte[] getMD5Digest(byte[] data) {
		return getDigest(data, M_DIGEST_ALGORITHM_MD5);
	}
	
	/**
	 * 获取SHA1的消息摘要
	 * @param data
	 * @return 可能为null
	 */
	public static byte[] getSHA1Digest(byte[] data) {
		return getDigest(data, M_DIGEST_ALGORITHM_SHA1);
	}
	
	private static byte[] getFileDigest(String file, String algorithm) {
		if(StringUtils.isEmpty(file)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "path is empty, cannot calculate its digest.");
			return null;
		}
		
		if(StringUtils.isEmpty(algorithm)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("algorithm is empty, cannot calculate the digest of file [{0}]", file));
			return null;
		}
		
		byte[] res = null;
		MessageDigest digest = null;
		FileInputStream fis = null;
		try {
			digest = MessageDigest.getInstance(algorithm);
			fis = new FileInputStream(file);
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = fis.read(buffer, 0, buffer.length)) > 0) {
                digest.update(buffer, 0, len);
			}
			
			res = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 获取文件的md5消息摘要
	 * @param file 文件路径
	 * @return 可能为null
	 */
	public static byte[] getFileDigestMD5(String file) {
		return getFileDigest(file, M_DIGEST_ALGORITHM_MD5);
	}
	
	/**
	 * 获取文件的sha1消息摘要
	 * @param file 文件路径
	 * @return 可能为null
	 */
	public static byte[] getFileDigestSHA1(String file) {
		return getFileDigest(file, M_DIGEST_ALGORITHM_SHA1);
	}
}
