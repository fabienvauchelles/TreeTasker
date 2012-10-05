package com.vaushell.treetasker.application.window;

/**
 * Interface for a form with ok and cancel buttons.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 * 
 */
public interface I_Form
{

	/**
	 * Called when cancel button is pressed.
	 */
	public void cancel();

	/**
	 * Called when ok button is pressed.
	 */
	public void ok();
}
