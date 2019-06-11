package com.spier.common.bean.db.task;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建时间表 tasks_history_info
 * 
 * @author ruoyi
 * @date 2019-04-01
 */
public class TasksHistoryInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**  */
	private Long id;
	/**  */
	private String taskId;
	/**  */
	private Integer taskTotalCount;
	/**  */
	private Integer taskFinishedCount;
	/**  */
	private Date createTime;

	public void setId(Long id) 
	{
		this.id = id;
	}

	public Long getId() 
	{
		return id;
	}
	public void setTaskId(String taskId) 
	{
		this.taskId = taskId;
	}

	public String getTaskId() 
	{
		return taskId;
	}
	public void setTaskTotalCount(Integer taskTotalCount) 
	{
		this.taskTotalCount = taskTotalCount;
	}

	public Integer getTaskTotalCount() 
	{
		return taskTotalCount;
	}
	public void setTaskFinishedCount(Integer taskFinishedCount) 
	{
		this.taskFinishedCount = taskFinishedCount;
	}

	public Integer getTaskFinishedCount() 
	{
		return taskFinishedCount;
	}
	public void setCreateTime(Date createTime) 
	{
		this.createTime = createTime;
	}

	public Date getCreateTime() 
	{
		return createTime;
	}

    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskId", getTaskId())
            .append("taskTotalCount", getTaskTotalCount())
            .append("taskFinishedCount", getTaskFinishedCount())
            .append("createTime", getCreateTime())
            .toString();
    }
}
