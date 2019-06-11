/*
 * Copyright: Guangzhou Junbo Network Tech. Co., Ltd.
 * Author: GHB
 * Date: 2015年9月25日
 * Description：文件相关的工具类，此文件最终要同既有文件工具类融合。
 */

package com.spier.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * 与文件相关的操作类<br>
 * 方法全部都是类的静态方法，无需构造对象<br>
 * 修改记录：<br>
 * 1.移除deleteAll 内的new NullPointerException<br>
 * 2.移除deleteFile 抛异常的信息<br>
 * 修改时间：2015/10/23<br>
 * 修改内容:<br>
 * 移除deleteFile()抛异常的信息<br>
 * 修改人:zhouh<br>
 * 版本号：V1.2<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2015年11月25日<br>
 * 修改内容：整合潘总的IoUtils类的内容<br>
 * 版本号：V1.3<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2015年12月7日<br>
 * 修改内容：添加阻塞式锁文件的方法：lockFileBlocked<br>
 * 版本号：1.4<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2016.8.23<br>
 * 修改内容：修改deleteFile方法和deleteAll方法，在删除前先改名。这样做是避免安卓操作fat32文件系统的bug（删除后无法重建同名的文件）<br>
 * 版本号：1.5<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2016.9.18<br>
 * 修改内容：修改createFile方法，在覆盖处理部分，原有的删除操作之后会返回true，这是一个bug。会导致原文件删除，新文件未创建的bug。本次修改修复了此bug<br>
 * 版本号：1.6<br>
 * <br>
 * 修改人：GHB<br>
 * 修改时间：2016.11.7<br>
 * 修改内容：修改方法renameFile()，将原有的“目标文件存在就不重命名”改为“目标文件存在，则删除后再重命名”<br>
 * 版本号：1.7<br>
 * <br>
 * @waring 本类还未经过完整的单元测试
 * @author GHB
 * @date 2015年9月26日
 * @version 1.7
 */
public class FileUtils {

	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * 私有构造器，提示用户使用时直接用类的静态方法即可
	 */
	private FileUtils() {

	}

