package com.vaushell.treetasker.module;

import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vaushell.treetasker.model.TT_Task;

@XmlRootElement
public class WS_Task
{
	public static final int	TODO	= 0;
	public static final int	DONE	= 1;
	public static final int	DELETED	= 2;

	public WS_Task()
	{
		this( null, null, null, null, TODO, null );
	}

	public WS_Task(
		String id,
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

	public WS_Task(
		TT_Task task )
	{
		this( task.getID(), task.getTitle(), task.getDescription(), task.getLastModificationDate(), task.getStatus(),
			task.getParent() != null ? task.getParent().getID() : null );
	}

	@Override
	public boolean equals(
		Object obj ) {
		if ( this == obj )
		{
			return true;
		}
		if ( obj == null )
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}
		WS_Task other = (WS_Task) obj;
		if ( description == null )
		{
			if ( other.description != null )
			{
				return false;
			}
		}
		else if ( !description.equals( other.description ) )
		{
			return false;
		}
		if ( id == null )
		{
			if ( other.id != null )
			{
				return false;
			}
		}
		else if ( !id.equals( other.id ) )
		{
			return false;
		}
		if ( lastModificationDate == null )
		{
			if ( other.lastModificationDate != null )
			{
				return false;
			}
		}
		else if ( !lastModificationDate.equals( other.lastModificationDate ) )
		{
			return false;
		}
		if ( parentId == null )
		{
			if ( other.parentId != null )
			{
				return false;
			}
		}
		else if ( !parentId.equals( other.parentId ) )
		{
			return false;
		}
		if ( status != other.status )
		{
			return false;
		}
		if ( title == null )
		{
			if ( other.title != null )
			{
				return false;
			}
		}
		else if ( !title.equals( other.title ) )
		{
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	@XmlJavaTypeAdapter( JerseyDateAdapter.class )
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public String getParentId() {
		return parentId;
	}

	public int getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( description == null ? 0 : description.hashCode() );
		result = prime * result + ( id == null ? 0 : id.hashCode() );
		result = prime * result + ( lastModificationDate == null ? 0 : lastModificationDate.hashCode() );
		result = prime * result + ( parentId == null ? 0 : parentId.hashCode() );
		result = prime * result + status;
		result = prime * result + ( title == null ? 0 : title.hashCode() );
		return result;
	}

	public void setDescription(
		String description ) {
		this.description = description;
	}

	public void setId(
		String id ) {
		this.id = id;
	}

	public void setLastModificationDate(
		Date lastModificationDate ) {
		this.lastModificationDate = lastModificationDate;
	}

	public void setParentId(
		String parentId ) {
		this.parentId = parentId;
	}

	public void setStatus(
		int status ) {
		this.status = status;
	}

	public void setTitle(
		String title ) {
		this.title = title;
	}

	public void update(
		TT_Task task ) {
		task.setID( id );
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
	}

	public void update(
		TT_Task task,
		HashMap<String, TT_Task> tasksMap ) {
		task.setID( id );
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
		task.setParent( tasksMap.get( parentId ) );
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private String	id;
	private String	title;
	private String	description;
	private Date	lastModificationDate;
	private int		status;

	private String	parentId;
}
