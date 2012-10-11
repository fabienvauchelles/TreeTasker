/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.content.layout;

import java.util.regex.Pattern;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaushell.treetasker.application.window.I_Form;

/**
 * This layout is a form to register new users.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 * 
 */
public class RegistrationLayout
	extends CustomComponent
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public RegistrationLayout(
		I_Form parent )
	{
		buildMainLayout();
		setCompositionRoot( mainLayout );
		this.parent = parent;
		initComponents();
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getPassword() {
		return (String) vTFpasswordValue.getValue();
	}

	public String getUserName() {
		return (String) vTFmailValue.getValue();
	}

	public boolean isValid() {
		boolean isValid = true;
		String password = (String) vTFpasswordValue.getValue();

		String mail = (String) vTFmailValue.getValue();
		if ( !Pattern.matches( EMAIL_PATTERN, mail ) )
		{
			isValid = false;
			errorMsg = "Veuillez entrer une adresse mail valide.";
		}
		else if ( password == null || password.length() < 4 )
		{
			isValid = false;
			errorMsg = "Mot de passe trop court.";
		}
		else if ( !password.equals( vTFpasswordConfirmationValue.getValue() ) )
		{
			isValid = false;
			errorMsg = "La confirmation du mot de passe ne correspond pas au mot de passe entr�.";
		}
		return isValid;
	}

	@AutoGenerated
	private GridLayout			mainLayout;
	@AutoGenerated
	private HorizontalLayout	buttonsLayout;
	@AutoGenerated
	private Button				vBTregister;
	@AutoGenerated
	private Button				vBTcancel;
	@AutoGenerated
	private PasswordField		vTFpasswordConfirmationValue;
	@AutoGenerated
	private Label				vLBLpassword2;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private PasswordField		vTFpasswordValue;

	@AutoGenerated
	private Label				vLBLpassword;

	@AutoGenerated
	private TextField			vTFmailValue;

	@AutoGenerated
	private Label				vLBLmail;

	@AutoGenerated
	private Label				vLBLregistration;

	private static final String	EMAIL_PATTERN	= "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@AutoGenerated
	private HorizontalLayout buildButtonsLayout() {
		// common part: create layout
		buttonsLayout = new HorizontalLayout();
		buttonsLayout.setImmediate( false );
		buttonsLayout.setWidth( "-1px" );
		buttonsLayout.setHeight( "-1px" );
		buttonsLayout.setMargin( true );
		buttonsLayout.setSpacing( true );

		// vBTcancel
		vBTcancel = new Button();
		vBTcancel.setCaption( "Annuler" );
		vBTcancel.setImmediate( true );
		vBTcancel.setWidth( "-1px" );
		vBTcancel.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTcancel );

		// vBTregister
		vBTregister = new Button();
		vBTregister.setCaption( "S'enregistrer" );
		vBTregister.setImmediate( true );
		vBTregister.setWidth( "-1px" );
		vBTregister.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTregister );

		return buttonsLayout;
	}

	@AutoGenerated
	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout();
		mainLayout.setImmediate( false );
		mainLayout.setWidth( "-1px" );
		mainLayout.setHeight( "-1px" );
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );
		mainLayout.setColumns( 2 );
		mainLayout.setRows( 5 );

		// top-level component properties
		setWidth( "-1px" );
		setHeight( "-1px" );

		// vLBLregistration
		vLBLregistration = new Label();
		vLBLregistration.setImmediate( false );
		vLBLregistration.setWidth( "-1px" );
		vLBLregistration.setHeight( "-1px" );
		vLBLregistration.setValue( "<h2>Enregistrement</h2>" );
		vLBLregistration.setContentMode( 3 );
		mainLayout.addComponent( vLBLregistration, 0, 0 );

		// vLBLmail
		vLBLmail = new Label();
		vLBLmail.setImmediate( false );
		vLBLmail.setWidth( "-1px" );
		vLBLmail.setHeight( "-1px" );
		vLBLmail.setValue( "E-mail" );
		mainLayout.addComponent( vLBLmail, 0, 1 );

		// vTFmailValue
		vTFmailValue = new TextField();
		vTFmailValue.setImmediate( true );
		vTFmailValue.setWidth( "-1px" );
		vTFmailValue.setHeight( "-1px" );
		mainLayout.addComponent( vTFmailValue, 1, 1 );

		// vLBLpassword
		vLBLpassword = new Label();
		vLBLpassword.setImmediate( false );
		vLBLpassword.setWidth( "-1px" );
		vLBLpassword.setHeight( "-1px" );
		vLBLpassword.setValue( "Mot de passe :" );
		mainLayout.addComponent( vLBLpassword, 0, 2 );

		// vTFpasswordValue
		vTFpasswordValue = new PasswordField();
		vTFpasswordValue.setImmediate( true );
		vTFpasswordValue.setWidth( "-1px" );
		vTFpasswordValue.setHeight( "-1px" );
		mainLayout.addComponent( vTFpasswordValue, 1, 2 );

		// vLBLpassword2
		vLBLpassword2 = new Label();
		vLBLpassword2.setImmediate( false );
		vLBLpassword2.setWidth( "-1px" );
		vLBLpassword2.setHeight( "-1px" );
		vLBLpassword2.setValue( "Confirmation de mot de passe :" );
		mainLayout.addComponent( vLBLpassword2, 0, 3 );

		// vTFpasswordConfirmationValue
		vTFpasswordConfirmationValue = new PasswordField();
		vTFpasswordConfirmationValue.setImmediate( true );
		vTFpasswordConfirmationValue.setWidth( "-1px" );
		vTFpasswordConfirmationValue.setHeight( "-1px" );
		mainLayout.addComponent( vTFpasswordConfirmationValue, 1, 3 );

		// buttonsLayout
		buttonsLayout = buildButtonsLayout();
		mainLayout.addComponent( buttonsLayout, 1, 4 );

		return mainLayout;
	}

	private void initComponents() {
		errorMsg = "";
		vBTcancel.addListener( new Button.ClickListener()
		{

			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				if ( parent != null )
				{
					parent.cancel();
				}
			}
		} );

		vBTregister.addListener( new Button.ClickListener()
		{

			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				if ( parent != null )
				{
					parent.ok();
				}
			}
		} );
	}

	private final I_Form	parent;

	private String			errorMsg;

}