	/**
	 * 读取文件，并返回数据。适用于文本文件或非文本文件，如二进制文件等。
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件数据
	 */
	public static byte[] readfile(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	/**
	 * 检查某个路径指向的文件是否是文件
	 * 
	 * @param path
	 *            绝对路径
	 * @return
	 */
	public static boolean isFile(String path) {
		return isFile(new File(path));
	}

	/**
	 * 检查传入的参数是否是文件
	 * 
	 * @param file
	 * @return true--存在且是文件；false--参数为null、文件不存在或文件是目录
	 */
	public static boolean isFile(File file) {
		if (null == file) {
			return false;
		} else if (!file.exists()) {
			return false;
		} else {
			return file.isFile();
		}
	}

	/**
	 * 检查某个路径指向的文件是否是一个目录
	 * 
	 * @param path
	 *            绝对路径
	 * @return true--存在且是目录；false--参数为null、不存在或不是目录
	 */
	public static boolean isDirectory(String path) {
		if (null == path) {
			return false;
		} else {
			return isDirectory(new File(path));
		}
	}

	/**
	 * 检查传入的参数是否是目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean isDirectory(File dir) {
		return exists(dir) && dir.isDirectory();
	}

	/**
	 * 检查文件或目录是否存在
	 * 
	 * @param path
	 *            绝对路径
	 * @return 是否存在
	 */
	public static boolean exists(String path) {
		if (null == path) {
			return false;
		} else {
			return exists(new File(path));
		}
	}

	/**
	 * 检查文件是否存在，此处的文件是广义上的文件，包括狭义的文件和目录
	 * 
	 * @param file
	 * @return
	 */
	public static boolean exists(File file) {
		if (null == file) {
			return false;
		} else {
			return file.exists();
		}
	}

	/**
	 * 文件是否存在<br>
	 * 此处会检查两个内容：是否存在，是否是文件
	 * 
	 * @param path
	 *            绝对路径
	 * @return 是否存在
	 */
	public static boolean fileExists(String path) {
		return exists(path) && isFile(path);
	}

	/**
	 * 检查文件是否存在，且不是目录
	 * 
	 * @param file
	 * @return
	 */
	public static boolean fileExists(File file) {
		if (null == file) {
			return false;
		} else {
			return exists(file) && isFile(file);
		}
	}

	/**
	 * 目录是否存在
	 * 
	 * @param path
	 *            绝对路径
	 * @return 是否存在
	 */
	public static boolean directoryExists(String path) {
		return exists(path) && isDirectory(path);
	}

	/**
	 * 目录是否存在
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean directoryExists(File dir) {
		return exists(dir) && isDirectory(dir);
	}

	/**
	 * 根据文件名获取目录路径，此处的文件为广义的文件，即狭义的文件和目录
	 * 
	 * @param path
	 *            文件绝对路径
	 * @return 目录名称或null（文件不存在）
	 */
	public static String getDirFromFilePath(String path) {
		String result = null;

		if (null == path) {
			return result;
		}

		File f = new File(path);
		result = f.getParent();

		return result;
	}

	/**
	 * 根据文件或目录的绝对路径获取文件名
	 * 
	 * @param abPath
	 *            绝对路径
	 * @return 文件名或null
	 */
	public static String getFileNameFromFileAbPath(String abPath) {
		String name = null;

		if (null == abPath || abPath.isEmpty()) {
			return name;
		}

		File f = new File(abPath);
		return f.getName();
	}

	/**
	 * 创建目录，如果目录已存在，则不删除。
	 * 
	 * @author GHB
	 * @param path
	 *            文件夹路径
	 * @return 操作是否成功
	 */
	public static boolean createDirectory(String path) {
		return createDirectory(path, false);
	}

	/**
	 * 创建文件夹，默认不覆盖已存在的目录
	 * 
	 * @author eric
	 * @param path
	 *            文件夹路径
	 * @param orIfExists
	 *            如果目标已经存在，是否覆盖
	 * @return 操作是否成功
	 */
	public static boolean createDirectory(String path, boolean orIfExists) {

		if (null == path || path.trim().isEmpty()) {
			return false;
		}

		return createDirectory(new File(path), orIfExists);
	}

	/**
	 * 创建文件夹，方法重载
	 * 
	 * @author eric
	 * @param file
	 * @param orIfExists
	 *            如果目标已经存在，是否覆盖
	 * @return 是否创建成功
	 */
	public static boolean createDirectory(File file, boolean orIfExists) {
		if (file == null) {
			return false;
		}

		// 要创建的目录已经存在，但是一个普通文件
		if (fileExists(file)) {
			return false;
		}

		// 要创建的目录已经存在
		if (directoryExists(file)) {
			if (orIfExists) {
				if(!deleteDirectoryAndFile(file.getAbsolutePath())) {
					return false;
				}
				
				return file.mkdirs();
			} else {
				return true;
			}
		}

		// 如果创建的目录不存在
		
		return file.mkdirs();
	}

	/**
	 * 创建普通文件（非目录），默认不覆盖同名文件<br>
	 * 
	 * @author eric
	 * @param path
	 *            文件路径
	 * @return 是否创建成功
	 * @see #createFile(File, boolean)
	 */
	public static boolean createFile(String path) {
		if (null == path || path.trim().isEmpty()) {
			return false;
		}

		return createFile(new File(path));
	}

	/**
	 * 创建普通文件（非目录），默认不覆盖同名文件<br>
	 * 修改记录：<br>
	 * 修改人：GHB<br>
	 * 修改时间：2015年10月2日<br>
	 * 修改内容： 1.File.getParentFile()的返回值有可能为空，对此做出处理
	 * 
	 * @author eric
	 * @param file
	 * @return 是否创建成功
	 * @see #createFile(File, boolean)
	 */
	public static boolean createFile(File file) {
		if (file == null) {
			return false;
		}

		return createFile(file, false);
	}

	/**
	 * 创建文件，狭义上的文件
	 * 
	 * @param file
	 *            文件
	 * @param override
	 *            如果存在是否覆盖
	 * @return 操作是否成功
	 */
	public static boolean createFile(File file, boolean override) {
		if (file == null) {
			return false;
		}

		File parentDir = file.getParentFile();
		if (null == parentDir) {
			return false;
		}
		
		if(!directoryExists(parentDir)) {
			if(fileExists(parentDir)) {
				if(!deleteFile(parentDir.getAbsolutePath())) {
					return false;
				}
			}
			
			if(!createDirectory(parentDir, false)) {
				return false;
			}
		}

		// 如果是目录，且已经存在，则无法处理，创建失败
		if (directoryExists(file)) {
			return false;
		}

		// 如果文件已经存在，则或者删除重新创建，或者使用旧的
		if (fileExists(file)) {
			if (override) {
				deleteFile(file.getAbsolutePath());
			} else {
				return true;
			}
		}

		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 创建文件，狭义上的文件
	 * @param filePath 文件路径
	 * @param override 是否覆盖已有文件
	 * @return 操作是否成功
	 */
	public static boolean createFile(String filePath, boolean override) {
		return createFile(new File(filePath), override);
	}

	/**
	 * 删除普通文件<br>
	 * 由于安卓系统对fat32文件系统删除后可能不能再创建同名文件的bug，这里需要在删除前先改名
	 * @param filePath
	 *            文件路径
	 * @return 是否删除成功
	 */
	public static boolean deleteFile(String filePath) {
		if(StringUtils.isEmpty(filePath)) {
			return false;
		}

		File file = new File(filePath);

		if (fileExists(file)) {
			return file.delete();
		}

		if (directoryExists(file)) {
			return false;
		}

		return false;
	}

	
	/**
	 * 删除普通文件和目录
	 * @param filePath 文件路径
	 * @return 操作是否成功
	 */
	public static boolean deleteDirectoryAndFile(String filePath) {
		if(StringUtils.isEmpty(filePath)) {
			return false;
		}

		File file = new File(filePath);
		if (!file.exists()) {
			return true;
		}

		if (file.isFile()) {
			return deleteFile(filePath);
		}

		if(filePath.endsWith(File.separator)) {
			filePath = filePath.substring(0, filePath.length() - 1);
		}

		String[] tempList = file.list();
		if (tempList != null && tempList.length > 0) {
			for (int i = 0; i < tempList.length; i++) {
				deleteDirectoryAndFile(filePath + File.separator + tempList[i]);
			}
		}

		// 删除目录
		File rnmFile = new File(filePath + System.currentTimeMillis());
		file.renameTo(rnmFile);
		return rnmFile.delete();
	}
	
	/**
	 * 非阻塞式锁定文件
	 * 
	 * @param fos
	 *            文件输出流
	 * @return 锁对象，以便释放时使用
	 * @throws IOException
	 */
	public static FileLock lockFile(FileOutputStream fos) throws IOException {
		FileLock flock = null;

		flock = fos.getChannel().tryLock();
		if (null == flock || !flock.isValid()) {
			flock = null;
		}

		return flock;
	}

	/**
	 * 非阻塞式锁定文件
	 * 
	 * @param raf
	 *            RandomAccessFile
	 * @return 锁对象，以便释放时使用
	 * @throws IOException
	 */
	public static FileLock lockFile(RandomAccessFile raf) throws IOException {
		FileLock flock = null;

		flock = raf.getChannel().tryLock();
		if (null == flock || !flock.isValid()) {
			flock = null;
		}

		return flock;
	}

	/**
	 * 文件锁释放
	 * 
	 * @param lock
	 * @throws IOException
	 */
	public static void releaseFileLock(FileLock lock) throws IOException {
		if (lock != null && lock.isValid()) {
			lock.release();
		}
	}

	/**
	 * 实现普通文件的拷贝操作
	 * 
	 * @param srcPath
	 *            源文件的绝对路径
	 * @param dstPath
	 *            目的文件的绝对路径
	 * @param override
	 *            目标文件已经存在，是否覆盖；如果不覆盖，则中止拷贝操作
	 * @return 操作是否成功
	 */
	public static boolean copyFile(String srcPath, String dstPath, boolean override) {
		if(StringUtils.isEmpty(srcPath)) {
			return false;
		}

		if (StringUtils.isEmpty(dstPath)) {
			return false;
		} 

		if(StringUtils.equals(srcPath, dstPath)) {
			return false;
		}

		boolean opResult = false;

		// 检查源文件是否存在
		if (!fileExists(srcPath) || !isFile(srcPath)) {
			return opResult;
		}

		// 目的文件已经存在，且是一个目录
		if (directoryExists(dstPath)) {
			return opResult;
		}

		// 目的文件已存在，是一个普通文件
		if (fileExists(dstPath)) {
			if (override) {
				deleteFile(dstPath);
			} else {
				return true;
			}
		} else {
			if(!createFile(dstPath)) {
				return false;
			}
		}

		DataInputStream dis = null;
		DataOutputStream dos = null;

		try {
			dis = new DataInputStream(new FileInputStream(new File(srcPath)));
			dos = new DataOutputStream(new FileOutputStream(new File(dstPath)));

			int byteRead = 0;
			byte[] buffer = new byte[1024];
			while ((byteRead = dis.read(buffer)) > 0) {
				dos.write(buffer, 0, byteRead);
			}

			opResult = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			opResult = false;
		} catch (IOException e) {
			e.printStackTrace();
			opResult = false;
		} finally {
			if (null != dis) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != dos) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return opResult;
	}

	/**
	 * 复制文件夹，也支持文件复制
	 * 
	 * @author eric
	 * @param oldPath
	 * @param newPath
	 * @param orIfExists
	 *            如果目标文件已经存在，是否覆盖
	 * @return 操作是否成功
	 */
	public static boolean copyDir(String oldPath, String newPath,
			boolean orIfExists) {
		boolean result = true;

		if(StringUtils.isEmpty(oldPath)) {
			return false;
		}
		
		if(StringUtils.isEmpty(newPath)) {
			return false;
		}
		
		if(StringUtils.equals(oldPath, newPath)) {
			return false;
		}

		File old = new File(oldPath);
		if (!old.exists()) {
			return false;
		}

		if (old.isFile()) {
			result &= copyFile(oldPath, newPath, orIfExists);
		} else if (old.isDirectory()) {
			if (!oldPath.endsWith(File.separator)) {
				oldPath = oldPath + File.separator;
			}

			if (!newPath.endsWith(File.separator)) {
				newPath = newPath + File.separator;
			}

			if (!createDirectory(newPath, orIfExists)) {
				return false;
			}

			String[] files = old.list();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					boolean tmp = copyDir(oldPath + files[i], newPath
							+ files[i], orIfExists);
					if (tmp) {
					} else {
						result = false;
						break;
					}

					result &= copyDir(oldPath + files[i], newPath + files[i],
							orIfExists);
				}
			}

		}

		return result;
	}

