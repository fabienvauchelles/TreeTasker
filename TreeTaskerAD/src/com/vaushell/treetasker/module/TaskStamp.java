package com.vaushell.treetasker.module;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskStamp
{
	// PUBLIC
	public TaskStamp()
	{
		this( null, null );
	}

	public TaskStamp( String taskID,
	                  Date lastModificationDate )
	{
		this.taskID = taskID;
		this.lastModificationDate = lastModificationDate;
		init();
	}

	public String getTaskID()
	{
		return taskID;
	}

	public void setTaskID( String taskID )
	{
		this.taskID = taskID;
	}

	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate( Date lastModificationDate )
	{
		this.lastModificationDate = lastModificationDate;
	}

	// PROTECTED
	// PRIVATE
	private String	taskID;
	private Date	lastModificationDate;

	private void init()
	{

	}
}
