package com.vaushell.treetasker.module;

import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import com.vaushell.treetasker.model.TT_Task;

@XmlRootElement
public class WS_Task
{
	public static final int	  TODO	             = 0;
	public static final int	  DONE	             = 1;
	public static final int	  DELETED	         = 2;

	public WS_Task()
	{
		this( null, null, null, null, TODO, null );
	}

	public WS_Task( String id,
	                String title,
	                String description,
	                Date lastModificationDate,
	                int status,
	                String parentId )
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.lastModificationDate = lastModificationDate;
		this.status = status;
		this.parentId = parentId;
		init();
	}

	public WS_Task( TT_Task task )
	{
		this( task.getID(), task.getTitle(), task.getDescription(),
		      task.getLastModificationDate(), task.getStatus(),
		      ( task.getParent() != null ) ? task.getParent().getID() : null );
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate( Date lastModificationDate )
	{
		this.lastModificationDate = lastModificationDate;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus( int status )
	{
		this.status = status;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId( String parentId )
	{
		this.parentId = parentId;
	}

	public void update( TT_Task task )
	{
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
	}

	public void update( TT_Task task,
	                    HashMap<String, TT_Task> tasksMap )
	{
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
		task.setParent( tasksMap.get( parentId ) );
	}

	// PROTECTED
	// PRIVATE
	private String	id;
	private String	title;
	private String	description;
	private Date	lastModificationDate;
	private int	   status;
	private String	parentId;

	private void init()
	{
	}
}
