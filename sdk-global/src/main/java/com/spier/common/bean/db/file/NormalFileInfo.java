package com.spier.common.bean.db.file;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

/**
 * 普通文件信息实体
 * @author GHB
 * @version 1.0
 * @date 2018.12.28
 */
public class NormalFileInfo implements Serializable{
	
	private static final long serialVersionUID = -3926292289424823853L;

	/**
	 * 检查文件信息对于保存操作是否有效
	 * @param info
	 * @return
	 */
	public static boolean isInfoValidForFileSaving(NormalFileInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无效！");
			return false;
		}
		
		if(StringUtils.isEmpty(info.getId())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "id为空，不能保存！");
			return false;
		}
		
		if(info.getFileVerCode() < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("版本号【{0}】无效，不能保存！", info.getFileVerCode()));
			return false;
		}
		
		if(StringUtils.isEmpty(info.getFilePath())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "文件本地路径为空，不能保存！");
			return false;
		}
		
		return true;
	}

	// 在数据库中的序号，主键
	private int mSn;
	
	// 文件的id，唯一标识
	private String mId;
	
	// 文件的描述
	private String mFileDesc;
	
	// 文件版本号
	private int mFileVerCode;
	
	// 文件哈希值
	private String mFileHashCode;
	
	// 文件下载链接
	private String mFileUrl;
	
	// 文件的本地路径
	private String mFilePath;

	/**
	 * 获取此条记录在数据库中的索引号
	 * @return 
	 */
	public int getSn() {
		return mSn;
	}

	/**
	 * 获取文件的唯一标识
	 * @return 不为null
	 */
	public String getId() {
		return mId;
	}

	/**
	 * 设置文件唯一标识
	 * @param 
	 */
	public void setId(String id) {
		this.mId = id;
	}

	/**
	 * 获取文件描述
	 * @return 可能为null
	 */
	public String getFileDesc() {
		return mFileDesc;
	}

	/**
	 * 设置文件描述
	 * @param mFileDesc the mFileDesc to set
	 */
	public void setFileDesc(String fileDesc) {
		this.mFileDesc = fileDesc;
	}

	/**
	 * 获取文件版本号
	 * @return 
	 */
	public int getFileVerCode() {
		return mFileVerCode;
	}

	/**
	 * 设置文件版本号
	 * @param mFileVerCode the mFileVerCode to set
	 */
	public void setFileVerCode(int fileVerCode) {
		this.mFileVerCode = fileVerCode;
	}

	/**
	 * 获取文件哈希值
	 * @return 不为null
	 */
	public String getFileHashCode() {
		return mFileHashCode;
	}

	/**
	 * 设置文件哈希值
	 * @param mFileHashCode the mFileHashCode to set
	 */
	public void setFileHashCode(String fileHashCode) {
		this.mFileHashCode = fileHashCode;
	}

	/**
	 * 获取文件的下载url
	 * @return 不为null
	 */
	public String getFileUrl() {
		return mFileUrl;
	}

	/**
	 * 设置文件的下载url
	 * @param mFileUrl the mFileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.mFileUrl = fileUrl;
	}
	
	/**
	 * 获取文件的本地路径
	 * @return 不能为null
	 */
	public String getFilePath() {
		return mFilePath;
	}
	
	/**
	 * 设置文件的本地路径
	 * @param path
	 */
	public void setFilePath(String path) {
		mFilePath = path;
	}
	
	/**
	 * 检查文件信息对于保存操作是否有效
	 * @return
	 */
	public boolean isValidForFileSaving() {
		return isInfoValidForFileSaving(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MessageFormat.format("Sn: {0}, Id: {1}, Desc: {2}, VerCode: {3}, Hash: {4}, Url: {5}", 
				getSn(), getId(), getFileDesc(), getFileVerCode(), getFileHashCode(), getFileUrl());
	}
	
	
}
