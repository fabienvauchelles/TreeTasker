/*
 * MyApplication.java
 *
 * Created on 6 septembre 2012, 10:24
 */
package com.vaushell.treetasker;

import com.vaadin.Application;
import com.vaadin.ui.*;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.window.UserWindow;

/** 
 *
 * @author Fred
 * @version 
 */
public class TreeTaskerWebApplication
        extends Application
{
    @Override
    public void init()
    {
        this.controller = new TreeTaskerWebApplicationController( this );
        
        this.controller.showLoginWindow();
        
    }
    
    private TreeTaskerWebApplicationController controller;
}
