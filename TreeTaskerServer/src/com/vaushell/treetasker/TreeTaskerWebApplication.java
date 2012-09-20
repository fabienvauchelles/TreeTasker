/*
 * MyApplication.java
 *
 * Created on 6 septembre 2012, 10:24
 */
package com.vaushell.treetasker;

import com.vaadin.Application;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/** 
 *
 * @author Fred
 * @version 
 */
public class TreeTaskerWebApplication
        extends Application
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public void init()
    {
        this.controller = new TreeTaskerWebApplicationController( this );
        
        this.controller.showLoginWindow();
        
    }
    
    private TreeTaskerWebApplicationController controller;
}