	/**
	 * 实现文件移动，移动过程中文件名可以随意指定，但必须是原路径和目的路径必须是绝对路径<br>
	 * 如果源文件和目的文件位于同一目录下，则实现了重命名的功能。
	 * 
	 * @param srcPath
	 *            源路径
	 * @param dstPath
	 *            目的路径
	 * @param deleteIfExist
	 *            如果目的文件已经存在，是否覆盖
	 * @return 操作是否成功
	 */
	public static boolean moveFile(String srcPath, String dstPath,
			boolean deleteIfExist) {
		boolean result = true;

		if(StringUtils.isEmpty(srcPath)) {
			return false;
		}
		
		if(StringUtils.isEmpty(dstPath)) {
			return false;
		}
		
		if(StringUtils.equals(srcPath, dstPath)) {
			return false;
		}

		result &= copyFile(srcPath, dstPath, deleteIfExist);

		if (result) {
			deleteFile(srcPath);
		}

		return result;
	}

	/**
	 * 将文件夹移动到新的地址
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static void moveFolder(String oldPath, String newPath,
			boolean override) {

		if (copyDir(oldPath, newPath, override)) {
			deleteDirectoryAndFile(oldPath);
//			deleteAll(oldPath);
		}
	}

	/**
	 * 将内容追加到文件末尾，如果文件不存在则创建新文件
	 * 
	 * @param filePath
	 *            文件绝对路径
	 * @param content
	 *            要追加的内容
	 * @return 操作是否成功
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static boolean appendContent2File(String filePath, String content)
			throws IOException {

		if(StringUtils.isEmpty(filePath)) {
			return false;
		}

		if (null == content || content.isEmpty()) {
			return false;
		} else {
			return appendContent2File(filePath, content.getBytes());
		}
	}

	/**
	 * 将内容追加到文件末尾，如果文件不存在则创建新文件
	 * 
	 * @param filePath
	 *            文件绝对路径
	 * @param content
	 *            要追加的内容
	 * @return 操作是否成功
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static boolean appendContent2File(String filePath, byte[] content)
			throws IOException {
		boolean result = false;

		if(StringUtils.isEmpty(filePath)) {
			return false;
		}

		if (null == content) {
			return result;
		} else if (content.length == 0) {
			return true;
		} else if (isDirectory(filePath)) {
			return result;
		}

		if (!fileExists(filePath)) {
			createFile(filePath);
		}

		RandomAccessFile rfile = null;
		try {
			rfile = new RandomAccessFile(filePath, "rw");
			long len = rfile.length();
			rfile.seek(len);
			rfile.write(content);

			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != rfile) {
				rfile.close();
			}
		}

		return result;
	}

	/**
	 * 把数据写入文件的某个位置（覆盖），如果文件不存在，则创建新文件并从头写入；如果欲写入文件位置超过了文件大小，则实现追加
	 * 
	 * @author GHB
	 * @param filePath
	 *            文件绝对路径
	 * @param content
	 *            要写入的内容
	 * @param pos
	 *            要写入的位置
	 * @param erase
	 *            如果写入的内容未达到文件末尾，是否擦除文件后面的内容
	 * @return 操作是否成功
	 * @throws IOException
	 */
	public static boolean writeContent2FileAt(String filePath, byte[] content,
			long pos, boolean erase) throws IOException {
		boolean result = false;

		if(StringUtils.isEmpty(filePath)) {
			return false;
		}

		if (null == content) {
			return result;
		} else if (content.length == 0) {
			return true;
		} else if (isDirectory(filePath)) {
			return result;
		} else if (pos < 0) {
			return result;
		}

		if (!fileExists(filePath)) {
			createFile(filePath);
		}

		RandomAccessFile rfile = null;
		try {
			rfile = new RandomAccessFile(filePath, "rw");

			// 调整pos
			long position = pos > 0 ? pos : 0;
			position = pos > rfile.length() ? rfile.length() : pos;
			rfile.seek(position);
			rfile.write(content);
			// 截断
			if (erase) {
				rfile.setLength(position + content.length);
			}
			result = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (null != rfile) {
				rfile.close();
			}
		}

		return result;
	}

