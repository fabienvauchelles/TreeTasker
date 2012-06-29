package com.vaushell.treetasker.application.activity;

import java.util.List;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;

public class TT_TaskListActivity
    extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		TreeStateManager<TT_Task> manager = new InMemoryTreeStateManager<TT_Task>();
		TreeBuilder<TT_Task> builder = new TreeBuilder<TT_Task>( manager );

		List<TT_Task> tasks = TreeTaskerControllerDAO.getMockTaskList1();
		for ( TT_Task task : tasks )
		{
			builder.sequentiallyAddNextNode( task, 0 );
			buildRecursively( task, builder );
		}

		AbstractTreeViewAdapter<TT_Task> adapter = new AbstractTreeViewAdapter<TT_Task>( this,
		                                                                                 manager,
		                                                                                 4 )
		{

			@Override
			public long getItemId( int position )
			{
				return getTreeId( position ).getID().hashCode();
			}

			@Override
			public View updateView( View view,
			                        TreeNodeInfo<TT_Task> treeNodeInfo )
			{
				( (CheckedTextView) view.findViewById( R.id.textValue ) ).setText( treeNodeInfo.getId()
				                                                                               .getTitle() );
				return view;
			}

			@Override
			public View getNewChildView( TreeNodeInfo<TT_Task> treeNodeInfo )
			{
				View taskView = getLayoutInflater().inflate( R.layout.task_view,
				                                             null );
				CheckedTextView textView = (CheckedTextView) taskView.findViewById( R.id.textValue );
				textView.setText( treeNodeInfo.getId().getTitle() );
				return taskView;
			}
		};

		setContentView( R.layout.tree_task_view );
		TreeViewList test = (TreeViewList) findViewById( R.id.treeView );
		test.setAdapter( adapter );

	}

	private void buildRecursively( TT_Task parent,
	                               TreeBuilder<TT_Task> builder )
	{
		for ( TT_Task child : parent.getChildrenTask() )
		{
			builder.addRelation( parent, child );
			buildRecursively( child, builder );
		}
	}
}