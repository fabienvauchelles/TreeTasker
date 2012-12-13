package com.vaushell.treetasker.application.service;

import java.util.Collection;
import java.util.Set;

import com.vaushell.treetasker.application.service.DataAccessService.DataAccessServiceListener;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.net.UserSession;

public interface I_DataAccessService
{
	public void createOrUpdateTask(
		TT_Task task,
		boolean isExpanded );

	public void createOrUpdateTasks(
		Collection<TT_Task> tasks,
		Set<String> expandedSet );

	public void deleteTask(
		String taskId );

	public void deleteTasks(
		Collection<String> taskIds );

	public void forceSynchronization(
		UserSession userSession );

	public void registerListener(
		DataAccessServiceListener listener );

	public void requestAllTasks();

	public void requestSynchronization();

	public void unregisterListener(
		DataAccessServiceListener listener );

}
