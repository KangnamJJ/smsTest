package com.spier.common.bean.db.task;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.config.GlobalConfig;

/**
 * 任务信息类
 * @author GHB
 * @version 1.0
 * @date 2019.1.8
 */
public class TaskInfo implements Serializable{

	private static final long serialVersionUID = 3439088075612220903L;
	private int mInd;
	private String mTaskId;
	private String mTaskType;
	private String mTaskDesc;
	private int mTaskState;
	private int mTaskTotalCount;
	private int mTaskFinishedCount;
	private String mScriptId;
	private int mForTest;
	private Timestamp mCreateTime;
	
	/**
	 * 在表中的索引号
	 * @return the mInd
	 */
	public int getInd() {
		return mInd;
	}
	
	/**
	 * @param mInd the mInd to set
	 */
	public void setInd(int ind) {
		this.mInd = ind;
	}
	
	/**
	 * 获取任务id
	 * @return the mTaskId
	 */
	public String getTaskId() {
		return mTaskId;
	}
	
	/**
	 * 设置任务id
	 * @param mTaskId the mTaskId to set
	 */
	public void setTaskId(String taskId) {
		if(!StringUtils.isEmpty(taskId)) {
			mTaskId = new String(taskId.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"));
		}
	}
	
	/**
	 * 获取结算类型，如CPI、CPA等
	 * @return
	 */
	public String getTaskType() {
		return mTaskType;
	}
	
	/**
	 * 设置结算类型
	 * @param type
	 */
	public void setTaskType(String type) {
		mTaskType = type;
	}
	
	/**
	 * 获取任务描述
	 * @return the mTaskDesc
	 */
	public String getTaskDesc() {
		return mTaskDesc;
	}
	
	/**
	 * @param mTaskDesc the mTaskDesc to set
	 */
	public void setTaskDesc(String taskDesc) {
		if(!StringUtils.isEmpty(taskDesc)) {
			mTaskDesc = new String(taskDesc.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"));
		}
	}
	
	/**
	 * 任务状态，参见:{@link #M_TASK_STATE_PAUSE}和{@link #M_TASK_STATE_RUNNING}
	 * @return the mTaskState
	 */
	public int getTaskState() {
		return mTaskState;
	}
	
	/**
	 * @param mTaskState the mTaskState to set
	 */
	public void setTaskState(int taskState) {
		this.mTaskState = taskState;
	}
	
	public void setTaskState(String state) {
		if(StringUtils.isEmpty(state)) {
			return;
		}
		
		try {
			mTaskState = Integer.parseInt(state);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取任务总数
	 * @return the mTaskCount
	 */
	public int getTaskTotalCount() {
		return mTaskTotalCount;
	}
	
	/**
	 * 设置任务总数
	 * @param mTaskCount the mTaskCount to set
	 */
	public void setTaskTotalCount(int taskCount) {
		this.mTaskTotalCount = taskCount;
	}
	
	public void setTaskTotalCount(String count) {
		if(StringUtils.isEmpty(count)) {
			return;
		}
		
		try {
			mTaskTotalCount = Integer.parseInt(count);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取已完成数量
	 * @return
	 */
	public int getTaskFinishedCount() {
		return mTaskFinishedCount;
	}
	
	/**
	 * 设置已完成数量
	 * @param count
	 */
	public void setTaskFinishedCount(int count) {
		mTaskFinishedCount = count;
	}
	
	public void setTaskFinishedCount(String str) {
		if(StringUtils.isEmpty(str)) {
			return;
		}
		
		try {
			mTaskFinishedCount = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取脚本id，与脚本表的id一一对应
	 * @return the mScriptInd
	 */
	public String getScriptId() {
		return mScriptId;
	}
	
	/**
	 * @param mScriptInd the mScriptInd to set
	 */
	public void setScriptId(String scriptId) {
		mScriptId = transToUTF8(scriptId);
	}
	
	
	public Timestamp getCreateTime() {
		return mCreateTime;
	}
	
	public void setCreateTime(Timestamp time) {
		mCreateTime = time;
	}
	
	/**
	 * 此任务是否是测试任务
	 * @return 0-否，1-是
	 */
	public int isTaskForTest() {
		return mForTest;
	}
	
	/**
	 * 设置此任务是否是测试任务
	 * @param forTest
	 */
	public void setTaskForTest(int forTest) {
		mForTest = forTest;
	}
	
	public void setTaskForTest(String str) {
		try {
			mForTest = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof TaskInfo)) {
			return false;
		}
		
		TaskInfo other = (TaskInfo) obj;
		
		if(mInd != other.mInd) {
			return false;
		}
		
		if(!StringUtils.equals(mTaskId, other.mTaskId)) {
			return false;
		}
		
		if(!StringUtils.equals(mTaskType, other.mTaskType)) {
			return false;
		}
		
		if(!StringUtils.equals(mTaskDesc, other.mTaskDesc)) {
			return false;
		}
		
		if(mTaskState != other.mTaskState) {
			return false;
		}
		
		if(mTaskTotalCount != other.mTaskTotalCount) {
			return false;
		}
		
		if(mTaskFinishedCount != other.mTaskFinishedCount) {
			return false;
		}
		
		if(!StringUtils.equals(mScriptId, other.mScriptId)) {
			return false;
		}
		
		if(mForTest != other.mForTest) {
			return false;
		}
		
		return true;
	}
	
	public boolean isInfoValid() {
		if(StringUtils.isEmpty(getTaskId())) {
			return false;
		}
		
		if(!GlobalConfig.isTaskStateValid(getTaskState())) {
			return false;
		}
		
		if(getTaskTotalCount() <= 0) {
			return false;
		}
		
		if(getTaskFinishedCount() < 0) {
			return false;
		}
		
		if(StringUtils.isEmpty(getScriptId())) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MessageFormat.format("ID:{0}, TYPE:{1}, DESC:{2}, TASK_STATE:{3}, "
				+ "TOTAL_COUNT:{4}, FINISHED_COUNT:{5}, SCRIPT_ID:{6}", 
				getTaskId(), getTaskType(), getTaskDesc(), getTaskState(), 
				getTaskTotalCount(), getTaskFinishedCount(), getScriptId());
	}
	
	private String transToUTF8(String src) {
		if(StringUtils.isEmpty(src)) {
			return src;
		}
		
		return new String(src.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"));
	}
}
