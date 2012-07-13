package com.vaushell.treetasker.application.activity;

import java.util.List;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;

public class TT_TaskListActivity_ViewPager_Test
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
			public View getNewChildView( final TreeNodeInfo<TT_Task> treeNodeInfo )
			{
				View taskView = getLayoutInflater().inflate( R.layout.task_view_pager,
				                                             null );
				CheckedTextView textView = (CheckedTextView) taskView.findViewById( R.id.textValue );
				textView.setText( treeNodeInfo.getId().getTitle() );
				textView.setOnClickListener( new View.OnClickListener()
				{
					
					@Override
					public void onClick( View v )
					{
						((CheckedTextView) v).toggle();
						treeNodeInfo.getId().setStatus( TT_Task.DONE );
						
					}
				} );

				ViewPager viewPager = (ViewPager) taskView.findViewById( R.id.taskViewPager );
				viewPager.setAdapter( new MyPagerAdapter( textView ) );
				textView.measure( 0, 0 );

				viewPager.getLayoutParams().height = textView.getMeasuredHeight();

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

	private class MyPagerAdapter
	    extends PagerAdapter
	{
		private View	mainView;

		public MyPagerAdapter( View mainView )
		{
			this.mainView = mainView;
		}

		@Override
		public int getCount()
		{
			return 3;
		}

		@Override
		public boolean isViewFromObject( View view,
		                                 Object object )
		{
			return view == object;
		}

		@Override
		public void destroyItem( ViewGroup collection,
		                         int position,
		                         Object object )
		{
			collection.removeView( (View) object );
		}

		@Override
		public Object instantiateItem( ViewGroup container,
		                               int position )
		{
			TextView tv = new CheckedTextView( TT_TaskListActivity_ViewPager_Test.this );
			switch ( position )
			{
				case 0:
					tv.setText( "TODO" );
					tv.setBackgroundColor( Color.RED );
					break;
				case 1:
					container.addView( mainView, 0 );
					return mainView;
				case 2:
					tv.setText( "DONE" );
					tv.setBackgroundColor( Color.GREEN );
					break;
			}
			container.addView( tv, 0 );
			return tv;

		}

		@Override
		public void finishUpdate( ViewGroup container )
		{
			super.finishUpdate( container );
			( (ViewPager) container ).setCurrentItem( 1 );
		}

	}
}
