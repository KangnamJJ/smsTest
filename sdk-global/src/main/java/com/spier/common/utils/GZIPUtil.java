package com.spier.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * GZIP工具，用于压缩、解压缩gzip格式的文件和数据、检查文件/数据是否是gzip格式的
 * @author GHB
 * @version 1.0
 * @date 2017.8.22
 */
public class GZIPUtil {

	/**
	 * 解压缩gzip格式的文件
	 * @param zipFilePath 要解压缩的文件路径
	 * @param destPath 解压缩后的目的路径
	 * @return 可能为null
	 */
	public static String gunzipFile(String zipFilePath, String destPath) {
		if(StringUtils.isEmpty(zipFilePath)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "zipFilePath is empty, cannot unzip");
			return null;
		}
		
		if(!FileUtils.fileExists(zipFilePath)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"the file[{0}] does not exist, cannot unzip.", zipFilePath));
			return null;
		}
		
		if(StringUtils.isEmpty(destPath)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"destPath is empty, cannot unzip file[{0}]", zipFilePath));
			return null;
		}
		
		// 如果目标文件或目录已经存在，则删除
		if(FileUtils.fileExists(destPath)) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"dest file[{0}] exists, now delete it.", destPath));
			if(!FileUtils.deleteFile(destPath)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"failed to delete dest file[{0}], cannot unzip [{1}]", 
						destPath, zipFilePath));
				return null;
			}
		} else if(FileUtils.directoryExists(destPath)) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"dest dir[{0}] exists, now delete it.", destPath));
			if(!FileUtils.deleteDirectoryAndFile(destPath)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"failed to delete dest dir[{0}], cannot unzip [{1}]", 
						destPath, zipFilePath));
				return null;
			}
		}
		
		// 检查是否是gzip格式的
		if(!isGZIPFormat(zipFilePath)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"文件【{0}】不是gzip格式的，无法解压缩！", zipFilePath));
			return null;
		}
		
		String res = null;
		
		// 解压缩
		GZIPInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new GZIPInputStream(new FileInputStream(zipFilePath));
			out = new FileOutputStream(destPath);
			
			byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
			
			res = destPath;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 解压缩gzip格式的数据
	 * @param gzipData 要解压缩的数据
	 * @return 解压后的数据，如果解压成功就返回解压后的数据，否则返回null
	 */
	public static byte[] gunzipData(byte[] gzipData) {
		if(null == gzipData) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "gzipData is null!");
			return null;
		}
		
		if(!isGZIPFormat(gzipData)) {
			Logger.getAnonymousLogger().log(Level.INFO, "数据不是gzip格式的，解压失败");
			return null;
		}
		
		GZIPInputStream in = null;
		ByteArrayOutputStream baos = null;
		byte[] res = null;
		try {
			in = new GZIPInputStream(new ByteArrayInputStream(gzipData));
			baos = new ByteArrayOutputStream();
			
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = in.read(buf)) > 0) {
				baos.write(buf, 0, len);
			}
			
			res = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 用gzip压缩数据
	 * @param rawData 原始数据
	 * @return 压缩过的数据，或原有数据
	 */
	public static byte[] gzipData(byte[] rawData) {
		if(null == rawData) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "要压缩的数据为null!");
			return rawData;
		}
		
		byte[] res = null;
		GZIPOutputStream gos = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		
		try {
			bais = new ByteArrayInputStream(rawData);
			baos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(baos);
			
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = bais.read(buf)) >= 0) {
				gos.write(buf, 0, len);
			}
			
			// 必须调用，否则数据拿不全
			gos.finish();
			gos.flush();
			
			res = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null!= bais) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != gos) {
				try {
					gos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 用gzip压缩文件
	 * @param srcFile 原始文件
	 * @param destFile 压缩后的文件路径
	 * @return 压缩后的文件路径，返回null表示压缩失败
	 */
	public static String gzipFile(String srcFile, String destFile) {
		if(StringUtils.isEmpty(srcFile)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "srcFile is empty, cannot compress file with gzip!");
			return null;
		}
		
		if(!FileUtils.fileExists(srcFile)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"原始文件【{0}】不存在， 压缩失败！", srcFile));
			return null;
		}
		
		if(StringUtils.isEmpty(destFile)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"destFile is empty, cannot compress file[{0}]", srcFile));
			return null;
		}
		
		if(FileUtils.fileExists(destFile)) {
			if(!FileUtils.deleteFile(destFile)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"destFile[{0}] exists, but cannot delete it, failed to compress file[{1}] with gzip.", 
						destFile, srcFile));
				return null;
			}
		} else if(FileUtils.directoryExists(destFile)) {
			if(!FileUtils.deleteDirectoryAndFile(destFile)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"destFile[{0}] exists, but cannot delete it, failed to compress file[{1}] with gzip.", 
						destFile, srcFile));
				return null;
			}
		}
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		GZIPOutputStream gos = null;
		
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			gos = new GZIPOutputStream(fos);
			
			byte[] buf = new byte[1024];
			int len = 0;
			
			while((len = fis.read(buf)) > 0) {
				gos.write(buf, 0, len);
			}
			
			// 必须调用，否则数据不全
			gos.finish();
			gos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return destFile;
	}
	
	/**
	 * 检查数据是否是gzip格式的
	 * @param data 要检查的数据
	 * @return 
	 */
	public static boolean isGZIPFormat(byte[] data) {
		if(null == data) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "data is null!");
			return false;
		}
		
		if(data.length < 2) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "data length must be greater than 2!");
			return false;
		}
		
		int code = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
		
		return code == GZIPInputStream.GZIP_MAGIC;
	}
	
	/**
	 * 检查文件是不是gzip压缩的
	 * @param file 文件路径
	 * @return
	 * @throws IllegalArgumentException 参数file为空或null；文件中未读取到数据
	 * @throws IllegalAccessError 文件不存在
	 */
	public static boolean isGZIPFormat(String file) throws IllegalArgumentException, IllegalAccessError {
		if(StringUtils.isEmpty(file)) {
			throw new IllegalArgumentException("param 'file' is empty");
		}
		
		if(!FileUtils.fileExists(file)) {
			throw new IllegalAccessError(MessageFormat.format("file[{0}] does not exist!", file));
		}
		
		long fileLength = new File(file).length();
		if(fileLength < 2) {
			Logger.getAnonymousLogger().log(Level.INFO, "the file under GZIP format must be larger than 2 bytes");
			return false;
		}
		
		FileInputStream fis = null;
		byte[] buf = new byte[2];
		try {
			fis = new FileInputStream(file);
			fis.read(buf);
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
		
		return isGZIPFormat(buf);
	}
}