	/**
	 * 文件重命名<br>
	 * 
	 * @param newPath
	 *            文件新路径
	 * @param oldPath
	 *            文件旧路径
	 * @warning 路径，而非文件名！
	 */
	public static void renameFile(String newPath, String oldPath) {

		if(!fileExists(oldPath) || !isFile(oldPath)) {
			return;
		}
		
		if(StringUtils.isEmpty(newPath)) {
			return;
		}
		
		if(StringUtils.equals(oldPath, newPath)) {
			return;
		}
		
		if(fileExists(newPath)) {
			if(!deleteFile(newPath)) {
				return;
			}
		}

		File oldfile = new File(oldPath);
		File newfile = new File(newPath);
		if(!oldfile.renameTo(newfile)) {
		}
	}

	/**
	 * 列出某个目录下面所有的文件，不支持递归查询
	 * 
	 * @param dirPath
	 *            目录的路径
	 * @return 所有子文件或null
	 * @warning 功能未完成
	 */
	public static File[] listChildFiles(String dirPath) {
		File[] result = null;

		if (null == dirPath || dirPath.trim().isEmpty()) {
			return result;
		} else if (!directoryExists(dirPath)) {
			return result;
		}

		File dir = new File(dirPath);
		result = dir.listFiles();

		return result;
	}

