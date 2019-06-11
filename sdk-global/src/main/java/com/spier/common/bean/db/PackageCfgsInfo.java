package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 包配置信息
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public class PackageCfgsInfo implements Serializable{
	private static final long serialVersionUID = -1770437022343150523L;
	private String mPkgName;
	private String mCfgs;
	private Timestamp mCreateTime;
	
	/**
	 * @return the mPkgName
	 */
	public String getPkgName() {
		return mPkgName;
	}
	
	/**
	 * @param mPkgName the mPkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.mPkgName = pkgName;
	}
	
	/**
	 * @return the mCfgs
	 */
	public String getCfgs() {
		return mCfgs;
	}
	
	/**
	 * @param mCfgs the mCfgs to set
	 */
	public void setCfgs(String cfgs) {
		this.mCfgs = cfgs;
	}
	
	/**
	 * @return the mCreateTime
	 */
	public Timestamp getCreateTime() {
		return mCreateTime;
	}
	
	/**
	 * @param mCreateTime the mCreateTime to set
	 */
	public void setCreateTime(Timestamp createTime) {
		this.mCreateTime = createTime;
	}
	
	private static final Gson mGson =
			new GsonBuilder().disableHtmlEscaping().create();
	
	/**
	 * 反序列化配置
	 * @return 不为null
	 */
	public Map<String, Boolean> deserializeCfgs() {
		Map<String, Boolean> res = new HashMap<String, Boolean>();
		
		try {
			res = mGson.fromJson(getCfgs(), new TypeToken<Map<String, Boolean>>(){}.getType());
		} catch (Exception e) {
			
		}
		
		if(null == res) {
			res = new HashMap<String, Boolean>();
		}
		
		return res;
	}
}