	/**
	 * 从某个目录下查找某个名字的文件或目录
	 * 
	 * @param fileName
	 *            文件或目录名，名字而非路径
	 * @param dirPath
	 *            从那个目录下查找
	 * @return 所有符合条件的文件或目录的绝对路径或null
	 */
	public static List<String> findFileByName(final String fileName,
			String dirPath) {
		List<String> result = null;

		if (null == fileName || fileName.isEmpty()) {
			return result;
		} else if (!directoryExists(dirPath)) {
			return result;
		}

		File dir = new File(dirPath);
		File[] children = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				boolean result = false;

				if (file.isFile() && file.getName().equals(fileName)) {
					result = true;
				} else if (file.isDirectory()) { // 如果是目录，则返回给方法，以备递归
					result = true;
				}

				return result;
			}
		});

		if (children == null || children.length == 0) {
			return result;
		}

		result = new ArrayList<String>();
		for (File child : children) {
			if (null == child) {
				continue;
			}

			if (child.isDirectory()) { // 是目录，添加绝对路径并递归查找
				if (child.getName().equals(fileName)) {
					result.add(child.getAbsolutePath());
				}

				List<String> tmpResult = findFileByName(fileName,
						child.getAbsolutePath());
				if (null != tmpResult && tmpResult.size() > 0) {
					result.addAll(tmpResult);
				}
			} else if (child.isFile()) {
				result.add(child.getAbsolutePath());
			}
		}

		return result;
	}

	/**
	 * 适用于上G大的文件<br>
	 * 修改记录：<br>
	 * 修改人：GHB<br>
	 * 修改时间：2015年10月3日 修改内容：<br>
	 * 1.buffer在堆中开辟了10M空间，对于PC程序来讲问题不大，但是对于安卓程序，就比较危险了。这里修改为1K<br>
	 * 2.文件输入流的创建在try-catch块之外，无法捕获filenotfoundexception，此异常发生时将会按照IOException抛出
	 * ，修改try-catch逻辑 3.对参数做非空检查
	 * 
	 * @throws FileNotFoundException
	 *             文件不存在
	 * @throws OutOfMemoryError
	 *             内存溢出
	 */
	public static String getFileSha1(String path) throws OutOfMemoryError,
			FileNotFoundException {
		String result = null;

		if (!FileUtils.fileExists(path)) {
			return result;
		}

		File file = new File(path);
		FileInputStream in = null;
		MessageDigest messagedigest;
		try {
			in = new FileInputStream(file);
			messagedigest = MessageDigest.getInstance("SHA-1");

			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = in.read(buffer)) > 0) {
				// 该对象通过使用 update（）方法处理数据
				messagedigest.update(buffer, 0, len);
			}

			// 对于给定数量的更新数据，digest 方法只能被调用一次。在调用 digest 之后，MessageDigest
			// 对象被重新设置成其初始状态。
			result = HexUtil.hex2Str(messagedigest.digest());
//			HexEncoder he = HexEncoder.getInstance();
//			if (null != he) {
//				result = he.encode(messagedigest.digest());
//			}
			// return HexEncoder.getInstance().encode(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return result;
	}

	final static int DEFAULT_BUFFER_SIZE = 1024;

	/**
	 * 将数据写入文件——覆盖
	 * 
	 * @param datas
	 * @param file
	 */
	public static boolean writeFile(String file, String datas) {
		if(StringUtils.isEmpty(file)) {
			return false;
		}
		
		return writefile(file, null == datas ? "".getBytes() : datas.getBytes());
	}

	/**
	 * 将数据写入文件——覆盖
	 * 
	 * @param filePath
	 * @param data
	 * @return 操作是否成功
	 */
	public static boolean writefile(String filePath, byte[] data) {

		FileOutputStream fos = null;

		File f = new File(filePath);
		if (!f.exists()) {
			createFile(f);
		} else if (directoryExists(f)) {
			return false;
		}

		boolean res = false;
		try {
			fos = new FileOutputStream(filePath);
			fos.write(data);
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return res;

	}

	/**
	 * 从输入流读取数据并写入文件
	 * 
	 * @param path
	 *            要写入文件的路径
	 * @param input
	 *            输入流
	 * @param current
	 *            当前已写入的字节长度
	 * @param totleLength
	 *            总共的字节长度
	 * @return 写入数据后产生的文件引用
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @warning 此方法在操作完输入流之后会关闭输入流，调用此方法后，请勿对输入流进行操作。
	 */
	public static File writeFileFromInput(String path, InputStream input,
			long current, long totleLength) throws FileNotFoundException,
			IOException {
		File file = null;
		OutputStream output = null;
		try {
			file = new File(path);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while (!(current >= totleLength)
					&& (readLen = input.read(buffer)) != -1) {
				output.write(buffer, 0, readLen);
				current += readLen;
			}
			output.flush();
		} finally {
			if(null != output) {
				output.close();
			}
		}

		return file;
	}

	/**
	 * 将指定数据内容写入文件
	 * 
	 * @param filePath 文件路径
	 * @param datas 数据串
	 * @param append 是否追加
	 */
	public static void writeStrToFile(String filePath, String datas,
			boolean append) {
		FileOutputStream fos = null;
		if (!createFile(filePath)) {
			return;
		}
		BufferedWriter out = null;
		try {
			fos = new FileOutputStream(filePath, append);
			out = new BufferedWriter(new OutputStreamWriter(fos));
			out.write(datas);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
